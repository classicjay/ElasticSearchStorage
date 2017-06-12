package com.example.ElasticSearchStorage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ElasticSearchStorage.service.ElasticSearchService;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.ElasticSearchStorage.ElasticSearchStorageApplication.*;

/**
 * <p>Title: BONC -  DataFetch</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
@Service
public class DataFetch {
    private static Logger logger = Logger.getLogger(DataFetch.class);

    @Autowired
    ElasticSearchService elasticSearchService;
    /**
     *
     * @param client
     * @param userId 用户ID
     * @param selectId 查询类型：指标（1）、专题（2）、报告（3）、全部（999）
     */
    public String search(TransportClient client, String userId, String selectId) {
        SearchResponse response = null;
        String indexName = "dw3.0_nginx_log_proccessed";
        String typeName = "nginxlog";
        if (selectId.equals("999")){
            response = client.prepareSearch(indexName)
                    .setTypes(typeName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .addSort("date", SortOrder.DESC)
                    .addSort("MarkName", SortOrder.DESC)
                    .addSort("SpecificMark", SortOrder.DESC)
                    .setQuery(QueryBuilders.matchQuery("UserID",userId))
                    .setFrom(0)
                    .setSize(10)
                    .get();
        }else {
            response = client.prepareSearch(indexName)
                    .setTypes(typeName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .addSort("date", SortOrder.DESC)
                    .addSort("MarkName", SortOrder.DESC)
                    .addSort("SpecificMark", SortOrder.DESC)
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.matchQuery("UserID",userId))
                            .must(QueryBuilders.matchQuery("MarkName",urlTypeCodeMap.get(selectId))))
                    .setFrom(0)
                    .setSize(10)
                    .get();
        }
        SearchHits hits = response.getHits();
        logger.info("查询到"+ hits.getTotalHits() +"条记录");
        List<HashMap<String,Object>> resultList = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZZ", Locale.ENGLISH);
        for (SearchHit hit:hits){
            JSONObject jsonObject = JSON.parseObject(hit.getSourceAsString());
            String markName = jsonObject.get("MarkName").toString();
            String specificMark = jsonObject.get("SpecificMark").toString();
            HashMap<String,Object> resultMap = new HashMap<>();
            HashMap<String,String> tempMap = new HashMap<>();
            HashMap<String,String> typeMap = new HashMap<>();
            typeMap = elasticSearchService.getSelectType("/"+markName);
            if (null!=typeMap && !typeMap.isEmpty()){
                resultMap.put("class",typeMap.get("MODULE_NAME"));
                resultMap.put("classId",typeMap.get("MODULE_CODE"));
            }else {
                resultMap.put("class","未知");
                resultMap.put("classId","未知");
            }
            resultMap.put("detailUrl","/"+markName);
            resultMap.put("detailId",specificMark);
            if (markName.equals("indexDetails")){//指标
                tempMap = elasticSearchService.getKpiNameAcct(specificMark);
                if (null != tempMap && !tempMap.isEmpty()){
                    resultMap.put("detailName",tempMap.get("KPI_NAME"));
                    resultMap.put("detailFlag",tempMap.get("LABEL_TYPE"));
                }else {
                    resultMap.put("detailName","未知");
                    resultMap.put("detailFlag","未知");
                }
            }else if (markName.equals("specialReport")){//专题
                tempMap = elasticSearchService.getSubNameAcct(specificMark);
                if (null != tempMap && !tempMap.isEmpty()){
                    resultMap.put("detailName",tempMap.get("SUBJECT_NAME"));
                    resultMap.put("detailFlag",tempMap.get("LABEL_TYPE"));
                }else {
                    resultMap.put("detailName","未知");
                    resultMap.put("detailFlag","未知");
                }
            }else if (markName.equals("reportPPT")){//报告
                tempMap = elasticSearchService.getReportName(specificMark);
                if (null != tempMap && !tempMap.isEmpty()){
                    resultMap.put("detailName",tempMap.get("FILENAME"));
                    resultMap.put("detailFlag","-1");
                }else {
                    resultMap.put("detailName","未知");
                    resultMap.put("detailFlag","未知");
                }
            }
            resultList.add(resultMap);
        }
//        resultList = mergeRedundancy(resultList);
        HashMap<String,Object> resultMap = new HashMap<>();
        if (!resultList.isEmpty()){
            resultMap.put("recentVisitList",resultList);
        }
        String resultJson = JSON.toJSONString(resultMap);
        logger.info("最终结果为："+resultJson);
        return resultJson;
    }

    /**
     * 合并相邻Map
     * @param originalList
     * @return
     */
    public static List<HashMap<String,Object>> mergeRedundancy(List<HashMap<String,Object>> originalList){
        List<HashMap<String,Object>> resultList = new ArrayList<>();
        for (int i=0;i<originalList.size();i++){
            //保存当前元素的MarkName和SpecificMark值
            String currentMark = (String) originalList.get(i).get("detailUrl");
            String currentSpecific = (String) originalList.get(i).get("detailId");
            //保存下一个元素的MarkName和SpecificMark值
            String nextMark = (String) originalList.get(i+1).get("detailUrl");
            String nextSpecific = (String) originalList.get(i+1).get("detailId");
            //保存resultList中末尾元素的MarkName和SpecificMark值
            String resLastMark = null;
            String resLastSpecific = null;
            if (!resultList.isEmpty()){
                resLastMark = (String) resultList.get(resultList.size()-1).get("detailUrl");
                resLastSpecific = (String) resultList.get(resultList.size()-1).get("detailId");
            }
            if (currentMark.equals(nextMark) && currentSpecific.equals(nextSpecific)){
                if (resultList.isEmpty() || !resultList.isEmpty() && (!resLastMark.equals(currentMark)||!resLastSpecific.equals(currentSpecific))){
                    resultList.add(originalList.get(i));
                }
            }else if (i!=originalList.size()-2){
                if (resultList.isEmpty() || !resultList.isEmpty() && (!resLastMark.equals(currentMark)||!resLastSpecific.equals(currentSpecific))){
                    resultList.add(originalList.get(i));
                }
            }else {
                resultList.add(originalList.get(originalList.size()-1));
            }
            if (i==originalList.size()-2){
                break;
            }
        }
        return resultList;
    }

}
