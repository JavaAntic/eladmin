package me.zhengjie.modules.system.repository;

import me.zhengjie.modules.system.domain.DocumentParagraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * DocumentParagraphRepository
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DocumentParagraphRepository extends JpaRepository<DocumentParagraph, String>, JpaSpecificationExecutor<DocumentParagraph> {
}
