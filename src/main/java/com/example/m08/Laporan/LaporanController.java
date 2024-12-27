package com.example.m08.Laporan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.example.m08.User.Pelanggan;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LaporanController {

    @Autowired
    private LaporanRepository laporanRepository;

    @GetMapping("/kelolaLaporan")
    public String viewLaporan(Model model, HttpSession session) {
        
        List<Laporan> reports = laporanRepository.findAll();
        double totalRevenue = calculateTotalRevenue(reports);
        
        model.addAttribute("reports", reports);
        model.addAttribute("totalRevenue", totalRevenue);
        
        return "admin/kelolaLaporan";
    }
    
    @GetMapping("/laporan/search")
public String searchReports(@RequestParam int month, @RequestParam int year, Model model) {
    List<Laporan> reports = laporanRepository.findByMonthAndYear(month, year);
    double totalRevenue = calculateTotalRevenue(reports);
    
    model.addAttribute("reports", reports);
    model.addAttribute("totalRevenue", totalRevenue);
    model.addAttribute("selectedMonth", month);
    model.addAttribute("selectedYear", year);
    
    return "admin/kelolaLaporan";
}

    private double calculateTotalRevenue(List<Laporan> reports) {
        return reports.stream()
                .mapToDouble(report -> report.getHargaSewa() + report.getDenda())
                .sum();
    }

    @GetMapping("/laporan/download")
public void downloadReport(@RequestParam int month, @RequestParam int year, HttpServletResponse response) throws IOException {
    List<Laporan> reports = laporanRepository.findByMonthAndYear(month, year);
    
    // Mengatur header respons untuk unduhan file
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=\"laporan_" + month + "_" + year + ".csv\"");
    
    // Menulis data laporan ke respons dalam format CSV
    try (PrintWriter writer = response.getWriter()) {
        writer.println("ID Laporan,ID Sewa,Judul Film,Username,Tanggal Rental,Tanggal Kembali,Harga Sewa,Denda,Status");
        for (Laporan report : reports) {
            writer.println(String.format("%d,%d,\"%s\",\"%s\",%s,%s,%.2f,%.2f,%s",
                report.getIdLaporan(),
                report.getIdSewa(),
                escapeString(report.getMovieTitle()),
                escapeString(report.getUsername()),
                report.getRentDate().toString(),
                report.getDueDate().toString(),
                report.getHargaSewa(),
                report.getDenda(),
                report.getStatus()
            ));
        }
    }
}

private String escapeString(String value) {
    return value.replaceAll("\"", "\"\"");
}
}