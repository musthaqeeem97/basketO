package com.example.basketo.shopadmin.product.model;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.basketo.shopadmin.admin.model.BaseEntity;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category extends BaseEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
	private UUID uuid;
	
	@Column(unique=true)
	private String name;
	
	@Lob
	private String description;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<CategoryImage> images;

	@OneToMany(mappedBy = "category")
    @ToString.Exclude
    @Builder.Default
    private List<Product> products = new ArrayList<>();

}
