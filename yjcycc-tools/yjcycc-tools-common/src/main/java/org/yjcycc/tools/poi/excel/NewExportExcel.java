package org.yjcycc.tools.poi.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.yjcycc.tools.common.util.EncodeUtil;
import org.yjcycc.tools.common.util.ReflectionUtil;
import org.yjcycc.tools.poi.excel.annotation.ExcelField;
import org.yjcycc.tools.poi.excel.util.ExcelColumnUtil;
import org.yjcycc.tools.poi.excel.worksheet.ExcelStyle;
import org.yjcycc.tools.poi.excel.worksheet.WorkSheetOperation;

import com.google.common.collect.Lists;

/**
 * 内部/外部系统模板导出
 *
 * @author Yangjun
 * 2018-08-12 00:08
 */
public abstract class NewExportExcel {

//    private static Logger log = LoggerFactory.getLogger(NewExportExcel.class);

    /**
     * 指定内存中缓存记录数
     */
    protected static final int rowAccessWindowSize = 500;

    /**
     * voClass
     */
    protected Class<?> voClass;

    /**
     * 工作薄对象
     */
    protected Workbook workbook;

    /**
     * 工作表对象
     */
    protected Sheet sheet;

    /**
     * excel样式
     */
    protected ExcelStyle excelStyle;

    /**
     * Sheet名称
     */
    protected String sheetName = "Export";

    /**
     * 文件名
     */
    protected String fileName;

    /**
     * 文件路径
     */
    protected String filePath;

    /**
     * 标题样式, 格式String[]{ 标题,样式 }
     */
    protected String[] title;

    /**
     * 头部样式列表, 格式[标题,样式]
     */
    protected Map<Integer,List<String[]>> headerMap = new HashMap<>(200);

    /**
     * 表格样式
     */
    protected List<String> cellStyles = Lists.newArrayList();

    /**
     * 注解列表（Object[]{ Field, ExcelField }）
     */
    protected List<Object[]> annotationFieldList = Lists.newArrayList();

    /**
     * 注解列表（Object[]{ Method, ExcelField }）
     */
//    protected List<Object[]> annotationMethodList = Lists.newArrayList();

    /**
     * 用于构造公式
     */
    protected Map<String, Integer> fieldMap = new TreeMap<>();

    /**
     * 默认构造函数
     */
    public NewExportExcel() {}

    /**
     * 获取annotations
     * [获取annotation中配置的sheetName, title, header, colunmWidth, sort等信息]
     */
    protected void initAnnotations() throws Exception {
        // 获取annotation属性
        Field[] fs = voClass.getDeclaredFields();
        Object instance = voClass.newInstance();

//        int index = 0;
        for (Field f : fs){
            String fieldName = f.getName();
            Object val = ReflectionUtil.invokeGetter(instance, fieldName);

            // 初始化文件名
            if (fieldName.equals("fileName")) {
                fileName = (String) val;
            }

            // 初始化文件路径
            if (fieldName.equals("filePath")) {
                filePath = (String) val;
            }

            // 初始化sheetName
            if (fieldName.equals("sheetName")) {
                sheetName = (String) val;
            }

            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null) {
                // 初始化标题和标题样式
                if (fieldName.equals("title")) {
                    if (StringUtils.isNotEmpty(ef.title())) {
                        title[0] = ef.title();
                        title[1] = ef.titleStyle();
                    }
                    continue;
                }

                // 初始化头部和头部样式
                assemble(ef.headers(), ef.headerStyles());

                // 列样式
                cellStyles.add(ef.cellStyle());

                // 用于公式
                fieldMap.put(fieldName, ef.sort());
            }
            annotationFieldList.add(new Object[]{f, ef});
        }

        // 根据annotation中配置的sort排序
        sortingList(annotationFieldList);

        // 根据map中的sort排序
        sortingMap(fieldMap);

