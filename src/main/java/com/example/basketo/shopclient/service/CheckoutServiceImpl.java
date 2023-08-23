package com.example.basketo.shopclient.service;

import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.order.enums.OrderType;
import com.example.basketo.shopadmin.user.service.AddressService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.example.basketo.shopclient.model.Checkout;
import com.example.basketo.shopclient.repository.CheckoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl {

	private final CheckoutRepository checkoutRepository;
	private final UsernameProviderService usernameProviderService;
	private final AddressService addressService;

	public UUID saveDeliveryAddress(UUID addressUuid) {
		
		if (!findCheckout().isPresent()) {
			
			createCheckoutID();
		
		}
		
		Checkout checkout = findCheckout().get();
		
		checkout.setAddress(addressService.findAddressById(addressUuid));
		
		checkoutRepository.save(checkout);
		
		return findCheckout().get().getUserID();
		
		
	}
	
	public Optional<Checkout> findCheckout() {
		
		return checkoutRepository.findById(usernameProviderService.get());
	}

	public void createCheckoutID() {
	
	Checkout newCheckout = Checkout.builder()
			.userID(usernameProviderService.get())
			.build();
	checkoutRepository.save(newCheckout);
	}

	public void savePaymentMethod(OrderType paymentMethod) {
		
		if (!findCheckout().isPresent()) {			
			createCheckoutID();
		}
		
		Checkout checkout = findCheckout().get();
		
		checkout.setPaymentMethod(paymentMethod);
		checkoutRepository.save(checkout);		
		
	}

	public void saveCoupon(Coupon coupon) {
		
		if (!findCheckout().isPresent()) {			
			createCheckoutID();
		}
		
		Checkout checkout = findCheckout().get();
		
		checkout.setCoupon(coupon);
		checkoutRepository.save(checkout);		
		
	}

	public void saveOrders(List<CartItem> cart) {
		// TODO Auto-generated method stub
		
	
		
	}
	

		
	
	
}
