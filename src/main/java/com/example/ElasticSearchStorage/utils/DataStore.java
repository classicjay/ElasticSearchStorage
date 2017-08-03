package com.example.ElasticSearchStorage.utils;


import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: BONC -  DataStore</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 *
 * @author zhaojie
 * @version 1.0.0
 */
public class DataStore {
    TransportClient client;
    LoadXML loadXML;
    private static Logger logger = org.apache.log4j.Logger.getLogger(DataStore.class);

    public DataStore(LoadXML loadXML) {
        this.loadXML = loadXML;
    }

//    public static void main(String[] args) {
//        LoadXML loadXML = new LoadXML();
//        loadXML.readXML("");
//        DataStore dataStore = new DataStore(loadXML);
//        dataStore.createClient();
//    }

    /**
     * Bulk批量导入
     * @param indexName
     * @param typeName
     * @param path
     */
    public void bulkDataStorage(String indexName,String typeName,String path){
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        IndexRequestBuilder indexRequestBuilder;
        BulkProcessor bulkProcessor = BulkProcessor.builder(client,
                new BulkProcessor.Listener(){
                    //可以从BulkRequest中获取请求信息request.requests()或者请求数量request.numberOfActions()。
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {}
                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        logger.info("executionId "+executionId);
                        logger.info("提交" + response.getItems().length + "个文档，用时"
                                + response.getTookInMillis() + "ms"
                                + (response.hasFailures() ? " 有文档提交失败！" : ""));
                        logger.info("response"+response.buildFailureMessage());
                    }
                    //出错时执行
                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        logger.info(" 有文档提交失败！after failure=" + failure);
                    }
                })
                //当请求超过10000个（default=1000）或者总大小超过1GB（default=5MB）时，触发批量提交动作。另外每隔5秒也会提交一次（默认不会根据时间间隔提交）。
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
        File file = new File(path);
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        inputStreamReader = new InputStreamReader(fileInputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        int count = 0;
        String[] splitArr = null;
        try {
            while ((line = bufferedReader.readLine()) !=null){
                count++;
                splitArr = line.split("\\|",-1);
                try {
                    XContentBuilder data = XContentFactory.jsonBuilder().startObject();
                    Iterator iterator = loadXML.colMap.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry entry = (Map.Entry)iterator.next();
                        data = data.field((String) entry.getValue(),splitArr[(Integer) entry.getKey()]);
                    }
                    String dataStr = data.endObject().string();
                    IndexRequest indexrequest;
                    if(loadXML.hasParent)
                    {
                        indexrequest = new IndexRequest(indexName, typeName,splitArr[Integer.parseInt(loadXML.id)]).source(dataStr).routing(splitArr[Integer.parseInt(loadXML.routing)]).parent(splitArr[Integer.parseInt(loadXML.id)]);
                    }else
                    {
                        indexrequest = new IndexRequest(indexName, typeName,splitArr[Integer.parseInt(loadXML.id)]).source(dataStr).routing(splitArr[Integer.parseInt(loadXML.routing)]);
                    }
                    bulkProcessor.add(indexrequest);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                count++;
                if(count%10000==0)
                {
                    logger.info("已提交："+count/10000);
                    bulkProcessor.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        logger.info("it took " + (endTime - startTime) /1000 + "s,it contains " + count + " lines");
        try {
            bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
            bulkProcessor.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.close();
        try {
            fileInputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建映射
     * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
     * @param indexName 索引名
     * @param typeName 类型名
     * @throws Exception
     */
    public void createMapping(String indexName,String typeName)throws Exception{
        XContentBuilder mapping= XContentFactory.jsonBuilder().startObject();
        String mappingStr;
        if (loadXML.hasParent){
            mapping = mapping.startObject(typeName).startObject("_parent").field("type",loadXML.parent).endObject().startObject("properties");
            Iterator iterator = loadXML.colMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                mapping = mapping.startObject((String) entry.getValue()).field("type","text").endObject();
            }
            mappingStr = mapping.endObject().endObject().endArray().string();
        }else {
            mapping = mapping.startObject(typeName).startObject("properties");
            Iterator iterator = loadXML.colMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry)iterator.next();
                mapping = mapping.startObject((String)entry.getValue()).field("type", "text").endObject();
            }
            mappingStr = mapping.endObject().endObject().endObject().string();
        }

        PutMappingRequest request = Requests.putMappingRequest(indexName).type(typeName).source(mappingStr);
        client.admin().indices().putMapping(request).actionGet();
    }

    /**
     * 创建索引
     * @param indexName
     */
    public void createIndex(String indexName) {
        client.admin().indices().create(new CreateIndexRequest(indexName))
                .actionGet();
    }

    /**
     * 创建Transport连接
     */
    public void createClient(){
        TransportClient client = null;
        Settings settings = Settings.builder()
                .put("cluster.name", "Logs_Collect_V1")
                .put("client.transport.sniff", true )
                .build();
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.2"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.3"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.5"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


}
