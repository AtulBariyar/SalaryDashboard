package com.atulbariyar.SalaryGenerator.service;

import com.atulbariyar.SalaryGenerator.model.EmployeeSalary;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

@Service
public class PdfGeneratorService {

    private static final String OUTPUT_DIR = "salary_slips/";

    public File generateSalarySlip(EmployeeSalary emp, String month) throws Exception {

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) dir.mkdirs();

        String filename = OUTPUT_DIR + emp.getName().replace(" ", "_") + "_" + month + "_SalarySlip.pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        // ✅ Add Logo (optional)
        try {
            Image logo = Image.getInstance("src/main/resources/static/1631323634190.jpg");
            logo.scaleAbsolute(80, 80);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception ignored) {}

        // ✅ Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(30, 30, 30));
        Paragraph title = new Paragraph("Salary Slip - " + month, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15f);
        document.add(title);

        // ✅ Employee Details Table
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(10f);

        addInfoCell(infoTable, "Employee ID", String.valueOf(emp.getEmpId()));
        addInfoCell(infoTable, "Name", emp.getName());
        addInfoCell(infoTable, "Email", emp.getEmail());
        addInfoCell(infoTable, "Month", month);

        document.add(infoTable);

        // ✅ Salary Breakdown Table
        PdfPTable salaryTable = new PdfPTable(2);
        salaryTable.setWidthPercentage(100);
        salaryTable.setSpacingBefore(10f);
        salaryTable.setSpacingAfter(10f);

        addSalaryCell(salaryTable, "Basic Salary", emp.getBasic());
        addSalaryCell(salaryTable, "HRA", emp.getHra());
        addSalaryCell(salaryTable, "Allowance", emp.getAllowance());
        addSalaryCell(salaryTable, "Deductions", emp.getDeductions());

        // ✅ Highlight Net Salary
        PdfPCell netLabel = new PdfPCell(new Phrase("Net Salary", new Font(Font.HELVETICA, 13, Font.BOLD, Color.WHITE)));
        netLabel.setBackgroundColor(new Color(0, 102, 204));
        netLabel.setPadding(8f);

        PdfPCell netValue = new PdfPCell(new Phrase("₹ " + emp.getNetSalary(), new Font(Font.HELVETICA, 13, Font.BOLD, Color.WHITE)));
        netValue.setBackgroundColor(new Color(0, 102, 204));
        netValue.setPadding(8f);

        salaryTable.addCell(netLabel);
        salaryTable.addCell(netValue);
        document.add(salaryTable);

        // ✅ Footer
        Paragraph footer = new Paragraph(
                "This is a system generated document. No signature required.",
                new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(15);
        document.add(footer);

        document.close();
        return new File(filename);
    }

    // Helpers
    private void addInfoCell(PdfPTable table, String key, String value) {
        PdfPCell k = new PdfPCell(new Phrase(key, new Font(Font.HELVETICA, 11, Font.BOLD)));
        PdfPCell v = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 11)));

        k.setBorder(Rectangle.NO_BORDER);
        v.setBorder(Rectangle.NO_BORDER);

        k.setPadding(5f);
        v.setPadding(5f);

        table.addCell(k);
        table.addCell(v);
    }

    private void addSalaryCell(PdfPTable table, String label, double value) {
        PdfPCell l = new PdfPCell(new Phrase(label, new Font(Font.HELVETICA, 11)));
        PdfPCell v = new PdfPCell(new Phrase("₹ " + value, new Font(Font.HELVETICA, 11)));

        l.setPadding(8f);
        v.setPadding(8f);

        table.addCell(l);
        table.addCell(v);
    }
}
