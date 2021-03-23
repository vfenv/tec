package com.huawei.aiflow;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsoupTest {
    public static void main(String[] args) throws IOException {
        List<String[]> ls = new ArrayList<>();
        File f = new File("c:/aa/1.txt");
        InputStream in = new FileInputStream(f);
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String s = null;
        while ((s = bf.readLine()) != null) {
            String[] split = s.split("--");
            ls.add(split);
        }
        in.close();

        System.out.println(ls.size());
        if (ls != null && ls.size() > 0) {
            for (int i = 0; i < ls.size(); i++) {
                String[] x = ls.get(i);
                String fileName = x[1] + ".json";
                String repath = x[2];
                System.out.println(fileName);
                System.out.println(repath);

                String url = "https://www.rtings.com/images/graphs/" + repath + "/graph-isolation.json";
                Connection connect = Jsoup.connect(url).ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)");

                connect.proxy("proxycn.huawei.com",8080);


                Document document = connect.timeout(500000).get();
                Element body = document.body();
                String content= body.html();
//            System.out.println(body.html());

                File jsonfile = new File("c:/aa/"+fileName);
                FileUtils.write(jsonfile,content,"utf-8");
            }
        }
    }
}
