package me.zhengjie.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;

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
public class DocumentTableCellDto implements Serializable {
    /**
     * 位置
     */
    private int location;
    /**
     * 列内文本
     */
    private List<DocumentTableParagraphDto> paragraphs;
}
