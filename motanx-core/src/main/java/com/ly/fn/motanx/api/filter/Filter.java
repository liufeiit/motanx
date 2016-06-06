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

package com.ly.fn.motanx.api.filter;

import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.Caller;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;

/**
 * 
 * filter before transport.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-5-16
 */
@Spi
public interface Filter {

    Response filter(Caller<?> caller, Request request);
}
