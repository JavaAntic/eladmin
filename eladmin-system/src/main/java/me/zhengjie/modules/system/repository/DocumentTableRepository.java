package me.zhengjie.modules.system.repository;

import me.zhengjie.modules.system.domain.DocumentTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * DocumentTableRepository
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DocumentTableRepository extends JpaRepository<DocumentTable, String>, JpaSpecificationExecutor<DocumentTable> {
}
