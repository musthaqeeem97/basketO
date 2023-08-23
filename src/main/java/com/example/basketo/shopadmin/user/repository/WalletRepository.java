package com.example.basketo.shopadmin.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.model.Wallet;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Wallet findByUserInfo(UserInfo user);
}
