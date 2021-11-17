package me.zhengjie.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
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
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(String name, boolean isModel, MultipartFile multipartFile) throws Exception {
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
        String suffix = FileUtil.getExtensionName(name);
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, properties.getPath().getPath() + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("上传失败");
        }
        if (StringUtils.isEmpty(suffix)) {
            throw new BadRequestException("文件类型不支持");
        }
        if (!isModel) {
            final String substring = name.substring(0, name.indexOf("."));
            final String[] split = substring.split("_");
            String safeType = split[split.length - 1];
            final List<String> safeTypeList = properties.getSafeType();
            if (!safeTypeList.contains(safeType)) {
                throw new BadRequestException("文件名不符合规则：解析安全类型失败！");
            }
            try (InputStream inputStream = new FileInputStream(file)) {
                // 读取word文件,转文件流
                final Document document = new Document(name, FileTypeEnum.getType(suffix), safeType);
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
    }

    @Override
    public void update(Document resources) {

    }

    @Override
    public List<Document> getList() {
        return documentRepository.findAll();
    }
}
