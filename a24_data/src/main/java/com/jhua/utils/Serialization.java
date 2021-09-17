package com.jhua.utils;

import joinery.DataFrame;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import joinery.DataFrame;
import joinery.DataFrame.NumberDefault;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.prefs.CsvPreference.Builder;

public class Serialization {
    private static final String EMPTY_DF_STRING = "[empty data frame]";
    private static final String ELLIPSES = "...";
    private static final String NEWLINE = "\n";
    private static final String DELIMITER = "\t";
    private static final Object INDEX_KEY = new Object();
    private static final int MAX_COLUMN_WIDTH = 20;

    public Serialization() {
    }

    public static String toString(DataFrame<?> df, int limit) {
        int len = df.length();
        if (len == 0) {
            return "[empty data frame]";
        } else {
            StringBuilder sb = new StringBuilder();
            Map<Object, Integer> width = new HashMap();
            List<Class<?>> types = df.types();
            List<Object> columns = new ArrayList(df.columns());
            width.put(INDEX_KEY, 0);
            Iterator names = df.index().iterator();

            Object column;
            while(names.hasNext()) {
                column = names.next();
                Class<? extends Object> rowClass = column == null ? null : column.getClass();
                width.put(INDEX_KEY, clamp((Integer)width.get(INDEX_KEY), 20, fmt(rowClass, column).length()));
            }

            int c;
            int w;
            for(c = 0; c < columns.size(); ++c) {
                column = columns.get(c);
                width.put(column, String.valueOf(column).length());

                for(w = 0; w < df.length(); ++w) {
                    width.put(column, clamp((Integer)width.get(column), 20, fmt((Class)types.get(c), df.get(w, c)).length()));
                }
            }

            sb.append(lpad("", (Integer)width.get(INDEX_KEY)));

            for(c = 0; c < columns.size(); ++c) {
                sb.append("\t");
                column = columns.get(c);
                sb.append(lpad(column, (Integer)width.get(column)));
            }

            sb.append("\n");
            names = df.index().iterator();

            for(int r = 0; r < len; ++r) {
                w = (Integer)width.get(INDEX_KEY);
                Object row = names.hasNext() ? names.next() : r;
                Class<? extends Object> rowClass = row == null ? null : row.getClass();
                sb.append(truncate(lpad(fmt(rowClass, row), w), w));

                for(int k = 0; k < df.size(); ++k) {
                    sb.append("\t");
                    Class<?> cls = (Class)types.get(k);
                    w = (Integer)width.get(columns.get(k));
                    if (Number.class.isAssignableFrom(cls)) {
                        sb.append(lpad(fmt(cls, df.get(r, k)), w));
                    } else {
                        sb.append(truncate(rpad(fmt(cls, df.get(r, k)), w), w));
                    }
                }

                sb.append("\n");
                if (limit - 3 < r && r < limit << 1 && r < len - 4) {
                    sb.append("\n").append("...").append(" ").append(len - limit).append(" rows skipped ").append("...").append("\n").append("\n");

                    for(; r < len - 2; ++r) {
                        if (names.hasNext()) {
                            names.next();
                        }
                    }
                }
            }

            return sb.toString();
        }
    }

    private static final int clamp(int lower, int upper, int value) {
        return Math.max(lower, Math.min(upper, value));
    }

    private static final String lpad(Object o, int w) {
        StringBuilder sb = new StringBuilder();
        String value = String.valueOf(o);

        for(int i = value.length(); i < w; ++i) {
            sb.append(' ');
        }

        sb.append(value);
        return sb.toString();
    }

    private static final String rpad(Object o, int w) {
        StringBuilder sb = new StringBuilder();
        String value = String.valueOf(o);
        sb.append(value);

        for(int i = value.length(); i < w; ++i) {
            sb.append(' ');
        }

        return sb.toString();
    }

