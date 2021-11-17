package me.zhengjie.modules.system.service;

import me.zhengjie.modules.system.domain.Document;
import org.springframework.web.multipart.MultipartFile;

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
    void upload(String name, boolean isModel, MultipartFile file) throws Exception;

    /**
     * 修改文档
     *
     * @param resources 文档
     */
    void update(Document resources);

    Object getList();
}
