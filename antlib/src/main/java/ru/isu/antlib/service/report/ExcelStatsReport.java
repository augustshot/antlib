package ru.isu.antlib.service.report;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.Map;


public class ExcelStatsReport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> input,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response){

        response.setHeader("Content-Disposition", "attachment; filename=\"antlib-statistics.xlsx\"");

        String username = (String) input.get("username");
        Map<String, Integer> status = (Map<String, Integer>) input.get("status");
        Map<String, Integer> source = (Map<String, Integer>) input.get("source");
        Map<String, Long> language = (Map<String, Long>) input.get("language");
        Integer pages = (Integer) input.get("pages");
        Double rating = (Double) input.get("rating");

        Sheet sheet = workbook.createSheet("Статистика " + username);

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        int currentRow = 0;

        // status
        Row statusTitleRow = sheet.createRow(currentRow++);
        Cell statusTitleCell = statusTitleRow.createCell(0);
        statusTitleCell.setCellValue("СТАТУСЫ ЧТЕНИЯ");
        statusTitleCell.setCellStyle(titleStyle);

        Row statusHeaderRow = sheet.createRow(currentRow++);
        createCell(statusHeaderRow, 0, "Статус", headerStyle);
        createCell(statusHeaderRow, 1, "Количество книг", headerStyle);

        int statusStartRow = currentRow;
        if (status != null) {
            for (Map.Entry<String, Integer> entry : status.entrySet()) {
                Row row = sheet.createRow(currentRow++);
                createCell(row, 0, entry.getKey(), valueStyle);
                createCell(row, 1, entry.getValue(), valueStyle);
            }
        }

        if (status != null && !status.isEmpty()) {
            createPieChart(sheet, statusStartRow, currentRow - 1, 0, 1,
                    5, 1, 14, 14,
                    "Статистика по статусам");
        }

        currentRow++; 
        
        // source
        Row sourceTitleRow = sheet.createRow(currentRow++);
        Cell sourceTitleCell = sourceTitleRow.createCell(0);
        sourceTitleCell.setCellValue("ИСТОЧНИКИ КНИГ");
        sourceTitleCell.setCellStyle(titleStyle);

        Row sourceHeaderRow = sheet.createRow(currentRow++);
        createCell(sourceHeaderRow, 0, "Источник", headerStyle);
        createCell(sourceHeaderRow, 1, "Количество книг", headerStyle);

        int sourceStartRow = currentRow;
        if (source != null) {
            for (Map.Entry<String, Integer> entry : source.entrySet()) {
                Row row = sheet.createRow(currentRow++);
                createCell(row, 0, entry.getKey(), valueStyle);
                createCell(row, 1, entry.getValue(), valueStyle);
            }
        }

        if (source != null && !source.isEmpty()) {
            createPieChart(sheet, sourceStartRow, currentRow - 1, 0, 1,
                    5, 17, 14, 30,
                    "Статистика по источникам");
        }

        currentRow += 2;

        // language
        Row langTitleRow = sheet.createRow(currentRow++);
        Cell langTitleCell = langTitleRow.createCell(0);
        langTitleCell.setCellValue("СТАТИСТИКА ПО ЯЗЫКАМ");
        langTitleCell.setCellStyle(titleStyle);

        Row langHeaderRow = sheet.createRow(currentRow++);
        createCell(langHeaderRow, 0, "Язык", headerStyle);
        createCell(langHeaderRow, 1, "Количество книг", headerStyle);

        if (language != null) {
            for (Map.Entry<String, Long> entry : language.entrySet()) {
                Row row = sheet.createRow(currentRow++);
                createCell(row, 0, entry.getKey(), valueStyle);
                createCell(row, 1, entry.getValue(), valueStyle);
            }
        }

        currentRow += 2;

        // pages

        Row pagesRow = sheet.createRow(currentRow++);
        createCell(pagesRow, 0, "Всего страниц прочитано", headerStyle);
        if (pages != null) {
            createCell(pagesRow, 1, pages, valueStyle);
        } else {
            createCell(pagesRow, 1, 0, valueStyle);
        }

        currentRow += 2;

        // rating

        Row ratingRow = sheet.createRow(currentRow++);
        createCell(ratingRow, 0, "Средняя оценка (1-5)", headerStyle);
        if (rating != null) {
            createCell(ratingRow, 1, Math.round(rating * 10) / 10.0, valueStyle);
        } else {
            createCell(ratingRow, 1, 0, valueStyle);
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int col, Integer value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int col, Double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int col, Long value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }


    private void createPieChart(Sheet sheet, int startRow, int endRow, int labelCol, int valueCol,
                                int chartCol1, int chartRow1, int chartCol2, int chartRow2,
                                String title) {
        try {
            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, chartCol1, chartRow1, chartCol2, chartRow2);
            XSSFChart chart = drawing.createChart(anchor);

            chart.setTitleText(title);
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.RIGHT);

            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(startRow, endRow, labelCol, labelCol));
            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(startRow, endRow, valueCol, valueCol));

//            var pieData = chart.createData(ChartTypes.PIE, null, null);
            XDDFChartData pieData = new XDDFPieChartData(chart.getCTChart().getPlotArea().addNewPieChart());
            pieData.setVaryColors(true);

            pieData.addSeries(categories, values);
            chart.plot(pieData);

            // проценты

                var pieChart = chart.getCTChart().getPlotArea().getPieChartArray(0);
                if (pieChart.getSerArray().length > 0) {
                    var series = pieChart.getSerArray(0);

                    if (!series.isSetDLbls()) {
                        series.addNewDLbls();
                    }

                    var dlbls = series.getDLbls();

                    dlbls.addNewShowPercent().setVal(true);

                    dlbls.addNewShowVal().setVal(false);
                    dlbls.addNewShowCatName().setVal(false);
                    dlbls.addNewShowSerName().setVal(false);
                    dlbls.addNewShowLegendKey().setVal(false);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
