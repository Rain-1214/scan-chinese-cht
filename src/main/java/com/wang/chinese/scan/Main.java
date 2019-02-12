package com.wang.chinese.scan;

import java.util.*;

public class Main {

    private CheckChinese checkChinese = new CheckChinese();
    private ReadFile readFile = ReadFile.getInstance();

    private String[] excludeType = new String[]{"png", "jpeg", "jpg"};
    private Map<String, String> annotateMark;

    private boolean isAnnotate = false;

    public void main (String[] args) throws Exception {
        this.initAnnotateMark();
        String projectPath = "/Users";
        var fileMap = this.readFile.readFile(projectPath, this.excludeType);
        var keySet = fileMap.keySet();
        for (String key: keySet) {
            ArrayList<String> fileContent = fileMap.get(key);
            int curLine = 1;
            var textLineData = new HashMap<String, Integer>();
            for (String line: fileContent) {
                String valideText = this.getIsNotAnnotationStr(line);
//                System.out.println(valideText);
                ArrayList<String> chinese = this.checkChinese.getChinese(valideText);
                String curLineChinese = this.transList2Str(chinese, ".");
//                System.out.println(curLineChinese);
                if (!curLineChinese.equals("")) {
                    textLineData.put(curLineChinese, curLine);
                }
                curLine++;
            }
            var textLineDataKey = textLineData.keySet();
            if (textLineDataKey.size() <= 0) {
                continue;
            }
            String transValue = this.transList2Str(textLineDataKey, "，");
            String transValueRes = this.checkChinese.translate(transValue);
            String[] transValueArr = transValue.split("，");
            String[] transValueResArr = transValueRes.split("，");
            System.out.println("开始输出" + key + ";的检测结果");
            for (var i = 0; i < transValueArr.length; i++) {
                String curTransValue = transValueArr[i];
                if (curTransValue.contains(".")) {
                    String[] allTransValue = curTransValue.split("\\.");
                    String[] allTransValueRes = transValueResArr[i].split("\\.");
                    for (var y = 0; y < allTransValue.length; y++) {
                        boolean isMatching = allTransValue[y].equals(allTransValueRes[y]);
                        if (isMatching) {
                            System.out.println("匹配成功: 找到的值 -> " + allTransValue[y] + ";期望的值 -> " + allTransValueRes[y]);
                        } else {
                            System.out.println("-------Error--------");
                            System.out.println("匹配失败: 找到的值 -> " + allTransValue[y] + ";期望的值 -> " + allTransValueRes[y]);
                            System.out.println("错误发生在文件 ->" + key + ";的第" + textLineData.get(transValueArr[i]) + "行");
                        }
                    }
                } else {
                    boolean isMatching = transValueArr[i].equals(transValueResArr[i]);
                    if (isMatching) {
                        System.out.println("匹配成功: 找到的值 -> " + transValueArr[i] + ";期望的值 -> " + transValueResArr[i]);
                    } else {
                        System.out.println("-------Error--------");
                        System.out.println("匹配失败: 找到的值 -> " + transValueArr[i] + ";期望的值 -> " + transValueResArr[i]);
                        System.out.println("错误发生在文件 ->" + key + ";的第" + textLineData.get(transValueArr[i]) + "行");
                    }
                }
            }
            System.out.println("==============");
        }
    }

    private String getIsNotAnnotationStr (String text) {
        var keySet = this.annotateMark.keySet();
        var result = "";
        var trimText = text.trim();
        for (var i = 0; i < trimText.length(); i++) {
            if (this.isAnnotate) {
                for (String key: keySet) {
                    if (i + key.length() > trimText.length()) {
                        continue;
                    }
                    var tempStr = trimText.substring(i, i + key.length());
                    if (tempStr.equals(this.annotateMark.get(key))) {
                        this.isAnnotate = false;
                        i += key.length();
                    }
                }
            } else {
                for(String key: keySet) {
                    if (i + key.length() > trimText.length()) {
                        continue;
                    }
                    var tempStr = trimText.substring(i, i + key.length());
                    if (tempStr.equals(key)) {
                        if (key.equals("//")) {
                            return result;
                        } else {
                            this.isAnnotate = true;
                        }
                    }
                }
                result += this.isAnnotate ? "" : trimText.substring(i, i + 1);
            }
        }
        return result;
    }

    public void initAnnotateMark () {
        this.annotateMark = new HashMap<>();
        this.annotateMark.put("//", "");
        this.annotateMark.put("/*", "*/");
        this.annotateMark.put("<!-", "-->");
    }

    private <T extends Collection<String>> String transList2Str (T list, String separator) {
        var result = "";
        for (String str: list) {
            if (!result.equals("")) {
                result += separator;
            }
            result += str;
        }
        return result;
    }

}
