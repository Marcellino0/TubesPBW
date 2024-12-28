package com.example.m08.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.time.format.DateTimeFormatter;

import com.example.m08.Laporan.Laporan;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ExportPdf {
    public static ByteArrayInputStream laporanReport(List<Laporan> reports, int month, int year) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Configure table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 3, 3, 3});

            // Configure fonts
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            
            // Add title
            Paragraph title = new Paragraph("Laporan Rental Film", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            
            // Add period
            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
            Paragraph period = new Paragraph(
                "Periode: " + month + " " + year,
                FontFactory.getFont(FontFactory.HELVETICA, 12)
            );
            period.setSpacingAfter(20);

            // Create table headers
            PdfPCell hcell;
            
            hcell = new PdfPCell(new Phrase("Judul Film", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Tanggal Rental", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Tanggal Kembali", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Harga Sewa", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            // Add data rows
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
            for (Laporan report : reports) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(report.getMovieTitle()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(report.getRentDate().format(dateFormatter)));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(report.getDueDate().format(dateFormatter)));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.format("Rp %.2f", report.getHargaSewa())));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingRight(5);
                table.addCell(cell);
            }

            // Calculate total revenue
            double totalRevenue = reports.stream()
                .mapToDouble(report -> report.getHargaSewa())
                .sum();

            // Add total row
            PdfPCell totalCell = new PdfPCell(new Phrase("Total Pendapatan", headFont));
            totalCell.setColspan(3);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setPadding(5);
            table.addCell(totalCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("Rp %.2f", totalRevenue), headFont));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setPaddingRight(5);
            table.addCell(totalValueCell);

            PdfWriter.getInstance(document, out);
            document.open();
            document.add(title);
            document.add(period);
            document.add(table);
            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}