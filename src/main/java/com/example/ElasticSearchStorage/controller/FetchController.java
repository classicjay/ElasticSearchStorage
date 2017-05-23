package com.example.ElasticSearchStorage.controller;

import com.example.ElasticSearchStorage.utils.DataFetch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
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
@Api(value="ElasticSearch",description="EarthShaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaker")
@RequestMapping("/es")
public class FetchController {

    /**
     *
     * @param param 索引，类型，用户ID，查询类型：报告（Reqort4G）、专题（）、指标（）、全部（）
     * @return
     */
    @PostMapping("/fetch")
    public Object fetchData(@RequestBody @ApiParam("请求参数") String param){
        String[] paramArr = param.split(",");
        List<HashMap<String,Object>> resultList = new ArrayList<>();
        if (paramArr.length == 4){
            String index = paramArr[0];
            String type = paramArr[1];
            String userId = paramArr[2];
            String fetchType = paramArr[3];
            resultList = DataFetch.search(client,index,type,userId,fetchType);
            return resultList;
        }else {
            return "参数错误";
        }
    }
}
