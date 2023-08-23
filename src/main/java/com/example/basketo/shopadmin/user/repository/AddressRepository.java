package com.example.basketo.shopadmin.user.repository;

import java.util.List;
import java.util.UUID;

import com.example.basketo.shopadmin.user.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.user.model.Address;


@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUser(UserInfo user);

    @Query(value = "SELECT * FROM address WHERE default_address=true", nativeQuery = true)
	Address findByDefaultAddress();
    
}

