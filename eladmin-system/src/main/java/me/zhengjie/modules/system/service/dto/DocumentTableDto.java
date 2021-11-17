package me.zhengjie.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * DocumentTableDto
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@Setter
public class DocumentTableDto extends BaseDTO implements Serializable {
    private String id;

    private DocumentDto document;

    private Integer location;

    private Integer dataSize;

    private String text;

    private List<DocumentTableRowDto> rows;
}
