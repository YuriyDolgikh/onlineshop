package org.onlineshop.service.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class PdfOrderGenerator {
    public static byte[] generatePdfOrder(Order order) {

        try {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Order Confirmation", titleFont);
        document.add(title);
        document.add(Chunk.NEWLINE);

        Font intoFont = new Font(Font.HELVETICA, 12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        document.add(new Paragraph("Order ID: " + order.getOrderId(), intoFont));
        document.add(new Paragraph("Date: " + order.getCreatedAt().format(formatter), intoFont));
        document.add(new Paragraph("Customer: " + order.getUser().getUsername(), intoFont));
        document.add(new Paragraph("Status: " + order.getStatus(), intoFont));
        document.add(new Paragraph("Delivery method: " + order.getDeliveryMethod(), intoFont));
        document.add(new Paragraph("Delivery address: " + order.getDeliveryAddress(), intoFont));
        document.add(new Paragraph("Contact phone:" + order.getContactPhone(), intoFont));
        document.add(new Paragraph("Email:" + order.getUser().getEmail(), intoFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addTableHelper(table, "Product", "Quantity", "Price for 1 item", "Discount", "Final price", "Total");
        BigDecimal totalSumm = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()) {
            String product = item.getProduct().getName();
            Integer quantity = item.getQuantity();
            BigDecimal price = item.getProduct().getPrice();
            BigDecimal discount = item.getPriceAtPurchase();
            BigDecimal finalPrice = item.getPriceAtPurchase() != null ? item.getPriceAtPurchase() : item.getProduct().getPrice();
            BigDecimal total = finalPrice.multiply(BigDecimal.valueOf(quantity));
            totalSumm = totalSumm.add(total);

            addTableRow(table,
                    product,
                    String.valueOf(quantity),
                    price.toString(),
                    discount.toString(),
                    finalPrice.toString(),
                    total.toString()
            );
        }
        document.add(table);

        Font boldFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        Paragraph totalParagraph = new Paragraph("Pended payment  : " + totalSumm + " €", boldFont);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalParagraph);

        document.close();
        return out.toByteArray();
    }catch(Exception e){
            throw  new RuntimeException("Error generating PDF order : " + e.getMessage(),e);
        }
    }

    private static void addTableHelper(PdfPTable table, String... headers) {
        Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        for (String h : headers) {
            PdfPCell header = new PdfPCell(new Phrase(h, headFont));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(header);
        }
    }

    private static void addTableRow(PdfPTable table, String... values) {
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

    }
}
