package com.example.basketo.shopadmin.user.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.basketo.shopadmin.admin.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address extends BaseEntity {

	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
	    @GenericGenerator(name = "uuid2", strategy = "uuid2")
	    @Type(type = "org.hibernate.type.UUIDCharType")
	    private UUID uuid;
	 
    private String fullName;

    private String phone;

    private String city;

    private String state;
    
    private String addressLine1 ;
    
    private String addressLine2;

    private String pincode;

    private String landmark;

    private boolean defaultAddress;
    
   

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;

//    @OneToMany(mappedBy = "address")
//    @ToString.Exclude
//    private List<Order> orders;
}

