package com.example.basketo.shopclient.controller;

import com.example.basketo.shopadmin.user.model.Wallet;
import com.example.basketo.shopadmin.user.model.WalletTransactionType;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.example.basketo.shopadmin.user.service.WalletHistoryService;
import com.example.basketo.shopadmin.user.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

	private final WalletService walletService;
	private final WalletHistoryService walletHistoryService;
	private final UserInfoService userInfoService;
	private final UsernameProviderService usernameProviderService;
	 @GetMapping("/debit")
	    public ResponseEntity<String> debit(@RequestParam("total") double total){


	        
	        Wallet wallet = walletService.findByUserInfo(userInfoService.findByUuid(usernameProviderService.get()).get());
	        if (wallet.getBalance()<total){
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("invalid amount");
	        }

	        //debit amount
	        wallet.setBalance(wallet.getBalance() - total);
	        walletService.save(wallet);

	        //Save history
	        walletHistoryService.transfer(WalletTransactionType.DEBITED, wallet.getUserInfo(), total);

	        System.out.println(total+" debited from the wallet of "+wallet.getUserInfo().getUsername());
	        return ResponseEntity.ok(" wallet payment success");
		 
	    }
	    
	    
	    
	    
	    
}
