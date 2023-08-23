package com.example.basketo.shopadmin.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.inventory.model.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

}
