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
//        return translateVal;
        var list = new ArrayList();
        var map = new HashMap<String, String>();
        map.put("from", "zh");
        map.put("to", "cht");
        map.put("query", translateVal);
        map.put("transtype", "enter");
        map.put("simple_means_flag", "3");
        map.put("token", "d2d367a94bc0dfb95514016303190ac2");
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
        httpPost.setHeader("Cookie", "BAIDUID=2F741B9C9F7DFAA5DD24D9C11E21B172:FG=1; BIDUPSID=2F741B9C9F7DFAA5DD24D9C11E21B172; PSTM=1543580476; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BDSFRCVID=Zt0sJeCCxG3JD_39S25wbuGrFhfpzuq1UpSA3J; H_BDCLCKID_SF=JJkO_D_atKvjDbTnMITHh-F-5fIX5-RLfaQJoPOF5lOTJh0RyxOrDTF9DGof5JQx-ec30tJLb4DaStJbLjbke4tX-NFHqT0J3J; REALTIME_TRANS_SWITCH=1; FANYI_WORD_SWITCH=1; HISTORY_SWITCH=1; SOUND_SPD_SWITCH=1; SOUND_PREFER_SWITCH=1; delPer=0; PSINO=2; H_PS_PSSID=1462_21096_28328_26350; locale=zh; Hm_lvt_64ecd82404c51e03dc91cb9e8c025574=1549286195,1549287501,1549432084; Hm_lpvt_64ecd82404c51e03dc91cb9e8c025574=1549432084; from_lang_often=%5B%7B%22value%22%3A%22en%22%2C%22text%22%3A%22%u82F1%u8BED%22%7D%2C%7B%22value%22%3A%22zh%22%2C%22text%22%3A%22%u4E2D%u6587%22%7D%5D; to_lang_often=%5B%7B%22value%22%3A%22zh%22%2C%22text%22%3A%22%u4E2D%u6587%22%7D%2C%7B%22value%22%3A%22en%22%2C%22text%22%3A%22%u82F1%u8BED%22%7D%2C%7B%22value%22%3A%22cht%22%2C%22text%22%3A%22%u4E2D%u6587%u7E41%u4F53%22%7D%5D");
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
//        System.out.println(sginJs.exists());
        return result;
    }

}
