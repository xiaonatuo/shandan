package com.keyware.shandan.browser.service.builders.impl;

import com.keyware.shandan.browser.enums.ViewType;
import com.keyware.shandan.browser.service.builders.ViewAggregation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;

public class ViewAggregationEquipment extends ViewAggregation {
    private static final String alias = "equipment_histogram";
    private static final ViewType type = ViewType.equipment;

    public ViewAggregationEquipment(SearchRequest request, RestHighLevelClient client) {
        super(request, client);
    }

    @Override
    public String setAlias() {
        return alias;
    }

    @Override
    public ViewType setType() {
        return type;
    }

}
