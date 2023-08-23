package com.example.basketo.shopadmin.user.service;

import java.util.List;


import com.example.basketo.shopadmin.user.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.user.model.WalletHistory;
import com.example.basketo.shopadmin.user.model.WalletTransactionType;
import com.example.basketo.shopadmin.user.repository.WalletHistoryRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WalletHistoryService {

	
    private final WalletHistoryRepository walletHistoryRepository;

  
    public WalletHistory recordTransaction(WalletHistory walletHistory) {
        return walletHistoryRepository.save(walletHistory);
    }

    public List<WalletHistory> findTransactionsByUser(UserInfo userInfo) {
        return walletHistoryRepository.findByUserInfo(userInfo);
    }

  
    public WalletHistory transfer(WalletTransactionType walletTransactionType, UserInfo userInfo, double total) {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setAmount(total);
        walletHistory.setUserInfo(userInfo);
        walletHistory.setType(walletTransactionType);
        return walletHistoryRepository.save(walletHistory);
    }

 
    public Page<WalletHistory> findByUserSortedByDate(UserInfo userInfo) {
        Pageable pageable = PageRequest.of(1, 100, Sort.by(Sort.Direction.fromString("ASC"), "createdAt"));
        return walletHistoryRepository.findAll(pageable);
    }


    public Page<WalletHistory> findByUserInfo(UserInfo userInfo, Pageable pageable) {
        return walletHistoryRepository.findByUserInfo(userInfo, pageable);
    }

  
    public Page<WalletHistory> findByUserInfoPaged(UserInfo user, Pageable pageable) {

        return walletHistoryRepository.findByUserInfo(user, pageable);
    }
}
