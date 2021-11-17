package me.zhengjie.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.FileProperties;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.repository.DocumentRepository;
import me.zhengjie.modules.system.service.DocumentService;
import me.zhengjie.modules.system.service.dto.DocumentDto;
import me.zhengjie.modules.system.service.dto.DocumentParagraphDto;
import me.zhengjie.modules.system.service.dto.DocumentTableDto;
import me.zhengjie.modules.system.service.mapstruct.DocumentMapper;
import me.zhengjie.util.DocumentUtils;
import me.zhengjie.util.FileTypeEnum;
import me.zhengjie.util.ReadWordCopy;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final DocumentMapper documentMapper;
    private final FileProperties properties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(String name, String safeType, MultipartFile multipartFile) throws Exception {
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
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 读取word文件,转文件流
            final Document document = Document.builder().fileName(name).fileType(FileTypeEnum.getType(suffix)).safeType(safeType).build();
            final DocumentDto documentDto = documentMapper.toDto(documentRepository.save(document));
            DocumentUtils.exportWord(inputStream, documentDto);
            List<DocumentParagraphDto> paragraphList = DocumentUtils.getParagraphList();
            List<DocumentTableDto> tableList = DocumentUtils.getTableList();
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }
}
