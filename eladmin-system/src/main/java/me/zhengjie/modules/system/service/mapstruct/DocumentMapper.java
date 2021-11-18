package me.zhengjie.modules.system.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.service.dto.DocumentDto;
import me.zhengjie.util.TypeConversionWorker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * DocumentMapper
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends BaseMapper<DocumentDto, Document> {
    @Override
    @Mapping(target = "docNum", expression = "java(typeConversionWorker.docNum2String(dto.getDocNum(), dto.getDocType()))")
    Document toEntity(DocumentDto dto);

    @Override
    @Mapping(target = "docNum", expression = "java(typeConversionWorker.docNum2Integer(entity.getDocNum(), entity.getDocType()))")
    DocumentDto toDto(Document entity);
}
