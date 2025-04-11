package ExtraSources;

import ExtraSources.DBConnect;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClassListGenerator {

    public static void generateClassList(String subjectCode, String sectionName, File outputFile) throws SQLException, IOException {
        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Class List");

        // Create header styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);

        // Get subject name from subject code
        String subjectName = getSubjectName(subjectCode);
        if (subjectName == null) {
            throw new SQLException("Subject not found: " + subjectCode);
        }

        // Add title and headers
        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("CLASS LIST: " + subjectCode + " - " + subjectName + " (" + sectionName + ")");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        // Empty row
        rowNum++;

        // Date row
        Row dateRow = sheet.createRow(rowNum++);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Date Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        // Empty row
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No.", "Student ID", "Student Name", "Remarks"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Get student data
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT s.sr_code, s.first_name, s.middle_name, s.last_name " +
                    "FROM student s " +
                    "JOIN student_section ss ON s.id = ss.student_id " +
                    "JOIN section sec ON ss.section_id = sec.section_id " +
                    "JOIN enrolled e ON s.id = e.student_id " +
                    "JOIN subjects sub ON e.sub_id = sub.sub_id " +
                    "WHERE sub.subj_code = ? AND sec.section_name = ? " +
                    "ORDER BY s.last_name, s.first_name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subjectCode);
            stmt.setString(2, sectionName);
            ResultSet rs = stmt.executeQuery();

            int studentNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                // Number
                row.createCell(0).setCellValue(studentNum++);

                // Student ID
                row.createCell(1).setCellValue(rs.getString("sr_code"));

                // Full Name (Last, First Middle)
                String middleInitial = rs.getString("middle_name");
                middleInitial = (middleInitial != null && !middleInitial.isEmpty())
                        ? " " + middleInitial.charAt(0) + "."
                        : "";
                String fullName = rs.getString("last_name") + ", " +
                        rs.getString("first_name") + middleInitial;
                row.createCell(2).setCellValue(fullName);

                // Remarks (empty)
                row.createCell(3).setCellValue("");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save workbook
            String fileName = subjectCode + "_" + sectionName.replace(" ", "_") + "_ClassList.xlsx";
            // Instead of generating a filename, use the provided file
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                workbook.write(outputStream);
            }

            workbook.close();
        }
    }

    private static String getSubjectName(String subjectCode) throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT subject_name FROM subjects WHERE subj_code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subjectCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("subject_name");
            }
            return null;
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
}