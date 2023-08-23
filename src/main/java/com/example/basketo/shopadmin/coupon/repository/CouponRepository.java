package com.example.basketo.shopadmin.coupon.repository;

import com.example.basketo.shopadmin.coupon.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query(value = "SELECT * FROM coupon WHERE uuid LIKE %:searchTerm% OR code LIKE %:searchTerm% OR "+
            "discount  LIKE %:searchTerm%  OR "  +
            "expiration_period LIKE %:searchTerm%  OR " +
            "coupon_stock LIKE %:searchTerm% OR " +
            "maximum_discount_amount  LIKE %:searchTerm%", nativeQuery = true)
    Page<Coupon> findAll(@Param("searchTerm")String searchTerm, Pageable pageable);

	Optional<Coupon> findByCode(String code);
	
	@Query(value = "SELECT * FROM coupon WHERE code = :code AND coupon_stock > 0 AND expiration_period > CURRENT_DATE AND is_deleted = false", nativeQuery = true)
    Optional<Coupon> findValidCouponByCode(@Param("code") String code);
}
