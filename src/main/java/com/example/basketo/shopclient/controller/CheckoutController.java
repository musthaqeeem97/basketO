package com.example.basketo.shopclient.controller;


import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.cart.service.CartServiceImpl;
import com.example.basketo.shopadmin.coupon.enums.CouponType;
import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.coupon.service.CouponService;
import com.example.basketo.shopadmin.inventory.service.InventoryService;
import com.example.basketo.shopadmin.order.enums.OrderStatus;
import com.example.basketo.shopadmin.order.enums.OrderType;
import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.order.service.OrderHistoryService;
import com.example.basketo.shopadmin.order.service.OrderService;
import com.example.basketo.shopadmin.user.model.Address;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.AddressService;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.example.basketo.shopclient.dto.DiscountDetailsDto;
import com.example.basketo.shopclient.dto.OrderFormDto;
import com.example.basketo.shopclient.model.Checkout;
import com.example.basketo.shopclient.service.CheckoutServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/buy")
@RequiredArgsConstructor
public class CheckoutController {

    private final AddressService addressService;
    private final UsernameProviderService usernameProviderService;
    private final UserInfoService userInfoService;
    private final CartServiceImpl cartService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final CheckoutServiceImpl checkoutService;
    private final OrderHistoryService orderHistoryService;
    private final InventoryService inventoryService;
    
    @GetMapping
    public String getCheckoutPage( Model model){
    	
    	if (!checkoutService.findCheckout().isPresent()) {
    		checkoutService.createCheckoutID();
		}
    	
    	
        List<Address> addressList = addressService.findAddressesByUser(usernameProviderService.get());
        model.addAttribute("addressList",addressList);
        
        UserInfo customer = userInfoService.getUser(usernameProviderService.get());
	    List<CartItem> listCartItems = cartService.findCartByCustomer(customer);
	    
	    String  imageName = listCartItems.get(0).getProduct().getMainImage().getFileName();
	  
	    System.err.println("image name: "+ imageName);
	    int totalItems=listCartItems.stream().mapToInt(item->item.getQuantity()).sum();
	    
	    Double totalPrice = listCartItems.stream()
	            .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
	            .sum();
	  
	   System.out.println("total items: "+totalItems  );
	   System.out.println("total price: "+totalPrice  );
	  
//	    model.addAttribute("totalItems", totalItems);
	    model.addAttribute("totalPrice", totalPrice);
       
        return "front-end/checkout";
    }
//    @GetMapping("/cart/{cartId}")
//    public String getCartCheckout(@PathVariable("cartId") UUID ID, Model model){
//        return "front-end/checkout
//    }
    
    @PostMapping("/saveaddress")
    public String saveSelectedDeliveryAddress(@ModelAttribute("selectedAddress")UUID addressUuid ,Model model){
    	
    
    	System.out.println("selected address uuid: " + addressUuid);
    	
    	System.out.println("address uuid: "+ checkoutService.saveDeliveryAddress(addressUuid));
        checkoutService.saveDeliveryAddress(addressUuid);
        
        UserInfo customer = userInfoService.getUser(usernameProviderService.get());
	    List<CartItem> listCartItems = cartService.findCartByCustomer(customer);
	    
	    if (checkoutService.findCheckout().isPresent()) {
	    	 Address deliveryAddress  = checkoutService.findCheckout().get().getAddress();
	    	 model.addAttribute("address", deliveryAddress);	
		}
	   
	   
	    //int totalItems=listCartItems.stream().mapToInt(item->item.getQuantity()).sum();
	    
	    Double totalPrice = listCartItems.stream()
	            .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
	            .sum();
	  
	  // System.out.println("total items: "+totalItems  );
	   System.out.println("total price: "+totalPrice  );
	  
	    //model.addAttribute("totalItems", totalItems);
	    model.addAttribute("totalPrice", totalPrice);	   
	    model.addAttribute("cod",OrderType.COD);
	    model.addAttribute("online",OrderType.ONLINE);
	    model.addAttribute("wallet", OrderType.WALLET);
	    model.addAttribute("payement",true);
	    model.addAttribute("usePayment",true);
	    model.addAttribute("customer", customer);
        
        return "front-end/checkout";
    }
//    @GetMapping("/cart/{cartId}")
//    public String getCartCheckout(@PathVariable("cartId") UUID ID, Model model){
//        return "front-end/checkout
//    }
    
