package me.zhengjie.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: ReadWord
 * @date: 2021/11/25
 * @author: 王瑾
 */
public class ReadWordCopy {

    /**
     * 段落信息
     */
    @Data
    static class Paragraph {
        // 段落位置
        int index;
        // 文本级别
        String style;
        // 段落文本
        String text;
        // 段落内信息
        List<Run> runs;
    }
    /**
     * 表格信息
     */
    @Data
    static class Table {
        // 表格位置
        int index;
        //表格文本
        String text;
        //表格数据量
        int dataSize;
        // 表格内行信息
        List<TableRow> rows;
    }
    /**
     * 文本信息
     */
    @Data
    static class Run {
        // 文本位置
        int index;
        // 文本内容
        String text;
    }
    /**
     * 表格内行信息
     */
    @Data
    static class TableRow {
        // 位置
        int index;
        // 列集合
        List<TableCell> cells;
    }
    /**
     * 表格内列信息
     */
    @Data
    static class TableCell {
        // 位置
        int index;
        // 列内文本
        List<Paragraph> paragraphs;
    }

    /**
     * 段落集合信息
     */
    private static List<Paragraph> paragraphList = new ArrayList<>();
    /**
     * 表格集合信息
     */
    private static List<Table> tableList = new ArrayList<>();

    public static void main(String[] args) {
        InputStream inputStream = null;
        //String templatePath = RadeWordCopy.class.getResource("/templates/test.docx").getPath();
        //File templatePath = new File("D:\\Desktop\\poc\\CCCCFK-iOS应用安全测评报告（2021-08-23-14-27-41)_梆梆.docx");
        File templatePath = new File("D:\\Desktop\\poc\\光大发卡_4.4.40_iOS应用安全检测报告_监管者_20210823222852_爱加密.docx");
        try {
            inputStream = new FileInputStream(templatePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 读取word文件,转文件流
        byte[] a = exportWord(inputStream);
        byteToFile(a, "D:\\Desktop\\个人\\test", "zzz.docx");
        // 调用任务管理器打开目标文件
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("cmd.exe /c " + "D:\\Desktop\\个人\\test\\zzz.docx");
            InputStream in = process.getInputStream();
            while (in.read() != -1) {
                System.out.println(in.read());
            }
            in.close();
            process.waitFor();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    /**
     * 读取word主流程
     * @return
     */
    public static byte[] exportWord(InputStream inputStream) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        XWPFDocument document = null;
        try {
            //解析docx模板并获取document对象
            document = new XWPFDocument(inputStream);
            // 获取段落信息
            getParagraphText(document);
            // 获取表格信息
            getTableText(document);
            document.write(byteOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return byteOut.toByteArray();
    }

    /**
     * byte成File文件
     *
     * @param path
     * @return String
     * @author
     * @description 将byte 转成File
     */
    public static File byteToFile(byte[] byteStr, String path, String fileName) {
        File savefile = null;
        savefile = new File(path, fileName);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            byte[] bytes = byteStr;
            fos = FileUtils.openOutputStream(savefile);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return savefile;
    }

    /**
     * 获取文档中段落文本方法
     * 将段落信息封装到自建内部类,放到段落集合
     * @param document docx解析对象
     */
    public static void getParagraphText(XWPFDocument document) {
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        List<Paragraph> pList = readParagraph(paragraphs);
        paragraphList.addAll(pList);
        System.out.println("----------------------------------------------------------------");
        System.out.println(JSONObject.toJSONString(paragraphList));
        System.out.println("----------------------------------------------------------------");
    }

    /**
     * 获取表格内对象方法
     * 将表格内数据放入自建内部类集合
     * @param document docx解析对象
     */
    public static void getTableText(XWPFDocument document) {
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            XWPFTable table = tables.get(i);
            Table t = new Table();
            // 处理表格行
            List<TableRow> rows = readRow(table);
            t.setDataSize(table.getNumberOfRows());
            t.setIndex(i);
            t.setText(table.getText());
            t.setRows(rows);
           tableList.add(t);
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println(JSONObject.toJSONString(tableList));
        System.out.println("----------------------------------------------------------------");
    }

    /**
     * 读表格行信息
     * @param table 表格
     * @return 处理后的行
     */
    private static List<TableRow> readRow(XWPFTable table){
        int rowIndex = 0;
        List<TableRow> rows = new ArrayList<>();
        // 处理当前行
        for (XWPFTableRow row : table.getRows()) {
            TableRow row1 = new TableRow();
            List<TableCell> tCells = new ArrayList();
            List<XWPFTableCell> cells = row.getTableCells();
            int cellIndex =0;
            // 处理当前列
            for (XWPFTableCell cell : cells) {
                TableCell cell1 = new TableCell();
                List<Paragraph> pList = readParagraph(cell.getParagraphs());
                cell1.setParagraphs(pList);
                cell1.setIndex(cellIndex);
                tCells.add(cell1);
                cellIndex++;
            }
            row1.setIndex(rowIndex);
            row1.setCells(tCells);
            rows.add(row1);
            rowIndex++;
        }
        return rows;
    }

    /**
     * 读段落信息
     * @param paragraphs 段落集合
     * @return 段落信息
     */
    private static List<Paragraph> readParagraph (List<XWPFParagraph> paragraphs){
        List<Paragraph> pList = new ArrayList();
        int index = 0;
        // 处理当前列里面的小段落.
        for (XWPFParagraph paragraph : paragraphs) {
            Paragraph p = new Paragraph();
            List<Run> runs = new ArrayList<>();
            int runIndex =0;
            for (XWPFRun run : paragraph.getRuns()) {
                Run run1 = new Run();
                run1.setIndex(runIndex);
                run1.setText(run.toString());
                runs.add(run1);
                runIndex++;
            }
            p.setStyle(paragraph.getStyle());
            p.setIndex(index);
            p.setText(paragraph.getText());
            p.setRuns(runs);
            pList.add(p);
            index++;
        }
        return pList;
    }

}
