package com.example.ElasticSearchStorage.service;

import com.example.ElasticSearchStorage.mapper.ElasticSearchMapper;
import com.example.ElasticSearchStorage.utils.DataFetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.example.ElasticSearchStorage.ElasticSearchStorageApplication.client;

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

    @Autowired
    DataFetch dataFetch;

    @Autowired
    private RestTemplate restTemplate;
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

    public List<HashMap<String,Object>> getIntelligentRecommend(HashMap<String,Object> paramMap){
        // 1.获取用户周记录数
        // 2.获取用户所在部门的M和Q
        // 3.比较M和Q
        // 4.判断周记录数在哪个区间
        // 5.按照比例进行组合，得到一个list<map>存放code
        // 6.根据code通过post请求到指标或专题页进行取数
        String userId = paramMap.get("userId").toString();
        String searchType = paramMap.get("searchType").toString();
        HashMap<String,Object> esResMap = new HashMap<>();
        esResMap = dataFetch.getEsSorted(client,userId,searchType);
        List<HashMap<String,String>> alsList = new ArrayList<>();
        if (searchType.equals("999")){
            alsList = (List<HashMap<String,String>>) esResMap.get("all");
        }else if (searchType.equals("1")){
            alsList = (List<HashMap<String,String>>) esResMap.get("kpi");
        }else if (searchType.equals("2")){
            alsList = (List<HashMap<String,String>>) esResMap.get("sub");
        }
        System.out.println("协同alsList为："+alsList+"*****");
        HashMap<String,Object> oneWeekCountMap = new HashMap<>();
        HashMap<String,Object> userDeptProv = new HashMap<>();
        HashMap<String,String> queryWeekMap = new HashMap<>();
        queryWeekMap.put("userId",userId);
        queryWeekMap.put("searchType",searchType);
        oneWeekCountMap = elasticSearchMapper.getOneWeekLogCount(queryWeekMap);
        userDeptProv = elasticSearchMapper.getDeptProv(userId);
        long oneWeekCount = 0;
        String deptId = userDeptProv.get("DEPTID").toString();
        String provId = userDeptProv.get("PROV_ID").toString();
        if (null != oneWeekCountMap && !oneWeekCountMap.isEmpty()){
            oneWeekCount = Long.parseLong(oneWeekCountMap.get("WEEKCOUNT").toString());
        }
        HashMap<String,String> queryMQMap = new HashMap<>();
        queryMQMap.put("deptId",deptId);
        queryMQMap.put("provId",provId);
        queryMQMap.put("searchType",searchType);
        HashMap<String,Object> mQMap = new HashMap<>();
        mQMap = elasticSearchMapper.getMQ(queryMQMap);
        //部门排序前四
        List<HashMap<String,String>> depList = new ArrayList<>();
        depList = elasticSearchMapper.getDepSorted(queryMQMap);
        System.out.println("部门depList为："+depList+"*****");
        long m = 0;
        long q1 = 0;
        long q2 = 0;
        long q3 = 0;
        //存放4个code的list
        List<HashMap<String,String>> blendList = new ArrayList<>();
        if (null == mQMap || mQMap.isEmpty()){
            //统计表中没有这个部门的MQ数据，全部走无脑推
            System.out.println("-----------当前用户为新用户，此时统计表里没有对应部门，全走无脑推");
            blendList = elasticSearchMapper.getReserveHotSpot(searchType);
        }else {
            m = Long.parseLong(mQMap.get("M").toString());
            q1 = Long.parseLong(mQMap.get("Q1").toString());
            q2 = Long.parseLong(mQMap.get("Q2").toString());
            q3 = Long.parseLong(mQMap.get("Q3").toString());

            System.out.println("一周总记录数"+oneWeekCount+"***");
            System.out.println("M值"+m+"***");
            if (oneWeekCount<m){//全部走部门排序
                alsList.subList(0,alsList.size()).clear();
                if (depList.size()>=4){
                    depList.subList(4,depList.size()).clear();
                }
                System.out.println("协同和部门混合比例："+"0:4");
            }else if (oneWeekCount >= m && oneWeekCount < getMax(m,q1)){
                alsList.subList(1,alsList.size()).clear();
                removeRepeat(alsList,depList);
                if (depList.size()>=3){
                    depList.subList(3,depList.size()).clear();
                }
                System.out.println("协同和部门混合比例："+"1:3");
            }else if (oneWeekCount >= getMax(m,q1) && oneWeekCount < getMax(m,q2)){
                alsList.subList(2,alsList.size()).clear();
                removeRepeat(alsList,depList);
                if (depList.size()>=2){
                    depList.subList(2,depList.size()).clear();
                }
                System.out.println("协同和部门混合比例："+"2:2");
            }else if (oneWeekCount >= getMax(m,q2) && oneWeekCount < getMax(m,q3)){
                alsList.subList(3,alsList.size()).clear();
                removeRepeat(alsList,depList);
                if (depList.size()>=1){
                    depList.subList(1,depList.size()).clear();
                }
                System.out.println("协同和部门混合比例："+"3:1");
            }else if (oneWeekCount >= getMax(m,q3)){
//            alsList.subList(4,alsList.size()).clear();
                depList.subList(0,depList.size()).clear();
                System.out.println("协同和部门混合比例："+"4:0");
            }
            loopAdd(blendList,alsList);
            loopAdd(blendList,depList);
        }
        System.out.println("blendList为"+blendList);
//        String paramStr = new String();
        List<HashMap<String,Object>> dataList = new ArrayList<>();
        String maxDayDate = new String();
        for (int i=0;i<blendList.size();i++){
            HashMap<String,String> map = blendList.get(i);
            String markType = map.get("MARKTYPE");
            if (markType.equals("1")){
//                paramStr = "-1,-1,"+map.get("BM");
//                HashMap<String,Object> resMap = (HashMap<String, Object>) restTemplate.postForObject("http://DW3-NEWQUERY-HOMEPAGE-ZUUL-TEST/index/indexForHomepage/dataOfAllKpi",paramStr,Object.class);
                HashMap<String,Object> sigMap = new HashMap<>();
                HashMap<String,Object> detailMap = new HashMap<>();
//                maxDayDate = elasticSearchMapper.getMaxDayDate(map.get("BM"));
                maxDayDate = elasticSearchMapper.getMaxDate(map.get("BM"));
                String date = new String();
                if (null != maxDayDate && !"".equals(maxDayDate)){
                    maxDayDate= maxDayDate.replace("-", "");
                    date=maxDayDate.substring(0,4)+"年"+maxDayDate.substring(4,6)+"月"+maxDayDate.substring(6,8)+"日";
                }else {
                    date = "-";
                }
//                detailMap.put("date",resMap.get("date").toString());
                detailMap.put("date",date);
                detailMap.put("dayOrMonth",map.get("ACCT_TYPE"));
                detailMap.put("markName","指标");
//                detailMap.put("chartType",resMap.get("chartType").toString());
                detailMap.put("title",map.get("KSNAME"));
//                detailMap.put("chart",resMap.get("chart"));
                sigMap.put("ord",String.valueOf(i+1));
                sigMap.put("markType","1");
                sigMap.put("id",map.get("BM"));
//                sigMap.put("isMinus",map.get("IS_MINUS"));
//                sigMap.put("isPercentage",map.get("IS_PERCENTAGE"));
                sigMap.put("url","/indexDetails");
                sigMap.put("data",detailMap);
                dataList.add(sigMap);
            }else if (markType.equals("2")){
//                paramStr = map.get("BM");
//                HashMap<String,Object> resMap = (HashMap<String, Object>) restTemplate.postForObject("http://DW3-NEWQUERY-HOMEPAGE-ZUUL-TEST/subject/specialForHomepage/icon",paramStr,Object.class);
                HashMap<String,Object> sigMap = new HashMap<>();
                HashMap<String,Object> detailMap = new HashMap<>();
                sigMap.put("ord",String.valueOf(i+1));
                sigMap.put("id",map.get("BM"));
//                sigMap.put("url",resMap.get("url").toString());
                sigMap.put("url",map.get("MARKNAME"));
                sigMap.put("markType","2");
                detailMap.put("tabName",map.get("ACCT_TYPE"));
                detailMap.put("title",map.get("KSNAME"));
                detailMap.put("type","专题");
//                detailMap.put("src",resMap.get("src").toString());
                sigMap.put("data",detailMap);
                dataList.add(sigMap);
            }
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<dataList为："+dataList+">>>>>>>>>>>>>>>>>>>>");
        return dataList;
    }

    private void loopAdd(List<HashMap<String,String>> blendList, List<HashMap<String,String>> loopList){
        if (null != loopList && !loopList.isEmpty()){
            for (HashMap<String,String> map:loopList){
                blendList.add(map);
            }
        }
    }

    private long getMax(long num1,long num2){
        if (num1 - num2 >0){
            return num1;
        }else{
            return num2;
        }
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

    private void removeRepeat(List<HashMap<String,String>> alsList,List<HashMap<String,String>> depList){
        Iterator<HashMap<String,String>> iterator = depList.iterator();
        while (iterator.hasNext()){
            HashMap<String,String> depMap = iterator.next();
            String depBM = depMap.get("BM");
            for (HashMap<String,String> alsMap:alsList){
                String alsBM = alsMap.get("BM");
                if (depBM.equals(alsBM)){
                    iterator.remove();
                }
            }
        }
    }
}
