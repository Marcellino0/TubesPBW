package com.example.m08.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import com.example.m08.Rental.MovieRentalStats;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ExportRentalStatsPdf {
    public static ByteArrayInputStream rentalStatsReport(List<MovieRentalStats> stats) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Configure table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{5, 2, 2, 2});

            // Configure fonts
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

            // Add title
            Paragraph title = new Paragraph("Laporan Statistik Penyewaan Film", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);

            // Add date
            Paragraph date = new Paragraph(
                "Tanggal: " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                FontFactory.getFont(FontFactory.HELVETICA, 12)
            );
            date.setSpacingAfter(20);

            // Create table headers
            PdfPCell hcell;

            hcell = new PdfPCell(new Phrase("Judul Film", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Total Penyewaan", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Target", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Pencapaian (%)", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            // Add data rows
            for (MovieRentalStats stat : stats) {
                PdfPCell cell;

                // Movie Title
                cell = new PdfPCell(new Phrase(stat.getMovieTitle()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPadding(5);
                table.addCell(cell);

                // Rental Count
                cell = new PdfPCell(new Phrase(String.valueOf(stat.getRentalCount())));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                // Target Count
                cell = new PdfPCell(new Phrase(String.valueOf(stat.getTargetCount())));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                // Achievement Percentage
                double achievement = stat.getTargetCount() > 0 
                    ? (double) stat.getRentalCount() / stat.getTargetCount() * 100 
                    : 0;
                cell = new PdfPCell(new Phrase(String.format("%.1f%%", achievement)));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Calculate totals
            int totalRentals = stats.stream().mapToInt(MovieRentalStats::getRentalCount).sum();
            int totalTargets = stats.stream().mapToInt(MovieRentalStats::getTargetCount).sum();
            double totalAchievement = totalTargets > 0 ? (double) totalRentals / totalTargets * 100 : 0;

            // Add total row
            PdfPCell totalCell = new PdfPCell(new Phrase("Total", headFont));
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setPadding(5);
            table.addCell(totalCell);

            PdfPCell totalRentalsCell = new PdfPCell(new Phrase(String.valueOf(totalRentals), headFont));
            totalRentalsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(totalRentalsCell);

            PdfPCell totalTargetsCell = new PdfPCell(new Phrase(String.valueOf(totalTargets), headFont));
            totalTargetsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(totalTargetsCell);

            PdfPCell totalAchievementCell = new PdfPCell(new Phrase(String.format("%.1f%%", totalAchievement), headFont));
            totalAchievementCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(totalAchievementCell);

            PdfWriter.getInstance(document, out);
            document.open();
            document.add(title);
            document.add(date);
            document.add(table);
            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}