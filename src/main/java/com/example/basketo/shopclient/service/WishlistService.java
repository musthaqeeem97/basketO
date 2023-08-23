package com.example.basketo.shopclient.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopclient.model.WishlistItem;
import com.example.basketo.shopclient.repository.WishlistRepository;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final UserInfoService userInfoService; 
	private final UsernameProviderService usernameProviderService;

	public boolean addToWishlist(UserInfo user,Product product) {
		
	WishlistItem item = wishlistRepository.findByUserInfoAndProduct(user,product);
	if(item ==  null) {
		item = WishlistItem.builder()
				.userInfo(user)
				.product(product)
				.build();
		wishlistRepository.save(item);
		return true;
	}
	return false;
	}
	
	public void deleteItem(WishlistItem wishlistItem) {
		wishlistRepository.delete(wishlistItem);
	}

	public Page<WishlistItem> findPaginated(int pageNo, int offset, String sortField, String sortDir,
			String searchTerm) {
		 Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
             Sort.by(sortField).descending();


     Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);
     UserInfo user = userInfoService.findByUuid(usernameProviderService.get()).get();
     
     System.err.println(" end of wishlist pagination service");

    return wishlistRepository.findAllByUserInfo(user,pageable);
    
	}

	public WishlistItem findById(UUID itemId) {
		
		return wishlistRepository.findById(itemId).get();
	}
}
