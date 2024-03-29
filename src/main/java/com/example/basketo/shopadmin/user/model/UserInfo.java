package com.example.basketo.shopadmin.user.model;


import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.product.model.Review;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


//to prevent stack overflow, exclude cartItems
/*
The stack overflow error is occurring due to a bidirectional relationship
between the UserInfo and Cart entities. The UserInfo entity has a OneToMany
relationship with Cart, and the Cart entity has a ManyToOne relationship with UserInfo.
This circular relationship causes an infinite loop when generating the toString method.

To fix this issue, you can exclude CartItems
*/

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Column(unique=true)

    @NotNull(message = "username is empty")
    private String username;
    @NotNull(message = "First name is empty")
    private String firstName;
    private String lastName;
    @NotNull(message = "password is empty")
    private String password;

    @Column(unique = true)
    @NotNull(message = "email is empty")
    @Email(message = "invalid email")
    private String email;

    @Column(unique = true)
    private String phone;
    private boolean enabled ;

    //Verifying all users by default because twilio only sends otp to my phone.
    private boolean verified ;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id")
    private Role role;
    
    private boolean registered;
	private String otp;
	private LocalDateTime otpRequestedTime;
	
	
	 @OneToMany(mappedBy ="user")
	    @ToString.Exclude
	    @Builder.Default
	    private List<Address> addresses = new ArrayList<Address>();
	 @OneToMany(mappedBy = "userInfo")
	    @ToString.Exclude
	    @Builder.Default
	    private List<CartItem> cartItems = new ArrayList<>();

	    @ManyToOne
	    @JoinColumn(name="coupon_id")
	    private Coupon coupon;

	    @OneToMany(mappedBy = "userInfo")
	    @ToString.Exclude
	    @Builder.Default
	    private List<OrderHistory> orderHistories = new ArrayList<>();

	    @OneToMany(mappedBy = "userInfo")
	    @ToString.Exclude
	    @Builder.Default
	    private List<Wishlist> wishlist = new ArrayList<>();

	    @OneToOne(mappedBy = "userInfo")
	    private Wallet wallet;


	    @OneToMany(mappedBy = "userInfo")
	    @ToString.Exclude
	    @Builder.Default
	    private List<WalletHistory> transactions = new ArrayList<>();

	    @OneToMany(mappedBy = "userInfo")
	    @ToString.Exclude
	    private List<Review> reviews;
    
}