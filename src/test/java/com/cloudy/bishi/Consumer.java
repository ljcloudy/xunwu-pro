package com.cloudy.bishi;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by ljy_cloudy on 2018/7/7.
 */
public class Consumer implements Runnable {
    public static String NEW_LINE = System.getProperty("line.separator");

    private FileData fileData;

    public Consumer(FileData fileData) {
        this.fileData = fileData;
    }

    @Override
    public void run() {
        while (true) {
            List<Data> result = fileData.consume();
            if (fileData.isFinished() && fileData.getBlockingDeque().isEmpty()) {
                System.out.println(result);
                String basePath = fileData.getBasePath();
                String path = basePath + File.separator + "result.txt";
                FileWriter fw = null;

                try {
                    fw = new FileWriter(new File(path));
                    for (Data data : result) {
                        fw.write(data.getGroupId() + "," + data.getId() + "," + data.getQuota() + NEW_LINE);
                    }
                    fw.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(fw != null){
                        try {
                            fw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("关闭失败！");
                        }
                    }
                }

                break;
            }
        }
    }
}
