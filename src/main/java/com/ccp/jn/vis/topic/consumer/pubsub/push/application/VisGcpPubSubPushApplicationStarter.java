package com.ccp.jn.vis.topic.consumer.pubsub.push.application;


import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.exceptions.process.CcpAsyncTask;
import com.ccp.implementations.db.bulk.elasticsearch.CcpElasticSerchDbBulk;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.email.sendgrid.CcpSendGridEmailSender;
import com.ccp.implementations.file.bucket.gcp.CcpGcpFileBucket;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.instant.messenger.telegram.CcpTelegramInstantMessenger;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.jn.async.business.support.JnAsyncBusinessNotifyError;
import com.ccp.vis.async.business.factory.CcpVisAsyncBusinessFactory;
import com.jn.commons.entities.JnEntityAsyncTask;

@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@CrossOrigin
@RestController
@RequestMapping(value = "/{topic}")
@SpringBootApplication
public class VisGcpPubSubPushApplicationStarter {

	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies( 
				new CcpElasticSearchQueryExecutor(),
				new CcpTelegramInstantMessenger(),
				new CcpVisAsyncBusinessFactory(),
				new CcpElasticSearchDbRequest(),
				new CcpSendGridEmailSender(),
				new CcpElasticSerchDbBulk(),
				new CcpElasticSearchDao(),
				new CcpGsonJsonHandler(),
				new CcpApacheMimeHttp(),
				new CcpGcpFileBucket()  
				);
		SpringApplication.run(VisGcpPubSubPushApplicationStarter.class, args);
	}
	@PostMapping
	public void onReceiveMessage(@PathVariable("topic") String topic, @RequestBody Map<String, Object> body) {
		CcpJsonRepresentation ccpMapDecorator = new CcpJsonRepresentation(body);
		CcpJsonRepresentation internalMap = ccpMapDecorator.getInnerJson("message");
		String data = internalMap.getAsString("data");
		String str = new CcpStringDecorator(data).text().asBase64();
		CcpJsonRepresentation json = new CcpJsonRepresentation(str);
		CcpAsyncTask.executeProcess(topic, json, JnEntityAsyncTask.INSTANCE, JnAsyncBusinessNotifyError.INSTANCE);
	}

	@PostMapping("/testing")
	public void onReceiveMessageTesting(@PathVariable("topic") String topic, @RequestBody Map<String, Object> json) {
		CcpJsonRepresentation md = new CcpJsonRepresentation(json);
		CcpAsyncTask.executeProcess(topic, md, JnAsyncBusinessNotifyError.INSTANCE);
	}

}
