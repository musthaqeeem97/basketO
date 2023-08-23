package com.example.basketo.shopadmin.user.service;


import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.model.WalletTransactionType;
import com.example.basketo.shopadmin.user.repository.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.user.model.Wallet;
import com.example.basketo.shopadmin.user.model.WalletHistory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

	
	    private final WalletRepository walletRepository;
	    private final UserInfoService userInfoService;
	    private final WalletHistoryService walletHistoryService;


	
	    public Wallet credit(Float amount) {
	        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername()).get();
	        Wallet wallet = userInfo.getWallet();
	        if(wallet == null){
	            System.out.println("No wallet found for user, creating a new wallet with 0 bal");
	            wallet = new Wallet();
	            wallet.setUserInfo(userInfo);
	            wallet.setBalance(0D);
	        }
	        wallet.setBalance(wallet.getBalance() + amount);

	    
	        WalletHistory walletHistory = new WalletHistory();
	        walletHistory.setAmount(amount);
	        walletHistory.setUserInfo(userInfo);
	        walletHistory.setType(WalletTransactionType.CREDITED);

	        walletHistoryService.recordTransaction(walletHistory);

	        wallet = walletRepository.save(wallet);
	        
	        return wallet;
	    }



	    public Wallet findByUserInfo(UserInfo user) {
	        return walletRepository.findByUserInfo(user);
	    }


	    public Wallet save(Wallet wallet) {
	        return walletRepository.save(wallet);
	    }


	    //getting current logged in username
	    public String getCurrentUsername() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        return authentication.getName();
	    }
}
