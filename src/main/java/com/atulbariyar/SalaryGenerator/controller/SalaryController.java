package com.atulbariyar.SalaryGenerator.controller;

import com.atulbariyar.SalaryGenerator.model.EmployeeSalary;
import com.atulbariyar.SalaryGenerator.service.EmailService;
import com.atulbariyar.SalaryGenerator.service.GoogleSheetService;
import com.atulbariyar.SalaryGenerator.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private GoogleSheetService sheetService;

    @Autowired
    private PdfGeneratorService pdfService;

    @Autowired
    private EmailService emailService;


    // ✅ 1. Send salary slips for all employees for a given month
    @PostMapping("/send-all")
    public ResponseEntity<String> sendAllSalarySlips(@RequestParam String month) {
        try {
            String range = month + "!A2:H"; // skip header
            List<List<Object>> sheetData = sheetService.readData(range);
            if (sheetData == null || sheetData.isEmpty())
                return ResponseEntity.badRequest().body("No employee data found for " + month);

            List<EmployeeSalary> employees = mapSheetDataToEmployees(sheetData);

            for (EmployeeSalary emp : employees) {
                File pdf = pdfService.generateSalarySlip(emp, month);
                emailService.sendEmailWithAttachment(
                        emp.getEmail(),
                        "Your Salary Slip for " + month,
                        "Dear " + emp.getName() + ",\n\nPlease find attached your salary slip for " + month + ".\n\nRegards,\nHR Team",
                        pdf
                );
            }

            return ResponseEntity.ok("✅ Salary slips emailed successfully for " + month + "!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error sending salary slips: " + e.getMessage());
        }
    }


    // ✅ 2. Send salary slip for a particular employee for a particular month
    @PostMapping("/send/{empId}")
    public ResponseEntity<String> sendEmployeeSalarySlip(
            @PathVariable int empId,
            @RequestParam String month) {
        try {
            String range = month + "!A2:H";
            List<List<Object>> sheetData = sheetService.readData(range);
            if (sheetData == null || sheetData.isEmpty())
                return ResponseEntity.badRequest().body("No data found for " + month);

            List<EmployeeSalary> employees = mapSheetDataToEmployees(sheetData);
            EmployeeSalary target = employees.stream()
                    .filter(e -> e.getEmpId() == empId)
                    .findFirst()
                    .orElse(null);

            if (target == null)
                return ResponseEntity.badRequest().body("Employee ID " + empId + " not found in " + month);

            File pdf = pdfService.generateSalarySlip(target, month);
            emailService.sendEmailWithAttachment(
                    target.getEmail(),
                    "Your Salary Slip for " + month,
                    "Dear " + target.getName() + ",\n\nPlease find attached your salary slip for " + month + ".\n\nRegards,\nHR Team",
                    pdf
            );

            return ResponseEntity.ok("📧 Salary slip sent to " + target.getName() + " for " + month);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error sending salary slip: " + e.getMessage());
        }
    }


    // ✅ 3. Download all salary slips for a given month (locally saved)
