package me.zhengjie.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;

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
public class DocumentParagraphDto extends BaseDTO implements Serializable {
    private String id;

    private DocumentDto document;

    private Integer index;

    private String style;

    private String text;

    private List<DocumentParagraphRunDto> runs;
}
