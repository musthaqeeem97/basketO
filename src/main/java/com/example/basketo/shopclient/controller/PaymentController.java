package com.example.basketo.shopclient.controller;




import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.cart.service.CartServiceImpl;
import com.example.basketo.shopadmin.coupon.enums.CouponType;
import com.example.basketo.shopadmin.inventory.service.InventoryService;
import com.example.basketo.shopadmin.order.enums.OrderStatus;
import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.order.service.OrderHistoryService;
import com.example.basketo.shopadmin.order.service.OrderService;
import com.example.basketo.shopclient.model.Checkout;
import com.example.basketo.shopclient.service.CheckoutServiceImpl;
import com.example.basketo.shopclient.service.RazorpayService;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.model.Wallet;
import com.example.basketo.shopadmin.user.model.WalletTransactionType;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.example.basketo.shopadmin.user.service.WalletHistoryService;
import com.example.basketo.shopadmin.user.service.WalletService;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

 
    private final RazorpayService razorpayService;

    private final CartServiceImpl cartService;  
    private final UserInfoService  userService;
    private final OrderHistoryService orderHistoryService;
    private final CheckoutServiceImpl checkoutService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final WalletService walletService;
	private final WalletHistoryService walletHistoryService;
	private final UserInfoService userInfoService;
	private final UsernameProviderService usernameProviderService;

    @Value("${razorpay.keyId}")
    private String keyId;
    @Value("${razorpay.keySecret}")
    private String keySecret;
    @Value("${razorpay.currency}")
    private String currency;
    @Value("${razorpay.company}")
    private String company;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    public String processPayment() {
     
    	
        return "front-end/order-confirmation";
    }
    
//    @GetMapping("/checkout")
//    public ResponseEntity<Double> processCheckout(@RequestParam("total") double total) {
//        // Process the payment and return a response
//        // In a real application, you would process the payment based on the 'total' value
//
//        // For demonstration purposes, we'll just return the same total value
//        return ResponseEntity.ok(total);
//    }
    @GetMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestParam("total") double total, Model model, HttpServletRequest request) {
       
        System.out.println("inside payment method total:" +total);
        
       
        try {
        	  System.out.println("inside payment try");
            // Call the Razorpay service to create an order
            String orderId = razorpayService.createOrder(1000, "INR");
      	  System.out.println("order id created");
            // Store the orderId and total in the session or model for further processing
            HttpSession session = request.getSession();
            session.setAttribute("orderId", orderId);
            session.setAttribute("total", total);
            System.out.println("end payment try");

            // Return a success response
            return ResponseEntity.ok(orderId);
        } catch (RazorpayException e) {
            // Handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during Razorpay integration");
        }
    } 
    
   


    @GetMapping("/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmPayment(@RequestParam("orderId") String orderId, HttpSession session,HttpServletRequest request) {
        
    	System.out.println("inside confirm payment method");
    	
    	try {

//            double total_double = (double) session.getAttribute("total");
//            int total = (int) (total_double*100);
//            System.out.println(total);
            System.out.println(keyId);
            // Assuming payment is successful, you can send a success response with orderId
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "success");
            responseJson.put("orderId", orderId);

            responseJson.put("key", keyId);

         double amount  =   placeOrder(request);
//          
           int orderTotal = (int) amount; 
//           
           System.out.println("order total: "+orderTotal);
           
           responseJson.put("amount", 1000);
            
            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            // Handle exception and return error response
            JSONObject errorJson = new JSONObject();
            errorJson.put("status", "error");
            errorJson.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson.toString());
        }
    }
    
    
    @GetMapping("/wallet")
    public ResponseEntity<String> payByWallet(@RequestParam("total") double total,HttpServletRequest request){
	 
	
        
        Wallet wallet = walletService.findByUserInfo(userInfoService.findByUuid(usernameProviderService.get()).get());
        if (wallet.getBalance()<total){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("invalid amount");
        }

        //debit amount
        wallet.setBalance(wallet.getBalance() - total);
        walletService.save(wallet);

        //Save history
        walletHistoryService.transfer(WalletTransactionType.DEBITED, wallet.getUserInfo(), total);
        HttpSession session = request.getSession();
        session.setAttribute("total", total);
        placeOrder(request);
        System.out.println(total+" debited from the wallet of "+wallet.getUserInfo().getUsername());
        return ResponseEntity.ok(" wallet payment success");
	 
    }
    
    
 
 
    
    
