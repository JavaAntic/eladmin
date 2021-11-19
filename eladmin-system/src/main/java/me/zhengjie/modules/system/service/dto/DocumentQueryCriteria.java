package me.zhengjie.modules.system.service.dto;

import lombok.Data;
import me.zhengjie.annotation.Query;

/**
 * DocumentQueryCriteria
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class DocumentQueryCriteria {

    @Query(type = Query.Type.EQUAL)
    private String docType;
}
