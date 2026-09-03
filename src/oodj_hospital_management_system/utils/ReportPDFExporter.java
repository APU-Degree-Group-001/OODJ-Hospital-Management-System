/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author sheaw
 */
public class ReportPDFExporter {
    // Color Palette
    private static final Color PRIMARY = new DeviceRgb(0, 63, 92);
    private static final Color ACCENT = new DeviceRgb(47, 141, 172);
    private static final Color LIGHT_BG = new DeviceRgb(235, 245, 249);
    private static final Color BORDER_GRAY = new DeviceRgb(210, 220, 227);
    private static final Color TEXT_GRAY = new DeviceRgb(90, 100, 110);
    private static final Color WHITE = new DeviceRgb(255, 255, 255);

    public static String filePath(String fileName, String folderPath) {
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = folderPath + "\\" + fileName + ".pdf";
        return filePath;
    }

    // Billing / Revenue Report
    public static boolean exportBillingRevenueReport(String filePath, String generatedBy, String datetime,
            LinkedHashMap<String, String> billingTotals, LinkedHashMap<String, Double> revenueByMonth,
            LinkedHashMap<String, Double> billStatusBreakdown) throws IOException {
        try {
            Document document = newDocument(filePath);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normalFont = PdfFontFactory.createFont("Helvetica");

            addHeaderBanner(document, "Billing / Revenue Report", generatedBy, datetime, boldFont, normalFont);
            addIntroBox(document, "A summary of billing totals, monthly revenue trends, and bill status breakdown.", normalFont);

            addSectionHeading(document, "Billing Summary", boldFont);
            addSummaryTable(document, billingTotals, boldFont, normalFont);

            addSectionHeading(document, "Revenue by Month", boldFont);
            addBreakdownTable(document, revenueByMonth, "Month", "Revenue (RM)", boldFont, normalFont, false);

            addSectionHeading(document, "Bill Status Breakdown", boldFont);
            addBreakdownTable(document, billStatusBreakdown, "Status", "No. of Bills", boldFont, normalFont, true);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insurance Usage Report
    public static boolean exportInsuranceUsageReport(String filePath, String generatedBy, String datetime,
            LinkedHashMap<String, String> insuranceTotals, LinkedHashMap<String, Double> usageByProvider,
            LinkedHashMap<String, Double> payableAmountByInsurance) throws IOException {
        try {
            Document document = newDocument(filePath);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normalFont = PdfFontFactory.createFont("Helvetica");

            addHeaderBanner(document, "Insurance Usage Report", generatedBy, datetime, boldFont, normalFont);
            addIntroBox(document, "A summary of insurance coverage usage across all bills, grouped by provider.", normalFont);

            addSectionHeading(document, "Insurance Summary", boldFont);
            addSummaryTable(document, insuranceTotals, boldFont, normalFont);

            addSectionHeading(document, "No. of Bills by Insurance Provider", boldFont);
            addBreakdownTable(document, usageByProvider, "Insurance Provider", "No. of Bills", boldFont, normalFont, true);

            addSectionHeading(document, "Payable Amount by Insurance Provider", boldFont);
            addBreakdownTable(document, payableAmountByInsurance, "Insurance Provider", "Payable Amount (RM)", boldFont, normalFont, false);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Consultation Report
    public static boolean exportConsultationReport(String filePath, String generatedBy, String datetime,
            LinkedHashMap<String, String> consultationTotals, LinkedHashMap<String, Double> consultationsByStatus,
            LinkedHashMap<String, Double> consultationsByDoctor, LinkedHashMap<String, Double> consultationsByMonth) throws IOException {
        try {
            Document document = newDocument(filePath);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normalFont = PdfFontFactory.createFont("Helvetica");

            addHeaderBanner(document, "Consultation Report", generatedBy, datetime, boldFont, normalFont);
            addIntroBox(document, "A summary of consultations, grouped by status, doctor, and month.", normalFont);

            addSectionHeading(document, "Consultation Summary", boldFont);
            addSummaryTable(document, consultationTotals, boldFont, normalFont);

            addSectionHeading(document, "Consultations by Status", boldFont);
            addBreakdownTable(document, consultationsByStatus, "Status", "No. of Consultations", boldFont, normalFont, true);

            addSectionHeading(document, "Consultations by Doctor", boldFont);
            addBreakdownTable(document, consultationsByDoctor, "Doctor", "No. of Consultations", boldFont, normalFont, true);

            addSectionHeading(document, "Consultations by Month", boldFont);
            addBreakdownTable(document, consultationsByMonth, "Month", "No. of Consultations", boldFont, normalFont, true);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Doctor Shift Report
    public static boolean exportDoctorShiftReport(String filePath, String generatedBy, String datetime,
            LinkedHashMap<String, String> doctorShiftTotals, LinkedHashMap<String, Double> shiftsByStatus,
            LinkedHashMap<String, Double> scheduledShiftHoursByDoctor) throws IOException {
        try {
            Document document = newDocument(filePath);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normalFont = PdfFontFactory.createFont("Helvetica");

            addHeaderBanner(document, "Doctor Shift Report", generatedBy, datetime, boldFont, normalFont);
            addIntroBox(document, "A summary of doctor shifts, grouped by status and scheduled hours per doctor.", normalFont);

            addSectionHeading(document, "Doctor Shift Summary", boldFont);
            addSummaryTable(document, doctorShiftTotals, boldFont, normalFont);

            addSectionHeading(document, "Shifts by Status", boldFont);
            addBreakdownTable(document, shiftsByStatus, "Status", "No. of Shifts", boldFont, normalFont, true);

            addSectionHeading(document, "Scheduled Shift Hours by Doctor", boldFont);
            addBreakdownTable(document, scheduledShiftHoursByDoctor, "Doctor", "Scheduled Hours", boldFont, normalFont, false);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Room / Hospital Asset Utilization Report
    public static boolean exportRoomUtilizationReport(String filePath, String generatedBy, String datetime,
            LinkedHashMap<String, String> roomUtilizationTotals, LinkedHashMap<String, Double> allocationCountByAsset,
            LinkedHashMap<String, Double> occupiedMinutesByAsset) throws IOException {
        try {
            Document document = newDocument(filePath);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normalFont = PdfFontFactory.createFont("Helvetica");

            addHeaderBanner(document, "Room / Hospital Asset Utilization Report", generatedBy, datetime, boldFont, normalFont);
            addIntroBox(document, "A summary of hospital asset allocations and occupancy, grouped by asset.", normalFont);

            addSectionHeading(document, "Room Utilization Summary", boldFont);
            addSummaryTable(document, roomUtilizationTotals, boldFont, normalFont);

            addSectionHeading(document, "Allocation Count by Asset", boldFont);
            addBreakdownTable(document, allocationCountByAsset, "Asset", "No. of Allocations", boldFont, normalFont, true);

            addSectionHeading(document, "Occupied Minutes by Asset", boldFont);
            addBreakdownTable(document, occupiedMinutesByAsset, "Asset", "Occupied Minutes", boldFont, normalFont, false);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helpers

    // Create New Document
    private static Document newDocument(String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler());
        Document document = new Document(pdf);
        document.setMargins(40, 40, 50, 40);
        return document;
    }

    // Header Banner: report title on the left, generated-by/date on the right,
    private static void addHeaderBanner(Document document, String titleText, String generatedBy, String datetime, PdfFont boldFont, PdfFont normalFont) {
        Table banner = new Table(new float[]{3, 2}).useAllAvailableWidth();
        banner.setBackgroundColor(PRIMARY);

        Cell titleCell = new Cell()
                .add(new Paragraph(titleText).setFont(boldFont).setFontSize(18).setFontColor(WHITE))
                .add(new Paragraph("Hospital Management System").setFont(normalFont).setFontSize(9).setFontColor(LIGHT_BG).setMarginTop(2))
                .setBorder(Border.NO_BORDER)
                .setPadding(16)
                .setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.MIDDLE);

        Cell metaCell = new Cell()
                .add(new Paragraph("Generated by: " + generatedBy).setFont(normalFont).setFontSize(9).setFontColor(WHITE).setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("Date: " + datetime).setFont(normalFont).setFontSize(9).setFontColor(WHITE).setTextAlignment(TextAlignment.RIGHT).setMarginTop(2))
                .setBorder(Border.NO_BORDER)
                .setPadding(16)
                .setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.MIDDLE);

        banner.addCell(titleCell);
        banner.addCell(metaCell);
        document.add(banner.setMarginBottom(20));
    }

    // Summary Info Box
    private static void addIntroBox(Document document, String introText, PdfFont normalFont) {
        Table introTable = new Table(1).useAllAvailableWidth();
        introTable.addCell(new Cell()
                .add(new Paragraph(introText).setFont(normalFont).setFontSize(10).setFontColor(TEXT_GRAY))
                .setBackgroundColor(LIGHT_BG)
                .setBorder(new SolidBorder(ACCENT, 1))
                .setPadding(14));
        document.add(introTable.setMarginBottom(18));
    }

    // Section Heading
    private static void addSectionHeading(Document document, String headingText, PdfFont boldFont) {
        Paragraph heading = new Paragraph(headingText)
                .setFont(boldFont)
                .setFontSize(13)
                .setFontColor(PRIMARY)
                .setBorderBottom(new SolidBorder(ACCENT, 1.5f))
                .setPaddingBottom(4)
                .setMarginTop(4)
                .setMarginBottom(10);
        document.add(heading);
    }

    // Renders a summary map (Eg. getBillingTotals()) as a striped "Metric / Value" table)
    private static void addSummaryTable(Document document, LinkedHashMap<String, String> summary, PdfFont boldFont, PdfFont normalFont) {
        if (summary == null || summary.isEmpty()) {
            document.add(noDataParagraph(normalFont));
            return;
        }
        Table pdfTable = new Table(UnitValue.createPercentArray(new float[]{2, 1})).useAllAvailableWidth();
        addHeaderCell(pdfTable, "Metric", boldFont, TextAlignment.LEFT);
        addHeaderCell(pdfTable, "Value", boldFont, TextAlignment.CENTER);

        int row = 0;
        for (Map.Entry<String, String> entry : summary.entrySet()) {
            Color rowColor = (row % 2 == 0) ? WHITE : LIGHT_BG;
            addBodyCell(pdfTable, prettifyKey(entry.getKey()), normalFont, TextAlignment.LEFT, rowColor);
            addBodyCell(pdfTable, entry.getValue(), normalFont, TextAlignment.CENTER, rowColor);
            row++;
        }
        document.add(pdfTable.setMarginBottom(18));
    }

    // Renders a breakdown map as a striped table; asInteger drops decimals for count-style values.
    private static void addBreakdownTable(Document document, LinkedHashMap<String, Double> breakdown,
            String keyLabel, String valueLabel, PdfFont boldFont, PdfFont normalFont, boolean asInteger) {
        if (breakdown == null || breakdown.isEmpty()) {
            document.add(noDataParagraph(normalFont));
            return;
        }
        Table pdfTable = new Table(UnitValue.createPercentArray(new float[]{2, 1})).useAllAvailableWidth();
        addHeaderCell(pdfTable, keyLabel, boldFont, TextAlignment.LEFT);
        addHeaderCell(pdfTable, valueLabel, boldFont, TextAlignment.CENTER);

        int row = 0;
        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            String formattedValue = asInteger
                    ? String.valueOf(entry.getValue().intValue())
                    : String.format("%.2f", entry.getValue());
            Color rowColor = (row % 2 == 0) ? WHITE : LIGHT_BG;
            addBodyCell(pdfTable, entry.getKey(), normalFont, TextAlignment.LEFT, rowColor);
            addBodyCell(pdfTable, formattedValue, normalFont, TextAlignment.CENTER, rowColor);
            row++;
        }
        document.add(pdfTable.setMarginBottom(18));
    }

    private static void addHeaderCell(Table table, String text, PdfFont boldFont, TextAlignment align) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(10).setFontColor(WHITE).setTextAlignment(align))
                .setBackgroundColor(PRIMARY)
                .setBorder(Border.NO_BORDER)
                .setPadding(8));
    }

    private static void addBodyCell(Table table, String text, PdfFont normalFont, TextAlignment align, Color background) {
        table.addCell(new Cell()
                .add(new Paragraph(text).setFont(normalFont).setFontSize(10).setFontColor(TEXT_GRAY).setTextAlignment(align))
                .setBackgroundColor(background)
                .setBorder(new SolidBorder(BORDER_GRAY, 0.5f))
                .setPadding(7));
    }

    private static Paragraph noDataParagraph(PdfFont normalFont) {
        return new Paragraph("No data available.")
                .setFont(normalFont)
                .setFontSize(10)
                .setFontColor(TEXT_GRAY)
                .setMarginBottom(18);
    }

    // Convert Camel Case (Eg. totalRevenue => Total Revenue)
    private static String prettifyKey(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        String spaced = key.replaceAll("([a-z])([A-Z])", "$1 $2");
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    // Footer (Page X)
    private static class FooterHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdfDoc.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            Canvas canvas = new Canvas(pdfCanvas, pdfDoc, pageSize);
            try {
                PdfFont normalFont = PdfFontFactory.createFont("Helvetica");
                canvas.showTextAligned(
                        new Paragraph("Page " + pageNumber).setFont(normalFont).setFontSize(9).setFontColor(TEXT_GRAY),
                        pageSize.getWidth() / 2, 25, TextAlignment.CENTER);
            } catch (IOException e) {
                e.printStackTrace();
            }
            canvas.close();
        }
    }
}
