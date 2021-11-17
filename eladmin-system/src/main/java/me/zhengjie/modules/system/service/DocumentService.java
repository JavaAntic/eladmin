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
     * @param name     文档名称
     * @param safeType 文档安全类型
     * @param file     文件
     * @throws Exception /
     */
    void upload(String name, String safeType, MultipartFile file) throws Exception;

    /**
     * 修改文档
     *
     * @param resources 文档
     */
    void update(Document resources);

    Object getList();
}
