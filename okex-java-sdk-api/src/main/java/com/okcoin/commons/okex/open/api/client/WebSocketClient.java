package com.okcoin.commons.okex.open.api.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.okcoin.commons.okex.open.api.bean.websocket.result.Depth;
import com.okcoin.commons.okex.open.api.config.WebSocketConfig;
import com.okcoin.commons.okex.open.api.enums.CharsetEnum;
import com.okcoin.commons.okex.open.api.utils.DateUtils;
import okhttp3.*;
import okio.ByteString;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;


/**
 * @author WeiLi
 * @create 2018-12-18 下午5:57
 **/

public class WebSocketClient {
    private static WebSocket webSocket = null;
    private static Boolean flag = false;
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketClient.class);
    private static WebSocketConfig config;
    private static String sign;
    private static TreeMap<Double, Double> askMap = new TreeMap<Double, Double>();
    private static TreeMap<Double, Double> bidMap = new TreeMap<Double, Double>(new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {
            double temp = o2 - o1;
            if (temp > 0) {
                return 1;
            } else if (temp == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    });

    public WebSocketClient() {
    }

    public WebSocketClient(WebSocketConfig config) {
        this.config = config;
    }

    //与服务器建立连接，参数为服务器的URL
    public static WebSocket connection(final String url) {

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            ScheduledExecutorService service;

            @Override
            public void onOpen(final WebSocket webSocket, final Response response) {
                //连接成功后，设置定时器，每隔25s，自动向服务器发送心跳，保持与服务器连接
                Runnable runnable = new Runnable() {
                    public void run() {
                        // task to run goes here
                        sendMessage("ping");
                    }
                };
                service = Executors.newSingleThreadScheduledExecutor();
                // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
                service.scheduleAtFixedRate(runnable, 25, 25, TimeUnit.SECONDS);
                System.out.println("Successful connection to server！");
            }

            @Override
            public void onMessage(final WebSocket webSocket, final String s) {
                isLogin(s);
                System.out.println("Received message:" + s);
                isDepth(s);

            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("Connection is about to disconnect！");
                webSocket.close(1000, "Long time no message was sent or received！");
                webSocket = null;
            }

            @Override
            public void onClosed(final WebSocket webSocket, final int code, final String reason) {
                System.out.println("Connection dropped！");
            }

            @Override
            public void onFailure(final WebSocket webSocket, final Throwable t, final Response response) {
                System.out.println("Connection failed,Please reconnect!");
                service.shutdown();
            }

            @Override
            public void onMessage(final WebSocket webSocket, final ByteString bytes) {
                String s = uncompress(bytes.toByteArray());
                isLogin(s);
                System.out.println("Received message:" + s);
                isDepth(s);
            }
        });
        return webSocket;
    }

    // 解压函数
    private static String uncompress(final byte[] bytes) {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
             final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             final Deflate64CompressorInputStream zin = new Deflate64CompressorInputStream(in)) {
            final byte[] buffer = new byte[1024];
            int offset;
            while (-1 != (offset = zin.read(buffer))) {
                out.write(buffer, 0, offset);
            }
            return out.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    //是深度，需要CRC32校验
    private static void isDepth(String str) {
        if (str.startsWith("{\"table") && str.contains("depth")) {
            JSON json = JSON.parseObject(str);
            JSONArray data = ((JSONObject) json).getJSONArray("data");
            JSONObject depthObj = (JSONObject) data.get(0);
            Depth depth = JSONObject.parseObject(depthObj + "", Depth.class);
            if (null != depth.getAsks() && depth.getAsks().size() > 0) {
                List<List<Double>> asks = depth.getAsks();
                changeMap(asks, askMap);
            }
            if (null != depth.getBids() && depth.getBids().size() > 0) {
                List<List<Double>> bids = depth.getBids();
                changeMap(bids, bidMap);
            }
            StringBuilder stringBuilder = new StringBuilder();
            Set<Map.Entry<Double, Double>> aEntry = askMap.entrySet();
            Set<Map.Entry<Double, Double>> bEntry = bidMap.entrySet();
            Iterator<Map.Entry<Double, Double>> aiterator = aEntry.iterator();
            Iterator<Map.Entry<Double, Double>> biterator = bEntry.iterator();
            if (askMap.size() >= 25 && bidMap.size() >= 25) {
                int index = 0;
                while (aiterator.hasNext() && biterator.hasNext() && index < 25) {
                    setString(stringBuilder, aiterator.next(), biterator.next());
                    index++;
                }
            }
            //askMap和bidMap的长度都小于25，但是askMap长度比bidMap大
            if (askMap.size() < 25 && bidMap.size() < 25 && askMap.size() > bidMap.size()) {
                while (aiterator.hasNext() && biterator.hasNext()) {
                    Map.Entry<Double, Double> a_next = aiterator.next();
                    Map.Entry<Double, Double> b_next = biterator.next();
                    setString(stringBuilder, a_next, b_next);
                }
                while (aiterator.hasNext() && !biterator.hasNext()) {
                    Map.Entry<Double, Double> a_next = aiterator.next();
                    addString(stringBuilder, a_next);
                }
            }
            //askMap和bidMap的长度都小于25，但是askMap长度比bidMap小
            if (askMap.size() < 25 && bidMap.size() < 25 && askMap.size() < bidMap.size()) {
                while (aiterator.hasNext() && biterator.hasNext()) {
                    setString(stringBuilder, aiterator.next(), biterator.next());
                }
                while (!aiterator.hasNext() && biterator.hasNext()) {
                    Map.Entry<Double, Double> b_next = biterator.next();
                    addString(stringBuilder, b_next);
                }
            }
            if (askMap.size() > 25 && bidMap.size() < 25) {
                int i = bidMap.size();
                while (aiterator.hasNext() && biterator.hasNext()) {
                    setString(stringBuilder, aiterator.next(), biterator.next());
                }
                while (aiterator.hasNext() && !biterator.hasNext() && i < 25) {
                    Map.Entry<Double, Double> a_next = aiterator.next();
                    addString(stringBuilder, a_next);
                    i++;
                }
            }
            if (askMap.size() < 25 && bidMap.size() > 25) {
                int j = askMap.size();
                while (aiterator.hasNext() && biterator.hasNext()) {
                    setString(stringBuilder, aiterator.next(), biterator.next());
                }
                while (!aiterator.hasNext() && biterator.hasNext() && j < 25) {
                    Map.Entry<Double, Double> b_next = biterator.next();
                    addString(stringBuilder, b_next);
                    j++;
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            CRC32 crc32 = new CRC32();
            crc32.update(stringBuilder.toString().getBytes());
            System.out.println("Real checksum is:" + depth.getChecksum());
            System.out.println("Your checksum is:" + (int) crc32.getValue());
        }
    }

    private static void setString(StringBuilder stringBuilder, Map.Entry<Double, Double> anext, Map.Entry<Double, Double> bnext) {
        String key1 = doubleToString(anext.getKey());
        String value1 = doubleToString(anext.getValue());
        String key2 = doubleToString(bnext.getKey());
        String value2 = doubleToString(bnext.getValue());
        stringBuilder.append(key2).append(":").append(value2).append(":");
        stringBuilder.append(key1).append(":").append(value1).append(":");
    }

    private static void addString(StringBuilder stringBuilder, Map.Entry<Double, Double> next) {
        String key = doubleToString(next.getKey());
        String value = doubleToString(next.getValue());
        stringBuilder.append(key).append(":").append(value).append(":");
    }

    private static void changeMap(List<List<Double>> list, Map<Double, Double> map) {
        for (List<Double> a : list) {
            map.put(a.get(0), a.get(1));
            if (a.get(1) == 0) {
                map.remove(a.get(0));
            }
        }
    }

    private static String doubleToString(double number) {
        if (number % 1 == 0) {
            String str = number + "";
            return str.substring(0, str.length() - 2);
        } else {
            return number + "";
        }
    }

    private static void isLogin(String s) {
        if (null != s && s.contains("login")) {
            if (s.endsWith("true}")) {
                flag = true;
            }
        }
    }

    //获得sign
    private static String sha256_HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(CharsetEnum.UTF_8.charset()), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes(CharsetEnum.UTF_8.charset()));
            hash = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;
    }

    private static String listToJson(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (String s : list) {
            jsonArray.add(s);
        }
        return jsonArray.toJSONString();
    }

    //登录
    public static void login(String apiKey, String passPhrase, String secretKey) {
        String timestamp = (Double.parseDouble(DateUtils.getEpochTime()) + 28800) + "";
        String message = timestamp + "GET" + "/users/self/verify";
        sign = sha256_HMAC(message, secretKey);
        String str = "{\"op\"" + ":" + "\"login\"" + "," + "\"args\"" + ":" + "[" + "\"" + apiKey + "\"" + "," + "\"" + passPhrase + "\"" + "," + "\"" + timestamp + "\"" + "," + "\"" + sign + "\"" + "]}";
        sendMessage(str);
    }


    //订阅，参数为频道组成的集合
    public static void subscribe(List<String> list) {
        String s = listToJson(list);
        String str = "{\"op\": \"subscribe\", \"args\":" + s + "}";
        if (null != webSocket)
            sendMessage(str);
    }

    //取消订阅，参数为频道组成的集合
    public static void unsubscribe(List<String> list) {
        String s = listToJson(list);
        String str = "{\"op\": \"unsubscribe\", \"args\":" + s + "}";
        if (null != webSocket)
            sendMessage(str);
    }

    private static void sendMessage(String str) {
        if (null != webSocket) {
            try {
                Thread.sleep(1300);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (str.contains("account") || str.contains("position") || str.contains("order")) {
                if (!flag) {
                    System.out.println("Channels contain channels that require login privileges to operate. Please login and operate again！");
                    return;
                }
            }
            System.out.println("Send a message to the server:" + str);
            webSocket.send(str);
        } else {
            System.out.println("Please establish the connection before you operate it！");
        }
    }

    //断开连接
    public static void closeConnection() {
        if (null != webSocket) {
            webSocket.close(1000, "User actively closes the connection");
        } else {
            System.out.println("Please establish the connection before you operate it！");
        }
    }
}
