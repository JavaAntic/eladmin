package me.zhengjie.modules.system.domain.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DocumentVo
 *
 * @author Jin
 * @Date 15:12 2021/11/17
 */
@Data
public class DocumentVo {

    @ApiModelProperty(value = " 1:工具报告 2自定义报告3模板")
    private String documentType;
}
