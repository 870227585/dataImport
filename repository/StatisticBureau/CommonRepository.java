package com.bdi.sselab.repository.StatisticBureau;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
@NoRepositoryBean
public interface CommonRepository<T,ID> extends JpaRepository<T,ID> {
    List<T> findByDate(String date);
}
