package com.ly.fn.motanx.api.registry.consul.client;

import java.util.ArrayList;
import java.util.List;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.health.model.HealthService.Service;
import com.ly.fn.motanx.api.registry.consul.ConsulConstants;
import com.ly.fn.motanx.api.registry.consul.ConsulResponse;
import com.ly.fn.motanx.api.registry.consul.ConsulService;
import com.ly.fn.motanx.api.util.LoggerUtil;

public class ConsulEcwidClient extends MotanxConsulClient {
	public static ConsulClient client;

	public ConsulEcwidClient(String host, int port) {
		super(host, port);
		client = new ConsulClient(host + ":" + port);
		LoggerUtil.info("ConsulEcwidClient init finish. client host:" + host
				+ ", port:" + port);
	}

	@Override
	public void checkPass(String serviceid) {
		client.agentCheckPass("service:" + serviceid);
	}

	@Override
	public void registerService(ConsulService service) {
		NewService newService = convertService(service);
		client.agentServiceRegister(newService);
	}

	@Override
	public void deregisterService(String serviceid) {
		client.agentServiceDeregister(serviceid);
	}

	@Override
	public ConsulResponse<List<ConsulService>> lookupHealthService(
			String serviceName, long lastConsulIndex) {
		QueryParams queryParams = new QueryParams(
				ConsulConstants.CONSUL_BLOCK_TIME_SECONDS, lastConsulIndex);
		Response<List<HealthService>> orgResponse = client.getHealthServices(
				serviceName, true, queryParams);
		ConsulResponse<List<ConsulService>> newResponse = null;
		if (orgResponse != null && orgResponse.getValue() != null
				&& !orgResponse.getValue().isEmpty()) {
			List<HealthService> HealthServices = orgResponse.getValue();
			List<ConsulService> ConsulServcies = new ArrayList<ConsulService>(
					HealthServices.size());

			for (HealthService orgService : HealthServices) {
				try {
					ConsulService newService = convertToConsulService(orgService);
					ConsulServcies.add(newService);
				} catch (Exception e) {
					String servcieid = "null";
					if (orgService.getService() != null) {
						servcieid = orgService.getService().getId();
					}
					LoggerUtil.error(
							"convert consul service fail. org consulservice:"
									+ servcieid, e);
				}
			}
			if (!ConsulServcies.isEmpty()) {
				newResponse = new ConsulResponse<List<ConsulService>>();
				newResponse.setValue(ConsulServcies);
				newResponse.setConsulIndex(orgResponse.getConsulIndex());
				newResponse.setConsulLastContact(orgResponse
						.getConsulLastContact());
				newResponse.setConsulKnownLeader(orgResponse
						.isConsulKnownLeader());
			}
		}

		return newResponse;
	}

	private NewService convertService(ConsulService service) {
		NewService newService = new NewService();
		newService.setAddress(service.getAddress());
		newService.setId(service.getId());
		newService.setName(service.getName());
		newService.setPort(service.getPort());
		newService.setTags(service.getTags());
		NewService.Check check = new NewService.Check();
		check.setTtl(service.getTtl() + "s");
		newService.setCheck(check);
		return newService;
	}

	private ConsulService convertToConsulService(HealthService healthService) {
		ConsulService service = new ConsulService();
		Service org = healthService.getService();
		service.setAddress(org.getAddress());
		service.setId(org.getId());
		service.setName(org.getService());
		service.setPort(org.getPort());
		service.setTags(org.getTags());
		return service;
	}

	@Override
	public void checkFail(String serviceid) {
		client.agentCheckFail("service:" + serviceid);
	}

}
