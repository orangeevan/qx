package com.haipaite.common.resource.reader;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.utility.ReflectionUtility;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;


@Component
public class ExcelReader implements ResourceReader {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);


    private static final String ROW_SERVER = "SERVER";


    private static final String ROW_END = "END";


    private static final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
    @Autowired
    private ConversionService conversionService;

    public String getFormat() {
        return "excel";
    }


    private static class FieldInfo {
        public final int index;


        public final Field field;


        public boolean isId = false;


        public FieldInfo(int index, Field field) {
            ReflectionUtility.makeAccessible(field);
            this.index = index;
            this.field = field;
        }
    }


    public <E> List<E> read(InputStream input, Class<E> clz) {
        Workbook wb = getWorkbook(input, clz);
        Sheet[] sheets = getSheets(wb, clz);


        ArrayList<E> result = new ArrayList<>();
        for (Sheet sheet : sheets) {
            Collection<FieldInfo> infos = getCellInfos(sheet, clz);
            boolean start = false;
            for (Row row : sheet) {

                if (!start) {
                    Cell cell1 = row.getCell(0);
                    if (cell1 == null) {
                        continue;
                    }
                    String str = getCellContent(cell1);
                    if (str == null) {
                        continue;
                    }
                    if (str.equals("SERVER")) {
                        start = true;
                    }

                    continue;
                }
                E instance = newInstance(clz);
                boolean sheetOver = false;
                for (FieldInfo info : infos) {
                    Cell cell1 = row.getCell(info.index);
                    if (cell1 == null) {
                        if (info.isId) {
                            sheetOver = true;
                            break;
                        }
                        continue;
                    }
                    String str = getCellContent(cell1);
                    if (StringUtils.isEmpty(str)) {

                        if (info.isId) {
                            sheetOver = true;
                            break;
                        }
                        continue;
                    }
                    inject(instance, info.field, str);
                }
                if (sheetOver) {
                    break;
                }
                result.add(instance);


                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }
                String content = getCellContent(cell);
                if (content == null) ;
            }
        }


        return result;
    }


    private String getCellContent(Cell cell) {
        if (cell.getCellType() != 1) {
            cell.setCellType(1);
        }
        return cell.getStringCellValue();
    }


    private void inject(Object instance, Field field, String content) {
        try {
            TypeDescriptor targetType = new TypeDescriptor(field);
            Object value = this.conversionService.convert(content, sourceType, targetType);
            field.set(instance, value);
        } catch (ConverterNotFoundException e) {
            FormattingTuple message = MessageFormatter.format("静态资源[{}]属性[{}]的转换器不存在", instance
                    .getClass().getSimpleName(), field.getName());
            logger.error(message.getMessage(), (Throwable) e);
            throw new IllegalStateException(message.getMessage(), e);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("属性[{}]注入失败", field);
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage(), e);
        }
    }


    private <E> E newInstance(Class<E> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("资源[{}]无法实例化", clz);
            logger.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
    }


    private Collection<FieldInfo> getCellInfos(Sheet sheet, Class<?> clz) {
        Row fieldRow = getFieldRow(sheet);
        if (fieldRow == null) {
            FormattingTuple message = MessageFormatter.format("无法获取资源[{}]的EXCEL文件的属性控制列", clz);
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage());
        }


        List<FieldInfo> result = new ArrayList<>();
        for (int i = 1; i < fieldRow.getLastCellNum(); i++) {
            Cell cell = fieldRow.getCell(i);
            if (cell != null) {


                String name = getCellContent(cell);
                if (!StringUtils.isBlank(name))

                    try {


                        Field field = null;
                        try {
                            field = clz.getDeclaredField(name);
                        } catch (Exception e) {
                            field = clz.getField(name);
                        }
                        FieldInfo info = new FieldInfo(i, field);
                        if (field.isAnnotationPresent((Class) Id.class)) {
                            info.isId = true;
                        }
                        result.add(info);
                    } catch (Exception e) {
                        FormattingTuple message = MessageFormatter.format("资源类[{}]的声明属性[{}]无法获取", clz, name);
                        logger.error(message.getMessage());
                        throw new IllegalStateException(message.getMessage(), e);
                    }
            }
        }
        return result;
    }


    private Row getFieldRow(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            String content = getCellContent(cell);
            if (content != null && content.equals("SERVER")) {
                return row;
            }
        }
        return null;
    }


    private Sheet[] getSheets(Workbook wb, Class<?> clz) {
        try {
            List<Sheet> result = new ArrayList<>();
            String name = clz.getSimpleName();

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet1 = wb.getSheetAt(i);
                if (sheet1.getLastRowNum() > 0) {


                    Row row = sheet1.getRow(0);
                    if (row.getLastCellNum() > 0) {


                        Cell cell = row.getCell(0);
                        if (cell != null) {
                            if (cell.getCellType() != 1) {
                                cell.setCellType(1);
                            }
                            String text = cell.getStringCellValue();
                            if (name.equals(text))
                                result.add(sheet1);
                        }
                    }
                }
            }
            if (result.size() > 0) {
                return result.<Sheet>toArray(new Sheet[0]);
            }


            Sheet sheet = wb.getSheet(name);
            if (sheet != null) {
                return new Sheet[]{sheet};
            }
            return new Sheet[]{wb.getSheetAt(0)};
        } catch (Exception e) {
            throw new IllegalArgumentException("无法获取资源类[" + clz.getSimpleName() + "]对应的Excel数据表", e);
        }
    }


    private Workbook getWorkbook(InputStream input, Class clz) {
        try {
            return WorkbookFactory.create(input);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("静态资源[" + clz.getSimpleName() + "]异常,无效的文件格式", e);
        } catch (IOException e) {
            throw new RuntimeException("静态资源[" + clz.getSimpleName() + "]异常,无法读取文件", e);
        }
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\reader\ExcelReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */