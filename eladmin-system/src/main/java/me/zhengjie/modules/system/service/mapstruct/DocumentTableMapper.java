package me.zhengjie.modules.system.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.system.domain.DocumentTable;
import me.zhengjie.modules.system.service.dto.DocumentTableDto;
import me.zhengjie.util.TypeConversionWorker;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * DocumentTableMapper
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {TypeConversionWorker.class, DocumentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DocumentTableMapper extends BaseMapper<DocumentTableDto, DocumentTable> {

    /**
     * DTO转Entity
     *
     * @param dto /
     * @return /
     */
    @Override
    @Mapping(target = "rows", source = "rows", qualifiedByName = "toJsonString")
    DocumentTable toEntity(DocumentTableDto dto);

    /**
     * Entity转DTO
     *
     * @param entity /
     * @return /
     */
    @Override
    @Mapping(target = "rows", expression = "java(typeConversionWorker.toStrList(entity.getRows(), me.zhengjie.modules.system.service.dto.DocumentTableRowDto.class))")
    DocumentTableDto toDto(DocumentTable entity);
}