public double placeOrder(HttpServletRequest request) {
        
    	System.out.println("inside place order method");
    	
    	 Checkout checkout = checkoutService.findCheckout().get();
    	 UserInfo customer = userService.findByUsername(getCurrentUsername()).get();
         List<CartItem> cart = cartService.findCartByCustomer(customer);
         
         checkout.setOrderItems(null);
         
         checkoutService.saveOrders(cart);
         
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderStatus(OrderStatus.UNSHIPPED);
        orderHistory.setOrderType(checkout.getPaymentMethod());
        orderHistory.setUserInfo(customer);
        orderHistory.setAddress(checkout.getAddress());
        orderHistoryService.save(orderHistory);
        
        System.out.println("inside place order method 2");
    	List<OrderItem> orderList = new ArrayList<>();
 
    	double ordertotal =0;
        for (CartItem item : cart) {
        	if (checkout.getCoupon()!=null) {
				
        		 System.out.println("inside place order method 3");
        	
            if (checkout.getCoupon().getType()== CouponType.PRODUCT) {
            	 System.out.println("inside place order method 4");
                if (item.getProduct() == checkout.getCoupon().getProduct()) {
                    item.setAmount((item.getProduct().getPrice() * item.getQuantity()) - checkout.getDiscountedAmount());
                } else {
                    item.setAmount(item.getProduct().getPrice() * item.getQuantity());
                }
            } else if (checkout.getCoupon().getType() == CouponType.CATEGORY) {
            	 System.out.println("inside place order method 4.5");
            	 System.out.println(checkout.getDiscountedAmount());
            	
            	 
                Double itemDiscount=  checkout.getDiscountedAmount()/couponCategoryProductsCount(cart);
                System.out.println(itemDiscount);
                if (item.getProduct().getCategory() == checkout.getCoupon().getCategory()) {
                	           	
                    item.setAmount((item.getProduct().getPrice() * item.getQuantity()) - itemDiscount);
                } else {
                    item.setAmount(item.getProduct().getPrice() * item.getQuantity());
                }
            }else {
            	 System.out.println("inside place order method 5");
            	 item.setAmount(item.getProduct().getPrice() * item.getQuantity()-checkout.getDiscountedAmount());
            }
            
            System.out.println("inside place order method 6");
        	}else {
        		 item.setAmount(item.getProduct().getPrice() * item.getQuantity());
        	}
        	
        	
        	if(item.getQuantity() <= inventoryService.findbyProduct(item.getProduct()).getAvailableQuantity()){
              
           
        	 OrderItem orderItem = OrderItem.builder()
             		.product(item.getProduct())
             		.quantity(item.getQuantity())
             		.orderHistory(orderHistory)
             		.userInfo(customer)
             		.status(OrderStatus.UNSHIPPED)
             		.amount(item.getAmount())
             		.build();
        	 
        	 inventoryService.changeQuantity(item.getProduct(),item.getQuantity());
        	 
        	 System.out.println("item price: "+item.getAmount());
        	 ordertotal += orderItem.getAmount();
        	 
        	 System.out.println("inside place order method 7");
        	 
        	 orderService.save(orderItem);
        	 System.out.println("inside place order method 7.5");
             orderList.add(orderItem);
             System.out.println("inside place order method 8");
             System.out.println("inside place order method 8.5");
             cartService.delete(item);
           
             
        	}else {
        		System.out.println(item.getProduct().getName()+" does not have "+item.getQuantity());
        		System.out.println("available quantity: "+inventoryService.findbyProduct(item.getProduct()).getAvailableQuantity());
        	}
        	
        	
        }
        HttpSession session = request.getSession();
        session.setAttribute("orderHistoryId", orderHistory.getUuid());
        
      System.out.println("inside place order method 9");
      orderHistory.setTotal(ordertotal);
        orderHistory.setOrderItems(orderList);
        orderHistory.setTotalDiscount(checkout.getDiscountedAmount());
        orderHistory.setCoupon(checkout.getCoupon());
        orderHistoryService.save(orderHistory);
        
      
       
        System.out.println("end of place order method");

        // Return the name of the view to display after order submission
        // Replace "order_confirmation" with the name of your confirmation view
        return ordertotal;
    }
 
    
 
 
 	public long couponCategoryProductsCount(List<CartItem> cart ) {
 	
   	 Checkout checkout = checkoutService.findCheckout().get();
   	 

    	 cart.stream().
 		 forEach(item->System.out.println(item.getProduct().getCategory()));
 		
    	 
    	 long count = cart.stream().
    			 filter(item->item.getProduct().getCategory()==checkout.getCoupon().getCategory())
    			 .count();
    	 System.out.println("count:"+count);
    	 return count==0?1:count;
 	}
    
    
}