    private static final String truncate(Object o, int w) {
        String value = String.valueOf(o);
        return value.length() - "...".length() > w ? value.substring(0, w - "...".length()) + "..." : value;
    }

    private static final String fmt(Class<?> cls, Object o) {
        if (cls == null) {
            return "null";
        } else {
            String s;
            if (o instanceof Number) {
                if (!Short.class.equals(cls) && !Integer.class.equals(cls) && !Long.class.equals(cls) && !BigInteger.class.equals(cls)) {
                    s = String.format("% .8f", ((Number)Number.class.cast(o)).doubleValue());
                } else {
                    s = String.format("% d", ((Number)Number.class.cast(o)).longValue());
                }
            } else if (o instanceof Date) {
                Date dt = (Date)Date.class.cast(o);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                DateFormat fmt = new SimpleDateFormat(cal.get(11) == 0 && cal.get(12) == 0 && cal.get(13) == 0 ? "yyyy-MM-dd" : "yyyy-MM-dd'T'HH:mm:ssXXX");
                s = fmt.format(dt);
            } else {
                s = o != null ? String.valueOf(o) : "";
            }

            return s;
        }
    }

    public static DataFrame<Object> readCsv(String file) throws IOException {
        return readCsv((InputStream)(file.contains("://") ? (new URL(file)).openStream() : new FileInputStream(file)), ",", DataFrame.NumberDefault.LONG_DEFAULT, (String)null);
    }

    public static DataFrame<Object> readCsv(String file, String separator, DataFrame.NumberDefault numDefault) throws IOException {
        return readCsv((InputStream)(file.contains("://") ? (new URL(file)).openStream() : new FileInputStream(file)), separator, numDefault, (String)null);
    }

    public static DataFrame<Object> readCsv(String file, String separator, DataFrame.NumberDefault numDefault, String naString) throws IOException {
        return readCsv((InputStream)(file.contains("://") ? (new URL(file)).openStream() : new FileInputStream(file)), separator, numDefault, naString);
    }

    public static DataFrame<Object> readCsv(String file, String separator, DataFrame.NumberDefault numDefault, String naString, boolean hasHeader) throws IOException {
        return readCsv((InputStream)(file.contains("://") ? (new URL(file)).openStream() : new FileInputStream(file)), separator, numDefault, naString, hasHeader);
    }

    public static DataFrame<Object> readCsv(InputStream input) throws IOException {
        return readCsv((InputStream)input, ",", DataFrame.NumberDefault.LONG_DEFAULT, (String)null);
    }

    public static DataFrame<Object> readCsv(InputStream input, String separator, DataFrame.NumberDefault numDefault, String naString) throws IOException {
        return readCsv(input, separator, numDefault, naString, true);
    }

    public static DataFrame<Object> readCsv(InputStream input, String separator, DataFrame.NumberDefault numDefault, String naString, boolean hasHeader) throws IOException {
        byte var7 = -1;
        switch(separator.hashCode()) {
            case 44:
                if (separator.equals(",")) {
                    var7 = 1;
                }
                break;
            case 59:
                if (separator.equals(";")) {
                    var7 = 2;
                }
                break;
            case 124:
                if (separator.equals("|")) {
                    var7 = 3;
                }
                break;
            case 2968:
                if (separator.equals("\\t")) {
                    var7 = 0;
                }
        }

        CsvPreference csvPreference;
        switch(var7) {
            case 0:
                csvPreference = CsvPreference.TAB_PREFERENCE;
                break;
            case 1:
                csvPreference = CsvPreference.STANDARD_PREFERENCE;
                break;
            case 2:
                csvPreference = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
                break;
            case 3:
                csvPreference = (new CsvPreference.Builder('"', 124, "\n")).build();
                break;
            default:
                throw new IllegalArgumentException("Separator: " + separator + " is not currently supported");
        }

        CsvListReader reader = new CsvListReader(new InputStreamReader(input), csvPreference);
        Throwable var23 = null;

        try {
            DataFrame df;
            CellProcessor[] procs;
            if (hasHeader) {
                List<String> header = Arrays.asList(reader.getHeader(true));
                procs = new CellProcessor[header.size()];
                df = new DataFrame(header);
            } else {
                reader.read();
                List<String> header = new ArrayList();

                for(int i = 0; i < reader.length(); ++i) {
                    header.add("V" + i);
                }

                procs = new CellProcessor[header.size()];
                df = new DataFrame(header);
                df.append(reader.executeProcessors(procs));
            }

            for(List row = reader.read(procs); row != null; row = reader.read(procs)) {
                df.append(new ArrayList(row));
            }

            DataFrame var25 = df.convert(numDefault, naString);
            return var25;
        } catch (Throwable var20) {
            var23 = var20;
            throw var20;
        } finally {
            if (reader != null) {
                if (var23 != null) {
                    try {
                        reader.close();
                    } catch (Throwable var19) {
                        var23.addSuppressed(var19);
                    }
                } else {
                    reader.close();
                }
            }

        }
    }

