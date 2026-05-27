package ru.isu.antlib.service.report;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.model.UserBookMark;

import java.util.List;
import java.util.Map;


public class ExcelBooksReport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> input,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        response.setHeader("Content-Disposition", "attachment; filename=\"antlib-books.xlsx\"");

        List<UserBookMark> userBookMarks = (List<UserBookMark>) input.get("userBookMarks");
        String username = (String) input.get("username");
        Sheet sheet = workbook.createSheet("Книги " + username);
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Название", "Автор", "Год", "ISBN", "Страницы", "Язык", "Издательство", "Аннотация",
                "Оценка", "Источник", "Статус чтения", "Дата начала", "Дата окончания", "Мои заметки"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (UserBookMark userBookMark : userBookMarks) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(70);
            BookDescription book = userBookMark.getBookDescription();

            int col = 0;
            row.createCell(col++).setCellValue(book.getTitle() != null ? book.getTitle() : "");
            row.createCell(col++).setCellValue(book.getAuthor() != null ? book.getAuthor() : "");

            if (book.getYear() != null) row.createCell(col++).setCellValue(book.getYear());
            else row.createCell(col++).setCellValue("");

            row.createCell(col++).setCellValue(book.getISBN() != null ? book.getISBN() : "");

            if (book.getPages() != null) row.createCell(col++).setCellValue(book.getPages());
            else row.createCell(col++).setCellValue("");

            row.createCell(col++).setCellValue(book.getLanguage() != null ? book.getLanguage() : "");
            row.createCell(col++).setCellValue(book.getPublisher() != null ? book.getPublisher() : "");

            Cell descCell = row.createCell(col++);
            descCell.setCellValue(book.getDescription() != null ? book.getDescription() : "");
            descCell.setCellStyle(wrapStyle);

            if (userBookMark.getRating() != null) row.createCell(col++).setCellValue(userBookMark.getRating());
            else row.createCell(col++).setCellValue("");

            row.createCell(col++).setCellValue(userBookMark.getSource() != null ? userBookMark.getSource().getValue() : "");
            row.createCell(col++).setCellValue(userBookMark.getStatus() != null ? userBookMark.getStatus().getValue() : "");
            if (userBookMark.getDateStart() != null) row.createCell(col++).setCellValue(userBookMark.getDateStart().toString());
            else row.createCell(col++).setCellValue("");

            if (userBookMark.getDateFinish() != null) row.createCell(col++).setCellValue(userBookMark.getDateFinish().toString());
            else row.createCell(col++).setCellValue("");

            Cell reviewCell = row.createCell(col++);
            reviewCell.setCellValue(userBookMark.getReview() != null ? userBookMark.getReview() : "");
            reviewCell.setCellStyle(wrapStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("Аннотация")) {
                sheet.setColumnWidth(i, 12000);
            } else if (headers[i].equals("Мои заметки")) {
                sheet.setColumnWidth(i, 12000);
            } else {
                sheet.autoSizeColumn(i);
            }
        }

        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers.length - 2));
    }
}
