package com.example.demo.repository;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.model.*;
@Service
public class ProfileService {
	@Autowired
	private RestHighLevelClient client;
	@Autowired
	private ObjectMapper objectMapper;	
	
	@Autowired
	public ProfileService(RestHighLevelClient restHighLevelClient,ObjectMapper objectMapper) {
		this.client=restHighLevelClient;
		this.objectMapper=objectMapper;
	}
	
	public Profile findById(String id) throws Exception {

        GetRequest getRequest = new GetRequest("profile", "_doc", id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();

        return objectMapper.convertValue(resultMap,Profile.class);
        }
	public String createProfileDocument(Profile profile) throws Exception {

        UUID uuid = UUID.randomUUID();
        profile.setId(uuid.toString());
        System.out.println(profile.getFirstName()+profile.getLastName()+profile.getId()+profile.getHobby().get(0).getHobbyName());
        IndexRequest indexRequest = new IndexRequest("profile", "_doc", profile.getId())
                .source(objectMapper.convertValue(profile,Map.class), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.getResult().name();
    }
	public List<Profile> searchByHobby(String name) throws Exception{
        SearchRequest searchRequest = buildSearchRequest("profile","_doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
       QueryBuilder queryBuilder= QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("hobby.hobbyName", name));
      //  MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("hobby.hobbyName",name).operator(Operator.AND);
        searchSourceBuilder.query(QueryBuilders.nestedQuery("hobby",queryBuilder,ScoreMode.Avg));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);

        return getSearchResult(response);
    }
	public List<Profile> findByName(String name) throws Exception{
		SearchRequest searchReq=buildSearchRequest("profile","_doc");
		SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
		MatchQueryBuilder queryBuilder=QueryBuilders.matchQuery("firstName", name).operator(Operator.AND);	
		searchSourceBuilder.query(queryBuilder);
		searchReq.source(searchSourceBuilder);
		SearchResponse searchResponse=client.search(searchReq,RequestOptions.DEFAULT);
		return getSearchResult(searchResponse);
	}
	
	public List<Profile> findAll() throws Exception {
        SearchRequest searchRequest = buildSearchRequest("profile","_doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }
	
	public List<Profile> getSearchResult(SearchResponse searchResponse){
		SearchHit[] searchHits=searchResponse.getHits().getHits();
		List<Profile> profile=new ArrayList<>();
		for(SearchHit hit:searchHits) {
			profile.add(objectMapper.convertValue(hit.getSourceAsMap(),Profile.class));
		}
		return profile;
	}
	
	private SearchRequest buildSearchRequest(String index, String type) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);
        return searchRequest;
    }
}
