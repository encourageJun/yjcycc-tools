package org.yjcycc.tools.poi.excel.worksheet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelStyle {

    private CellStyle title;

    private CellStyle title1;

    private CellStyle header;

    private CellStyle header1;

    private CellStyle cell;

    private CellStyle cell1;

    private Workbook wb;

    public ExcelStyle(Workbook workbook) {
        this.wb = workbook;
    }

    public CellStyle getTitle() {
        title = wb.createCellStyle();
        title.setAlignment(CellStyle.ALIGN_CENTER);
        title.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        title.setFont(titleFont);
        return title;
    }

    public void setTitle(CellStyle title) {
        this.title = title;
    }

    public CellStyle getTitle1() {
        title1 = wb.createCellStyle();
        title1.setAlignment(CellStyle.ALIGN_CENTER);
        title1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        title1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        title1.setBorderRight(CellStyle.BORDER_THIN);
        title1.setRightBorderColor(IndexedColors.BLACK.getIndex());
        title1.setBorderLeft(CellStyle.BORDER_THIN);
        title1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        title1.setBorderTop(CellStyle.BORDER_THIN);
        title1.setTopBorderColor(IndexedColors.BLACK.getIndex());
        title1.setBorderBottom(CellStyle.BORDER_THIN);
        title1.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font titleFont2 = wb.createFont();
        titleFont2.setFontName("Arial");
        titleFont2.setFontHeightInPoints((short) 10);
        titleFont2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        title1.setFont(titleFont2);
        return title1;
    }

    public void setTitle1(CellStyle title1) {
        this.title1 = title1;
    }

    public CellStyle getHeader() {
        header = wb.createCellStyle();
        header.cloneStyleFrom(getTitle());
        header.setAlignment(CellStyle.ALIGN_CENTER);
        header.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        header.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return header;
    }

    public void setHeader(CellStyle header) {
        this.header = header;
    }

    public CellStyle getHeader1() {
        header1 = wb.createCellStyle();
        header1.cloneStyleFrom(getTitle1());
        header1.setAlignment(CellStyle.ALIGN_CENTER);
        header1.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        header1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return header1;
    }

    public void setHeader1(CellStyle header1) {
        this.header1 = header1;
    }

    public CellStyle getCell() {
        cell = wb.createCellStyle();
        cell.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cell.setBorderRight(CellStyle.BORDER_THIN);
        cell.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setBorderLeft(CellStyle.BORDER_THIN);
        cell.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setBorderTop(CellStyle.BORDER_THIN);
        cell.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setBorderBottom(CellStyle.BORDER_THIN);
        cell.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        cell.setFont(dataFont);
        return cell;
    }

    public void setCell(CellStyle cell) {
        this.cell = cell;
    }

    public CellStyle getCell1() {
        cell1 = wb.createCellStyle();
        cell1.cloneStyleFrom(getCell());
        cell1.setAlignment(CellStyle.ALIGN_CENTER);
        cell1.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        cell1.setFont(dataFont);
        cell1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cell1.setBorderRight(CellStyle.BORDER_THIN);
        cell1.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cell1.setBorderLeft(CellStyle.BORDER_THIN);
        cell1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cell1.setBorderTop(CellStyle.BORDER_THIN);
        cell1.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cell1.setBorderBottom(CellStyle.BORDER_THIN);
        cell1.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        return cell1;
    }

    public void setCell1(CellStyle cell1) {
        this.cell1 = cell1;
    }

}
