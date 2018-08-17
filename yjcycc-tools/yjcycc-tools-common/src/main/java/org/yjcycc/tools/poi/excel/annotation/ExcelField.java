package org.yjcycc.tools.poi.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel注解定义

 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

	/**
	 * 导出字段名（默认调用当前字段的“get”方法，如指定导出字段为对象，请填写“对象名.对象属性”，例：“area.name”、“office.name”）
	 */
	String value() default "";
	
	/**
	 * 导出字段标题（需要添加批注请用“||”分隔，标题||批注，仅对导出模板有效）
	 */
	String title() default "";

	/**
	 * 导出字段头部（多行头部请用“||”分隔，头部1||头部2，仅对导出模板有效）
	 */
	String[] headers();
	
	/**
	 * 字段类型（0：导出导入；1：仅导出；2：仅导入）
	 */
	int type() default 0;

	/**
	 * 导出字段对齐方式（0：自动；1：靠左；2：居中；3：靠右）
	 */
	int align() default 0;
	
	/**
	 * 导出字段字段排序（升序）, 不可重复
	 */
	int sort() default 0;

	/**
	 * 如果是字典类型，请设置字典的type值
	 */
	String dictType() default "";
	
	/**
	 * 反射类型
	 */
	Class<?> fieldType() default Class.class;
	
	/**
	 * 字段归属组（根据分组导出导入）
	 */
	int[] groups() default {};
	
	/**
	 * 日期格式
	 */
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";

	/**
	 * 100为1个px
	 * @return
     */
	int colunmWidth() default 0;

	boolean isSkipZero() default false;
	
	String suffix() default "";

	/**
	 * 标题样式
	 * @return
	 */
	String titleStyle() default "";

	/**
	 * 头部样式
	 * @return
	 */
	String[] headerStyles() default {};

	/**
	 * 列样式
	 * @return
	 */
	String cellStyle() default "";

	/**
	 * 列公式
	 * @return
	 */
	String formula() default "";

}
