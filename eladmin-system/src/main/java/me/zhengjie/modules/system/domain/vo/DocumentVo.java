package me.zhengjie.modules.system.domain.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    @ApiModelProperty(value = "段落")
    private List<String> paragraphIds;
   // 文档头
    private String head;
}
