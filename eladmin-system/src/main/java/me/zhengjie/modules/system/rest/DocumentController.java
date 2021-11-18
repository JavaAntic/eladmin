package me.zhengjie.modules.system.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.service.DocumentService;
import me.zhengjie.modules.system.domain.vo.DocumentVo;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * DocumentController
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "报告：安全报告管理")
@RequestMapping("/api/document")
public class DocumentController {
    private final DocumentService documentService;

    @ApiOperation("上传文件")
    @PostMapping
    @PreAuthorize("@el.check('doc:upload')")
    public ResponseEntity<Object> upload(@RequestParam String name, @RequestParam Boolean isModel, @RequestParam("file") MultipartFile file) throws Exception {
        documentService.upload(name, isModel, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改文档详情")
    @ApiOperation("修改文档详情")
    @PutMapping
    @PreAuthorize("@el.check('doc:edit')")
    public ResponseEntity<Object> update(@Validated(Document.Update.class) @RequestBody Document resources) {
        documentService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("安全工具报告列表")
    @ApiOperation("安全工具报告列表")
    @GetMapping("/documentList")
    @PreAuthorize("@el.check('doc:list')")
    public ResponseEntity<Object> documentList(@RequestBody DocumentVo vo){
        if (StringUtils.isEmpty(vo.getDocType())){
            return new ResponseEntity<>("文档类型不能为空",HttpStatus.OK);
        }
        return new ResponseEntity<>(documentService.getList(vo),HttpStatus.OK);
    }
    @Log("生成文件")
    @ApiOperation("生成文件")
    @PostMapping("/create")
    @PreAuthorize("@el.check('doc:create')")
    public ResponseEntity<Object> create(@RequestBody DocumentVo vo){
        return new ResponseEntity<>(documentService.create(vo),HttpStatus.OK);
    }

    @Log("合并文件列表")
    @ApiOperation("合并文件列表")
    @PostMapping("/addList")
    @PreAuthorize("@el.check('doc:addList')")
    public ResponseEntity<Object> addList(@RequestBody DocumentVo vo){
        return new ResponseEntity<>(documentService.addList(vo),HttpStatus.OK);
    }

}
