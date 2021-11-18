package me.zhengjie.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.FileProperties;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.domain.DocumentParagraph;
import me.zhengjie.modules.system.domain.DocumentTable;
import me.zhengjie.modules.system.domain.vo.DocumentVo;
import me.zhengjie.modules.system.repository.DocumentParagraphRepository;
import me.zhengjie.modules.system.repository.DocumentRepository;
import me.zhengjie.modules.system.repository.DocumentTableRepository;
import me.zhengjie.modules.system.service.DocumentService;
import me.zhengjie.modules.system.service.dto.DocumentDto;
import me.zhengjie.modules.system.service.mapstruct.DocumentMapper;
import me.zhengjie.modules.system.service.mapstruct.DocumentParagraphMapper;
import me.zhengjie.modules.system.service.mapstruct.DocumentTableMapper;
import me.zhengjie.util.DocumentUtils;
import me.zhengjie.util.FileTypeEnum;
import me.zhengjie.util.ReadWord;
import me.zhengjie.util.SafeTypeEnum;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * DocumentServiceImpl
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "doc")
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentParagraphRepository documentParagraphRepository;
    private final DocumentTableRepository documentTableRepository;
    private final DocumentMapper documentMapper;
    private final DocumentParagraphMapper documentParagraphMapper;
    private final DocumentTableMapper documentTableMapper;
    private final FileProperties properties;
    @Value("${upload.filePath}")
    public String filePath;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(String name, boolean isModel, MultipartFile multipartFile) throws Exception {
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
        String suffix = FileUtil.getExtensionName(name);
        String type = FileUtil.getFileType(suffix);
        if (ObjectUtil.isNull(multipartFile)) {
            throw new BadRequestException("上传失败");
        }
        if (StringUtils.isEmpty(suffix)) {
            throw new BadRequestException("文件类型不支持");
        }
        final String docType = FileTypeEnum.getType(suffix);
        File file = saveDocument(multipartFile, type, docType);
        if (!isModel) {
            parseDocument(name, docType, file);
        }
    }

    /**
     * 解析非模板文档
     *
     * @param name    文件名
     * @param docType 文档类型
     * @param file    文档文件
     * @throws Exception /
     */
    private void parseDocument(String name, String docType, File file) throws Exception {
        final String substring = name.substring(0, name.lastIndexOf("."));
        final String[] split = substring.split("_");
        String safeType = split[split.length - 1];
        final List<String> safeTypeList = properties.getSafeType();
        if (!safeTypeList.contains(safeType)) {
            throw new BadRequestException("文件名不符合规则：解析安全类型失败！");
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            // 读取word文件,转文件流
            final Document document = new Document(name, docType, SafeTypeEnum.getType(safeType));
            final DocumentDto documentDto = documentMapper.toDto(documentRepository.save(document));
            DocumentUtils.exportWord(inputStream, documentDto);
            final List<DocumentParagraph> documentParagraphs = documentParagraphMapper.toEntity(DocumentUtils.getParagraphList());
            documentParagraphRepository.saveAll(documentParagraphs);
            final List<DocumentTable> documentTables = documentTableMapper.toEntity(DocumentUtils.getTableList());
            documentTableRepository.saveAll(documentTables);
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }

    /**
     * 文档保存本地磁盘
     * pdf：转word再保存
     * word直接保存
     *
     * @param multipartFile 媒体文件
     * @param type          文件类型
     * @param docType       文档类型
     * @return 本地文件
     * @throws IOException /
     */
    private File saveDocument(MultipartFile multipartFile, String type, String docType) throws IOException {
        File file;
        final String filePath = properties.getPath().getPath() + type + File.separator;
        if (FileTypeEnum.PDF.getCode().equals(docType)) {
            // PDF 转 WORD
            try (InputStream inputStream = multipartFile.getInputStream()) {
                final String absFilename = filePath + FileUtil.generateFileName(multipartFile, FileFormat.DOCX.getName().toLowerCase());
                PdfDocument pdf = new PdfDocument();
                //Load a PDF file
                pdf.loadFromStream(inputStream);
                //Save to .docx file
                pdf.saveToFile(absFilename, FileFormat.DOCX);
                pdf.close();
                file = new File(absFilename);
            }
        } else {
            // 保存文件到本地磁盘
            file = FileUtil.upload(multipartFile, filePath);
        }
        return file;
    }

    @Override
    public void update(Document resources) {

    }

    @Override
    public List<Document> getList(DocumentVo vo) {
        Specification<Document> spec = (root, query, cb) -> {
            return cb.and(cb.equal(root.get("doc_type").as(String.class), vo.getDocType()));
        };
        return documentRepository.findAll(spec);
    }

    @Override
    public List<Document> create(DocumentVo vo) {
        String fileName = "ZDY"+System.currentTimeMillis();
        String path = filePath +"/ZDY/"+fileName+".docx";
        // 生成文档首页信息
        Map<String, String> paragraphMap = new HashMap<>();
        paragraphMap.put("head", vo.getHead());
        paragraphMap.put("fileName", fileName);
        paragraphMap.put("time", LocalDate.now().toString());
        paragraphMap.put("system", "poc");

        // 生成文档表格数据
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
        ReadWord.writeDocument(path,paragraphMap,familyListMap);
       // 将该文件进行保存
        return null;
    }
    @Override
    public List<Document> addList(DocumentVo vo) {
        // 1.取得文件信息
        // 2.将关联信息查出来返回前端
        return null;
    }
}
