package me.zhengjie.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;

import java.io.Serializable;

/**
 * DocumentParagraphDto
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@Setter
public class DocumentParagraphRunDto implements Serializable {
    /**
     * 文本位置
     */
    private int index;
    /**
     * 文本内容
     */
    private String text;
}
