package com.example.ElasticSearchStorage.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * <p>Title: BONC -  LoadXML</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
public class LoadXML {
    String ipAddress;
    String port;
    int counts;
    String clusterName;
    String colName;
    String filePath;
    String source;
    String id;
    HashMap<Integer, String> colMap = new HashMap<>();
    String routing;
    boolean hasParent;
    String parent;

    /**
     *
     * @param path XML路径
     * @return
     */
    public void readXML(String path){
        XMLConfiguration xmlConfiguration = new XMLConfiguration();
        xmlConfiguration.setDelimiterParsingDisabled(true);
        xmlConfiguration.setAttributeSplittingDisabled(true);
        try {
            xmlConfiguration.load(path);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        xmlConfiguration.setEncoding("UTF-8");
        xmlConfiguration.setThrowExceptionOnMissing(true);//获取不到指定属性则报错
        HashMap<String,Object> rsMap = new HashMap<>();
        ipAddress = xmlConfiguration.getString("esconf.IP");
        port = xmlConfiguration.getString("esconf.Port");
        clusterName = xmlConfiguration.getString("esconf.ClusterName");
        filePath = xmlConfiguration.getString("table.FilePath");
        id = xmlConfiguration.getString("table.id");
        counts = xmlConfiguration.getInt("table.Counts");
        routing =  xmlConfiguration.getString("table.routing");
        hasParent = xmlConfiguration.getBoolean("table.hasParent");
        parent = xmlConfiguration.getString("table.Parent");
        for (int i=0;;i++){
            try {
                colMap.put(xmlConfiguration.getInt("table.Source("+i+")"),xmlConfiguration.getString("table.Source(" + i+")[@name]") );
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
