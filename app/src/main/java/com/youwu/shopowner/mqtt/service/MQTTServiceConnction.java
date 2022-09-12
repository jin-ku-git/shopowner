package com.youwu.shopowner.mqtt.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MQTTServiceConnction implements ServiceConnection {

    private MQTTService mqttService;
    private IGetMessageCallBack iGetMessageCallBack;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mqttService = ((MQTTService.CustomBinder) iBinder).getService();
        mqttService.setIGetMessageCallBack(iGetMessageCallBack);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public MQTTService getMqttService() {
        return mqttService;
    }

    public void setIGetMessageCallBack(IGetMessageCallBack iGetMessageCallBack) {
        this.iGetMessageCallBack = iGetMessageCallBack;
    }
}
