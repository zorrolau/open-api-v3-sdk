package com.okcoin.commons.okex.open.api.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.okcoin.commons.okex.open.api.bean.websocket.result.Depth;
import com.okcoin.commons.okex.open.api.config.WebSocketConfig;
import com.okcoin.commons.okex.open.api.constant.APIConstants;
import com.okcoin.commons.okex.open.api.enums.CharsetEnum;
import com.okcoin.commons.okex.open.api.utils.DateUtils;
import okhttp3.*;
import okio.Buffer;
import okio.ByteString;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.lang3.StringUtils;
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
            @Override
            public void onOpen(final WebSocket webSocket, final Response response) {
                //连接成功后，设置定时器，每隔25s，自动向服务器发送心跳，保持与服务器连接
                Runnable runnable = new Runnable() {
                    public void run() {
                        // task to run goes here
                        sendMessage("ping");
                    }
                };
                ScheduledExecutorService service = Executors
                        .newSingleThreadScheduledExecutor();
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
                t.printStackTrace();
                System.out.println("Connection failed!");
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
        sendMessage(str);
    }

    //取消订阅，参数为频道组成的集合
    public static void unsubscribe(List<String> list) {
        String s = listToJson(list);
        String str = "{\"op\": \"unsubscribe\", \"args\":" + s + "}";
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
            if (config.isPrint()) {
                printRequest(webSocket);
            }
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

    private static void printRequest(final WebSocket webSocket) {
        Request request = webSocket.request();
        final String method = request.method().toUpperCase();
        String url = request.url().toString();
        final RequestBody requestBody = request.body();
        String body = "";
        try {
            if (requestBody != null) {
                final Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                body = buffer.readString(APIConstants.UTF_8);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        final StringBuilder requestInfo = new StringBuilder();
        requestInfo.append("\n").append("\tSecret-Key: ").append(config.getSecretKey());
        requestInfo.append("\n\tRequest(").append(DateUtils.timeToString(null, 4)).append("):");
        requestInfo.append("\n\t\t").append("Url: ").append(url);
        requestInfo.append("\n\t\t").append("Method: ").append(method);
        requestInfo.append("\n\t\t").append("Headers: ");
        final Headers headers = request.headers();
        if (StringUtils.isNotEmpty(config.getSecretKey())) {
            requestInfo.append("\n\t\t\t").append("Accept").append(": ").append("application/json");
            requestInfo.append("\n\t\t\t").append("Content-Type").append(": ").append("application/json; charset=UTF-8");
            requestInfo.append("\n\t\t\t").append("Cookie").append(": ").append(config.getI18n());
            requestInfo.append("\n\t\t\t").append("OK-ACCESS-KEY").append(": ").append(config.getApiKey());
            requestInfo.append("\n\t\t\t").append("OK-ACCESS-SIGN").append(": ").append(sign);
            requestInfo.append("\n\t\t\t").append("OK-ACCESS-TIMESTAMP").append(": ").append(DateUtils.getUnixTime());
            requestInfo.append("\n\t\t\t").append("OK-ACCESS-PASSPHRASE").append(": ").append(config.getPassphrase());
        }
        WebSocketClient.LOG.info(requestInfo.toString());
    }

    private static void printResponse(Response response) {
        int status = response.code();
        String body = response.body().toString();
        boolean responseIsNotNull = response != null;
        StringBuilder responseInfo = new StringBuilder();
        responseInfo.append("\n\tResponse").append("(").append(DateUtils.timeToString(null, 4)).append("):");
        if (responseIsNotNull) {
            responseInfo.append("\n\t\t").append("Status: ").append(status);
            responseInfo.append("\n\t\t").append("Message: ").append(response.message());
            responseInfo.append("\n\t\t").append("Body: ").append(body);
        } else {
            responseInfo.append("\n\t\t").append("\n\tRequest Error: response is null");
        }
        LOG.info(responseInfo.toString());
    }
}