    public static <V> void writeCsv(DataFrame<V> df, String output) throws IOException {
        writeCsv(df, (OutputStream)(new FileOutputStream(output)));
    }

    public static <V> void writeCsv(DataFrame<V> df, OutputStream output) throws IOException {
        CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(output), CsvPreference.STANDARD_PREFERENCE);
        Throwable var3 = null;

        try {
            String[] header = new String[df.size()];
            Iterator<Object> it = df.columns().iterator();

            for(int c = 0; c < df.size(); ++c) {
                header[c] = String.valueOf(it.hasNext() ? it.next() : c);
            }

            writer.writeHeader(header);
            CellProcessor[] procs = new CellProcessor[df.size()];
            List<Class<?>> types = df.types();

            for(int c = 0; c < df.size(); ++c) {
                Class<?> cls = (Class)types.get(c);
                if (Date.class.isAssignableFrom(cls)) {
                    procs[c] = new ConvertNullTo("", new FmtDate("yyyy-MM-dd'T'HH:mm:ssXXX"));
                } else {
                    procs[c] = new ConvertNullTo("");
                }
            }

            ListIterator var20 = df.iterator();

            while(var20.hasNext()) {
                List<V> row = (List)var20.next();
                writer.write(row, procs);
            }
        } catch (Throwable var17) {
            var3 = var17;
            throw var17;
        } finally {
            if (writer != null) {
                if (var3 != null) {
                    try {
                        writer.close();
                    } catch (Throwable var16) {
                        var3.addSuppressed(var16);
                    }
                } else {
                    writer.close();
                }
            }

        }

    }

    public static DataFrame<Object> readXls(String file) throws IOException {
        return readXls((InputStream)(file.contains("://") ? (new URL(file)).openStream() : new FileInputStream(file)));
    }

    public static DataFrame<Object> readXls(InputStream input) throws IOException {
        Workbook wb = new HSSFWorkbook(input);
        Sheet sheet = wb.getSheetAt(0);
        List<Object> columns = new ArrayList();
        List<List<Object>> data = new ArrayList();
        Iterator var5 = sheet.iterator();

        while(true) {
            while(var5.hasNext()) {
                Row row = (Row)var5.next();
                if (row.getRowNum() == 0) {
                    Iterator var12 = row.iterator();

                    while(var12.hasNext()) {
                        Cell cell = (Cell)var12.next();
                        columns.add(readCell(cell));
                    }
                } else {
                    List<Object> values = new ArrayList();
                    Iterator var8 = row.iterator();

                    while(var8.hasNext()) {
                        Cell cell = (Cell)var8.next();
                        values.add(readCell(cell));
                    }

                    data.add(values);
                }
            }

            DataFrame<Object> df = new DataFrame(columns);
            Iterator var11 = data.iterator();

            while(var11.hasNext()) {
                List<Object> row = (List)var11.next();
                df.append(row);
            }

            return df.convert();
        }
    }

    public static <V> void writeXls(DataFrame<V> df, String output) throws IOException {
        writeXls(df, (OutputStream)(new FileOutputStream(output)));
    }

    public static <V> void writeXls(DataFrame<V> df, OutputStream output) throws IOException {
        Workbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);
        Iterator<Object> it = df.columns().iterator();

        int r;
        for(r = 0; r < df.size(); ++r) {
            Cell cell = row.createCell(r);
            writeCell(cell, it.hasNext() ? it.next() : r);
        }

        // 重构
        CellStyle date_style = wb.createCellStyle();
        date_style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        for(r = 0; r < df.length(); ++r) {
            row = sheet.createRow(r + 1);

            for(int c = 0; c < df.size(); ++c) {
                Cell cell = row.createCell(c);
//                writeCell(cell, df.get(r, c));

                Object value = df.get(r, c);
                if (value instanceof Number) {
                    cell.setCellType(0);
                    cell.setCellValue(((Number)Number.class.cast(value)).doubleValue());
                } else if (value instanceof Date) {
                    cell.setCellStyle(date_style);
                    cell.setCellType(0);
                    cell.setCellValue((Date)Date.class.cast(value));
                } else if (value instanceof Boolean) {
                    cell.setCellType(4);
                } else {
                    cell.setCellType(1);
                    cell.setCellValue(value != null ? String.valueOf(value) : "");
                }
            }
        }

        wb.write(output);
        output.close();
    }

    private static final Object readCell(Cell cell) {
        switch(cell.getCellType()) {
            case 0:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return DateUtil.getJavaDate(cell.getNumericCellValue());
                }

                return cell.getNumericCellValue();
            case 4:
                return cell.getBooleanCellValue();
            default:
                return cell.getStringCellValue();
        }
    }

    private static final void writeCell(Cell cell, Object value) {
        if (value instanceof Number) {
            cell.setCellType(0);
            cell.setCellValue(((Number)Number.class.cast(value)).doubleValue());
        } else if (value instanceof Date) {
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
            cell.setCellStyle(style);
            cell.setCellType(0);
            cell.setCellValue((Date)Date.class.cast(value));
        } else if (value instanceof Boolean) {
            cell.setCellType(4);
        } else {
            cell.setCellType(1);
            cell.setCellValue(value != null ? String.valueOf(value) : "");
        }

    }

    public static DataFrame<Object> readSql(ResultSet rs) throws SQLException {
        try {
            ResultSetMetaData md = rs.getMetaData();
            List<String> columns = new ArrayList();

            for(int i = 1; i <= md.getColumnCount(); ++i) {
                columns.add(md.getColumnLabel(i));
            }

            DataFrame<Object> df = new DataFrame(columns);
            ArrayList row = new ArrayList(columns.size());

            while(rs.next()) {
                Iterator var5 = columns.iterator();

                while(var5.hasNext()) {
                    String c = (String)var5.next();
                    row.add(rs.getString(c));
                }

                df.append(row);
                row.clear();
            }

            DataFrame var11 = df;
            return var11;
        } finally {
            rs.close();
        }
    }

    public static <V> void writeSql(DataFrame<V> df, PreparedStatement stmt) throws SQLException {
        try {
            ParameterMetaData md = stmt.getParameterMetaData();
            List<Integer> columns = new ArrayList();

            int r;
            for(r = 1; r <= md.getParameterCount(); ++r) {
                columns.add(md.getParameterType(r));
            }

            for(r = 0; r < df.length(); ++r) {
                for(int c = 1; c <= df.size(); ++c) {
                    stmt.setObject(c, df.get(r, c - 1));
                }

                stmt.addBatch();
            }

            stmt.executeBatch();
        } finally {
            stmt.close();
        }
    }
}
