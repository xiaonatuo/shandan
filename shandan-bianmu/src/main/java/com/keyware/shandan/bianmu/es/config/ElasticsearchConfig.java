package com.keyware.shandan.bianmu.es.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

/**
 * ElasticsearchConfig
 *
 * @author Administrator
 * @since 2021/10/20
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    EntityMapper entityMapper(ElasticsearchConverter converter) {
        return new ElasticsearchEntityMapper(converter.getMappingContext(), null);
    }
}