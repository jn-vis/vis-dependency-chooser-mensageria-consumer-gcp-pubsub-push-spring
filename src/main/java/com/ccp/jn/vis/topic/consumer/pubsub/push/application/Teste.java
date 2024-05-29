package com.ccp.jn.vis.topic.consumer.pubsub.push.application;

import java.util.Arrays;
import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.implementations.db.bulk.elasticsearch.CcpElasticSerchDbBulk;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityEmailParametersToSend;

public class Teste {
	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchCrud(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(),
				new CcpElasticSerchDbBulk());
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put(JnEntityEmailParametersToSend.Fields.templateId.name(), "teste");
		List<CcpJsonRepresentation> asList = Arrays.asList(put);
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(asList, CcpEntityOperationType.create, JnEntityEmailParametersToSend.INSTANCE);
	}
}
