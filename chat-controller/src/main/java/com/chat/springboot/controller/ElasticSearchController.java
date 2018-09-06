package com.chat.springboot.controller;

import java.io.IOException;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/search")
@Slf4j
@Api(value = "elastic-serach-controller", description = "elasticsearch控制层")
public class ElasticSearchController {

	@Autowired
	private TransportClient client;

	/**
	 * 根据id查询elasticsearch
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail/{id}")
	// @ValidateAttribute(attributes = {"id"})
	@ApiOperation(value = "根据id查询接口")
	@ApiImplicitParam(name = "id", value = "查询id", dataType = "String", paramType = "query")
	public ResponseResult<?> findBookDetail(@PathVariable("id") String id) {
		log.info("得到的id:{}, 类的类型是...{}", id, this.getClass().getName());
		GetResponse result = client.prepareGet("study", "book", id).get();
		return new ResponseResult<Map<String, Object>>(ResultStatus.SUCCESS, result.getSource());
	}

	/**
	 * 根据id查询elasticsearch
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/add")
	@ApiOperation(value = "添加书本")
	public ResponseResult<?> addBook(String title, String author, Integer world_count) {
		try {
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject().field("title", title)
					.field("author", author).field("world_count", world_count).endObject();
			IndexResponse indexResponse = client.prepareIndex("study", "book").setSource(contentBuilder).get();
			return new ResponseResult<String>(ResultStatus.SUCCESS, indexResponse.getId());
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseResult<>(ResultStatus.UNKNOW_ERROR);
		}
	}

	/**
	 * 根据id删除书本
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/del/{id}")
	@ApiOperation(value = "删除书本")
	public ResponseResult<?> deleteBook(@PathVariable("id") String id) {
		log.info("得到的id:{}, 类的类型是...{}", id, this.getClass().getName());
		DeleteResponse result = client.prepareDelete("study", "book", id).get();
		return new ResponseResult<>(ResultStatus.SUCCESS, result);
	}

	/**
	 * 根据id查询elasticsearch
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/edit")
	@ApiOperation(value = "更新书本")
	public ResponseResult<?> updateBook(String title, String author, Integer world_count, String id) {
		try {
			UpdateRequest updateResponse = new UpdateRequest("study", "book", id);
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject().field("title", title)
					.field("author", author).field("world_count", world_count).endObject();
			updateResponse.doc(contentBuilder);
			return new ResponseResult<String>(ResultStatus.SUCCESS,
					client.update(updateResponse).get().getResult().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseResult<>(ResultStatus.UNKNOW_ERROR);
		}
	}

	/**
	 * 复杂查询接口
	 * 
	 * @param title
	 * @param author
	 * @return
	 */
	@RequestMapping(value = "/complex/query")
	public ResponseResult<?> complexQuery(String title, String author, Integer gtWorld) {
		BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery();
		if (author != null) {
			booleanQuery.must(QueryBuilders.matchQuery("author", author));
		}
		if (title != null) {
			booleanQuery.must(QueryBuilders.matchQuery("title", title));
		}
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("world_count").from(gtWorld);
		booleanQuery.filter(rangeQuery);
		return new ResponseResult<>(ResultStatus.SUCCESS);
	}

}
