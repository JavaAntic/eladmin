package me.zhengjie.modules.system.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.domain.vo.DocumentVo;
import me.zhengjie.modules.system.service.DocumentService;
import me.zhengjie.modules.system.service.dto.DocumentQueryCriteria;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Log("上传文件")
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

    @ApiOperation("文件列表查询")
    @GetMapping
    @PreAuthorize("@el.check('doc:list')")
    public ResponseEntity<Object> query(DocumentQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(documentService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("生成文件")
    @ApiOperation("生成文件")
    @PostMapping("/create")
    @PreAuthorize("@el.check('doc:create')")
    public ResponseEntity<Object> create(@RequestBody DocumentVo vo) throws Exception {
        if(StringUtils.isEmpty(vo.getTemplateName())){
            throw new Exception("模板不能为空");
        }
        return new ResponseEntity<>(documentService.create(vo),HttpStatus.OK);
    }

    @ApiOperation("所有文件列表查询")
    @GetMapping(value = "/all")
    @PreAuthorize("@el.check('doc:listAll')")
    public ResponseEntity<Object> queryAll(DocumentQueryCriteria criteria) {
        return new ResponseEntity<>(documentService.queryAll(criteria), HttpStatus.OK);
    }
//
//    @ApiOperation("文件详情查询")
//    @GetMapping(value = "/detail")
//    @PreAuthorize("@el.check('doc:detail')")
//    public ResponseEntity<Object> query(@RequestParam String documentId) {
//        return new ResponseEntity<>(documentService.query(documentId), HttpStatus.OK);
//    }

}