        /*// 获取annotation方法
        Method[] ms = voClass.getDeclaredMethods();
        for (Method m : ms){
            ExcelField ef = m.getAnnotation(ExcelField.class);
            annotationMethodList.add(new Object[]{m, ef});
        }

        // 根据annotation中配置的sort排序
        sorting(annotationMethodList);*/
    }

    /**
     * 组装Map
     */
    private void assemble(String[] names, String[] styleNames) {
        for (int i = 0; i < names.length; i++) {
            if (StringUtils.isEmpty(names[i])) {
                continue;
            }
            if (headerMap.size() < names.length) {
                headerMap.put(i, Lists.<String[]>newArrayList());
            }
            String headerStyleName = null;
            if (styleNames != null && styleNames.length > 0 && StringUtils.isNotEmpty(styleNames[i])) {
                headerStyleName = styleNames[i];
            }
            headerMap.get(i).add(new String[]{names[i], headerStyleName});
        }
    }

    /**
     * 根据map中的sort排序
     * @param fieldMap
     */
    private void sortingMap(Map<String, Integer> fieldMap) {
        List<Map.Entry<String,Integer>> list = new ArrayList<>(fieldMap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String,Integer>>() {
            //升序排序
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        int index = 0;
        for(Map.Entry<String,Integer> mapping : list) {
            System.out.println(mapping.getKey()+":"+mapping.getValue());
            mapping.setValue(index++);
        }
    }

    /**
     * 根据annotation中的sort排序
     * @param list
     */
    private void sortingList(List<Object[]> list) {
        // 根据annotation中配置的sort排序
        Collections.sort(list, new Comparator<Object[]>() {
            public int compare(Object[] o1, Object[] o2) {
                ExcelField ef1 = (ExcelField)o1[1];
                ExcelField ef2 = (ExcelField)o2[1];
                if (ef1 == null || ef2 == null) {
                    return 0;
                }
                Integer sort1 = ef1.sort();
                Integer sort2 = ef2.sort();
                return sort1.compareTo(sort2);
            }
        });
    }

    /**
     * 初始化Workbook
     */
    protected void initWorkbook() {

        // 创建标题行
        if (title != null && title.length > 0) {
            initTitles();
        }

        // 创建头部行
        if (headerMap != null && !headerMap.isEmpty()) {
            initHeaders();
        }
    }

    /**
     * 创建标题行
     */
    protected void initTitles() {
        String titleName = this.title[0];
        String titleStyleName = this.title[1];
        if (StringUtils.isNotEmpty(titleName)) {
            Row row = WorkSheetOperation.addRow(sheet);
            CellStyle cellStyle = null;
            if (StringUtils.isNotEmpty(titleStyleName)) {
                cellStyle = (CellStyle) ReflectionUtil.invokeGetter(excelStyle, titleStyleName);
            }
            int mergedRegion = 0;
            if (headerMap != null && headerMap.get(0) != null && !headerMap.get(0).isEmpty()) {
                mergedRegion = headerMap.get(0).size() - 1;
            }
            WorkSheetOperation.addCell(sheet, row, 0, titleName, cellStyle, mergedRegion);
        }
    }

    /**
     * 创建头部行
     */
    protected void initHeaders() {
        for (Integer index : headerMap.keySet()) {
            List<String[]> headerList = headerMap.get(index);
            CellStyle cellStyle = null;
            int column = 0;
            if (headerList != null && !headerList.isEmpty()) {
                Row row = WorkSheetOperation.addRow(sheet);
                for (String[] headers : headerList) {
                    String headerName = headers[0];
                    String headerStyleName = headers[1];
                    if (StringUtils.isNotEmpty(headerStyleName)) {
                        cellStyle = (CellStyle) ReflectionUtil.invokeGetter(excelStyle, headerStyleName);
                    }
                    WorkSheetOperation.addCell(row, column++, headerName, cellStyle);
                }
            }
        }
    }

    /**
     * 创建数据行
     * @param list
     * @param <E>
     * @return
     * @throws Exception
     */
    public <E> NewExportExcel setDataList(List<E> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            E e = list.get(i);

            Row row = WorkSheetOperation.addRow(sheet);
            int column = 0;
            for (int j = 0; j < annotationFieldList.size(); j++){
                Object[] o = annotationFieldList.get(j);
                ExcelField ef = (ExcelField) o[1];
                if (ef == null) {
                    continue;
                }

                Field f = (Field) o[0];
                String fieldName = f.getName();

                // 格式化值
                Object val = ReflectionUtil.invokeGetter(e, fieldName);
                valueFormat(ef, val);

                // 列样式
                CellStyle cellStyle = null;
                if (cellStyles != null && !cellStyles.isEmpty()) {
                    String cellStyleName = cellStyles.get(column);
                    if (StringUtils.isNotEmpty(cellStyleName)) {
                        cellStyle = (CellStyle) ReflectionUtil.invokeGetter(excelStyle, cellStyleName);
                    }
                }

                // 添加列
                Cell cell = WorkSheetOperation.addCell(row, column++, val, cellStyle);

                // 设置列公式
                setFormula(ef, row, cell);

                // 设置列样式
                setStyle(ef, cell, cellStyle);
            }
        }
        return this;
    }

    /**
     * 格式化值
     * @param ef
     * @param val
     */
    private void valueFormat(ExcelField ef, Object val) {
        
    }

    /**
     * 设置列公式
     * @param ef
     * @param row
     * @param cell
     */
    private void setFormula(ExcelField ef, Row row, Cell cell) {
        String formula = ef.formula();
        if (StringUtils.isEmpty(formula)) {
            return;
        }

        String symbol = "\\*|\\+|-|/|%|\\(|\\)";
        String[] fieldNames = formula.split(symbol);
        if (fieldNames != null && fieldNames.length > 0) {
            for (String fieldName : fieldNames) {
                if (StringUtils.isEmpty(fieldName)) {
                    continue;
                }
                Integer sort = fieldMap.get(fieldName);
                if (sort != null) {
                    String indexLabel = ExcelColumnUtil.getLabel(sort) + (row.getRowNum()+1);
                    formula = formula.replace(fieldName, indexLabel);
                }
            }
            WorkSheetOperation.setFormula(cell, formula);
        }
    }

    /**
     * 设置列样式
     * @param ef
     * @param cell
     * @param cellStyle
     */
    @SuppressWarnings("rawtypes")
	private void setStyle(ExcelField ef, Cell cell, CellStyle cellStyle) {
        // 日期格式化
        Class fieldType = ef.fieldType();
        if (fieldType != null && fieldType == Date.class) {
            if (cellStyle == null) {
                cellStyle = workbook.createCellStyle();
            }
            DataFormat cellStyleDataFormat = workbook.createDataFormat();
            cellStyle.setDataFormat(cellStyleDataFormat.getFormat("yyyy/M/d"));

            WorkSheetOperation.setCellStyle(cell, cellStyle);
        }
    }

    /**
     * 输出数据流
     * @param os 输出数据流
     */
    public NewExportExcel write(OutputStream os) throws IOException {
        workbook.write(os);
        return this;
    }

    /**
     * 输出到文件
     */
    public NewExportExcel writeFile() throws FileNotFoundException, IOException{
        String fileName = "";
        FileOutputStream os = new FileOutputStream(fileName);
        write(os);
        return this;
    }

    /**
     * 输出到客户端
     */
    public NewExportExcel write(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+ EncodeUtil.urlEncode(fileName));
        write(response.getOutputStream());
        return this;
    }

    public NewExportExcel dispose() {
        if (workbook instanceof SXSSFWorkbook) {
            SXSSFWorkbook wb = (SXSSFWorkbook) workbook;
            wb.dispose();
        }
        return this;
    }

}
