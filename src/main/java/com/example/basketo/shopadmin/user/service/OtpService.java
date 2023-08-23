package com.example.basketo.shopadmin.user.service;

import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;



@Service
public class OtpService {

	@Autowired
    private JavaMailSender mailSender;
	@Autowired
    private UserInfoRepository customerRepository;

	private static final Logger logger = LoggerFactory.getLogger(OtpService.class);


    public void sendOtp(String email) {
    	
    	
        Optional<UserInfo> customer = customerRepository.findByEmail(email);
       UserInfo customerUser = customer.get();
        
        System.err.println("inside otp service ");
        
        if (customerUser != null) {
            String otp = generateOtp();
            // Send the OTP to the user's email address
            sendEmail("musthaqeem97@gmail.com", otp);

          
            System.err.println(" email send ");
            // Update the verified status of the user
           
            customerUser.setOtp(otp);
            customerUser.setOtpRequestedTime(LocalDateTime.now());
            customerRepository.save(customerUser);
        }
    }

    private void sendEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP for Account Verification");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);
    }
    
    public void resendOtp(String email) {
       Optional<UserInfo> customer_Optional = customerRepository.findByEmail(email);
      UserInfo customer = customer_Optional.get();
        if (customer != null && !customer.isVerified()) {
            String otp = generateOtp();
            // Send the new OTP to the user's email address
            sendEmail(customer.getEmail(), otp);
            customer.setOtp(otp);
            customer.setOtpRequestedTime(LocalDateTime.now());
       
          
        }
    }

    public boolean verifyOtp(String email, String otp) {
    	 Optional<UserInfo> customer_Optional = customerRepository.findByEmail(email);
         UserInfo customer = customer_Optional.get();
        if (customer != null &otp.equals(customer.getOtp()) ) {
            // Verify the OTP entered by the user
            customer.setVerified(true);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otpNumber = 100_000 + random.nextInt(900_000);
        return String.valueOf(otpNumber);
    }

    
    public void clearOTP(UserInfo customer) {
        customer.setOtp(null);
        customer.setOtpRequestedTime(null);
        customerRepository.save(customer);   
    }
}

