package com.bdi.sselab.repository.departmentLog;

import com.bdi.sselab.domain.log.GeneralUserLog;
import com.bdi.sselab.domain.log.RootLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/26
 * @Time:15:45
 */
public interface RootLogRepository extends JpaRepository<RootLog, Long> {
}
