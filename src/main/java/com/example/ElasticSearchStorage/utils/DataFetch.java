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
     * 根据搜索词进行正则匹配，取5条返回
     * @param client
     * @param selectedId
     * @param searchValue
     * @return
     */
    public String regSearch(TransportClient client,String selectedId,String searchValue){
        List<HashMap<String,String>> resultList = new ArrayList<>();
        SearchResponse response = null;
        String indexName = "es_dw3.0_v2_is_minus_test";
        if ("".equals(searchValue)){
            return JSON.toJSONString(resultList);
        }
        if ("999".equals(selectedId)){
            response = client.prepareSearch(indexName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.boolQuery()
                            .should(QueryBuilders.regexpQuery("KPI_Name.keyword",searchValue+".*"))
                            .should(QueryBuilders.regexpQuery("Subject_Name.keyword",searchValue+".*")))
                    .setFrom(0)
                    .setSize(5)
                    .get();
        }else if ("1".equals(selectedId)){
            response = client.prepareSearch(indexName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.regexpQuery("KPI_Name.keyword",searchValue+".*"))
                    .setFrom(0)
                    .setSize(5)
                    .get();
        }else if ("2".equals(selectedId)){
            response = client.prepareSearch(indexName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.regexpQuery("Subject_Name.keyword",searchValue+".*"))
                    .setFrom(0)
                    .setSize(5)
                    .get();
        }else if ("3".equals(selectedId)){
            response = client.prepareSearch(indexName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.regexpQuery("Report_Name.keyword",searchValue+".*"))
                    .setFrom(0)
                    .setSize(5)
                    .get();
        }

        //获取5条记录
        SearchHits hits = response.getHits();
        System.out.println("hits长度"+hits.getTotalHits());
        if (null != hits || hits.getTotalHits()>0){
            int maxIndex ;
            if (hits.getTotalHits()>5){
                maxIndex = 5;
            }else {
                maxIndex = (int)hits.getTotalHits();
            }
            for (int i=0;i<maxIndex;i++){
                SearchHit hit = hits.getAt(i);
                String type = hit.getType();
                JSONObject jsonObject = JSON.parseObject(hit.getSourceAsString());
                String resultWord = new String();
                if (null != type && "K".equals(type)){
                    resultWord = jsonObject.get("KPI_Name").toString();
                }else if (null != type && "T".equals(type)){
                    resultWord = jsonObject.get("Subject_Name").toString();
                }else if (null != type && "R".equals(type)){
                    resultWord = jsonObject.get("Report_Name").toString();
                }
                HashMap<String,String> map = new HashMap<>();
                map.put("id",String.valueOf(i+1));
                map.put("name",resultWord);
                resultList.add(map);
            }
        }
//        for (SearchHit hit:hits){
//            String type = hit.getType();
//            JSONObject jsonObject = JSON.parseObject(hit.getSourceAsString());
//            String resultWord = new String();
//            if (null != type && "K".equals(type)){
//                resultWord = jsonObject.get("KPI_Name").toString();
//            }else if (null != type && "T".equals(type)){
//                resultWord = jsonObject.get("Subject_Name").toString();
//            }
//            resultList.add(resultWord);
//        }
        String resultJson = JSON.toJSONString(resultList);
        System.out.println("最终结果为："+resultJson);
        return resultJson;
    }

    /**
     *
     * @param client
     * @param userId 用户ID
     * @param selectId 查询类型：指标（1）、专题（2）、报告（3）、全部（999）
     */
    public String search(TransportClient client, String userId, String selectId) {
        SearchResponse response = null;
//        String indexName = "dw3.0_nginx_log_proccessed";
        String indexName = "dw3.0_nginx_log_agg";
//        String indexName = "dw3.0_nginx_log_proccessed_shell";
        String typeName = "";
        if (selectId.equals("999")){
            typeName = "nginxlog";
        }else if (selectId.equals("1")){
            typeName = "indexdetails";
        }else if (selectId.equals("2")){
            typeName = "specialreport";
        }else if (selectId.equals("3")){
            typeName = "report";
        }
        response = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addSort("date", SortOrder.DESC)
                .addSort("MarkName", SortOrder.DESC)
                .addSort("SpecificMark", SortOrder.DESC)
                .setQuery(QueryBuilders.matchQuery("UserID",userId))
//                .setQuery(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.matchQuery("UserID",userId))
//                        .must(QueryBuilders.rangeQuery("date").gte("1500360926000").lte("1500369462000")))
                .setFrom(0)
                .setSize(10)
                .get();
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
            String moduleName = new String();
            if (null!=typeMap && !typeMap.isEmpty()){
                moduleName = typeMap.get("MODULE_NAME");
                resultMap.put("class",moduleName);
                resultMap.put("classId",typeMap.get("MODULE_CODE"));
            }else {
                moduleName = "未知";
                resultMap.put("class","未知");
                resultMap.put("classId","未知");
            }
            resultMap.put("detailUrl","/"+markName);
            resultMap.put("detailId",specificMark);
            if (moduleName.equals("指标")){//指标
                tempMap = elasticSearchService.getKpiNameAcct(specificMark);
                if (null != tempMap && !tempMap.isEmpty()){
                    resultMap.put("detailName",tempMap.get("KPI_NAME"));
                    resultMap.put("detailFlag",tempMap.get("LABEL_TYPE"));
                }else {
                    resultMap.put("detailName","未知");
                    resultMap.put("detailFlag","未知");
                }
            }else if (moduleName.equals("专题")){//专题
                tempMap = elasticSearchService.getSubNameAcct(specificMark);
                if (null != tempMap && !tempMap.isEmpty()){
                    resultMap.put("detailName",tempMap.get("SUBJECT_NAME"));
                    resultMap.put("detailFlag",tempMap.get("LABEL_TYPE"));
                }else {
                    resultMap.put("detailName","未知");
                    resultMap.put("detailFlag","未知");
                }
            }else if (moduleName.equals("报告")){//报告
                tempMap = elasticSearchService.getReportName(specificMark);
                if (null != tempMap && !tempMap.isEmpty()){
                    resultMap.put("detailName",tempMap.get("FILENAME"));
                    resultMap.put("detailFlag","-1");
                }else {
                    resultMap.put("detailName","未知");
                    resultMap.put("detailFlag","未知");
                }
            }else {
                resultMap.put("detailName","未知");
                resultMap.put("detailFlag","未知");
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
     *
     */
    public HashMap<String,Object> getEsSorted(TransportClient client, String userId, String searchType){
        SearchResponse response = null;
        String alsIndex = "als_predict";
        String alsType = "als_type";
        String codeIndex = "es_dw3.0_v2_is_minus_test";
        response = client.prepareSearch(alsIndex)
                .setTypes(alsType)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addSort("Rating", SortOrder.DESC)
                .setQuery(QueryBuilders.matchQuery("UserID",userId))
                .get();
        SearchHits hits = response.getHits();
        List<HashMap<String,String>> allList = new ArrayList<>();
        List<HashMap<String,String>> kpiList = new ArrayList<>();
        List<HashMap<String,String>> subList = new ArrayList<>();
        for (SearchHit hit:hits){
            JSONObject jsonObject = JSON.parseObject(hit.getSourceAsString());
            String code = jsonObject.get("Code").toString();
            HashMap<String,String> detailMap= getDetailList(code,codeIndex,client);
            if (detailMap.get("MARKTYPE").equals("1") && kpiList.size()<4){
                kpiList.add(detailMap);
            }else if (detailMap.get("MARKTYPE").equals("2") && subList.size()<4){
                subList.add(detailMap);
            }
            if (allList.size()<4){
                allList.add(detailMap);
            }
        }
        HashMap<String,Object> resultMap = new HashMap<>();
        resultMap.put("all",allList);
        resultMap.put("kpi",kpiList);
        resultMap.put("sub",subList);
        return resultMap;
    }

    /**
     * 根据指标或专题code到is_minus_test中进行全文匹配，得到对应指标或专题详细信息
     * @param code
     * @param codeIndex
     * @param client
     * @return
     */
    public HashMap<String,String> getDetailList(String code, String codeIndex, TransportClient client){
        SearchResponse response = null;
        response = client.prepareSearch(codeIndex)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("KPI_Code",code))
                        .should(QueryBuilders.matchQuery("Subject_Code",code)))
                .get();
        SearchHits hits = response.getHits();
        HashMap<String,String> resultMap = new HashMap<>();
        if (null != hits && hits.getTotalHits()>0){
            SearchHit hit = hits.getAt(0);
            String type = hit.getType();
            JSONObject jsonObject = JSON.parseObject(hit.getSourceAsString());
            if (type.equals("K")){
                resultMap.put("MARKTYPE","1");
                resultMap.put("BM",jsonObject.getString("KPI_Code"));
                resultMap.put("KSNAME",jsonObject.getString("KPI_Name"));
                resultMap.put("IS_MINUS",jsonObject.getString("IS_MINUS"));
                resultMap.put("MARKNAME","/indexDetails");
            }else if (type.equals("T")){
                resultMap.put("MARKTYPE","2");
                resultMap.put("BM",jsonObject.getString("Subject_Code"));
                resultMap.put("KSNAME",jsonObject.getString("Subject_Name"));
                resultMap.put("MARKNAME",jsonObject.getString("MARKNAME"));//完整格式，/themeMonthCheck
            }
            String acctType = jsonObject.getString("Acct_Type");
            if (acctType.equals("日报")){
                resultMap.put("ACCT_TYPE","日");
            }else if(acctType.equals("月报")){
                resultMap.put("ACCT_TYPE","月");
            }
            resultMap.put("DATASOURCE","ALS");
        }
        return resultMap;
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
