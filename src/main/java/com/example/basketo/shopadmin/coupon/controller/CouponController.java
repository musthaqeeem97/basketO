package com.example.basketo.shopadmin.coupon.controller;

import com.example.basketo.shopadmin.coupon.enums.CouponType;
import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.coupon.service.CouponService;

import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.service.CategoryServiceImpl;
import com.example.basketo.shopadmin.product.service.ProductServiceImpl;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("adminpanel/coupon")
@RequiredArgsConstructor
public class CouponController {
    @Value("${table.pageNo}")
    private int pageNo;

    @Value("${table.offset}")
    private int offset;

    @Value("${table.sortDir}")
    private String sortDir;

    @Value("${coupon.table.sortField}")
    private String sortField;

    private final CouponService couponService;
    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final UserInfoService userInfoService;
    private final UsernameProviderService usernameProvider;


    @GetMapping
    public String couponList(Model model) {
        return getCouponPaginated(pageNo, sortField, sortDir, "", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getCouponPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String searchTerm,
                                       Model model) {

        Page<Coupon> page = couponService.findPaginated(pageNo, offset, sortField, sortDir, searchTerm);
        List<Coupon> listCoupon = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("listCoupon", listCoupon);

        return "coupon/adminpanel-coupon";
    }

    @GetMapping("/create")
    public String createCoupon(Model model){
        List<Product> productList = productService.findAll();
        List<Category> categoryList = categoryService.findAll();


        model.addAttribute("categoryList", categoryList);
        model.addAttribute("productList", productList);
        model.addAttribute("coupon", new Coupon());
        return "coupon/add-coupon";
    }
    
    @PostMapping("/create")
    public String saveCoupon(@ModelAttribute Coupon coupon,
                             BindingResult result, Model model) {

        if (couponService.findByCode(coupon.getCode()).isPresent() && !couponService.findByCode(coupon.getCode()).get().isDeleted()) {
            result.rejectValue("code", "error.coupon", "Coupon code must be unique");
            model.addAttribute("categoryList", productService.findAll());
            model.addAttribute("productList", categoryService.findAll());
            model.addAttribute("coupon", new Coupon());
            model.addAttribute("nameError","coupon code exists.");
            return "coupon/add-coupon";
        }else{
            if (coupon.getType() == CouponType.PRODUCT) {
                coupon.setCategory(null);
            }else if (coupon.getType() == CouponType.CATEGORY){
                coupon.setProduct(null);
            }else {
                coupon.setProduct(null);
                coupon.setCategory(null);
            }
            couponService.save(coupon);
            return "redirect:/adminpanel/coupon";
        }
    
    
    }
    
    @GetMapping("/edit/{id}")
    public String editCoupon(@PathVariable String id, Model model){

     

        UUID uuid = UUID.fromString(id);
        Coupon coupon = couponService.findById(uuid).orElse(null);
        model.addAttribute("coupon", coupon);
        return "coupon/update-coupon";
    }

//    @GetMapping("/delete/{id}")
//    public String deleteCoupon(@PathVariable String id, Model model){
//
//        UUID uuid = UUID.fromString(id);
//        Coupon coupon = couponService.findById(uuid).orElse(null);
//        coupon.setDeleted(true);
//        couponService.save(coupon);
//        return "redirect:/adminpanel/coupon";
//    }

    
    //soft delete
    @GetMapping("/active/{id}")
    public String enableOrDisableUser(@PathVariable("id") UUID id) {
    	Coupon coupon = couponService.findById(id).orElse(null);
       
        	System.err.println("coupon: "+coupon);
            
        	 System.out.println(coupon.isDeleted());
          //if isDeleted == true will change it to false if false will change it true
//            Boolean status = !coupon.isDeleted();
//           
//            coupon.setDeleted(status);
//            System.err.println(status);
//            
            if (coupon.isDeleted()==false) {
            	coupon.setDeleted(true);
            	coupon.setDeletedAt(new Date());
            	coupon.setDeletedBy(usernameProvider.get());
			}else {
				coupon.setDeleted(false);
				coupon.setCreatedAt(new Date());
			}
            couponService.save(coupon);
     
        return "redirect:/adminpanel/coupon";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") UUID id ){

        Coupon coupon = couponService.findById(id).orElse(null);
       
        couponService.remove(coupon);
        
        System.out.println("coupon deleted");
        return "redirect:/adminpanel/coupon";
    }
    
    @PostMapping("/update")
    public String updateCoupon(@ModelAttribute("coupon") Coupon coupon){

        //TODO: NEW TEMPLATE CHECK THE UPDATE SPECIFICALLY THE DATE
        System.out.println("coupon id: "+coupon.getUuid());
        Coupon existingCoupon = couponService.findById(coupon.getUuid()).orElse(null);


        existingCoupon.setCouponStock(coupon.getCouponStock());
        existingCoupon.setExpirationPeriod(coupon.getExpirationPeriod());
        existingCoupon.setDiscount(coupon.getDiscount());
        existingCoupon.setMaximumDiscountAmount(coupon.getMaximumDiscountAmount());

        couponService.save(existingCoupon);

        System.out.println(coupon);

//        couponService.save(coupon);

        return "redirect:/adminpanel/coupon";
    }
}


