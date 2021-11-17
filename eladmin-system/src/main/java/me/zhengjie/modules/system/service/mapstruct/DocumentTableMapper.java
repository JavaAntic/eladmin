package me.zhengjie.modules.system.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.system.domain.DocumentTable;
import me.zhengjie.modules.system.service.dto.DocumentTableDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * DocumentTableMapper
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentTableMapper extends BaseMapper<DocumentTableDto, DocumentTable> {
}
