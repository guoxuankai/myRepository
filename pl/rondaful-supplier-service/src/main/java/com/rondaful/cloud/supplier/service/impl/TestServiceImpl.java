package com.rondaful.cloud.supplier.service.impl;

import com.rondaful.cloud.supplier.config.es.EsClient;
import com.rondaful.cloud.supplier.service.ITestService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
@Service("testServiceImpl")
public class TestServiceImpl implements ITestService {


    @Override
    public void test() {
        RestHighLevelClient levelClient=EsClient.getClient();



        SearchRequest searchRequest=new SearchRequest("freight");
        //searchRequest.searchType("search");
        searchRequest.types("search");
        SearchSourceBuilder builder=new SearchSourceBuilder();
        builder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("searchId","EO0036701AF2_ZSW3","BG0021400AF2_ZSW3"))
        );
        searchRequest.source(builder);
        builder.aggregation(
                AggregationBuilders.terms("logisticsCode").field("logisticsCode")
                .subAggregation(AggregationBuilders.sum("totalCost").field("totalCost"))
        );
        try {
            SearchResponse response=levelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations=response.getAggregations();
            SearchHits hits = response.getHits();
            System.out.println(111);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EsClient.closeClient(levelClient);
    }
}
