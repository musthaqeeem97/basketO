package com.example.basketo.shopadmin.coupon.service;


import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.cart.service.CartServiceImpl;
import com.example.basketo.shopadmin.coupon.enums.CouponType;
import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.coupon.repository.CouponRepository;
import com.example.basketo.shopclient.dto.DiscountDetailsDto;
import com.example.basketo.shopclient.model.Checkout;
import com.example.basketo.shopclient.repository.CheckoutRepository;
import com.example.basketo.shopclient.service.CheckoutServiceImpl;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.UserInfoService;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {
	
	private final CheckoutRepository checkoutRepository;
    private final CheckoutServiceImpl checkoutService;
    private final CouponRepository couponRepository;
    private final UserInfoService userInfoService;
    private final CartServiceImpl cartService;
    public Page<Coupon> findPaginated(int pageNo, int offset, String sortField, String sortDir, String searchTerm) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();


        Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);

        System.err.println(" end of coupon service");

       return couponRepository.findAll(searchTerm,pageable);

    }

    public Optional<Coupon> findById(UUID uuid) {
       return couponRepository.findById(uuid);
    }

    public void save(Coupon coupon) {
    couponRepository.save(coupon);
    }

    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }
    
    public Optional<Coupon> findValidCouponByCode(String code) {
        return couponRepository.findValidCouponByCode(code);
    }

	public void remove(Coupon coupon) {
		couponRepository.delete(coupon);
		
	}

	public DiscountDetailsDto applyCoupon(Coupon coupon) {
		
		UserInfo customer = userInfoService.findByUsername(getCurrentUsername()).get();		
		
		List <CartItem>  cart =   cartService.findCartByCustomer(customer);
		double discountedAmount;
		if (coupon.getType()== CouponType.GENERAL) {
			
			double totalPrice = cart.stream()		        
			        .mapToDouble(item -> item.getProduct().getPrice()*item.getQuantity())
			        .sum();
			
			discountedAmount = totalPrice*coupon.getDiscount()/100 ;
			
		
		}
		else if (coupon.getType()==CouponType.CATEGORY) {
			
			System.err.println("inside coupon type category");

			
			
//			cart.stream()
//		    .map(item -> item.getProduct().getCategory())
//		    .forEach(category -> System.out.println(category));
//			System.out.println(coupon.getCategory());
			
			double totalCategoryPrice = cart.stream()
			        .filter(item -> item.getProduct().getCategory() == coupon.getCategory())
			        .mapToDouble(item -> item.getProduct().getPrice()*item.getQuantity())
			        .sum();
			
			System.out.println("total category price: "+totalCategoryPrice);
			
			discountedAmount = totalCategoryPrice*coupon.getDiscount()/100;
			
			
		}
		else  {
			double productPrice = cart.stream()
			        .filter(item -> item.getProduct() == coupon.getProduct())
			        .mapToDouble(item -> item.getProduct().getPrice()*item.getQuantity())
			        .sum();
			
			System.out.println("product price: "+productPrice);
			
			discountedAmount = productPrice*coupon.getDiscount()/100;
		
		}
		
		System.err.println("discounted Amount: "+discountedAmount);
		
		discountedAmount = discountedAmount<coupon.getMaximumDiscountAmount()?discountedAmount:coupon.getMaximumDiscountAmount();
		
		DiscountDetailsDto discountDetailsDto = DiscountDetailsDto.builder()
		        .couponCode(coupon.getCode())
		        .type(coupon.getType())
		        .discountedAmount(discountedAmount)
		        .discountPercentage(coupon.getDiscount())
		        .build();

		if (coupon.getProduct() != null) {
		    discountDetailsDto.setDiscountedProduct(coupon.getProduct().getName());
		}

		if (coupon.getCategory() != null) {
		    discountDetailsDto.setDiscountedCategory(coupon.getCategory().getName());
		}
        
		if (checkoutService.findCheckout().get()!=null) {
			Checkout checkout = checkoutService.findCheckout().get();
			checkout.setDiscountedAmount(discountedAmount);
	        checkoutRepository.save(checkout);
		}
		
		
		
		return discountDetailsDto;
			
	}
		
		
		
	

public String  getCurrentUsername() {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    

}
