package me.zhengjie.util;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import java.io.*;
import java.util.*;

/**
 * @description: RadeWord
 * @date: 2021/3/31
 * @author: 王瑾
 */
public class RadeWord {
    /**
     * 段落信息
     */
    @Data
    public static class Paragraph {
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
    public static class Table {
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
    public static class Run {
        // 文本位置
        int index;
        // 文本内容
        String text;
    }
    /**
     * 表格内行信息
     */
    @Data
    public  static class TableRow {
        // 位置
        int index;
        // 列集合
        List<TableCell> cells;
    }
    /**
     * 表格内列信息
     */
    @Data
    public static class TableCell {
        // 位置
        int index;
        // 列内文本
        List<Paragraph> paragraphs;
    }

    /**
     * 段落集合信息
     */
    public static List<ReadWordCopy.Paragraph> paragraphList = new ArrayList<>();
    /**
     * 表格集合信息
     */
    public static List<ReadWordCopy.Table> tableList = new ArrayList<>();
    public static void main(String[] args) {
        // 模拟点数据
        Map<String, String> paragraphMap = new HashMap<>();
        Map<String, String> tableMap = new HashMap<>();
        Map<String, String> tableMap1 = new HashMap<>();
        Map<String,Map<String, String>> tableMapMap = new HashMap<>();
        List<String[]> familyList = new ArrayList<>();
        List<String[]> familyList1 = new ArrayList<>();
        Map<String,List<String[]>> familyListMap = new HashMap<>();
        LinkedHashMap<String,List<String[]>> activeListMap = new LinkedHashMap<>();
        paragraphMap.put("number", "10000");
        paragraphMap.put("date", "2020-03-25");
        tableMap.put("name", "赵云");
        tableMap.put("sexual", "男");
        tableMap.put("birthday", "2020-01-01");
        tableMap.put("identify", "123456789");
        tableMap.put("phone", "18377776666");
        tableMap.put("address", "王者荣耀");
        tableMap.put("domicile", "中国-腾讯");
        tableMap.put("QQ", "是");
        tableMap.put("chat", "是");
        tableMap.put("blog", "是");
        tableMap1.put("name", "赵云1");
        tableMap1.put("sexual", "男1");
        tableMap1.put("birthday", "2020-01-11");
        tableMap1.put("identify", "123456781");
        tableMap1.put("phone", "18377776661");
        tableMap1.put("address", "王者荣耀1");
        tableMap1.put("domicile", "中国-腾讯1");
        tableMap1.put("QQ", "是1");
        tableMap1.put("chat", "是1");
        tableMap1.put("blog", "是1");
        tableMapMap.put("tb0",tableMap);
        tableMapMap.put("tb1",tableMap1);
        tableMapMap.put("tb2",tableMap);
        tableMapMap.put("tb3",tableMap1);
        familyList.add(new String[]{"露娜", "女", "野友", "666", "6660"});
        familyList.add(new String[]{"鲁班", "男", "射友", "222", "2220"});
        familyList.add(new String[]{"程咬金", "男", "肉友", "999", "9990"});
        familyList.add(new String[]{"太乙真人", "男", "辅友", "111", "1110"});
        familyList.add(new String[]{"貂蝉", "女", "法友", "888", "8880"});
        familyList1.add(new String[]{"露娜1", "女", "野友", "666", "6660"});
        familyList1.add(new String[]{"鲁班1", "男", "射友", "222", "2220"});
        familyList1.add(new String[]{"程咬金1", "男", "肉友", "999", "9990"});
        familyList1.add(new String[]{"太乙真人1", "男", "辅友", "111", "1110"});
        familyList1.add(new String[]{"貂蝉1", "女", "法友", "888", "8880"});
        familyListMap.put("tl0",familyList);
        familyListMap.put("tl1",familyList1);
        familyListMap.put("tl2",familyList);
        familyListMap.put("tl3",familyList1);
        activeListMap.put("tl01",familyList1);
        activeListMap.put("tl00",familyList);
        writeDocument("D:\\Desktop\\poc\\demo.docx",paragraphMap, tableMapMap, familyListMap,activeListMap);
    }
    public static void writeDocument(String templatePath,Map<String,String> paragraphMap,Map<String,Map<String, String>> tableMapMap,Map<String,List<String[]>> familyListMap,LinkedHashMap<String,List<String[]>> activeListMap){
        //String templatePath = RadeWord.class.getResource("/templates/test.docx").getPath();
        File file = new File(templatePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] a = exportWord(inputStream,paragraphMap, tableMapMap, familyListMap,activeListMap);
        InputStream sbs = new ByteArrayInputStream(a);
        a = exportWord(sbs,null, null, null,activeListMap);
        byteToFile(a, "D:\\Desktop\\个人\\test", "zzz.docx");
        Runtime run = Runtime.getRuntime();
        try {
//			Process process = run.exec("cmd.exe /k start " + cmdStr);
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
     *
     * @param paragraphMap word中文本
     * @param tableMapMap 表格中数据(非循环)
     * @param familyListMap 表格中数据列表
     * @return
     */
    public static byte[] exportWord(InputStream inputStream, Map<String, String> paragraphMap,Map<String,Map<String, String>> tableMapMap,Map<String,List<String[]>> familyListMap,LinkedHashMap<String,List<String[]>> activeListMap) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        XWPFDocument document = null;
        try {
            //解析docx模板并获取document对象
            document = new XWPFDocument(inputStream);
            changeParagraphText(document, paragraphMap);
            changeTableText(document, tableMapMap);

            XWPFParagraph p2 = document.insertNewParagraph(document.getParagraphs().get(10).getCTP().newCursor());
            p2.setStyle("2");
            XWPFRun r2 = p2.createRun(); // 创建段落文本
            // 设置文本
            r2.setText("999999999");
            r2.setFontSize(14);
            r2.setBold(true);
            XmlCursor cursor = document.getParagraphs().get(11).getCTP().newCursor();
            XWPFTable newTable = document.insertNewTbl(cursor);
            document.setTable(3, document.getTables().get(2));

            //copyHeaderInsertText(document, familyListMap, 7);
            // *****************动态追加表格开始***************
            //copyActiveTable(document,familyListMap!=null?true:false,activeListMap,7);
            // *****************动态追加表格结束***************
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
     *
     * @param document word文件
     * @param flag true:创建表格 ,false:放入数据
     * @param activeListMap 传入数据
     * @param headerIndex
     */
    private static void copyActiveTable(XWPFDocument document, Boolean flag, Map<String, List<String[]>> activeListMap, int headerIndex) {
        if (flag) {
            for (String key : activeListMap.keySet()) {
                // 创建段落
                XWPFParagraph p2 = document.insertNewParagraph(document.getParagraphs().get(10).getCTP().newCursor());
                p2.setStyle("2");
                XWPFRun r2 = p2.createRun(); // 创建段落文本
                // 设置文本
                r2.setText(key);
                r2.setFontSize(14);
                r2.setBold(true);
                XmlCursor cursor = document.getParagraphs().get(11).getCTP().newCursor();
                XWPFTable newTable = document.insertNewTbl(cursor);
                document.setTable(3, document.getTables().get(2));
            }
        } else {
            copyHeaderInsertText(document, activeListMap, headerIndex);
        }
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
        // 创建文件夹
        //if (savefile.exists()) {
        //    savefile.delete();
        //    savefile.mkdirs();
        //} else {
        //    savefile.mkdirs();
        //}
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
     * 替换文档中段落文本
     *
     * @param document docx解析对象
     * @param textMap  需要替换的信息集合
     */
    public static void changeParagraphText(XWPFDocument document, Map<String, String> textMap) {
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            //判断此段落时候需要进行替换
            String text = paragraph.getText();
            if (checkText(text)) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    //替换模板原来位置
                    run.setText(changeValue(run.toString(), textMap), 0);
                }
            }
        }
    }

    /**
     * 复制表头 插入行数据，这里样式和表头一样
     *
     * @param document      docx解析对象
     * @param tableListMap 需要插入数据集合
     * @param headerIndex   表头的行索引，从0开始
     */
    public static void copyHeaderInsertText(XWPFDocument document, Map<String,List<String[]>> tableListMap, int headerIndex) {
        if (null == tableListMap) {
            return;
        }
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        List<String[]> tableList = new ArrayList<>();
        for (int h = 0; h < tables.size(); h++) {
            XWPFTable table = tables.get(h);
            if(tableListMap.get("tl"+h)==null) {
                int k = h + 3;
                if (tableListMap.get("tl0" + h) == null) {
                    continue;
                } else{
                    table = tables.get(k);
                    tableList = tableListMap.get("tl0"+h);
                }
            } else {
                tableList = tableListMap.get("tl"+h);
            }

            XWPFTableRow copyRow = table.getRow(headerIndex);
            if (null == copyRow) {
                break;
            }
            List<XWPFTableCell> cellList = copyRow.getTableCells();
            if (null == cellList) {
                break;
            }

            //遍历要添加的数据的list
            for (int i = 0; i < tableList.size(); i++) {
                //插入一行
                XWPFTableRow targetRow = table.insertNewTableRow(headerIndex + 1 + i);
                //复制行属性
                targetRow.getCtRow().setTrPr(copyRow.getCtRow().getTrPr());

                String[] strings = tableList.get(i);
                for (int j = 0; j < strings.length; j++) {
                    XWPFTableCell sourceCell = cellList.get(j);
                    //插入一个单元格
                    XWPFTableCell targetCell = targetRow.addNewTableCell();
                    //复制列属性
                    targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
                    targetCell.setText(strings[j]);
                }
            }
        }
    }

    /**
     * 替换表格对象方法
     *
     * @param document    docx解析对象
     * @param textMapMap 需要替换的信息集合
     */
    public static void changeTableText(XWPFDocument document, Map<String,Map<String, String>> textMapMap) {
        if(textMapMap==null){
            return;
        }
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            //只处理行数大于等于2的表格
            XWPFTable table = tables.get(i);
            if (table.getRows().size() > 1) {
                //判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
                if (checkText(table.getText())) {
                    List<XWPFTableRow> rows = table.getRows();
                    // TODO: 2021/10/12 数组越界
                    if(textMapMap.get("tb"+i)==null){
                        break;
                    }
                    //遍历表格,并替换模板
                    eachTable(rows, textMapMap.get("tb"+i));
                }
            }
        }
    }

