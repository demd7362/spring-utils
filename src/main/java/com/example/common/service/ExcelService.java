package com.example.common.service;


import com.example.common.annotation.Excel;
import com.example.common.annotation.Processor;
import com.example.common.strategy.NamingStrategies;
import com.example.common.strategy.NamingStrategy;
import com.example.common.strategy.NamingStrategyEnum;
import com.example.common.utils.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExcelService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public static final String ALLOWED_EXTENSION = ".xlsx";


    private List<String> getHeaders(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        List<String> headerRows = new ArrayList<>();

        for (Cell cell : headerRow) {
            headerRows.add(cell.getStringCellValue());
        }
        return headerRows;
    }

    private Pair<Class<?>, List<Field>> findMatchingExcelClass(List<String> headers) {
        loop:
        for (Class<?> excelClass : ReflectionUtils.EXCEL_CLASSES) { // Excel 어노테이션이 달린 모든 클래스를 순회
            // Excel.Header 어노테이션이 달린 필드를 가져와서 order 순서대로 오름차순 정렬
            List<Field> fields = ReflectionUtils.getOwnFields(excelClass)
                    .stream()
                    .filter(field -> field.getAnnotation(Excel.Header.class) != null)
                    .sorted(Comparator.comparingInt(field -> field.getAnnotation(Excel.Header.class).order()))
                    .toList();
            // Header 어노테이션이 안달려있다면 다음으로
            if (fields.isEmpty()) {
                continue;
            }
            for (int i = 0; i < fields.size(); i++) {
                String header = headers.get(i);
                Field field = fields.get(i);
                Excel.Header excelHeader = field.getAnnotation(Excel.Header.class);
                // 엑셀 헤더 이름과 Excel.Header에 명시한 name 값이 일치하지 않으면 다음 클래스로,
                if (excelHeader.order() != i + 1 || !excelHeader.name().equals(header)) {
                    continue loop;
                }
            }
            // 여기까지 온다면 일치하는 클래스니 리턴
            return Pair.of(excelClass, fields);
        }
        throw new RuntimeException("템플릿 형식이 잘못되었습니다.");
    }

    @SneakyThrows
    public <T> List<T> readAsObjects(byte[] bytes) {
        try (InputStream is = new ByteArrayInputStream(bytes);
             BufferedInputStream bis = new BufferedInputStream(is);
             Workbook workbook = new XSSFWorkbook(bis)) {

            List<String> headers = getHeaders(workbook);
            Pair<Class<?>, List<Field>> matchingPair = findMatchingExcelClass(headers);
            Class<T> clazz = (Class<T>) matchingPair.getFirst();
            List<Field> fields = matchingPair.getSecond();
            List<T> results = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                T target = ReflectionUtils.createInstance(clazz);
                for (Cell cell : row) {
                    int colIndex = cell.getColumnIndex();
                    Field targetField = fields.get(colIndex);
                    targetField.setAccessible(true);
                    Object value = getCellValue(cell, targetField.getType());
                    targetField.set(target, value);
                }
                results.add(target);
            }
            return results;
        }
    }

    private Object getCellValue(Cell cell, Class<?> type) {
        switch (cell.getCellType()) {
            case NUMERIC -> {
//                if(type == String.class){ // 변수 타입은 string인데, 엑셀에선 number일 경우
//                    return cell.getNumericCellValue();
//                } else
                if (type == int.class || type == Integer.class) {
                    return (int) cell.getNumericCellValue();
                } else if (type == long.class || type == Long.class) {
                    return (long) cell.getNumericCellValue();
                } else if (type == double.class || type == Double.class) {
                    return cell.getNumericCellValue();
                } else if (type == float.class || type == Float.class) {
                    return (float) cell.getNumericCellValue();
                } else if (type == byte.class || type == Byte.class) {
                    return (byte) cell.getNumericCellValue();
                } else if (type == short.class || type == Short.class) {
                    return (short) cell.getNumericCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            }
            case BOOLEAN -> {
                return cell.getBooleanCellValue();
            }
            case STRING -> {
                return cell.getStringCellValue();
            }
            default -> {
                return null;
            }
        }
    }

    private void setCellValue(Cell cell, Class<?> type, Object value) {
        String $value = value.toString();
        if (Number.class.isAssignableFrom(type) ||
                type == int.class ||
                type == long.class ||
                type == double.class ||
                type == float.class ||
                type == byte.class ||
                type == short.class
        ) {
            cell.setCellValue(Double.parseDouble($value));
        } else if (type == boolean.class || type == Boolean.class) {
            cell.setCellValue(Boolean.parseBoolean($value));
        } else if (type == char.class || type == Character.class) {
            cell.setCellValue($value.charAt(0));
        } else {
            cell.setCellValue($value);
        }
    }



    @SneakyThrows
    public <T> byte[] writeAsBytes(List<T> objects) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("An object list is empty");
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            T target = objects.get(0);
            String sheetName = getSheetName(target.getClass());
            Sheet sheet = workbook.createSheet(sheetName);
            List<Field> fields = ReflectionUtils.getOwnFields(target.getClass());
            createHeaderRow(sheet, target.getClass());
            int rowNum = 1;
            for (T object : objects) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < fields.size(); i++) {
                    Field targetField = fields.get(i);
                    targetField.setAccessible(true);
                    Object value = targetField.get(object);
                    Cell cell = row.createCell(i);
                    setCellValue(cell, targetField.getType(), value);
                }
            }
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private List<CellType> createHeaderRow(Sheet sheet, Class<?> clazz) {
        List<Pair<Field, Excel.Header>> headers = ReflectionUtils.getOwnFields(clazz)
                .stream()
                .filter(field -> field.getAnnotation(Excel.Header.class) != null)
                .map(field -> Pair.of(field, field.getAnnotation(Excel.Header.class)))
                .sorted(Comparator.comparingInt(a -> a.getSecond().order()))
                .toList();
        Row headerRow = sheet.createRow(0);
        List<CellType> cellTypes = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i).getSecond().name());
            if (headers.get(i).getFirst().getType() == String.class) {
                cellTypes.add(CellType.STRING);
            } else {
                cellTypes.add(CellType.NUMERIC);
            }
        }
        return cellTypes;
    }

    private String getSheetName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    private Class<?> getClass(String className) throws ClassNotFoundException {
        String classFullName = ReflectionUtils.CLASS_MAP.get(className);
        if (classFullName != null) {
            className = classFullName;
        }
        return Class.forName(className);
    }

    public byte[] writeTemplateAsBytes(String className) throws ClassNotFoundException {
        Class<?> clazz = getClass(className);
        return writeTemplateAsBytes(clazz);
    }

    @SneakyThrows
    public byte[] writeTemplateAsBytes(Class<?> clazz) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            String sheetName = getSheetName(clazz);
            Sheet sheet = workbook.createSheet(sheetName);

            createHeaderRow(sheet, clazz);
            workbook.write(bos);
            return bos.toByteArray();
        }
    }


    @SneakyThrows
    public void writeTemplateAsFile(String className, String filePath) {
        Class<?> clazz = getClass(className);
        writeTemplateAsFile(clazz, filePath);
    }

    @SneakyThrows
    public void writeTemplateAsFile(Class<?> clazz, String filePath) {
        if (!filePath.endsWith(ALLOWED_EXTENSION)) {
            filePath += ALLOWED_EXTENSION;
        }
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            String sheetName = getSheetName(clazz);
            Sheet sheet = workbook.createSheet(sheetName);
            createHeaderRow(sheet, clazz);
            workbook.write(bos);
        }
    }

    private String sliceExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(ALLOWED_EXTENSION));
    }

    private List<String> convertToColumn(List<Field> fields, String... excludes) {
        Set<String> set = new HashSet<>(Arrays.asList(excludes));
        NamingStrategy namingStrategy = NamingStrategies.getInstance(NamingStrategyEnum.SNAKE_UPPER_CASE);
        List<String> selectColumns = fields.stream()
                .map(field -> namingStrategy.convert(field.getName()))
                .filter(column -> !set.contains(column))
                .toList();
        return selectColumns;
    }

    @SneakyThrows
    @Transactional
    public <T> void insertTemplate(byte[] bytes) {
        List<T> objects = readAsObjects(bytes); // Excel 어노테이션 달린 클래스중에서 엑셀 헤더와 일치하는 클래스 찾아서 list로 반환
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("Empty templates cannot be inserted.");
        }
        T target = objects.get(0);
        Class<?> targetClass = target.getClass();
        // 데이터 후처리 및 밸리데이션
        for (T object : objects) {
            List<Field> processingFields = ReflectionUtils.getAnnotatedFields(targetClass, Excel.Processor.class);
            List<Field> validatingFields = ReflectionUtils.getAnnotatedFields(targetClass, Excel.Header.class);
            for (Field field : validatingFields) {
                Excel.Header excelHeader = field.getAnnotation(Excel.Header.class);
                String regex = excelHeader.regex();
                if (regex.isEmpty()) {
                    continue;
                }
                Pattern pattern = Pattern.compile(regex);
                field.setAccessible(true);
                String value = field.get(object).toString();
                Matcher matcher = pattern.matcher(value);
                if (!matcher.matches()) {
                    String errorMessage = excelHeader.message();
                    if (errorMessage.isEmpty()) {
                        throw new RuntimeException("유효성 검사에 실패했습니다.");
                    } else {
                        throw new RuntimeException(excelHeader.message() + " [" + value + "]");
                    }
                }
            }
            for (Field field : processingFields) {
                Excel.Processor processorAnnotation = field.getAnnotation(Excel.Processor.class);
                Processor processor = (Processor) ReflectionUtils.createInstance(processorAnnotation.value());
                processor.process(object, field);
            }
        }
        List<Field> uniqueFields = ReflectionUtils.getAnnotatedFields(targetClass, Excel.Unique.class);
        List<String> uniqueColumns = convertToColumn(uniqueFields);

        Excel excelAnnotation = targetClass.getAnnotation(Excel.class);
        String tableName = excelAnnotation.table();
        List<Field> mergeFields = ReflectionUtils.getAllFields(target.getClass());
        List<String> mergeColumns = convertToColumn(mergeFields);
        List<String> mergeParams = mergeFields.stream().map(field -> ":" + field.getName()).toList();

        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder
                .append("insert into ")
                .append(tableName)
                .append("(")
                .append(String.join(",", mergeColumns))
                .append(") ")
                .append("values (")
                .append(String.join(",", mergeParams))
                .append(")");

        String insertQuery = insertBuilder.toString();
        log.info("insert query [{}]", insertQuery);

        StringBuilder selectBuilder = new StringBuilder();
        if (!uniqueColumns.isEmpty()) {
            selectBuilder.append("select count(*) from ")
                    .append(tableName)
                    .append(" where ");
            for (int i = 0; i < uniqueColumns.size(); i++) {
                selectBuilder.append(uniqueColumns.get(i))
                        .append(" = :")
                        .append(uniqueFields.get(i).getName());
                if (i < uniqueColumns.size() - 1) {
                    selectBuilder.append(" and ");
                }
            }
        }
        String selectQuery = selectBuilder.toString();
        log.info("select query [{}]", selectQuery);

        for (T object : objects) {
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(object);
            if (selectQuery.isEmpty()) { // 셀렉트 쿼리로 먼저 db에서 찾고 insert, selectQuery 값이 비어있다면 unique 컬럼 없으니 바로 insert
                int result = jdbcTemplate.update(insertQuery, namedParameters);
                log.info("insert result [{}]", result);
            } else {
                int count = jdbcTemplate.queryForObject(selectQuery, namedParameters, Integer.class);
                if (count == 0) {
                    int result = jdbcTemplate.update(insertQuery, namedParameters);
                    log.info("insert result [{}]", result);
                }
            }
        }
    }


}
