package com.keyware.shandan.control.es.repository;

import com.keyware.shandan.system.entity.SysFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * EsSysFileRepository
 *
 * @author Administrator
 * @since 2021/6/30
 */
@Repository
public interface EsSysFileRepository extends ElasticsearchRepository<SysFile, String> {
}
