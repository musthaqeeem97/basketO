package com.example.basketo.shopadmin.order.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.repository.OrderHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.basketo.shopclient.controller.TemplateInvoiceGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryRepository orderHistoryRepository;
 private final TemplateInvoiceGenerator templateInvoiceGenerator;
	
	public void save(OrderHistory orderHistory) {
		
		orderHistoryRepository.save(orderHistory);
	}
	
	public  Optional<OrderHistory> findById(UUID id) {
		
		return orderHistoryRepository.findById(id);
	}
	
	 public List<OrderHistory> findOrdersByDate(Date startDate, Date endDate){
		 return orderHistoryRepository.findByCreatedAtBetween(startDate, endDate);
	 }
	 
		public  List<OrderHistory> findHistories(){ 
		 return orderHistoryRepository.findAll();
	 }

		public Page<OrderHistory> findPaginated(int pageNo, int offset, String sortField, String sortDir) {
			 Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
	             Sort.by(sortField).descending();


	     Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);

	     System.err.println(" end of coupon service");

	    return orderHistoryRepository.findAll(pageable);
		}

		public void generateInvoice(UUID uuid) {
	        templateInvoiceGenerator.generateInvoice(findById(uuid).get());
	    }
}
