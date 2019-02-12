package com.wang.chinese.scan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckChinese {

    private String baiduTranslateUrl = "https://fanyi.baidu.com/v2transapi";

    public ArrayList<String> getChinese (String fileContent) {
        ArrayList result = new ArrayList<String>();
        String currentChinese = "";
        for (int i = 0; i < fileContent.length(); i++) {
            String temp = String.valueOf(fileContent.charAt(i));
//            System.out.println(temp);
            if (temp.matches("[\\u4e00-\\u9fa5]")) {
                currentChinese += temp;
                if (i == fileContent.length() - 1) {
                    result.add(currentChinese);
                }
            } else if (currentChinese != "") {
                result.add(currentChinese);
                currentChinese = "";
            }
        }
        return result;
    }

    public String translate (String translateVal) throws Exception {
        var list = new ArrayList();
        var map = new HashMap<String, String>();
        map.put("from", "zh");
        map.put("to", "cht");
        map.put("query", translateVal);
        map.put("transtype", "enter");
        map.put("simple_means_flag", "3");
        map.put("token", "");
        map.forEach((key, value) -> {
            list.add(new BasicNameValuePair(key, value));
        });
        var httpPost = new HttpPost(this.baiduTranslateUrl);
        var translateStr = map.get("query");
        list.add(new BasicNameValuePair("sign", this.sgin(translateStr)));
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Accept-Encoding", "gzip");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader("Host", "fanyi.baidu.com");
        httpPost.setHeader("Origin" ,"https://fanyi.baidu.com");
        httpPost.setHeader("Referer", "https://fanyi.baidu.com/?aldtype=16047");
        httpPost.setHeader("Cookie", "");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        httpPost.setEntity(new UrlEncodedFormEntity(list, Charset.forName("utf-8")));
        var httpClient = HttpClients.createDefault();
        var response = httpClient.execute(httpPost);
        var bufferReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        var stringBuffer = new StringBuffer();
        var temp = "";
        while ((temp = bufferReader.readLine()) != null) {
            stringBuffer.append(temp);
        }
        bufferReader.close();
        JSONObject res;
        try {
            JSONObject jsonObject = (JSONObject) JSONObject.parse(stringBuffer.toString());
            JSONObject trans_result = (JSONObject) jsonObject.get("trans_result");
            JSONArray data = (JSONArray) trans_result.get("data");
            res = (JSONObject) data.get(0);
        } catch (Exception e) {
            System.out.println(translateVal);
            System.out.println(stringBuffer.toString());
            throw e;
        }
        return (String) res.get("dst");
    }

    public String sgin(String value) throws Exception {
        String result = "";
        String currentProjectDir = System.getProperty("user.dir");
        String signJsPath = currentProjectDir + "/src/main/java/com/wang/chinese/scan/sgin.js";
        File sginJs = new File(signJsPath);
        if (sginJs.exists()) {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
            FileReader fileReader = new FileReader(signJsPath);
            scriptEngine.eval(fileReader);
            if (scriptEngine instanceof Invocable) {
                Invocable invocable = (Invocable) scriptEngine;
                result = (String) invocable.invokeFunction("e", value);
            }
        } else {
            throw new Exception("签名文件不存在，当前查找的文件路径为:" + signJsPath);
        }
        return result;
    }

}
