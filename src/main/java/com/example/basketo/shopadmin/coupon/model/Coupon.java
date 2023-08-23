package com.example.basketo.shopadmin.coupon.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;

import com.example.basketo.shopadmin.coupon.enums.CouponType;
import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.user.model.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Coupon extends BaseEntity { 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Coupon code is required")
    private String code;

    private CouponType type;

    private int discount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirationPeriod;

    private int couponStock;

    private int maximumDiscountAmount;

    //private CouponType type;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    private boolean isDeleted;

    public boolean isExpired() {
        return (couponStock == 0 || expirationPeriod != null && expirationPeriod.before(new Date()));
    }
}
