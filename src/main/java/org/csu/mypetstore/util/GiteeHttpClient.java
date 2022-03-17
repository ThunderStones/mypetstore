package org.csu.mypetstore.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Objects;

public class GiteeHttpClient {

    private static final String CLIENT_ID = "58cc8ced7d5f3086d01f5f0ff0f9bea53e79aacd7e8078f1e6fc05ddb40d04ee";
    private static final String CLIENT_SECRET = "0d6e225889b5148b905f7f1a645b51f3481201e7c04401bec0225dec4e71c0ee";
    private static final String REDIRECT_URI = "http://127.0.0.1/account/giteeLoginCallback";


    /**
     * 获取Gitee的access_token
     *
     * @param code 授权码
     * @return JSONObject 返回JSONObject
     * post请求
     */
    public static String getGiteeToken(String code) {
        //1. 创建http请求，构建请求体和请求url等，并向gitee发起请求
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String bodyData = String.format("grant_type=authorization_code&code=%s&client_id=%s&redirect_uri=%s&client_secret=%s",
                code, CLIENT_ID, REDIRECT_URI, CLIENT_SECRET);
        RequestBody body = RequestBody.create(JSON.toJSONString(bodyData), mediaType);
        String url = "https://gitee.com/oauth/token?grant_type=authorization_code&code=" + code
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&client_secret=" + CLIENT_SECRET;
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        //2. 获取gitee对应的响应消息，根据消息解析出用户的 access token
        try (Response response = client.newCall(request).execute()) {
            String tokenStr = Objects.requireNonNull(response.body()).string();
            String accessToken = tokenStr.split(",")[0].split(":")[1];
            accessToken = accessToken.substring(1, accessToken.length() - 1);
            //System.out.println("accessToken = " + accessToken);
            return accessToken;
        } catch (Exception e) {
            e.getStackTrace();
            //log.error("getAccessToken error,{}", accessTokenDTO, e);
        }
        return null;
    }

    /**
     * 获取用户信息
     * @param token access_token
     *            get请求
     *            返回JSONObject
     */
    public static String getUserName(String token) throws IOException {
        JSONObject jsonObject = null;
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://gitee.com/api/v5/user?access_token=" + token);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            JSONObject jsonObject1 = JSON.parseObject(result);
            String name = jsonObject1.getString("login");
            return name;
        }
        httpGet.releaseConnection();
        return null;
    }

}
