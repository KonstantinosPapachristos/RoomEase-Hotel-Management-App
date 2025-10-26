package com.example.roomease;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PDFGenerator {

    public static void generateReceipt(Booking booking) throws Exception {
        // Determine the user's home directory
        String userHome = System.getProperty("user.home");

        // Check if Desktop folder exists
        String desktopPath = userHome + "/Desktop";
        if (!Files.exists(Paths.get(desktopPath))) {
            throw new Exception("Desktop directory not found on this system.");
        }

        // Set file name for receipt
        String fileName = desktopPath + "/Receipt_" + booking.getBookingID() + ".pdf";
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Booking Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add some space
            document.add(new Paragraph("\n"));

            // Add booking details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Add table header
            addTableHeader(table, "Field", "Details");

            // Add booking details as rows
            addRow(table, "Booking ID", booking.getBookingID());
            addRow(table, "Customer Name", booking.getCustomerName());
            addRow(table, "Room Type", booking.getRoomType());
            addRow(table, "Booking Type", booking.getBookingType());
            addRow(table, "Check-In Date", booking.getCheckInDate().toString());
            addRow(table, "Check-Out Date", booking.getCheckOutDate().toString());
            addRow(table, "Group Booking", booking.isGroup() ? "Yes" : "No");
            addRow(table, "Total Price", "$" + booking.getTotalAmount());
            addRow(table, "Amount Paid", "$" + booking.getAmountPaid());

            document.add(table);

            // Add footer
            document.add(new Paragraph("\nThank you for choosing RoomEase!"));

            System.out.println("Receipt generated successfully: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to generate receipt.");
        } finally {
            document.close();
        }
    }

    private static void addTableHeader(PdfPTable table, String col1, String col2) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell header1 = new PdfPCell(new Phrase(col1, headerFont));
        header1.setBackgroundColor(BaseColor.BLACK);
        header1.setHorizontalAlignment(Element.ALIGN_CENTER);
        header1.setPadding(8);

        PdfPCell header2 = new PdfPCell(new Phrase(col2, headerFont));
        header2.setBackgroundColor(BaseColor.BLACK);
        header2.setHorizontalAlignment(Element.ALIGN_CENTER);
        header2.setPadding(8);

        table.addCell(header1);
        table.addCell(header2);
    }

    private static void addRow(PdfPTable table, String col1, String col2) {
        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        PdfPCell cell1 = new PdfPCell(new Phrase(col1, rowFont));
        cell1.setPadding(8);

        PdfPCell cell2 = new PdfPCell(new Phrase(col2, rowFont));
        cell2.setPadding(8);

        table.addCell(cell1);
        table.addCell(cell2);
    }
}
