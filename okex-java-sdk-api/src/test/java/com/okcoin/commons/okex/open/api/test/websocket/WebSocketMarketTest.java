package com.okcoin.commons.okex.open.api.test.websocket;

import com.okcoin.commons.okex.open.api.client.WebSocketClient;
import com.okcoin.commons.okex.open.api.config.WebSocketConfig;
import org.junit.Before;
import org.junit.Test;

public class WebSocketMarketTest extends WebSocketBaseTest {

    @Before
    public void before() {
        config = setConfig();
    }

    @Test
    public void subscribeChannel(){
        WebSocketClient webSocketClient = new WebSocketClient(config);
        //与服务器建立连接
        webSocketClient.connection(config.getUrl());
        //调用订阅方法
        webSocketClient.subscribe(config.getList());
        //为保证测试方法不停，需要让线程延迟
        try{
            Thread.sleep(10000000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Test
    public void unsubscribeChannel(){
        WebSocketClient webSocketClient = new WebSocketClient(config);
        //与服务器建立连接
        webSocketClient.connection(config.getUrl());
        //调用取消订阅方法
        webSocketClient.unsubscribe(config.getList());
        //为保证测试方法不停，需要让线程延迟
        try{
            Thread.sleep(10000000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
