package com.bdi.sselab.repository.departmentLog;

import com.bdi.sselab.domain.log.SchemeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/6/13
 * @Time:9:49
 */
public interface SchemeStatusRepository extends JpaRepository<SchemeStatus, Integer> {
    SchemeStatus findByName(String name);
    boolean existsByName(String name);
}
