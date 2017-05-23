package com.example.ElasticSearchStorage;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class ElasticSearchStorageApplication {

	public static TransportClient client;

	public static void main(String[] args) throws UnknownHostException{
		SpringApplication.run(ElasticSearchStorageApplication.class, args);
		Settings settings = Settings.builder()
				.put("cluster.name", "Logs_Collect_V1")//设置ES实例的名称
				.put("client.transport.sniff", true)//自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
				.build();
		if(client == null) {
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.2"), 9300))//此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.3"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.5"), 9300));
		}
	}
}
