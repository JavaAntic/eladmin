package me.zhengjie.modules.system.domain.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.modules.system.domain.DocumentTable;

import java.util.List;

/**
 * DocumentVo
 *
 * @author Jin
 * @Date 15:12 2021/11/17
 */
@Data
public class DocumentVo {

    @ApiModelProperty(value = "0:上传文件 1:自定义 2:模板文件")
    private String docType;

    @ApiModelProperty(value = "表格")
    private List<DocumentTable> documentTables;
   // 文档头
    private String head;
   // 模板名称
    private String templateName;
   // 模板id
    private String templateId;
}
