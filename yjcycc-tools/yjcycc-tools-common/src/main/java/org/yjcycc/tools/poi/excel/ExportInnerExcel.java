package org.yjcycc.tools.poi.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.yjcycc.tools.poi.excel.worksheet.ExcelStyle;

public class ExportInnerExcel extends NewExportExcel {

    /**
     * 构造函数
     * 用于系统内部模板导出
     * @param voClass
     * @param sheetName 自定义sheet名称, 默认使用VO中定义的sheetName
     */
    public ExportInnerExcel(Class<?> voClass, String sheetName) throws Exception {
        this.voClass = voClass;

        // 获取annotation中配置的sheetName, title, header, colunmWidth, sort等信息
        initAnnotations();

        if (StringUtils.isNotEmpty(sheetName)) {
            this.sheetName = sheetName;
        }

        // 初始化Workbook
        this.workbook = new SXSSFWorkbook(rowAccessWindowSize);
        this.sheet = workbook.createSheet(this.sheetName);
        excelStyle = new ExcelStyle(workbook);
        initWorkbook();
    }

}
