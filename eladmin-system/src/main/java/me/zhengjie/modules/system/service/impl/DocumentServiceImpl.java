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
import me.zhengjie.util.SafeTypeEnum;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public void upload(String name, Boolean isModel, MultipartFile multipartFile) throws Exception {
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        name = StringUtils.isBlank(name) ? multipartFile.getOriginalFilename() : name;
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException("文件名不能为空");
        }
        if (ObjectUtil.isNull(isModel)) {
            throw new BadRequestException("是否模板属性不能为空");
        }
        if (ObjectUtil.isNull(multipartFile)) {
            throw new BadRequestException("上传失败");
        }
        String suffix = FileUtil.getExtensionName(name);
        final List<String> fileTypeList = properties.getFileType();
        if (StringUtils.isEmpty(suffix) || !fileTypeList.contains(suffix)) {
            throw new BadRequestException("文件类型[" + suffix + "]不支持");
        }
        final String fileNameNoEx = FileUtil.getFileNameNoEx(name);
        final String regex = "_";
        if (!fileNameNoEx.contains(regex)) {
            throw new BadRequestException("文件名不符合规则：安全类型不存在");
        }
        final String[] split = fileNameNoEx.split(regex);
        String subName = split[split.length - 1];
        final List<String> safeTypeList = properties.getSafeType();
        if (StringUtils.isEmpty(subName) || !safeTypeList.contains(subName)) {
            throw new BadRequestException("未知安全类型：" + subName);
        }
        saveDocument(name, isModel, multipartFile, suffix, fileTypeList, subName, safeTypeList);
    }

    /**
     * 文档保存本地磁盘
     *
     * @param name          文件名
     * @param isModel       是否模板
     * @param multipartFile 媒体文件
     * @param suffix        文件后缀
     * @param fileTypeList  文件类型列表
     * @param subName       文件名截取
     * @param safeTypeList  安全类型列表
     * @throws Exception /
     */
    private void saveDocument(String name, Boolean isModel, MultipartFile multipartFile, String suffix,
                              List<String> fileTypeList, String subName, List<String> safeTypeList) throws Exception {
        String type = FileUtil.getFileType(suffix);
        final String filePath = properties.getPath().getPath() + type + File.separator;
        // 保存文件到本地磁盘
        File file = FileUtil.upload(multipartFile, filePath);
        if (file == null || !file.isFile()) {
            throw new Exception("文件保存失败");
        }
        String docType = !isModel ? "0" : "2";
        final int fileTypeIndex = fileTypeList.indexOf(suffix);
        final String fileType = String.valueOf(fileTypeIndex);
        final String safeType = String.valueOf(safeTypeList.indexOf(subName));
        final Document first = documentRepository.findFirstByDocTypeOrderByDocNumDesc(docType);
        int docNum;
        if (first == null) {
            docNum = 1;
        } else {
            docNum = first.getDocNum() + 1;
        }
        final Document document = new Document(name, fileType, safeType, file.getAbsolutePath(), docNum, docType);
//        try {
        final Document save = documentRepository.save(document);
        if (!isModel) {
            parseDocument(fileTypeIndex, file, save);
        }
//        } catch (Exception e) {
//            FileUtil.del(file);
//            throw e;
//        }
    }

    /**
     * 解析非模板文档
     *
     * @param fileType 文件类型 0:word 1:pdf 2:excel
     * @param file     本地磁盘文件
     * @param save     保存数据库的文档信息
     * @throws IOException /
     */
    private void parseDocument(int fileType, File file, Document save) throws IOException {
        // 文档类型为PDF
        if (fileType == 1) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // PDF 转 WORD
            PdfDocument pdf = new PdfDocument();
            //Load a PDF file
            pdf.loadFromFile(file.getAbsolutePath(), FileFormat.PDF);
            //Save to .docx file
            pdf.saveToStream(baos, FileFormat.DOCX);
            pdf.close();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                parseDocument(save, bais);
            }
        } else {
            try (InputStream inputStream = new FileInputStream(file)) {
                parseDocument(save, inputStream);
            }
        }
    }

    /**
     * 解析非模板文档
     *
     * @param save        保存数据库的文档信息
     * @param inputStream 文档输入流
     * @throws IOException /
     */
    private void parseDocument(Document save, InputStream inputStream) throws IOException {
        // 读取word文件,转文件流
        final DocumentDto documentDto = documentMapper.toDto(save);
        DocumentUtils.exportWord(inputStream, documentDto);
        final List<DocumentParagraph> documentParagraphs = documentParagraphMapper.toEntity(DocumentUtils.getParagraphList());
        documentParagraphRepository.saveAll(documentParagraphs);
        final List<DocumentTable> documentTables = documentTableMapper.toEntity(DocumentUtils.getTableList());
        documentTableRepository.saveAll(documentTables);
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