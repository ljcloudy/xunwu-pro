package com.cloudy.bishi;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * Created by ljy_cloudy on 2018/7/7.
 */
public class FileData {

    /**
     * 阻塞队列大小
     */
    private static final int MAX_SIZE = 200;

    private LinkedBlockingDeque<Data> blockingDeque = new LinkedBlockingDeque<>(MAX_SIZE);

    private List<Data> totalData = new ArrayList<>();

    private List<Data> resultData = new ArrayList<>();

    private volatile boolean finished = false;

    private String[] paths;
    private String basePath;


    public FileData(String path) {
        this.basePath = path;

        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("指定的目录不存在！path:" + path);
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("请指定目录,path: " + path);
        }

        String[] names = file.list((dir, name) -> name.endsWith(".txt"));
        String[] realPaths = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            realPaths[i] = path + File.separator + names[i];
        }
        this.paths = realPaths;

    }

    /**
     * 生产数据
     * @param data
     */
    public void produce(Data data) {
        if (blockingDeque.size() == MAX_SIZE) {
            System.out.println("队列已满" + MAX_SIZE + ",不能继续生产！");
        }
        blockingDeque.push(data);
    }

    /**
     * 消费数据
     * @return
     */
    public List<Data> consume() {
        if (blockingDeque.isEmpty()) {
            System.out.println("队列为空，不能继续消费！");
        }
        try {
            Data data = blockingDeque.take();
            totalData.add(data);

            Map<String, Optional<Data>> optionalMap = totalData.stream()
                    .collect(Collectors.groupingBy(Data::getGroupId, Collectors.minBy(Comparator.comparing(Data::getQuota))));
            //groupId为字符串
            resultData = optionalMap.values().stream().map(o -> o.get()).sorted(Comparator.comparing(Data::getGroupId)).collect(Collectors.toList());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultData;
    }


    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public LinkedBlockingDeque<Data> getBlockingDeque() {
        return blockingDeque;
    }

    public void setBlockingDeque(LinkedBlockingDeque<Data> blockingDeque) {
        this.blockingDeque = blockingDeque;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
