package me.zhengjie.modules.system.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * 文档段落
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Getter
@Setter
@Table(name = "st_document_paragraph")
public class DocumentParagraph extends BaseEntity implements Serializable {

    @Id
    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(generator = "paragraph_id")
    @GenericGenerator(name = "paragraph_id", strategy = "uuid")
    @Column(name = "id", length = 32, unique = true, nullable = false)
    private String id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    @ApiModelProperty(value = "文档id")
    private Document document;

    @ApiModelProperty(value = "段落位置")
    private Integer location;

    @ApiModelProperty(value = "文本级别")
    private String style;

    @ApiModelProperty(value = "段落文本")
    private String text;

    @ApiModelProperty(value = "段落内信息")
    private String runs;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentParagraph documentParagraph = (DocumentParagraph) o;
        return Objects.equals(id, documentParagraph.id) &&
                Objects.equals(document, documentParagraph.document) &&
                Objects.equals(location, documentParagraph.location) &&
                Objects.equals(style, documentParagraph.style) &&
                Objects.equals(text, documentParagraph.text) &&
                Objects.equals(runs, documentParagraph.runs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, document, location, style, text, runs);
    }
}
