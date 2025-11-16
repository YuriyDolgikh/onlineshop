package org.onlineshop.service.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class PdfOrderGenerator {
    public static byte[] generatePdfOrder(Order order) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Order Confirmation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            Font infoFont = new Font(Font.HELVETICA, 12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            document.add(new Paragraph("Order ID: " + order.getOrderId(), infoFont));
            document.add(new Paragraph("Date: " + order.getCreatedAt().format(formatter), infoFont));
            document.add(new Paragraph("Customer: " + order.getUser().getUsername(), infoFont));
            document.add(new Paragraph("Status: " + order.getStatus(), infoFont));
            document.add(new Paragraph("Delivery method: " + order.getDeliveryMethod(), infoFont));
            document.add(new Paragraph("Delivery address: " +
                    (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "Not specified"), infoFont));
            document.add(new Paragraph("Contact phone: " +
                    (order.getContactPhone() != null ? order.getContactPhone() : "Not specified"), infoFont));
            document.add(new Paragraph("Email: " + order.getUser().getEmail(), infoFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addTableHeader(table, "Product", "Quantity", "Price for 1 item", "Discount", "Final price", "Total");

            DecimalFormat df = new DecimalFormat("#0.00");

            BigDecimal totalSumm = BigDecimal.ZERO;
            for (OrderItem item : order.getOrderItems()) {
                String product = item.getProduct().getName();
                Integer quantity = item.getQuantity();
                BigDecimal price = item.getProduct().getPrice();
                BigDecimal discount = item.getProduct().getDiscountPrice();

                BigDecimal finalPrice;
                if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                    finalPrice = price.subtract(price.multiply(discount).divide(BigDecimal.valueOf(100)));
                } else {
                    finalPrice = price;
                }

                BigDecimal total = finalPrice.multiply(BigDecimal.valueOf(quantity));
                totalSumm = totalSumm.add(total);

                addTableRow(table,
                        product,
                        String.valueOf(quantity),
                        df.format(price),
                        (discount != null ? df.format(discount) : "0.00") + "%",
                        df.format(finalPrice),
                        df.format(total)
                );
            }
            document.add(table);
            document.add(Chunk.NEWLINE);

            Font boldFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Paragraph totalParagraph = new Paragraph("Total amount: " + df.format(totalSumm), boldFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF order : " + e.getMessage(), e);
        }
    }

    private static void addTableHeader(PdfPTable table, String... headers) {
        Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addTableRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
}