package com.example.ElasticSearchStorage.service;

import com.example.ElasticSearchStorage.mapper.ElasticSearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public HashMap<String,Object> getHotSpot(HashMap<String,Object> paramMap){
        String userId = paramMap.get("userId").toString();
        HashMap<String,String> authMap = new HashMap<>();
        authMap = elasticSearchMapper.getAuthority(userId);
        if (null == authMap){
            System.out.println("此用户不在权限表中");
            return null;
        }
        List<String> kpiAuthList = new ArrayList<>();
        List<String> subAuthList = new ArrayList<>();
        List<String> repAuthList = new ArrayList<>();
        if (null != authMap.get("K_AUTHORITY") && !"".equals(authMap.get("K_AUTHORITY"))){
            String[] kpiAuthArr = authMap.get("K_AUTHORITY").split(",");
            kpiAuthList = Arrays.asList(kpiAuthArr);
        }
        if (null != authMap.get("T_AUTHORITY") && !"".equals(authMap.get("T_AUTHORITY"))){
            String[] subAuthArr = authMap.get("T_AUTHORITY").split(",");
            subAuthList = Arrays.asList(subAuthArr);
        }
        if (null != authMap.get("R_AUTHORITY") && !"".equals(authMap.get("R_AUTHORITY"))){
            String[] repAuthArr = authMap.get("R_AUTHORITY").split(",");
            repAuthList = Arrays.asList(repAuthArr);
        }
        List<HashMap<String,String>> kpiSortedList = new ArrayList<>();
        kpiSortedList = elasticSearchMapper.getKpiSortedData();
        List<HashMap<String,String>> subSortedList = new ArrayList<>();
        subSortedList = elasticSearchMapper.getSubjectSortedData();
        authFilter(kpiAuthList,kpiSortedList);
        authFilter(subAuthList,subSortedList);

        //此时kpiSortedList和subSortedList已经根据权限筛选完毕
        HashMap<String,Object> resultMap = new HashMap<>();
        HashMap<String,Object> kpiTitleListMap = new HashMap<>();
        List<HashMap<String,Object>> svgList = new ArrayList<>();

        HashMap<String,Object> kpiMap = new HashMap<>();
        kpiTitleListMap.put("titleClassId","1");
        kpiTitleListMap.put("titleClassName","热门内容");
        kpiTitleListMap.put("list",processList(kpiSortedList));
        kpiMap.put("id","1");
        kpiMap.put("name","指标");
        kpiMap.put("titleList",kpiTitleListMap);

        HashMap<String,Object> subTitleListMap = new HashMap<>();
        subTitleListMap.put("titleClassId","1");
        subTitleListMap.put("titleClassName","热门内容");
        subTitleListMap.put("list",processList(subSortedList));
        HashMap<String,Object> subMap = new HashMap<>();
        subMap.put("id","2");
        subMap.put("name","专题");
        subMap.put("titleList",subTitleListMap);

        svgList.add(kpiMap);
        svgList.add(subMap);
        resultMap.put("svgList",svgList);
        return resultMap;
    }

    public HashMap<String,Object> getIntelligentRecommend(HashMap<String,Object> paramMap){

        return null;
    }

    private List<HashMap<String,String>> processList(List<HashMap<String,String>> paramList){
        if (paramList.size()>3){
            paramList.subList(3,paramList.size()).clear();
        }
        List<HashMap<String,String>> resultList = new ArrayList<>();
        for (int i=0;i<paramList.size();i++){
            String markName = paramList.get(i).get("MARKNAME");
            if (markName.equals("specialreport")){
                markName = "specialReport";
            }else if (markName.equals("indexdetails")){
                markName = "indexDetails";
            }else if (markName.equals("thememonthcheck")){
                markName = "ThemeMonthCheck";
            }
            HashMap<String,String> sigMap = new HashMap<>();
            sigMap.put("titleId",paramList.get(i).get("BM"));
            sigMap.put("titleName",paramList.get(i).get("KS_NAME"));
            sigMap.put("titleUrl","/"+markName);
            sigMap.put("flag",paramList.get(i).get("LABEL_TYPE"));
            resultList.add(sigMap);
        }
        return resultList;
    }

    /**
     * 根据权限过滤过滤
     * @param authList 需要剔除的指标list
     * @param sortedList 生数据list
     * @return
     */
    private void authFilter(List<String> authList,List<HashMap<String,String>> sortedList){
        if (null != authList && authList.size()!=0){
            Iterator<HashMap<String,String>> iterator = sortedList.iterator();
            while (iterator.hasNext()){
                HashMap<String,String> sigMap = iterator.next();
                for (String auth:authList){
                    if (sigMap.get("BM").equals(auth)){
                        iterator.remove();
                    }
                }
            }
        }
    }
}
