package com.ly.fn.motanx.api.switcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;

@SpiMeta(name = "localSwitcherService")
public class LocalSwitcherService implements SwitcherService {

    private static ConcurrentMap<String, Switcher> switchers = new ConcurrentHashMap<String, Switcher>();

    private Map<String, List<SwitcherListener>> listenerMap = new ConcurrentHashMap<String, List<SwitcherListener>>();

    @Override
    public Switcher getSwitcher(String name) {
        return switchers.get(name);
    }

    @Override
    public List<Switcher> getAllSwitchers() {
        return new ArrayList<Switcher>(switchers.values());
    }

    private void putSwitcher(Switcher switcher) {
        if (switcher == null) {
            throw new MotanxFrameworkException("LocalSwitcherService addSwitcher Error: switcher is null");
        }

        switchers.put(switcher.getName(), switcher);
    }

    @Override
    public void initSwitcher(String switcherName, boolean initialValue) {
        setValue(switcherName, initialValue);
    }

    @Override
    public boolean isOpen(String switcherName) {
        Switcher switcher = switchers.get(switcherName);
        return switcher != null && switcher.isOn();
    }

    @Override
    public boolean isOpen(String switcherName, boolean defaultValue) {
        Switcher switcher = switchers.get(switcherName);
        if (switcher == null) {
            switchers.putIfAbsent(switcherName, new Switcher(switcherName, defaultValue));
            switcher = switchers.get(switcherName);
        }
        return switcher.isOn();
    }

    @Override
    public void setValue(String switcherName, boolean value) {
        putSwitcher(new Switcher(switcherName, value));

        List<SwitcherListener> listeners = listenerMap.get(switcherName);
        if (listeners != null) {
            for (SwitcherListener listener : listeners) {
                listener.onValueChanged(switcherName, value);
            }
        }
    }

    @Override
    public void registerListener(String switcherName, SwitcherListener listener) {
        synchronized (listenerMap) {
            if (listenerMap.get(switcherName) == null) {
                List<SwitcherListener> listeners = Collections.synchronizedList(new ArrayList<SwitcherListener>());
                listenerMap.put(switcherName, listeners);
                listeners.add(listener);
            } else {
                List<SwitcherListener> listeners = listenerMap.get(switcherName);
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        }
    }

    @Override
    public void unRegisterListener(String switcherName, SwitcherListener listener) {
        synchronized (listenerMap) {
            if (listener == null) {
                listenerMap.remove(switcherName);
            } else {
                List<SwitcherListener> listeners = listenerMap.get(switcherName);
                listeners.remove(listener);
            }
        }
    }

}
