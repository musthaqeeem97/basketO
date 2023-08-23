package com.example.basketo.shopclient.controller;


import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.basketo.shopadmin.user.model.Address;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.AddressService;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;


import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
	
	private final AddressService addressService;
	private final UsernameProviderService usernameProviderService;
	private final UserInfoService  userInfoService;

	@GetMapping
	public String getAddAddressPage(Model model) {
		
		model.addAttribute("address",new Address());
		return "front-end/add-address";
	}
	
	@PostMapping("/create")
	public String saveAddress(@ModelAttribute("address") Address address, Model model){
		
		addressService.saveAddress(address);
		UserInfo userInfo = userInfoService.getUser(usernameProviderService.get());
		
		model.addAttribute("addresses", userInfo.getAddresses());
		model.addAttribute("saved","Address saved.");
		return "front-end/addresses";
	}
	
	@GetMapping("/addresses")
	public String getAddresses(Model model) {
		
		addressService.findAddressesByUser(usernameProviderService.get()).stream().forEach(address->System.out.println(address.isDefaultAddress()));
		model.addAttribute("addresses", addressService.findAddressesByUser(usernameProviderService.get()));
		return "front-end/addresses";
	}
	
	@GetMapping("/edit/{id}")
	public String editAddress(@PathVariable("id") UUID id, Model model) {
		
		
		model.addAttribute("address",addressService.findAddressById(id));
		
		return"front-end/update-address";
	}
	
	@PostMapping("/update")
	public String editAddress(@ModelAttribute("address")Address address,Model model) {
		
		addressService.saveAddress(address);
		UserInfo userInfo = userInfoService.getUser(usernameProviderService.get());
		
		model.addAttribute("saved","Address saved.");
		model.addAttribute("addresses",userInfo.getAddresses());
		return "front-end/addresses";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteAddress(@PathVariable("id")UUID id,Model model) {
		addressService.deleteAddress(id);
        UserInfo userInfo = userInfoService.getUser(usernameProviderService.get());
		
		model.addAttribute("deleted","Address deleted.");
		model.addAttribute("addresses",userInfo.getAddresses());
		
		System.err.println("end of delete address");
		return "front-end/addresses";
	}
	
	@GetMapping("/makedefault/{id}")
	public String makeDefault(@PathVariable("id") UUID id, Model model) {
		
		addressService.makeDefault(id);
			
		
		model.addAttribute("addresses", addressService.findAddressesByUser(usernameProviderService.get()));
		return "redirect:/";
	}
	
	
}
