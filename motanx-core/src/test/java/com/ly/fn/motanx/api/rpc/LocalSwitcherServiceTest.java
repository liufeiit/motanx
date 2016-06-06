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

package com.ly.fn.motanx.api.rpc;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.switcher.LocalSwitcherService;
import com.ly.fn.motanx.api.switcher.Switcher;

/**
 * @author maijunsheng
 * @version 创建时间：2013-6-17
 * 
 */
public class LocalSwitcherServiceTest extends TestCase {

    @Test
    public void testProtocolSwitcher() {

        String protocolSwitcher = MotanxConstants.PROTOCOL_SWITCHER_PREFIX + "motan";

        LocalSwitcherService localSwitcherService = new LocalSwitcherService();
        localSwitcherService.setValue(protocolSwitcher, false);

        Switcher switcher = localSwitcherService.getSwitcher(protocolSwitcher);

        Assert.assertNotNull(switcher);
        Assert.assertFalse(switcher.isOn());

        localSwitcherService.setValue(protocolSwitcher, true);

        switcher = localSwitcherService.getSwitcher(protocolSwitcher);
        Assert.assertNotNull(switcher);
        Assert.assertTrue(switcher.isOn());

    }
}
