package org.yjcycc.tools.poi.excel.worksheet;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class WorkSheetOperation {

    // excel行序号
    public static int rowNum = 0;

    /**
     * 添加行
     * @param sheet
     * @return
     */
    public static Row addRow(Sheet sheet) {
        return sheet.createRow(rowNum++);
    }

    /**
     * 添加列
     * @param row 列所在行
     * @param cellNum 列序号
     * @param value 列值
     * @param cellStyle 列样式
     * @param mergedRegion 合并单元格
     * @return
     */
    public static Cell addCell(Sheet sheet, Row row, int cellNum, String value, CellStyle cellStyle, int mergedRegion) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        if (mergedRegion > 0) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),
                    row.getRowNum(), row.getRowNum(), mergedRegion));
        }
        return cell;
    }

    /**
     * 添加列
     * @param row 列所在行
     * @param cellNum 列序号
     * @param value 列值
     * @param cellStyle 列样式
     * @return
     */
    public static Cell addCell(Row row, int cellNum, String value, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }

    /**
     * 添加列
     * @param row 列所在行
     * @param cellNum 列序号
     * @param value 列值
     * @param cellStyle 列样式
     * @return
     */
    public static Cell addCell(Row row, int cellNum, Object value, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNum);

        if (value == null){
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof BigDecimal) {
            BigDecimal temp = ((BigDecimal) value).setScale(2, BigDecimal.ROUND_HALF_UP);
            cell.setCellValue(Double.parseDouble(temp.toString()));
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        } else {
            cell.setCellValue("");
        }

        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }

    /**
     * 设置列公式
     * @param cell
     * @param formula
     * @return
     */
    public static Cell setFormula(Cell cell, String formula) {
        cell.setCellFormula(formula);
        return cell;
    }

    public static Cell setCellStyle(Cell cell, CellStyle cellStyle) {
        cell.setCellStyle(cellStyle);
        return cell;
    }

}
