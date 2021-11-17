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
 * 文档表格
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Getter
@Setter
@Table(name = "ST_DOCUMENT_TABLE")
public class DocumentTable extends BaseEntity implements Serializable {

    @Id
    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(generator = "table_id")
    @GenericGenerator(name = "table_id", strategy = "uuid")
    @Column(name = "id", length = 32, unique = true, nullable = false)
    private String id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false)
    @ApiModelProperty(value = "文档id")
    private Document document;

    @ApiModelProperty(value = "表格位置")
    private Integer index;

    @ApiModelProperty(value = "表格数据量")
    private Integer dataSize;

    @ApiModelProperty(value = "表格文本")
    private String text;

    @ApiModelProperty(value = "表格内行信息")
    private String rows;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentTable documentTable = (DocumentTable) o;
        return Objects.equals(id, documentTable.id) &&
                Objects.equals(document, documentTable.document) &&
                Objects.equals(index, documentTable.index) &&
                Objects.equals(dataSize, documentTable.dataSize) &&
                Objects.equals(text, documentTable.text) &&
                Objects.equals(rows, documentTable.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, document, index, dataSize, text, rows);
    }
}
