package me.zhengjie.modules.system.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.service.dto.DocumentDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * DocumentMapper
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends BaseMapper<DocumentDto, Document> {
}
