package com.example.basketo.shopclient.controller;

import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TemplateInvoiceGenerator {
    public void generateInvoice(OrderHistory orderHistory) {

        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads/invoices/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String outputFilePath = uploadDir + orderHistory.getUuid().toString() + ".pdf";

        // Delete the file if it already exists
        File file = new File(outputFilePath);
        if (file.exists()) {
            file.delete();
        }


        try (InputStream inputStream = new ClassPathResource("templates/invoice_template.html").getInputStream();
             OutputStream outputStream = new FileOutputStream(outputFilePath)) {

            // Load the HTML template
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Replace placeholders with actual data
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("{{orderId}}", orderHistory.getUuid().toString());
            dataMap.put("{{orderDate}}", DateFormatter.format(orderHistory.getCreatedAt()));
          
            dataMap.put("{{area}}", orderHistory.getAddress().getAddressLine1());
            dataMap.put("{{town}}", orderHistory.getAddress().getAddressLine2());
            dataMap.put("{{city}}", orderHistory.getAddress().getCity());
            dataMap.put("{{state}}", orderHistory.getAddress().getState());
            dataMap.put("{{pin}}", orderHistory.getAddress().getPincode());
            dataMap.put("{{phone}}", orderHistory.getUserInfo().getPhone());
            dataMap.put("{{email}}", orderHistory.getUserInfo().getEmail());
            dataMap.put("{{name}}", orderHistory.getUserInfo().getFirstName()+" "+orderHistory.getUserInfo().getLastName());
            dataMap.put("{{total}}",String.valueOf(orderHistory.getTotal()));
            
            dataMap.put("{{generatedDate}}", new Date().toString());


           
            dataMap.put("{{tax}}", orderHistory.getTax().toString());
           
            dataMap.put("{{type}}", orderHistory.getOrderType().name());
          

            StringBuilder orderItemsRows = new StringBuilder();

            for (OrderItem orderItem : orderHistory.getOrderItems()) {
                Map<String, String> orderItemData = new HashMap<>();
                orderItemData.put("{{description}}", orderItem.getProduct().getName());
                orderItemData.put("{{quantity}}", String.valueOf(orderItem.getQuantity()));
                orderItemData.put("{{unitPrice}}", String.valueOf(orderItem.getProduct().getPrice()));
              
                double subtotal = orderItem.getQuantity() * orderItem.getAmount();
                orderItemData.put("{{subtotal}}", String.valueOf(subtotal));
                // Replace placeholders with order item data
                String orderItemRow = "<tr>" +
                        "<td valign='top' style='text-align: left;'>" + orderItemData.get("{{description}}") + "</td>" +
                        "<td valign='top' style='text-align: center; margin-left: auto; margin-right: auto;'>" + orderItemData.get("{{quantity}}") + "</td>" +
                        "<td valign='top' style='text-align: center; margin-left: auto; margin-right: auto;'>" + orderItemData.get("{{unitPrice}}") + "</td>" +
                        "<td valign='top' style='text-align: center; margin-left: auto; margin-right: auto;'>" + orderItemData.get("{{subtotal}}") + "</td>" +
                        "</tr>";

                orderItemsRows.append(orderItemRow);
            }

            // Replace the placeholder in the HTML template with the generated order items rows
            htmlTemplate = htmlTemplate.replace("{{orderItemsRows}}", orderItemsRows.toString());

            // Replace the remaining placeholders in the HTML template with the actual data values
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                htmlTemplate = htmlTemplate.replace(entry.getKey(), entry.getValue());
            }

            // Generate the PDF from the modified HTML template
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            ConverterProperties converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlTemplate, pdfDocument, converterProperties);

            System.out.println("Invoice generated successfully!");
        } catch (IOException e) {
            System.err.println("Error occurred while generating the invoice: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0 21 * * *")
    public void deleteInvoices() throws IOException {

        System.out.println("     __    __    __  _                                   __    \n" +
                " ___/ /__ / /__ / /_(_)__  ___ _  _______ ___  ___  ____/ /____\n" +
                "/ _  / -_) / -_) __/ / _ \\/ _ `/ / __/ -_) _ \\/ _ \\/ __/ __(_-<\n" +
                "\\_,_/\\__/_/\\__/\\__/_/_//_/\\_, / /_/  \\__/ .__/\\___/_/  \\__/___/\n" +
                "                         /___/         /_/                     \n" +
                "\n");

        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads/reports/";
        File directory = new File(uploadDir);
        if (directory.exists() && directory.isDirectory()) {
            // Get the list of files in the directory
            File[] files = directory.listFiles();
            // Iterate over the files
            for (File file : files) {
                if (file.isFile()) {
                    // Get the file name
                    String fileName = file.getName();
                    handleDelete(fileName);
                    }
                }
            }
        }

    private void handleDelete(String fileName) throws IOException {
        // Define the directory
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads/reports/";

        // Get the file path
        String filePath = uploadDir + "/" + fileName;

        // Create a file object for the file to be deleted
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Delete the file
            file.delete();
            System.out.println(fileName + " deleted");
        } else {
            System.out.println("File not found!");
        }
    }
    class DateFormatter {
	    public static String format(Date date) {
	        //format date to readable format
	        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	        return dateTime.format(formatter);
	    }
	}
 

}
