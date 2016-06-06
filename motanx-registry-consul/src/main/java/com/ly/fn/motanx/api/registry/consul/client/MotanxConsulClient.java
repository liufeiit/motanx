package com.ly.fn.motanx.api.registry.consul.client;

import java.util.List;

import com.ly.fn.motanx.api.registry.consul.ConsulResponse;
import com.ly.fn.motanx.api.registry.consul.ConsulService;

public abstract class MotanxConsulClient {

	protected String host;

	protected int port;

	public MotanxConsulClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	/**
	 * 对指定checkid设置为pass状态
	 * 
	 * @param serviceid
	 */
	public abstract void checkPass(String serviceid);

	/**
	 * 设置checkid为不可用状态。
	 * 
	 * @param serviceid
	 */
	public abstract void checkFail(String serviceid);

	/**
	 * 注册一个consul service
	 * 
	 * @param service
	 */
	public abstract void registerService(ConsulService service);

	/**
	 * 根据serviceid注销service
	 * 
	 * @param serviceid
	 */
	public abstract void deregisterService(String serviceid);

	/**
	 * 获取最新的可用服务列表。
	 * 
	 * @param serviceName
	 * @param lastConsulIndex
	 * @return
	 */
	public abstract ConsulResponse<List<ConsulService>> lookupHealthService(
			String serviceName, long lastConsulIndex);

}
