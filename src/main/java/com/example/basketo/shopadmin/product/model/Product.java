package com.example.basketo.shopadmin.product.model;


import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


import com.example.basketo.shopadmin.admin.model.BaseEntity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
	
    private String name;


    private float price;
    
  

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private ProductImage mainImage;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Thumbnail> thumbnails;
    

    @Lob
    private String description;

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<Variant> variant;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Builder.Default
    private boolean enabled=true;



    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<Review> reviews;

//    @OneToOne(mappedBy = "product_id", cascade = CascadeType.ALL)
//    @ToString.Exclude
//    private ProductInventory inventory;
    

 
    
    public Double getRating(){
        return this.reviews
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }
}