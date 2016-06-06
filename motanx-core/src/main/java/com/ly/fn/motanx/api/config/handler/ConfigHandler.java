/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ly.fn.motanx.api.config.handler;

import java.util.Collection;
import java.util.List;

import com.ly.fn.motanx.api.cluster.Cluster;
import com.ly.fn.motanx.api.cluster.support.ClusterSupport;
import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.Exporter;
import com.ly.fn.motanx.api.rpc.URL;

/**
 * 
 * Handle urls which are from config.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-5-31
 */
@Spi(scope = Scope.SINGLETON)
public interface ConfigHandler {

    <T> ClusterSupport<T> buildClusterSupport(Class<T> interfaceClass, List<URL> registryUrls);

    <T> T refer(Class<T> interfaceClass, List<Cluster<T>> cluster, String proxyType);

    <T> Exporter<T> export(Class<T> interfaceClass, T ref, List<URL> registryUrls);

    <T> void unexport(List<Exporter<T>> exporters, Collection<URL> registryUrls);
}
