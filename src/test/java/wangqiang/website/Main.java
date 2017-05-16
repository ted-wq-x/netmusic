package wangqiang.website;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangq on 2017/5/15.
 */
public class Main {
    public static void main(String[] args) {
         AtomicInteger atomicInteger = new AtomicInteger(1000000000);

        System.out.println(atomicInteger.getAndIncrement());
        System.out.println(atomicInteger.getAndIncrement());
    }


}
