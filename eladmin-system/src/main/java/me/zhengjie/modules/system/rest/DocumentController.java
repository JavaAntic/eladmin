package me.zhengjie.modules.system.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.domain.vo.DocumentVo;
import me.zhengjie.modules.system.service.DocumentService;
import me.zhengjie.modules.system.service.dto.DocumentQueryCriteria;
import me.zhengjie.util.ReadWord;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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

    @Value("${upload.filePath}")
    public String filePath;
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

    /**
     * 上传模板
     * @param file 文件
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation("上传模板")
    public ResponseEntity<Object> uploadTemplate(@RequestParam("file") MultipartFile file)  throws Exception{
        if (file.isEmpty()) {
            throw new Exception("上传失败，请添加文件");
        }
        return new ResponseEntity<>(documentService.uploadTemplate(file), HttpStatus.OK);
    }

    /**
     * @param path 想要下载的文件的路径
     * @功能描述 下载文件:
     */
    @PostMapping("/download")
    @ApiOperation("下载文件")
    public void download(@RequestParam("path") String path, HttpServletResponse response) {
        System.out.println(path);
        try {
            ReadWord.downFile(filePath + path,response);
        } catch (Exception e) {
            e.printStackTrace();
            new Exception( "下载失败，原因:"+e.getMessage());
        }
    }

    /**
     * 下载基本模板
     * @功能描述 下载基本模板:
     */
    @PostMapping("/downloadBaseTemplate")
    @ApiOperation("下载基本模板")
    public void downloadBaseTemplate(HttpServletResponse response) {
        String path = "/baseTemplate/baseTemplate.docx";
        try {
            ReadWord.downFile(filePath + path, response);
        } catch (Exception e) {
            e.printStackTrace();
            new Exception("下载失败，原因:" + e.getMessage());
        }
    }

}
