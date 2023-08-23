package com.example.basketo.shopadmin.admin.controller;

import com.example.basketo.shopadmin.order.enums.OrderStatus;
import com.example.basketo.shopadmin.order.enums.OrderType;
import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.order.service.OrderHistoryService;
import com.example.basketo.shopadmin.order.service.OrderService;
import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.Product;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminController {
 
	
	private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;
    private final ReportGenerator reportGenerator;
	
	@GetMapping("/adminpanel")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String adminpanel(Model model, HttpServletRequest request,
    @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
    @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
    @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws IOException {
		String period;
		
		System.out.println("satrtm date: "+startDate);
		System.out.println("end date: "+endDate);

        switch (filter) {
            case "week" -> {
                period = "week";
                // Get the starting date of the current week
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                startDate = calendar.getTime();
                // Get today's date
                endDate = new Date();
            }
            case "month" -> {
                period = "month";
                // Get the starting date of the current month
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTime();
                // Get today's date
                endDate = new Date();
            }
            case "day" -> {
                period = "day";
                // Get today's date
                LocalDate today = LocalDate.now();
                // Set the start date to 12:00:00 AM
                LocalDateTime startDateTime = today.atStartOfDay();
                // Set the end date to 11:59:59 PM
                LocalDateTime endDateTime = today.atTime(23, 59, 59);

                // Convert to Date objects
                ZoneId zone = ZoneId.systemDefault();
                startDate = Date.from(startDateTime.atZone(zone).toInstant());
                endDate = Date.from(endDateTime.atZone(zone).toInstant());
            }

            case "year" -> {
                period = "year";
                // Get the starting date of the current year
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                startDate = calendar.getTime();
                // Get today's date
                endDate = new Date();
            }
            default -> {
                // Default case: filter
            	  period = "year";
                  // Get the starting date of the current year
                  Calendar calendar = Calendar.getInstance();
                  calendar.set(Calendar.DAY_OF_YEAR, 1);
                  startDate = calendar.getTime();
                  // Get today's date
                  endDate = new Date();
            }
        }
        
    	System.out.println("satrtm date: "+startDate);
		System.out.println("end date: "+endDate);
 List<OrderItem> orders = orderService.findOrdersByDate(startDate, endDate);
 
 System.out.println("first order:"+orders.toString());

 System.out.println("number of orders : "+orders.size());
        model.addAttribute("numberOfOrders", orders.size());
        
        double revenue = orders.stream()
                .mapToDouble(order -> order.getProduct().getPrice() * order.getQuantity())  // Calculate revenue for each order
                .sum(); 
        
      System.out.println("revenue: "+revenue);
        
        model.addAttribute("revenue", revenue);
       

        Set<String> uniqueProductNames = new HashSet<>();
        orders.forEach(item -> {
            Product product = item.getProduct();
            uniqueProductNames.add(product.getName());
        });

        System.out.println("unique product count: " + uniqueProductNames.size());
        model.addAttribute("productCount", uniqueProductNames.size());



        Map<String,Integer> catCount = new HashMap<>();
        orders.forEach(item ->{
                Category category = item.getProduct().getCategory();
                catCount.put(category.getName(), catCount.getOrDefault(category.getName(), 0) + 1);
            });
        
        List<String> categoryNames = new ArrayList<>();
        List<Integer> categoryValues = new ArrayList<>();
        
        System.out.println("category count: "+catCount);
        model.addAttribute("categoryCount", catCount);
        
        catCount.forEach((categoryName, categoryValue) -> {
            categoryNames.add(categoryName);
            categoryValues.add(categoryValue);
        });

        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("categoryValues", categoryValues);
        
        Map<Date, Float> revenueMap = new HashMap<>();

        orders.forEach(order -> {
            Date date = order.getCreatedAt();
            Calendar calendar = Calendar.getInstance();

            // Set the Date object to be modified
            calendar.setTime(date);

            // Set the desired time component
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MILLISECOND, 00);

            // Get the modified Date object
            Date modifiedDate = calendar.getTime();
            order.setCreatedAt(new Timestamp(modifiedDate.getTime()));

            revenueMap.put(order.getCreatedAt(), (float) (revenueMap.getOrDefault(order.getCreatedAt(), 0F)+order.getAmount()));
        });
        
        System.out.println("revenueMap: "+revenueMap.toString());
        model.addAttribute("revenueMap",revenueMap);

     
        Map<OrderStatus, Long> orderStatusCounts = orders.stream()
                .collect(Collectors.groupingBy(OrderItem::getStatus, Collectors.counting()));

        System.out.println("order status: "+orderStatusCounts.toString());
        model.addAttribute("orderStatusCounts", orderStatusCounts);
        Map<OrderType, Long> orderTypeCounts = orders.stream()
                .collect(Collectors.groupingBy(
                    order -> order.getOrderHistory().getOrderType(), // Accessing order type from OrderHistory
                    Collectors.counting()
                ));
        
        System.out.println("order type: "+orderTypeCounts.toString());
        model.addAttribute("orderTypeCounts", orderTypeCounts);
        
        
        Long couponsUsed = orders.stream()
                .filter(order -> order.getOrderHistory().getCoupon() != null)
                .count();

        System.out.println("coupons used: "+couponsUsed);
        model.addAttribute("couponsUsed", couponsUsed);
        
//        int totalItemCount = orders.size();
//
//        model.addAttribute("totalItemCount", totalItemCount);

        List<OrderHistory> orderList = orderHistoryService.findHistories(); ;


        //Recent 5 transactions
        model.addAttribute("lastFiveOrders", orderList
                .stream()
                .sorted(Comparator.comparing(OrderHistory::getCreatedAt)
                        .reversed())
                .limit(5)
                .collect(Collectors.toList()));
        
        System.out.println("last five orders: "+orderList);

        model.addAttribute("range", "From " + DateFormatter.format(startDate) + " to " + DateFormatter.format(endDate));
       
        System.out.println("period: "+period);
        model.addAttribute("period", period);
        
		return "adminpanel";
	}
	
	
	class DateFormatter {
	    public static String format(Date date) {
	        //format date to readable format
	        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	        return dateTime.format(formatter);
	    }
	}
 
	@PostMapping("/generateReport")
	  //  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	    @ResponseBody
	    public ResponseEntity<ByteArrayResource> salesReportGenerator(@RequestBody Map<String, Object> requestData ) throws ParseException, IOException, DocumentException {
	        String report = (String) requestData.get("report");
	        String type = (String) requestData.get("type");

	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        Date fromDate = dateFormat.parse((String) requestData.get("from"));
	        Date toDate = dateFormat.parse((String) requestData.get("to"));

	        String generatedFile="";
	        switch (report){
	            case "orders" ->{
	                List<OrderHistory> orders = orderHistoryService.findOrdersByDate(fromDate, toDate);
	                if(type.equals("csv")){
	                    generatedFile = reportGenerator.generateOrderHistoryCsv(orders);
	                }else {
	                    generatedFile = reportGenerator.generateOrderHistoryPdf(orders, (String) requestData.get("from"), (String) requestData.get("to"));
	                }
	            }
	        }
	        File requestedFile = new File(generatedFile);
	        ByteArrayResource resource = new ByteArrayResource(FileUtils.readFileToByteArray(requestedFile));
	        HttpHeaders headers = new HttpHeaders();

	        if(type.equals("csv")){
	            headers.setContentType(MediaType.parseMediaType("text/csv"));
	        }else{
	            headers.setContentType(MediaType.APPLICATION_PDF);
	        }
	        headers.setContentDispositionFormData("attachment", generatedFile);
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(resource);

	    }

	 


	
	
	

}
