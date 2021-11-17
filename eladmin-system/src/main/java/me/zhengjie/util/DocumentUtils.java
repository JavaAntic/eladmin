package me.zhengjie.util;

import me.zhengjie.modules.system.service.dto.DocumentDto;
import me.zhengjie.modules.system.service.dto.DocumentParagraphDto;
import me.zhengjie.modules.system.service.dto.DocumentParagraphRunDto;
import me.zhengjie.modules.system.service.dto.DocumentTableCellDto;
import me.zhengjie.modules.system.service.dto.DocumentTableDto;
import me.zhengjie.modules.system.service.dto.DocumentTableRowDto;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * DocumentUtils
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public class DocumentUtils {

    /**
     * 段落集合信息
     */
    public static List<DocumentParagraphDto> paragraphList = new ArrayList<>();
    /**
     * 表格集合信息
     */
    public static List<DocumentTableDto> tableList = new ArrayList<>();

    public static List<DocumentParagraphDto> getParagraphList() {
        return paragraphList;
    }

    public static List<DocumentTableDto> getTableList() {
        return tableList;
    }

    /**
     * 读取word主流程
     */
    public static void exportWord(InputStream inputStream, DocumentDto documentDto) throws IOException {
        //解析docx模板并获取document对象
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            // 获取段落信息
            getParagraphText(document, documentDto);
            // 获取表格信息
            getTableText(document, documentDto);
        }
    }

    /**
     * 获取文档中段落文本方法
     * 将段落信息封装到自建内部类,放到段落集合
     *
     * @param document docx解析对象
     */
    private static void getParagraphText(XWPFDocument document, DocumentDto documentDto) {
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        List<DocumentParagraphDto> pList = readParagraph(paragraphs, documentDto);
        paragraphList.addAll(pList);
    }

    /**
     * 获取表格内对象方法
     * 将表格内数据放入自建内部类集合
     *
     * @param document docx解析对象
     */
    private static void getTableText(XWPFDocument document, DocumentDto documentDto) {
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            XWPFTable table = tables.get(i);
            DocumentTableDto t = new DocumentTableDto();
            // 处理表格行
            List<DocumentTableRowDto> rows = readRow(table, documentDto);
            t.setDocument(documentDto);
            t.setDataSize(table.getNumberOfRows());
            t.setIndex(i);
            t.setText(table.getText());
            t.setRows(rows);
            tableList.add(t);
        }
    }

    /**
     * 读表格行信息
     *
     * @param table 表格
     * @return 处理后的行
     */
    private static List<DocumentTableRowDto> readRow(XWPFTable table, DocumentDto documentDto) {
        int rowIndex = 0;
        List<DocumentTableRowDto> rows = new ArrayList<>();
        // 处理当前行
        for (XWPFTableRow row : table.getRows()) {
            DocumentTableRowDto row1 = new DocumentTableRowDto();
            List<DocumentTableCellDto> tCells = new ArrayList<>();
            List<XWPFTableCell> cells = row.getTableCells();
            int cellIndex = 0;
            // 处理当前列
            for (XWPFTableCell cell : cells) {
                DocumentTableCellDto cell1 = new DocumentTableCellDto();
                List<DocumentParagraphDto> pList = readParagraph(cell.getParagraphs(), documentDto);
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
     *
     * @param paragraphs 段落集合
     * @return 段落信息
     */
    private static List<DocumentParagraphDto> readParagraph(List<XWPFParagraph> paragraphs, DocumentDto documentDto) {
        List<DocumentParagraphDto> pList = new ArrayList<>();
        int index = 0;
        // 处理当前列里面的小段落.
        for (XWPFParagraph paragraph : paragraphs) {
            DocumentParagraphDto p = new DocumentParagraphDto();
            List<DocumentParagraphRunDto> runs = new ArrayList<>();
            int runIndex = 0;
            for (XWPFRun run : paragraph.getRuns()) {
                DocumentParagraphRunDto run1 = new DocumentParagraphRunDto();
                run1.setIndex(runIndex);
                run1.setText(run.toString());
                runs.add(run1);
                runIndex++;
            }
            p.setDocument(documentDto);
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
