package com.cloudy.bishi;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ljy_cloudy on 2018/7/7.
 */
public class Producer {

    private FileData fileData;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Producer(FileData fileData) {
        this.fileData = fileData;
    }

    public void fetchData() {
        List<Worker> workerList = new ArrayList<>();
        String[] paths = fileData.getPaths();
        Worker worker = null;
        for (int i = 0; i < paths.length; i++) {
            worker = new Worker(fileData, paths[i]);
            workerList.add(worker);
        }
        try {
            List<Future<String>> futureList = executorService.invokeAll(workerList);

            System.out.println("执行完成！");
            executorService.shutdown();
            fileData.setFinished(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //...
        }
    }

    public static class Worker implements Callable<String> {

        private final String path;

        private FileData fileData;

        public Worker(FileData fileData, String path) {
            this.fileData = fileData;
            this.path = path;
        }

        @Override
        public String call() {
            FileReader fr = null;
            BufferedReader br = null;
            List<Data> list = new ArrayList<>();
            try {
                File f = new File(path);
                fr = new FileReader(f);
                br = new BufferedReader(fr);

                String readLine = br.readLine();
                Data data = null;
                while (readLine != null) {
                    String[] split = readLine.split(",");
                    //防止输入中文,
                    if (split == null) {
                        split = readLine.split("，");
                    }
                    if (split != null) {
                        data = new Data(split[0], split[1], Float.parseFloat(split[2]));
                        fileData.produce(data);
                    }
                    readLine = null;
                    readLine = br.readLine();
                }

            } catch (FileNotFoundException e) {
                System.out.println("文件找不到：" + path);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "ok";
        }
    }


        public static void main(String[] args) {
        String path = "D:\\tmp";
            FileData data = new FileData(path);
            Producer producer = new Producer(data);
            producer.fetchData();

            Consumer consumer = new Consumer(data);
            new Thread(consumer).start();


            System.out.println(data);


        }
    }