    /**
     * 遍历表格,并替换模板
     *
     * @param rows    表格行对象
     * @param textMap 需要替换的信息集合
     */
    public static void eachTable(List<XWPFTableRow> rows, Map<String, String> textMap) {
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                //判断单元格是否需要替换
                if (checkText(cell.getText())) {
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (XWPFParagraph paragraph : paragraphs) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (XWPFRun run : runs) {
                            run.setText(changeValue(run.toString(), textMap), 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 匹配传入信息集合与模板
     *
     * @param value   模板需要替换的区域
     * @param textMap 传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    public static String changeValue(String value, Map<String, String> textMap) {
        Set<Map.Entry<String, String>> textSets = textMap.entrySet();
        for (Map.Entry<String, String> textSet : textSets) {
            //匹配模板与替换值 格式${key}
            String key = "${" + textSet.getKey() + "}";
            if (value.indexOf(key) != -1) {
                value = textSet.getValue();
            }
        }
        //模板未匹配到区域替换为空
        if (checkText(value)) {
            value = "";
        }
        return value;
    }

    /**
     * 判断文本中时候包含$
     *
     * @param text 文本
     * @return 包含返回true, 不包含返回false
     */
    public static boolean checkText(String text) {
        boolean check = false;
        if (text.indexOf("$") != -1) {
            check = true;
        }
        return check;
    }

}
