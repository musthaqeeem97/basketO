package com.example.basketo.shopadmin.order.controller;

import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/adminpanel/order")
@RequiredArgsConstructor
public class OrderController {

	
	@Value("${table.pageNo}")
    private int pageNo;

    @Value("${order.table.offset}")
    private int offset;

    @Value("${table.sortDir}")
    private String sortDir;

    @Value("${order.table.sortField}")
    private String sortField;

    
    private final OrderService orderService;


    @GetMapping
    public String orderList(Model model) {
        return getOrderPaginated(pageNo, sortField, sortDir, "", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getOrderPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String searchTerm,
                                       Model model) {

        Page<OrderItem> page = orderService.findPaginated(pageNo, offset, sortField, sortDir, searchTerm);
        List<OrderItem> listOrder = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("listOrder", listOrder);

        return "order/adminpanel-order";
    }
    
    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable UUID id, Model model){
      
        OrderItem order = orderService.findById(id);
        model.addAttribute("order", order);
        return "order/update-order";
    }
    
    @PostMapping("/update")
    public String updateOrder(@ModelAttribute("order") OrderItem order){

        //TODO: NEW TEMPLATE CHECK THE UPDATE SPECIFICALLY THE DATE
        System.out.println("order item status: "+order.getStatus());
        System.out.println("order item id: "+order.getUuid() );
        OrderItem existingOrder = orderService.findById(order.getUuid());


      existingOrder.setStatus(order.getStatus());

        orderService.save(existingOrder);

        

//        couponService.save(coupon);

        return "redirect:/adminpanel/order";
    }
    
	
}
