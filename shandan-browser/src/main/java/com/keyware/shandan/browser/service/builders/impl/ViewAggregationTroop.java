package com.keyware.shandan.browser.service.builders.impl;

import com.keyware.shandan.browser.enums.ViewType;
import com.keyware.shandan.browser.service.builders.ViewAggregation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;

public class ViewAggregationTroop extends ViewAggregation {
    private static final String alias = "troop_histogram";
    private static final ViewType type = ViewType.troop;

    public ViewAggregationTroop(SearchRequest request, RestHighLevelClient client) {
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
