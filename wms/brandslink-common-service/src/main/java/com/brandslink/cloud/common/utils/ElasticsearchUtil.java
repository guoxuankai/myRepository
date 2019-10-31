/*
package com.brandslink.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ElasticsearchUtil {

    @Autowired
    private RestHighLevelClient client;

    */
/**
     * 创建索引
     * @param index
     * @throws IOException
     *//*

    public boolean createIndex(String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }


    */
/**
     * 判断索引是否存在
     * @param index
     * @return
     * @throws IOException
     *//*

    public boolean existsIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("existsIndex: " + exists);
        return exists;
    }


    */
/**
     * 增加记录
     * @param index
     * @param type
     * @param object
     * @throws IOException
     *//*

    public void add(String index, String type, Object object, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JSON.toJSONString(object), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("add: " + JSON.toJSONString(indexResponse));
    }


    */
/**
     * 判断记录是都存在
     * @param index
     * @param type
     * @param object
     * @return
     * @throws IOException
     *//*

    public boolean exists(String index, String type, Object object, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, type, id);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("exists: " + exists);
        return exists;
    }


    */
/**
     * 获取记录信息
     * @param index
     * @param type
     * @param id
     * @throws IOException
     *//*

    public void get(String index, String type, Long id) throws IOException {
        GetRequest getRequest = new GetRequest(index, type, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("get: " + JSON.toJSONString(getResponse));
    }

    */
/**
     * 更新记录信息
     * @param index
     * @param type
     * @param object
     * @throws IOException
     *//*

    public void update(String index, String type, Object object, String id) throws IOException {
        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(JSON.toJSONString(object), XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
        System.out.println("update: " + JSON.toJSONString(updateResponse));
    }

    */
/**
     * 删除记录
     * @param index
     * @param type
     * @param id
     * @throws IOException
     *//*

    public void delete(String index, String type, Long id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id.toString());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("delete: " + JSON.toJSONString(response));
    }


    */
/**
     * 搜索
     * @param index
     * @param type
     * @param name
     * @throws IOException
     *//*

    public void search(String index, String type, String name) throws IOException {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //boolBuilder.should(QueryBuilders.matchQuery("name", "道夫"));
       // boolBuilder.should(QueryBuilders.matchQuery("desc", "管理"));
        //boolBuilder.should(QueryBuilders.matchQuery("name", name));
        //boolBuilder.must(QueryBuilders.matchQuery("name", name)); // 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
        // boolBuilder.must(QueryBuilders.matchQuery("id", tests.getId().toString()));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MaxAggregationBuilder aggregationBuilder1 = AggregationBuilders.max("my_group_by_age2").field("creattime");
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("my_group_by_age").subAggregation(aggregationBuilder1).field("age").order(BucketOrder.compound(
                BucketOrder.aggregation("my_group_by_age2",false)//my_group_by_age2，降序排
        )); ;

        //aggregationBuilder.size(ESConstants.ES_AGG_SIZE);
        aggregationBuilder.collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST);
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.query(boolBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(100); // 获取记录数，默认10
        //sourceBuilder.sort("creattime", SortOrder.DESC);
        //sourceBuilder.fetchSource(new String[] { "id", "name" }, new String[] {}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("search: " + JSON.toJSONString(response));
        Aggregations aggs = response.getAggregations();
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            System.out.println("search -> " + hit.getSourceAsString());
        }
        */
/*Aggregations aggregations = response.getAggregations();
        Terms byCompanyAggregation = aggregations.get("by_company");
        Terms.Bucket elasticBucket = byCompanyAggregation.getBucketByKey("Elastic");
        Avg averageAge = elasticBucket.getAggregations().get("average_age");
        double avg = averageAge.getValue();*//*



    }


    */
/**
     * 批量操作
     * @throws IOException
     *//*

    public void bulk() throws IOException {
        */
/*//*
/ 批量增加
        BulkRequest bulkAddRequest = new BulkRequest();
        for (int i = 0; i < testsList.size(); i++) {
            tests = testsList.get(i);
            IndexRequest indexRequest = new IndexRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
            indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
            bulkAddRequest.add(indexRequest);
        }
        BulkResponse bulkAddResponse = client.bulk(bulkAddRequest, RequestOptions.DEFAULT);
        System.out.println("bulkAdd: " + JSON.toJSONString(bulkAddResponse));
        search(INDEX_TEST, TYPE_TEST, "this");

        // 批量更新
        BulkRequest bulkUpdateRequest = new BulkRequest();
        for (int i = 0; i < testsList.size(); i++) {
            tests = testsList.get(i);
            tests.setName(tests.getName() + " updated");
            UpdateRequest updateRequest = new UpdateRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
            updateRequest.doc(JSON.toJSONString(tests), XContentType.JSON);
            bulkUpdateRequest.add(updateRequest);
        }
        BulkResponse bulkUpdateResponse = client.bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
        System.out.println("bulkUpdate: " + JSON.toJSONString(bulkUpdateResponse));
        search(INDEX_TEST, TYPE_TEST, "updated");

        // 批量删除
        BulkRequest bulkDeleteRequest = new BulkRequest();
        for (int i = 0; i < testsList.size(); i++) {
            tests = testsList.get(i);
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
            bulkDeleteRequest.add(deleteRequest);
        }
        BulkResponse bulkDeleteResponse = client.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
        System.out.println("bulkDelete: " + JSON.toJSONString(bulkDeleteResponse));
        search(INDEX_TEST, TYPE_TEST, "this");*//*

    }




}
*/
