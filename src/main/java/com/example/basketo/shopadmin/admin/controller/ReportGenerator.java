package com.example.basketo.shopadmin.admin.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.service.OrderHistoryService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ReportGenerator {
    @Autowired
    OrderHistoryService orderHistoryService;




    public String generateOrderHistoryPdf(List<OrderHistory> orderHistories, String from, String to) throws DocumentException {
        String fileName = UUID.randomUUID().toString();

        // Define the directory to save the PDF file
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads/reports/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate the file path
        String filePath = uploadDir + fileName + ".pdf";

        Document document = new Document(PageSize.A1);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Create the title
            Paragraph title = new Paragraph("Order History Report");
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph period = new Paragraph("From "+from+" to "+to);
            period.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(period);

            document.add(new Paragraph("\n"));

            // Create the table
            PdfPTable table = new PdfPTable(5); // Adjust the number of columns as per your requirement

            // Add table headers
            table.addCell("UUID");
            table.addCell("Date");
            table.addCell("Total");
            table.addCell("Tax");
            table.addCell("Type");

            // Add table rows
            for (OrderHistory orderHistory : orderHistories) {
                table.addCell(orderHistory.getUuid().toString());
                table.addCell(orderHistory.getCreatedAt().toString());
                table.addCell(String.valueOf(orderHistory.getTotal()));
                table.addCell(String.valueOf(orderHistory.getTax()));
                table.addCell(String.valueOf(orderHistory.getOrderType()));
            }

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return filePath;
    }



    public String generateOrderHistoryCsv(List<OrderHistory> orderHistoryList) {
        // Generate a unique file name for the CSV file
        String fileName = UUID.randomUUID().toString();

        // Define the directory to save the CSV file
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads/reports/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate the full file path
        String filePath = uploadDir + fileName + ".csv";

        try (PrintWriter writer = new PrintWriter(filePath)) {
            // Write the CSV header
            writer.println("Order ID,Date,Total,Tax,Order Status,Order Type");

            // Iterate over the list of OrderHistory objects and write each record to the CSV file
            for (OrderHistory order : orderHistoryList) {
                writer.printf("%s,%s,%.2f,%.2f,%s,%s%n",
                        order.getUuid().toString(),
                        order.getCreatedAt().toString(),
                        order.getTotal(),
                        order.getTax(),
                        order.getOrderStatus().toString(),
                        order.getOrderType().toString());
            }

            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filePath;
    }
    
}
