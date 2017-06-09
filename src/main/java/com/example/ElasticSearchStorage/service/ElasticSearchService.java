package com.example.ElasticSearchStorage.service;

import com.example.ElasticSearchStorage.mapper.ElasticSearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: BONC -  ElasticSearchService</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
@Service
public class ElasticSearchService {
    @Autowired
    ElasticSearchMapper elasticSearchMapper;

    /**
     * 获取全部类型
     * @return
     */
    public List<HashMap<String,String>> getAllType(){
        List<HashMap<String,String>> resultList = new ArrayList<>();
        resultList = elasticSearchMapper.getAllType();
        return resultList;
    }

    /**
     * 获取查询类型名称
     * @param markName
     * @return
     */
    public HashMap<String,String> getSelectType(String markName){
        HashMap<String,String> resultMap = new HashMap<>();
        resultMap = elasticSearchMapper.getSelectType(markName);
        return resultMap;
    }

    /**
     * 获取指标编码对应名称以及日月标识
     * @param kpiCode
     * @return
     */
    public HashMap<String,String> getKpiNameAcct(String kpiCode){
        HashMap<String,String> resultMap = new HashMap<>();
        resultMap = elasticSearchMapper.getKpiNameAcct(kpiCode);
        return resultMap;
    }

    /**
     * 获取专题编码对应名称以及日月标识
     * @param subjectCode
     * @return
     */
    public HashMap<String,String> getSubNameAcct(String subjectCode){
        HashMap<String,String> resultMap = new HashMap<>();
        resultMap = elasticSearchMapper.getSubNameAcct(subjectCode);
        return resultMap;
    }

    /**
     * 获取报告编码对应名称
     * @param reportCode
     * @return
     */
    public HashMap<String,String> getReportName(String reportCode){
        HashMap<String,String> resultMap = new HashMap<>();
        resultMap = elasticSearchMapper.getReportName(reportCode);
        return resultMap;
    }


}
