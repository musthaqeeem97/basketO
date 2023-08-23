package com.example.basketo.shopadmin.user.service;

import com.example.basketo.shopadmin.user.model.Address;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final UsernameProviderService usernameProviderService;
	private final AddressRepository addressRepository;
	private final UserInfoService userInfoService;
	
	
	public void saveAddress(Address address) {
		
		UUID userUuid = usernameProviderService.get();
		UserInfo userInfo = userInfoService.getUser(userUuid);
		address.setUser(userInfo);
		
		if (address.isDefaultAddress()) {
			Address defaultAddress = addressRepository.findByDefaultAddress();
			if (defaultAddress!=null) {
				defaultAddress.setDefaultAddress(false);
				addressRepository.save(defaultAddress);
			}
		
			
			System.out.println(address.isDefaultAddress());
		}
		
		
		addressRepository.save(address);
		
	}
	
	public List<Address> findAddressesByUser(UUID uuid){
		
		
		UserInfo userInfo = userInfoService.getUser(uuid);
		return addressRepository.findByUser(userInfo);
		
	}
	
	public Address findAddressById(UUID id) {
		
		return addressRepository.findById(id).get();
	}

	public void deleteAddress(UUID id) {
		
		addressRepository.deleteById(id);
	}

	public void makeDefault(UUID id) {
		
		Address defaultAddress = addressRepository.findByDefaultAddress();
		if (defaultAddress!=null) {
			System.out.println("inside make default service, is default: "+ defaultAddress.isDefaultAddress() );
			
			defaultAddress.setDefaultAddress(false);
			addressRepository.save(defaultAddress);
			System.out.println("inside make default service, is default: "+ defaultAddress.isDefaultAddress() );

		}
		
		Address currentAddress = addressRepository.findById(id).get();
		currentAddress.setDefaultAddress(true);

		System.out.println("current address, is default: "+ currentAddress.isDefaultAddress() );
		addressRepository.save(currentAddress);
	}
}
