package com.example.service;

import com.example.model.Charge;
import com.example.model.Customer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PdfGeneratorService {
    public void generate(ArrayList<Charge> charges, Customer customer) throws DocumentException, FileNotFoundException {
        Document document = createDocument(customer);
        generateInvoice(charges, customer, document);
        document.close();
    }

    private Document createDocument(Customer customer) throws FileNotFoundException, DocumentException {
        String directoryPath = "../ProjektCharging/PDF_Files/";
        String filePath = directoryPath + "Invoice_" + customer.getCustomerId() + ".pdf";

        java.io.File directory = new java.io.File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        return document;
    }

    private void generateInvoice(ArrayList<Charge> charges, Customer customer, Document document) throws DocumentException {
        Font header = FontFactory.getFont(FontFactory.COURIER, 20, com.itextpdf.text.BaseColor.BLACK);
        Font subHeader = FontFactory.getFont(FontFactory.COURIER, 16, com.itextpdf.text.BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 12, com.itextpdf.text.BaseColor.BLACK);

        document.setPageSize(com.itextpdf.text.PageSize.A4);
        document.open();

        // Add header
        Paragraph headerParagraph = new Paragraph("Invoice", header);
        headerParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(headerParagraph);
        document.add(com.itextpdf.text.Chunk.NEWLINE);

        // Add sub-header
        Paragraph subHeaderParagraph = new Paragraph("For: " + customer.getFirstName() + " " + customer.getLastName(), subHeader);
        subHeaderParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(subHeaderParagraph);
        document.add(com.itextpdf.text.Chunk.NEWLINE);

        // Create table
        PdfPTable table = new PdfPTable(3); // 3 columns
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table

        // Set Column widths
        float[] columnWidths = {1f, 2f, 2f};
        table.setWidths(columnWidths);

        // Add table header
        PdfPCell cell1 = new PdfPCell(new Paragraph("No", font));
        PdfPCell cell2 = new PdfPCell(new Paragraph("kwh", font));
        PdfPCell cell3 = new PdfPCell(new Paragraph("Price", font));
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);

        // Add rows
        int i = 1;
        double costPerCharge;
        double totalCost = 0.0;
        double price_per_kwh = 0.30;
        for (Charge charge : charges) {
            if (charge.getCustomerID() == customer.getCustomerId()) {
                table.addCell(new PdfPCell(new Paragraph(String.valueOf(i), font)));
                table.addCell(new PdfPCell(new Paragraph(String.valueOf(charge.getKwh()), font)));
                costPerCharge = charge.getKwh() * price_per_kwh;
                table.addCell(new PdfPCell(new Paragraph(String.format("%.2f", costPerCharge) + " EUR", font)));
                totalCost += charge.getKwh() * price_per_kwh;
                i++;
            }
        }

        // Add table to document
        document.add(table);

        // Add total cost
        Paragraph totalCostParagraph = new Paragraph("Total Cost: " + String.format("%.2f", totalCost) + " EUR", subHeader);
        totalCostParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalCostParagraph);
    }
}
