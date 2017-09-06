package com.example.ElasticSearchStorage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: BONC -  ElasticSearchMapper</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
@Mapper
public interface ElasticSearchMapper {
    /**
     * 获取所有类型
     * @return
     */
    public List<HashMap<String,String>> getAllType();

    /**
     * 获取查询类型名称
     * @param markName
     * @return
     */
    public HashMap<String,String> getSelectType(String markName);

    /**
     * 获取指标编码对应名称以及日月标识
     * @param kpiCode
     * @return
     */
    public HashMap<String,String> getKpiNameAcct(String kpiCode);

    /**
     * 获取专题编码对应名称以及日月标识
     * @param subjectCode
     * @return
     */
    public HashMap<String,String> getSubNameAcct(String subjectCode);

    /**
     * 获取报告编码对应名称
     * @param reportCode
     * @return
     */
    public HashMap<String,String> getReportName(String reportCode);

    public HashMap<String,String> getAuthority(@Param(value = "userId")String userId);

    public List<HashMap<String,String>> getKpiSortedData();

    public List<HashMap<String,String>> getSubjectSortedData();

    public HashMap<String,Object> getOneWeekLogCount(HashMap<String,String> paramMap);

    public HashMap<String,Object> getDeptProv(@Param(value = "userId") String userId);

    public HashMap<String,Object> getMQ(HashMap<String,String> paramMap);

    public List<HashMap<String,String>> getDepSorted(HashMap<String,String> paramMap);

    public String getMaxDayDate(@Param(value = "kpiCode") String kpiCode);

    public String getMaxMonthDate(@Param(value = "kpiCode") String kpiCode);

    public String getMaxDate(@Param(value = "kpiCode") String kpiCode);

    public List<HashMap<String,String>> getReserveHotSpot(@Param(value = "searchType") String searchType);

}