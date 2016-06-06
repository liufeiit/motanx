package com.ly.fn.motanx.api.registry.support.command;

import com.ly.fn.motanx.api.rpc.URL;

public interface CommandListener {

    void notifyCommand(URL refUrl, String commandString);

}