    @PostMapping("/savepaymentmethod")
    public String savePaymentMethod  (@ModelAttribute("paymentMethod")OrderType paymentMethod ,Model model){
    	
    	
    	checkoutService.savePaymentMethod(paymentMethod);
    	
        UserInfo customer = userInfoService.getUser(usernameProviderService.get());
	    List<CartItem> listCartItems = cartService.findCartByCustomer(customer);
	    
	   
	  
	    System.err.println("image name: ");
	    int totalItems=listCartItems.stream().mapToInt(item->item.getQuantity()).sum();
	    
	    Double totalPrice = listCartItems.stream()
	            .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
	            .sum();
	  
	    
	    Checkout checkout =checkoutService.findCheckout().get();
	    
	   System.out.println("total items: "+totalItems  );
	   System.out.println("total price: "+totalPrice  );
	  
	   if (checkout.getCoupon()!=null) {
		   model.addAttribute("coupon", checkout.getCoupon());
	}else {
		 model.addAttribute("couponForm","enter coupon code");
	}
	   
	   
	    model.addAttribute("listCart",listCartItems);
	    String selectedPayment = (paymentMethod == OrderType.COD) ? "Cash On Delivery" :
            (paymentMethod == OrderType.WALLET) ? "Wallet Payment" :
            "Online Payment";

	    System.out.println("selected payment: "+selectedPayment);
	    model.addAttribute("selectedPayment", selectedPayment);
	    model.addAttribute("totalItems", totalItems);
	    model.addAttribute("totalPrice", totalPrice);
	   
	    	 Address deliveryAddress  = checkoutService.findCheckout().get().getAddress();
	    	 model.addAttribute("address", deliveryAddress);	

	    	 System.out.println("couponcode: "+checkout.getCoupon());
	    	
	    	 if (checkout.getCoupon()!=null ) {
				return "redirect:/buy/applycoupon?couponCode="+checkout.getCoupon().getCode();
			}
        
        return "front-end/checkout";
    }
    
    
    
    
    @GetMapping("/applycoupon")
    public String applyCoupon(@RequestParam("couponCode") String couponCode, Model model) {

	
        UserInfo customer = userInfoService.findByUsername(getCurrentUsername()).get();
        List<CartItem> cart = cartService.findCartByCustomer(customer);
      

        System.out.println(couponCode);
        
        Optional<Coupon> coupon = couponService.findValidCouponByCode(couponCode);
        System.out.println("validcoupon: "+couponService.findValidCouponByCode(couponCode)); 
      
        int totalItems=cart.stream().mapToInt(item->item.getQuantity()).sum();
        
        Checkout checkout = checkoutService.findCheckout().get();
        
        Address address = checkout.getAddress();
        OrderType selectedPayment = checkout.getPaymentMethod();
        
        double totalPrice = cart.stream()		        
			        .mapToDouble(item -> item.getProduct().getPrice()*item.getQuantity())
			        .sum();
        model.addAttribute("address",address);
        model.addAttribute("selectedPayment", selectedPayment==OrderType.COD?"Cash On Delivery":
            (selectedPayment == OrderType.WALLET) ? "Wallet Payment" :
            "Online Payment");
        model.addAttribute("listCart",cart);
	     model.addAttribute("totalItems", totalItems);
	     model.addAttribute("totalPrice", totalPrice);
        
        if (!cart.isEmpty()&& coupon.isPresent() ) {
        	 DiscountDetailsDto discountDetails = couponService.applyCoupon(coupon.get());
        	 checkoutService.saveCoupon(coupon.get());             
           
 			
             totalPrice = totalPrice-discountDetails.getDiscountedAmount();
           
             System.out.println("discount details:"+discountDetails);
             
             model.addAttribute("orderForm", new OrderFormDto());
             model.addAttribute("coupon", checkout.getCoupon());
     	     model.addAttribute("totalPrice", totalPrice);
     	     model.addAttribute("discountDetails", discountDetails);
     	    
     	    return "front-end/checkout";
        }

           model.addAttribute("couponForm","invalid coupon");
        return "front-end/checkout";

       
    }
    
