package com.youwu.shopowner.mqtt.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.youwu.shopowner.app.AppApplication;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

import me.goldze.mvvmhabit.utils.KLog;

public class MQTTService extends Service implements MqttCallback {

    private static final String HOST = "tcp://emq.youwuu.com:1883";
    private static final String USERNAME = "hmzc";
    private static final String PSD = "YOUwu2020#";
    private static MqttAndroidClient androidClient;
    private MqttConnectOptions connectOptions;
    private  String TOPIC = "";
    private static final String CLIENTID = "";
    private static final int QOS = 2;	//传输质量

    private IGetMessageCallBack iGetMessageCallBack;

    @Override
    public void onCreate() {
        super.onCreate();

        TOPIC= AppApplication.spUtils.getString("topic");

        KLog.a("TOPIC:"+TOPIC);
        init();
    }

    private void init() {
        androidClient = new MqttAndroidClient(this,HOST,CLIENTID);
        androidClient.setCallback(this);
        connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setConnectionTimeout(10);
        connectOptions.setKeepAliveInterval(20);
        connectOptions.setUserName(USERNAME);
        connectOptions.setPassword(PSD.toCharArray());
        //设置最后的遗嘱
        boolean doConnect = true;
        String message =  "{\"terminal_uid\":\"" + CLIENTID + "\"}";
//        if(!message.equals("")){
//            connectOptions.setWill(TOPIC,message.getBytes(),QOS,true);
//        }
        if(doConnect){
            doClientConnection();
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CustomBinder();
    }

    @Override
    public void onDestroy() {
        if(androidClient != null){
            try {
                androidClient.disconnect();
                androidClient.unregisterResources();
                androidClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void doClientConnection() {
        if(!androidClient.isConnected()){
            try {
                androidClient.connect(connectOptions,null,iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            //连接成功
            LogUtils.e("MQTT","---->connection success");
            try {
                androidClient.subscribe(TOPIC, QOS);//订阅主题，参数：主题、服务质量
                LogUtils.e("订阅主题","---->成功");
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            //连接失败
            LogUtils.e("MQTT","---->connection failure"+exception.toString());
            doClientConnection();
        }
    };

    public class CustomBinder extends Binder {
        public MQTTService getService(){
            return MQTTService.this;
        }
    }


    /**
     * 连接并监听消息
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {
        LogUtils.e("MQTT","---->connectionLost:"+cause.toString());
        LogUtils.e("MQTT", "连接断开 ");
        doClientConnection();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String getMessage = new String(message.getPayload());
        if (iGetMessageCallBack != null) {
            iGetMessageCallBack.setMessage(getMessage);
        }

//        response(getSerialNumber()+"收到了");
        LogUtils.e("MQTT","---->Message:"+getMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void setIGetMessageCallBack(IGetMessageCallBack iGetMessageCallBack) {
        this.iGetMessageCallBack = iGetMessageCallBack;
    }

    public void response(String message) {
//        String topic = "breakfast_cabinet_info/111";
//        Integer qos = 1;
//        Boolean retained = false;
//        try {
//            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
//            androidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    }

    /** 安卓系统获取设备序列号*/
    public String getSerialNumber() {
        String serial = null;
        try {
            serial = android.os.Build.SERIAL;
        } catch (Exception e) { }
        return serial;
    }

}