//    @GetMapping("/download-all")
//    public ResponseEntity<String> downloadAllSalarySlips(@RequestParam String month) {
//        try {
//            String range = month + "!A2:H";
//            List<List<Object>> sheetData = sheetService.readData(range);
//            if (sheetData == null || sheetData.isEmpty())
//                return ResponseEntity.badRequest().body("No employee data found for " + month);
//
//            List<EmployeeSalary> employees = mapSheetDataToEmployees(sheetData);
//
//            for (EmployeeSalary emp : employees) {
//                pdfService.generateSalarySlip(emp, month);
//            }
//
//            return ResponseEntity.ok("📂 All salary slips for " + month + " generated and saved locally.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("❌ Error generating salary slips: " + e.getMessage());
//        }
//    }

    @GetMapping("/download-all")
    public ResponseEntity<byte[]> downloadAllSalarySlips(@RequestParam String month) {
        try {
            String range = month + "!A2:H";
            List<List<Object>> sheetData = sheetService.readData(range);
            if (sheetData == null || sheetData.isEmpty())
                return ResponseEntity.badRequest().body(null);

            List<EmployeeSalary> employees = mapSheetDataToEmployees(sheetData);

            File tempDir = new File("batch_slips_" + month);
            if (!tempDir.exists()) tempDir.mkdirs();

            for (EmployeeSalary emp : employees) {
                pdfService.generateSalarySlip(emp, month);
            }

            // Create zip
            File zipFile = new File("SalarySlips_" + month + ".zip");
            org.zeroturnaround.zip.ZipUtil.pack(tempDir, zipFile);

            byte[] zipBytes = java.nio.file.Files.readAllBytes(zipFile.toPath());

            return ResponseEntity.ok()
                    .header("Content-Type", "application/zip")
                    .header("Content-Disposition", "attachment; filename=" + zipFile.getName())
                    .body(zipBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    // ✅ 4. Download salary slip for one employee for a given month (locally saved)
//    @GetMapping("/download/{empId}")
//    public ResponseEntity<String> downloadEmployeeSalarySlip(
//            @PathVariable int empId,
//            @RequestParam String month) {
//        try {
//            String range = month + "!A2:H";
//            List<List<Object>> sheetData = sheetService.readData(range);
//            if (sheetData == null || sheetData.isEmpty())
//                return ResponseEntity.badRequest().body("No data found for " + month);
//
//            List<EmployeeSalary> employees = mapSheetDataToEmployees(sheetData);
//            EmployeeSalary target = employees.stream()
//                    .filter(e -> e.getEmpId() == empId)
//                    .findFirst()
//                    .orElse(null);
//
//            if (target == null)
//                return ResponseEntity.badRequest().body("Employee ID " + empId + " not found in " + month);
//
//            pdfService.generateSalarySlip(target, month);
//            return ResponseEntity.ok("📄 Salary slip generated for " + target.getName() + " for " + month + ".");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("❌ Error generating salary slip: " + e.getMessage());
//        }
//    }

    @GetMapping("/download/{empId}")
    public ResponseEntity<byte[]> downloadEmployeeSalarySlip(
            @PathVariable int empId,
            @RequestParam String month) {
        try {
            String range = month + "!A2:H";
            List<List<Object>> sheetData = sheetService.readData(range);

            if (sheetData == null || sheetData.isEmpty())
                return ResponseEntity.badRequest().body(null);

            List<EmployeeSalary> employees = mapSheetDataToEmployees(sheetData);
            EmployeeSalary emp = employees.stream()
                    .filter(e -> e.getEmpId() == empId)
                    .findFirst()
                    .orElse(null);

            if (emp == null)
                return ResponseEntity.badRequest().body(null);

            File pdf = pdfService.generateSalarySlip(emp, month);

            byte[] pdfBytes = java.nio.file.Files.readAllBytes(pdf.toPath());

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=" + pdf.getName())
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    // 🧩 Utility method to map sheet rows to EmployeeSalary objects
    private List<EmployeeSalary> mapSheetDataToEmployees(List<List<Object>> sheetData) {
        List<EmployeeSalary> employees = new ArrayList<>();
        for (List<Object> row : sheetData) {
            if (row.size() < 8) continue;

            EmployeeSalary emp = new EmployeeSalary();
            emp.setEmpId(Integer.parseInt(row.get(0).toString()));
            emp.setName(row.get(1).toString());
            emp.setEmail(row.get(2).toString());
            emp.setBasic(Double.parseDouble(row.get(3).toString()));
            emp.setHra(Double.parseDouble(row.get(4).toString()));
            emp.setAllowance(Double.parseDouble(row.get(5).toString()));
            emp.setDeductions(Double.parseDouble(row.get(6).toString()));
            emp.setNetSalary(Double.parseDouble(row.get(7).toString()));

            employees.add(emp);
        }
        return employees;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listSalaries(@RequestParam String month) {
        String range = month + "!A2:H";
        List<List<Object>> sheetData = sheetService.readData(range);

        if (sheetData == null || sheetData.isEmpty())
            return ResponseEntity.ok(List.of()); // return empty list instead of error

        return ResponseEntity.ok(mapSheetDataToEmployees(sheetData));
    }

}
