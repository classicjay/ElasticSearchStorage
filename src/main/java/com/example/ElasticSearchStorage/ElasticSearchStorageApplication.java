package com.example.ElasticSearchStorage;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableDiscoveryClient
@Configuration
@CrossOrigin(origins = "*")
public class ElasticSearchStorageApplication {

	public static TransportClient client;

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

//	public final static HashMap urlTypeCodeMap = new HashMap(){{
//		put("1","indexDetails");
//		put("2","specialReport");
//		put("3","reportPPT");
//	}};

//	public final static HashMap<String,String> urlCodeNameMap = new HashMap<>();
//	static {
//		urlCodeNameMap.put("Kpi4G","指标");
//		urlCodeNameMap.put("Topic4G","专题");
//		urlCodeNameMap.put("Report4G","报告");
//	}

//	public final static HashMap<String,String> deatilCodeNameMap = new HashMap<>();
//	static {
//		deatilCodeNameMap.put("111","4G移动计费收入");
//		deatilCodeNameMap.put("222","线下实体渠道发展用户");
//		deatilCodeNameMap.put("CKP_11318","线下实体渠道发展用户");
//		deatilCodeNameMap.put("CKP_11319","20M及以上速率发展用户数");
//		deatilCodeNameMap.put("CKP_11316","移动业务发展用户");
//	}

	public static void main(String[] args) throws UnknownHostException{
		SpringApplication.run(ElasticSearchStorageApplication.class, args);
		Settings settings = Settings.builder()
				.put("cluster.name", "Logs_Collect_V1")//设置ES实例的名称
				.put("client.transport.sniff", true)//自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
				.build();
		if(client == null) {//此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.108"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.109"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.110"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.111"), 9300));
//					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.2"), 9300));
		}
	}
}
