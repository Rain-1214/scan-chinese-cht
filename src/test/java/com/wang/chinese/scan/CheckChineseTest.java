package com.wang.chinese.scan;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class CheckChineseTest {

    private CheckChinese checkChinese;

    @Before
    public void before() {
        this.checkChinese = new CheckChinese();
    }

    @Test
    public void translate() throws Exception {
        var res = this.checkChinese.translate("头发");
        System.out.println(res);
    }

    @Test
    public void sgin() throws Exception {
        System.out.println(this.checkChinese.sgin("头发"));
    }

}