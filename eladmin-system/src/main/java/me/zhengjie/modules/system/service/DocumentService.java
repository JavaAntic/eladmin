package me.zhengjie.modules.system.service;

import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.domain.vo.DocumentVo;
import me.zhengjie.modules.system.service.dto.DocumentDto;
import me.zhengjie.modules.system.service.dto.DocumentQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * DocumentService
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DocumentService {

    /**
     * 上传
     *
     * @param name    文档名称
     * @param isModel 是否模板文件
     * @param file    文件
     * @throws Exception /
     */
    void upload(String name, Boolean isModel, MultipartFile file) throws Exception;

    /**
     * 修改文档
     *
     * @param resources 文档
     */
    void update(Document resources);

    /**
     * @param vo /
     * @return /
     */
    Object create(DocumentVo vo);

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    Map<String, Object> queryAll(DocumentQueryCriteria criteria, Pageable pageable);

    /**
     * 列表查询
     *
     * @param criteria 条件
     * @return /
     */
    List<DocumentDto> queryAll(DocumentQueryCriteria criteria);
//
//    List<DocumentTableDto> query(String documentId);
}
