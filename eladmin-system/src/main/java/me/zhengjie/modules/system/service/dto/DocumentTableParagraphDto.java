package me.zhengjie.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DocumentParagraphDto
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@Setter
public class DocumentTableParagraphDto implements Serializable {
    private String id;

    private Integer index;

    private String style;

    private String text;

    private List<DocumentParagraphRunDto> runs;
}
