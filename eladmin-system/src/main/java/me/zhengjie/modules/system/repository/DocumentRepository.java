package me.zhengjie.modules.system.repository;

import me.zhengjie.modules.system.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * DocumentRepository
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DocumentRepository extends JpaRepository<Document, String>, JpaSpecificationExecutor<Document> {

    /**
     * 根据文档类型查找此类文档最大文档编号
     *
     * @param docType 文档类型
     * @return 文档编号最大的文档
     */
    Document findFirstByDocTypeOrderByDocNumDesc(String docType);
}
