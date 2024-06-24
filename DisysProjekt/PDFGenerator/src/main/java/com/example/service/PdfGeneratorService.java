package com.example.service;

import com.example.model.Charge;
import com.example.model.Customer;
import com.itextpdf.text.BaseColor;
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
        String path = "../DisysProjektAbgabe/PDF_Files/";
        String filePath = path + "Invoice_" + customer.getCustomerId() + ".pdf";

        java.io.File directory = new java.io.File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        return document;
    }

    private void generateInvoice(ArrayList<Charge> charges, Customer customer, Document document) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.DARK_GRAY);
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        BaseColor tableHeaderColor = new BaseColor(0, 121, 182);

        document.setPageSize(com.itextpdf.text.PageSize.A4);
        document.open();

        // Add header
        Paragraph headerParagraph = new Paragraph("Invoice", headerFont);
        headerParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(headerParagraph);
        document.add(com.itextpdf.text.Chunk.NEWLINE);

        // Add sub-header
        Paragraph subHeaderParagraph = new Paragraph("For: " + customer.getFirstName() + " " + customer.getLastName(), subHeaderFont);
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
        PdfPCell cell1 = new PdfPCell(new Paragraph("No", tableHeaderFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph("kWh", tableHeaderFont));
        PdfPCell cell3 = new PdfPCell(new Paragraph("Price", tableHeaderFont));

        cell1.setBackgroundColor(tableHeaderColor);
        cell2.setBackgroundColor(tableHeaderColor);
        cell3.setBackgroundColor(tableHeaderColor);

        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);

        // Add rows
        int i = 1;
        double costPerCharge;
        double totalCost = 0.0;
        double pricePerKwh = 0.30;
        for (Charge charge : charges) {
            if (charge.getCustomerID() == customer.getCustomerId()) {
                PdfPCell noCell = new PdfPCell(new Paragraph(String.valueOf(i), font));
                PdfPCell kwhCell = new PdfPCell(new Paragraph(String.valueOf(charge.getKwh()), font));
                costPerCharge = charge.getKwh() * pricePerKwh;
                PdfPCell priceCell = new PdfPCell(new Paragraph(String.format("%.2f", costPerCharge) + " EUR", font));

                noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                kwhCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(noCell);
                table.addCell(kwhCell);
                table.addCell(priceCell);
                totalCost += charge.getKwh() * pricePerKwh;
                i++;
            }
        }

        // Add total cost row
        PdfPCell totalLabelCell = new PdfPCell(new Paragraph("Total Cost", tableHeaderFont));
        totalLabelCell.setColspan(2);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setBackgroundColor(tableHeaderColor);

        PdfPCell totalValueCell = new PdfPCell(new Paragraph(String.format("%.2f", totalCost) + " EUR", font));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(totalLabelCell);
        table.addCell(totalValueCell);

        // Add table to document
        document.add(table);

        document.close();
    }
}
