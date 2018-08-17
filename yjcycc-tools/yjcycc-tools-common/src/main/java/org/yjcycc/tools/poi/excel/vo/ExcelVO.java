package org.yjcycc.tools.poi.excel.vo;

import org.yjcycc.tools.poi.excel.annotation.ExcelField;

public class ExcelVO {

    private String sheetName;

    @ExcelField(headers = "导出模板||导出数据")
    private String title;

    @ExcelField(headers="姓名", sort = 1, headerStyles = {"header1"}, cellStyle = "cell1", fieldType=String.class)
    private String userName;


    public ExcelVO() {

    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
