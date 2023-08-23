package com.example.basketo.shopadmin.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.basketo.shopadmin.user.model.Role;



public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query(value = "SELECT * FROM role WHERE role_name = :name", nativeQuery = true)
    Optional<Role> findRoleByName(@Param("name") String name);
    
    @Override
     List<Role> findAll();
    	// TODO Auto-generated method stub
    	
}
