package me.zhengjie.modules.system.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.system.domain.DocumentParagraph;
import me.zhengjie.modules.system.service.dto.DocumentParagraphDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * DocumentParagraphMapper
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentParagraphMapper extends BaseMapper<DocumentParagraphDto, DocumentParagraph> {
}
