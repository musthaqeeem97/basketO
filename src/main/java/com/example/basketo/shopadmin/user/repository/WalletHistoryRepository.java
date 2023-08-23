package com.example.basketo.shopadmin.user.repository;

import java.util.List;
import java.util.UUID;

import com.example.basketo.shopadmin.user.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.user.model.WalletHistory;



@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, UUID> {
    List<WalletHistory> findByUserInfo(UserInfo userInfo);
    Page<WalletHistory> findByUserInfo(UserInfo userInfo, Pageable pageable);

    Page<WalletHistory> findAll(Pageable pageable);
}
