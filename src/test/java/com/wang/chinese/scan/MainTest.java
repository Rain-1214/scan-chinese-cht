package com.wang.chinese.scan;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MainTest {

    private Main main;

    @Before
    public void beforeEach() {
        this.main = new Main();
    }

    @Test
    public void init() throws IOException {
        // String filePrefix = "/Users/wangxiaowen/Documents/fubon/src/App.vue";
        System.out.println("头".matches("[\\u4e00-\\u9fa5]"));
    }

    @Test
    public void main () throws Exception {
        this.main.main(new String[]{"1"});
    }

}