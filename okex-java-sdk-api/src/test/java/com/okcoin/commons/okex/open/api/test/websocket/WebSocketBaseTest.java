package com.okcoin.commons.okex.open.api.test.websocket;

import com.okcoin.commons.okex.open.api.config.WebSocketConfig;
import com.okcoin.commons.okex.open.api.enums.I18nEnum;

import java.util.ArrayList;

public class WebSocketBaseTest {
     public WebSocketConfig config;

     public WebSocketConfig setConfig(){
         WebSocketConfig config = new WebSocketConfig("haha");
         config.setUrl("ws://192.168.80.113:10442/ws/v3?_compress=true");
         config.setApiKey("bb57a1b3-6257-47ff-b06c-faafc4d28fad");
         config.setSecretKey("5CE31E70CD129F34B9E17C38534DDF90");
         config.setPassphrase("123456");
         //不需要登录就能订阅的频道
         ArrayList<String> withOutLoginList = new ArrayList<>();
         withOutLoginList.add("websocket/ticker:BTC-USD-SWAP");
//         withOutLoginList.add("websocket/candle180s:BTC-USD-SWAP");
//         withOutLoginList.add("websocket/mark_ticker:BTC-USD-SWAP");
         //需要登录才能订阅的频道
         ArrayList<String> loginList = new ArrayList<>();
         loginList.add("websocket/account:BTC-USD-SWAP");
         loginList.add("websocket/position:BTC-USD-SWAP");
         loginList.add("websocket/order:BTC-USD-SWAP");
         config.setList(withOutLoginList);
         return config;
     }
}