    @GetMapping("/discardcoupon")
    public String discardCoupon(Model model) {
    	
    	System.out.println("inside discard coupon method");
   
    	checkoutService.saveCoupon(null);
    	
    	displayCheckoutForm(model);
    	
    	return "front-end/checkout";

    }
    
    
    @GetMapping("/cod")   
    public String cod(HttpServletRequest request, HttpSession session,Model model) {
   placeOrder(request);
   	 
   System.out.println("order histroy id: " +session.getAttribute("orderHistoryId") );
        UUID orderID=  (UUID) session.getAttribute("orderHistoryId");
   	
         OrderHistory orderHistory = orderHistoryService.findById(orderID).get();
         List<OrderItem> recentOrders = orderHistory.getOrderItems();
         
         recentOrders.stream().forEach(item->System.out.println(item.getProduct().getName() + item.getAmount()));
   	 
      
     List<CartItem> cart =  cartService.findCartByCustomer(userInfoService.findByUsername(getCurrentUsername()).get());
     if (!cart.isEmpty()) {
    	 model.addAttribute("cart", cart);
     }
    
     model.addAttribute("orders", recentOrders);
     model.addAttribute("orderHistory", orderHistory);
     
   	return "front-end/orderconfirmation";
   }   
 
 
    
    
 public void placeOrder(HttpServletRequest request) {
        
    	System.out.println("inside place order method");
    	
    	 Checkout checkout = checkoutService.findCheckout().get();
    	 UserInfo customer = userInfoService.findByUsername(getCurrentUsername()).get();
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
        if (checkout.getDiscountedAmount()!=null) {
            orderHistory.setTotalDiscount(checkout.getDiscountedAmount());
        }
        orderHistoryService.save(orderHistory);
        
      
       
System.out.println("end of place order method");

        // Return the name of the view to display after order submission
        // Replace "order_confirmation" with the name of your confirmation view


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
    
    


    public String  getCurrentUsername() {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
//    public long couponCategoryProductsCount() {
//    	
//   	 Checkout checkout = checkoutService.findCheckout().get();
//   	 
//   	 long count = checkout.getOrderItems().stream().
//   			 filter(item->item.getProduct().getCategory()==checkout.getCoupon().getCategory())
//   			 .count();
//   	 return count;
//    }
//    
    public void displayCheckoutForm(Model model) {
    	UserInfo customer = userInfoService.findByUsername(getCurrentUsername()).get();
        List<CartItem> cart = cartService.findCartByCustomer(customer);
     
    
        int totalItems=cart.stream().mapToInt(item->item.getQuantity()).sum();
        
        Checkout checkout = checkoutService.findCheckout().get();
        
        Address address = checkout.getAddress();
        OrderType selectedPayment = checkout.getPaymentMethod();
        
        double totalPrice = cart.stream()		        
			        .mapToDouble(item -> item.getProduct().getPrice()*item.getQuantity())
			        .sum();
        model.addAttribute("address",address);
        model.addAttribute("selectedPayment", selectedPayment==OrderType.COD?"Cash On Delivery":"Online Payment");
        model.addAttribute("listCart",cart);
	     model.addAttribute("totalItems", totalItems);
	     model.addAttribute("totalPrice", totalPrice);
	     model.addAttribute("couponForm","enter coupon code");
    }
    
}
