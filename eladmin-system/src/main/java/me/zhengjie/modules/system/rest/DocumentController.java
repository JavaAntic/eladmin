package me.zhengjie.modules.system.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Object> upload(@RequestParam String name, @RequestParam boolean isModel, @RequestParam("file") MultipartFile file) throws Exception {
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
}
