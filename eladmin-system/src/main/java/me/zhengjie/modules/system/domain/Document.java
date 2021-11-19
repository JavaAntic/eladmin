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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * 文档
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Getter
@Setter
@Table(name = "ST_DOCUMENT")
public class Document extends BaseEntity implements Serializable {

    @Id
    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(generator = "document_id")
    @GenericGenerator(name = "document_id", strategy = "uuid")
    @Column(name = "id", length = 32, unique = true, nullable = false)
    private String id;

    @NotBlank
    @ApiModelProperty(value = "文件类型 0:word 1:pdf 2:excel")
    private String fileType;

    @ApiModelProperty(value = "安全类型0:通付盾 1:梆梆安全 2:爱加密")
    private String safeType;

    @NotBlank
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @NotBlank
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @NotNull
    @ApiModelProperty(value = "文档编号")
    private Integer docNum;

    @NotBlank
    @ApiModelProperty(value = "文档类型 0:上传文件 1:自定义 2:模板文件")
    private String docType;

    public Document() {
    }

    public Document(@NotBlank String fileName, @NotBlank String fileType, @NotBlank String filePath, @NotBlank Integer docNum, @NotBlank String docType) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.docNum = docNum;
        this.docType = docType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Document document = (Document) o;
        return Objects.equals(id, document.id) &&
                Objects.equals(fileType, document.fileType) &&
                Objects.equals(safeType, document.safeType) &&
                Objects.equals(fileName, document.fileName) &&
                Objects.equals(filePath, document.filePath) &&
                Objects.equals(docNum, document.docNum) &&
                Objects.equals(docType, document.docType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileType, safeType, fileName, filePath, docNum, docType);
    }
}
