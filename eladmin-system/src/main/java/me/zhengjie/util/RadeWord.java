package me.zhengjie.util;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @description: RadeWord
 * @date: 2021/3/31
 * @author: 王瑾
 */
public class RadeWord {

    public static void main(String[] args) {
        // 模拟点数据
        Map<String, String> paragraphMap = new HashMap<>();
        paragraphMap.put("head", "hahahaha");
        paragraphMap.put("fileName", "测试测试");
        paragraphMap.put("time", "2021-10-10 11:11:11");
        paragraphMap.put("system", "poc系统");
        List<String[]> familyList = new ArrayList<>();
        List<String[]> familyList1 = new ArrayList<>();
        Map<String, List<String[]>> familyListMap = new HashMap<>();
        familyList.add(new String[]{"露娜", "女", "野友", "666", "6660"});
        familyList.add(new String[]{"太乙真人", "男", "辅友", "111", "1110"});
        familyList.add(new String[]{"貂蝉", "女", "法友", "888", "8880"});
        familyList1.add(new String[]{"露娜1", "女", "野友", "666", "6660"});
        familyList1.add(new String[]{"貂蝉1", "女", "法友", "888", "8880"});
        familyListMap.put("tl0", familyList);
        familyListMap.put("tl1", familyList1);
        writeDocument("D:\\Desktop\\poc\\template\\template.docx", paragraphMap, familyListMap);
    }

    /**
     * 输出文件
     *
     * @param templatePath  模板路径
     * @param paragraphMap  文档中数据集合
     * @param familyListMap 表格信息
     */
    public static void writeDocument(String templatePath, Map<String, String> paragraphMap, Map<String, List<String[]>> familyListMap) {
        File file = new File(templatePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] a = exportWord(inputStream, paragraphMap, familyListMap);
        InputStream sbs = new ByteArrayInputStream(a);
        byteToFile(a, "D:\\Desktop\\个人\\test", "zzz.docx");
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
     * 替换word中文本
     *
     * @param paragraphMap word中文本
     * @return
     */
    public static byte[] exportWord(InputStream inputStream, Map<String, String> paragraphMap, Map<String, List<String[]>> familyListMap) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        XWPFDocument document = null;
        try {
            //解析docx模板并获取document对象
            document = new XWPFDocument(inputStream);
            changeParagraphText(document, paragraphMap);
            copyHeaderInsertText(document, familyListMap, 0);
            //copyActiveTable(document,familyListMap);
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
     * 替换表格对象方法
     *
     * @param document   docx解析对象
     * @param textMapMap 需要替换的信息集合
     */
    public static void changeTableText(XWPFDocument document, Map<String, Map<String, String>> textMapMap) {
        if (textMapMap == null) {
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
                    if (textMapMap.get("tb" + i) == null) {
                        break;
                    }
                    //遍历表格,并替换模板
                    eachTable(rows, textMapMap.get("tb" + i));
                }
            }
        }
    }

    /**
     * 复制表头 插入行数据，这里样式和表头一样
     *
     * @param document     docx解析对象
     * @param tableListMap 需要插入数据集合
     * @param headerIndex  表头的行索引，从0开始
     */
    public static void copyHeaderInsertText(XWPFDocument document, Map<String, List<String[]>> tableListMap, int headerIndex) {
        if (null == tableListMap) {
            return;
        }
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        List<String[]> tableList = new ArrayList<>();
        for (int h = 0; h < tables.size(); h++) {
            XWPFTable table = tables.get(h);
            tableList = tableListMap.get("tl" + h);
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
                XWPFTableRow targetRow = table.insertNewTableRow(headerIndex + i);
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
     * @param document      word文件
     * @param activeListMap 传入数据
     */
    private static void copyActiveTable(XWPFDocument document, Map<String, List<String[]>> activeListMap) {
        for (String key : activeListMap.keySet()) {
            // 创建段落
            XWPFParagraph p2 = document.insertNewParagraph(document.getParagraphs().get(30).getCTP().newCursor());
            p2.setStyle("2");
            XWPFRun r2 = p2.createRun(); // 创建段落文本
            // 设置文本
            r2.setText(key);
            r2.setFontSize(14);
            r2.setBold(true);
            XmlCursor cursor = document.getParagraphs().get(31).getCTP().newCursor();
            XWPFTable newTable = document.insertNewTbl(cursor);

            document.setTable(1, newTable);
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

    /**
     * 文件下载
     * @param downPath 文件路径
     * @throws Exception 异常抛出
     */
    public static void downFile(String downPath, HttpServletResponse response) throws Exception{
        File file = new File(downPath);
        // 获取文件名
        String filename = file.getName();
        // 获取文件后缀名
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        // 将文件写入输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStream fis = new BufferedInputStream(fileInputStream);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        // 告知浏览器文件的大小
        response.addHeader("Content-Length", "" + file.length());
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        outputStream.write(buffer);
        outputStream.flush();
    }
}
