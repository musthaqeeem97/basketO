package com.example.basketo.shopadmin.user.repository;


import java.util.Optional;
import java.util.UUID;

import com.example.basketo.shopadmin.user.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {
    
	Optional<UserInfo> findByUsername(String username);

    Optional<UserInfo> findByEmail(String email);

    UserInfo findByPhone(String phone);
    
   // List<UserInfo> findByUsernameLike(String pattern);

    @Query(value = "SELECT * FROM user_info WHERE username LIKE %:searchTerm% OR email LIKE %:searchTerm% OR phone LIKE %:searchTerm% OR phone LIKE %:searchTerm% OR first_name LIKE %:searchTerm% OR last_name LIKE %:searchTerm% OR uuid LIKE %:searchTerm%", nativeQuery = true)
    Page<UserInfo> findAll(@Param("searchTerm") String searchTerm, Pageable pageable);


   

	
}