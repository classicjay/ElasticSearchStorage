package com.example.ElasticSearchStorage.controller;

import com.example.ElasticSearchStorage.utils.DataFetch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/es")
public class FetchController {

    /**
     *
     * @param param 索引，类型，用户ID，查询类型：指标（1）、专题（2）、报告（3）、全部（999）
     * @return
     */
    @PostMapping("/fetch")
    public Object fetchData(@RequestBody @ApiParam("请求参数") String param){
        String[] paramArr = param.split(",");
        String resultJson = null;
        if (paramArr.length == 2){
            String userId = paramArr[0];
            String selectId = paramArr[1];
            resultJson = DataFetch.search(client,userId,selectId);
            return resultJson;
        }else {
            return "参数错误";
        }
    }
}
