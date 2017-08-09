package com.example.ElasticSearchStorage.controller;

import com.alibaba.fastjson.JSON;
import com.example.ElasticSearchStorage.service.ElasticSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: BONC -  HotSpotController</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
@Controller
@CrossOrigin(origins = "*")
@Api(value="ElasticSearch",description="热点关注")
@RequestMapping("/HomePage")
public class HotSpotController {

    @Autowired
    ElasticSearchService elasticSearchService;

    /**
     * 热门推荐
     * @param param
     * @param model
     * @return
     */
    @PostMapping("/titleList")
    public String getHotSpot(@ApiParam("用户id,登录令牌")@RequestBody HashMap<String,Object> param, Model model){
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap = elasticSearchService.getHotSpot(param);
        model.addAttribute("dataMap",dataMap);
        return "titleList";
    }


}
