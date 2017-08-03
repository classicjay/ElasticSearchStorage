//package com.example.ElasticSearchStorage.utils;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.LineIterator;
//import org.apache.log4j.Logger;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
//
///**
// * <p>Title: BONC -  FileReader</p>
// * <p>Description:  </p>
// * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
// * <p>Company: 北京东方国信科技股份有限公司 </p>
// *
// * @author zhaojie
// * @version 1.0.0
// */
//public class FileReader {
//
//    private File file;
//    private String splitCharactor;
//    private Map<String, Class<?>> colNames;
//    private static final Logger LOG = Logger.getLogger(FileReader.class);
//
//    /**
//     *
//     * @param file 文件名
//     *
//     * @param splitCharactor 拆分字符
//     *
//     * @param colNames  主键名称
//     *
//     */
//    public FileReader(File file, String splitCharactor, Map<String, Class<?>> colNames)
//    {
//        this.file = file;
//        this.splitCharactor = splitCharactor;
//        this.colNames = colNames;
//    }
//
//    /**
//     * 读取文件
//     *
//     * @return
//     * @throws Exception
//     */
//    public List<Map<String, Object>> readFile() throws Exception
//    {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (!file.isFile())
//        {
//            throw new Exception("文件"+file.getName()+"并不存在");
//        }
//        LineIterator lineIterator = null;
//        try
//        {
//            lineIterator = FileUtils.lineIterator(file, "UTF-8");
//            while (lineIterator.hasNext())
//            {
//                String line = lineIterator.next();
//                String[] values = line.split(splitCharactor);
//                if (colNames.size() != values.length)
//                {
//                    continue;
//                }
//                Map<String, Object> map = new HashMap<String, Object>();
//                Iterator<Map.Entry<String, Class<?>>> iterator = colNames.entrySet()
//                        .iterator();
//                int count = 0;
//                while (iterator.hasNext())
//                {
//                    Map.Entry<String, Class<?>> entry = iterator.next();
//                    Object value = values[count];
//                    if (!String.class.equals(entry.getValue()))
//                    {
//                        value = entry.getValue().getMethod("valueOf", String.class)
//                                .invoke(null, value);
//                    }
//                    map.put(entry.getKey(), value);
//                    count++;
//                }
//                list.add(map);
//            }
//        }
//        catch (IOException e)
//        {
//            LOG.error("文件读取错误" + e.toString(), e);
//        }
//        finally
//        {
//            LineIterator.closeQuietly(lineIterator);
//        }
//        return list;
//    }
//}
