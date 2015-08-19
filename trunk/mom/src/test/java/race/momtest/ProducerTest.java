package race.momtest;

import com.alibaba.middleware.race.mom.*;

public class ProducerTest {
	public static void main(String[] args) {
		Producer producer=new DefaultProducer();
		producer.setGroupId("PG-test");
		producer.setTopic("T-test");
		producer.start();
		Message message=new Message();
		message.setBody("Hello MOM".getBytes());
		message.setProperty("area", "us");
		SendResult result=producer.sendMessage(message);
		if (result.getStatus().equals(SendStatus.SUCCESS)) {
			System.out.println("send success:"+result.getMsgId());
		}
	}
}
