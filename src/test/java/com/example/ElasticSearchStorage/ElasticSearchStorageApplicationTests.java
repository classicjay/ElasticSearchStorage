package com.example.ElasticSearchStorage;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchStorageApplicationTests {
	TransportClient client;
	private IndexRequest source;

	/**
	 * //创建简易JSON对象
	 * @return
	 * @throws Exception
	 */
	public XContentBuilder createJSON() throws Exception{

		XContentBuilder source = XContentFactory.jsonBuilder()
				.startObject()
				.field("1技能","大地震击")
				.field("2技能","冰霜震击")
				.field("3技能","元素毁灭").endObject();
		return source;
	}

	@Before
	public void before() throws Exception {
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

	/**
	 * 查看集群信息
	 */
	@Test
	public void clusterInfo(){
		List<DiscoveryNode> nodes = client.connectedNodes();
		for (DiscoveryNode node:nodes){
			System.out.println(node.getHostAddress());
		}
	}

	/**
	 * 向指定索引入数
	 * @throws Exception
	 */
	@Test
	public void intoIndex() throws Exception{
		XContentBuilder source = createJSON();
		IndexResponse response = client.prepareIndex("heartstone", "shaman", "1").setSource(source).get();
		String index = response.getIndex();
		String type = response.getType();
		String id = response.getId();
		long version = response.getVersion();
		System.out.println("index:" + index + "type:" + type + "id:" + id + "version:" + +version);
	}



}
