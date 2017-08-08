package com.example.ElasticSearchStorage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.ElasticSearchStorage.service.ElasticSearchService;
import com.example.ElasticSearchStorage.utils.DataFetch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.ElasticSearchStorage.ElasticSearchStorageApplication.client;

/**
 * <p>Title: BONC -  FetchController</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
@RestController
@CrossOrigin(origins = "*")
@Api(value="ElasticSearch",description="根据指定参数从ES查询并处理结果集")
@RequestMapping("/HomePage")
public class FetchController {
    private static Logger logger = Logger.getLogger(FetchController.class);
    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    DataFetch dataFetch;

    /**
     * 近期访问筛选分类列表
     * @param param
     * @return
     */
    @PostMapping("/recentVisit")
    public Object getClassList(@RequestBody @ApiParam("用户id，登录令牌") HashMap<String,Object> param){
        List<HashMap<String,String>> resultList = new ArrayList<>();
        resultList = elasticSearchService.getAllType();
        logger.info("查询结果为："+resultList);
        HashMap<String,Object> resultMap = new HashMap<>();
        if (null!=resultList && !resultList.isEmpty()){
            HashMap<String,String> defaultMap = resultList.get(0);
            resultMap.put("default",defaultMap);
            resultMap.put("selectList",resultList);
        }
        String resultJson = JSON.toJSONString(resultMap, SerializerFeature.DisableCircularReferenceDetect);
        return resultJson;
    }

    /**
     * 近期访问内容
     * @param param 用户ID{指标（1）、专题（2）、报告（3）、全部（999)}，登录令牌，查询类型ID
     * @return
     */
    @PostMapping("/recentVisitList")
    public Object getRecentVisit(@RequestBody @ApiParam("用户id，登录令牌，查询类型id") HashMap<String,Object> param){
        String userId = param.get("userId").toString();
        String token = param.get("token").toString();
        String selectId = param.get("id").toString();
        String resultJSON = dataFetch.search(client,userId,selectId);
        return resultJSON;
    }

    /**
     * 搜索框提示
     * @param param
     * @return
     */
    @PostMapping("/recommendList")
    public Object getSearchBox(@RequestBody @ApiParam("筛选分类id,搜索词") HashMap<String,Object> param){
        String selectedId = param.get("selectedId").toString();
        String searchValue = param.get("searchValue").toString();
        String resultJSON = dataFetch.regSearch(client,selectedId,searchValue);
        System.out.println("******"+"收到请求"+"******");
        return resultJSON;
    }




}
