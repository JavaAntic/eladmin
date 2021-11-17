package me.zhengjie.modules.system.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.system.domain.DocumentParagraph;
import me.zhengjie.modules.system.service.dto.DocumentParagraphDto;
import me.zhengjie.util.TypeConversionWorker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * DocumentParagraphMapper
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentParagraphMapper extends BaseMapper<DocumentParagraphDto, DocumentParagraph> {

    /**
     * DTO转Entity
     *
     * @param dto /
     * @return /
     */
    @Override
    @Mapping(target = "runs", source = "runs", qualifiedByName = "toJsonString")
    DocumentParagraph toEntity(DocumentParagraphDto dto);

    /**
     * Entity转DTO
     *
     * @param entity /
     * @return /
     */
    @Override
    @Mapping(target = "runs", expression = "java(typeConversionWorker.toStrList(entity.getRuns(), me.zhengjie.modules.system.service.dto.DocumentParagraphRunDto.class))")
    DocumentParagraphDto toDto(DocumentParagraph entity);
//
//    /**
//     * DTO集合转Entity集合
//     * @param dtoList /
//     * @return /
//     */
//    @Override
//    @Mapping(target = "runs", source = "runs", qualifiedByName = "toJsonString")
//    List<DocumentParagraph> toEntity(List<DocumentParagraphDto> dtoList);
//
//    /**
//     * Entity集合转DTO集合
//     * @param entityList /
//     * @return /
//     */
//    @Override
//    @Mapping(target = "runs", expression = "java(typeConversionWorker.toStrList(entity.getRuns(), DocumentParagraphRunDto.class()))")
//    List<DocumentParagraphDto> toDto(List<DocumentParagraph> entityList);
}
