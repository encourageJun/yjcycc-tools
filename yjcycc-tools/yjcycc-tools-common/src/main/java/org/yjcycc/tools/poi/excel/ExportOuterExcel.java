package org.yjcycc.tools.poi.excel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yjcycc.tools.poi.excel.worksheet.ExcelStyle;
import org.yjcycc.tools.poi.excel.worksheet.WorkSheetOperation;

public class ExportOuterExcel extends NewExportExcel {

    /**
     * 构造函数
     * 用于系统外部模板导出
     * @param voClass
     * @param sheetName 自定义sheet名称
     */
    public ExportOuterExcel(Class<?> voClass, String sheetName) throws Exception {
        this.voClass = voClass;

        // 获取annotation中配置的sheetName, title, header, colunmWidth, sort等信息
        initAnnotations();

        if (StringUtils.isNotEmpty(sheetName)) {
            this.sheetName = sheetName;
        }

        // 初始化Workbook
        filePath = this.getClass().getResource(filePath).getPath();
        File file = new File(filePath);
        this.workbook = new XSSFWorkbook(new FileInputStream(file));
        this.sheet = workbook.getSheet(this.sheetName);
        excelStyle = new ExcelStyle(workbook);
        WorkSheetOperation.rowNum = 2;
        //initWorkbook();
    }

}
