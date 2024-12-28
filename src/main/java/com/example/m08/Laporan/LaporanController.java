package com.example.m08.Laporan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.example.m08.export.ExportPdf;

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


     @GetMapping("/laporan/download")
    public ResponseEntity<InputStreamResource> downloadReport(
            @RequestParam(defaultValue = "1") int month,
            @RequestParam(defaultValue = "2024") int year) throws IOException {
        
        List<Laporan> reports = laporanRepository.findByMonthAndYear(month, year);
        
        ByteArrayInputStream bis = ExportPdf.laporanReport(reports, month, year);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=laporan_" + month + "_" + year + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private double calculateTotalRevenue(List<Laporan> reports) {
        return reports.stream()
                .mapToDouble(report -> report.getHargaSewa() + report.getDenda())
                .sum();
    }
}