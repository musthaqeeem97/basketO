package com.example.basketo.shopadmin.order.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.basketo.shopadmin.order.enums.OrderStatus;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.model.Wallet;
import com.example.basketo.shopadmin.user.model.WalletTransactionType;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.example.basketo.shopadmin.user.service.WalletHistoryService;
import com.example.basketo.shopadmin.user.service.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService  {


    private final OrderRepository orderRepository;
    private final UserInfoService userInfoService;
    private final UsernameProviderService usernameProviderService;
    private final WalletService walletService;
    private final WalletHistoryService walletHistoryService;

	public void save(OrderItem orderItem) {
		if(orderItem.getStatus()== OrderStatus.RETURNED) {
			
			UserInfo user =userInfoService.findByUuid(usernameProviderService.get()).get();
			if(walletService.findByUserInfo(user)==null) {
				Wallet wallet = new Wallet();
				wallet.setBalance(orderItem.getAmount());
				wallet.setUserInfo(user);
				walletService.save(wallet);
			}else {
				Wallet wallet = walletService.findByUserInfo(user);
				wallet.setBalance(wallet.getBalance()+orderItem.getAmount());
				walletService.save(wallet);
			}
		
			walletHistoryService.transfer(WalletTransactionType.CREDITED,user,orderItem.getAmount());

	        System.out.println(orderItem.getAmount()+" credited to the wallet of "+user.getUsername());
	      
		}
		System.out.println("inside order service");
		orderRepository.save(orderItem);
		System.out.println("end order service");
	}


	public Page<OrderItem> findPaginated(int pageNo, int offset, String sortField, String sortDir, String searchTerm) {
		 Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
             Sort.by(sortField).descending();


     Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);

     System.err.println(" end of coupon service");

    return orderRepository.findAll(searchTerm,pageable);
	}
	public Page<OrderItem> findPaginatedByUser(int pageNo, int offset, String sortField, String sortDir, String searchTerm) {
		 Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
            Sort.by(sortField).descending();


    Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);

    System.err.println(" end of coupon service");
    UserInfo userInfo = userInfoService.findByUuid(usernameProviderService.get()).get();

   return orderRepository.findByCustomer(userInfo,pageable);
	}

	public OrderItem findById(UUID id) {
		// TODO Auto-generated method stub
		return orderRepository.findById(id).get();
	}
	public List<OrderItem> findOrdersByDate(Date startDate, Date endDate) {
		
        Timestamp startTime = new Timestamp(startDate.getTime());
        Timestamp endTime = new Timestamp(endDate.getTime());
        return orderRepository.findByCreatedAtBetween(startTime, endTime);

    }

	


	
	
}
