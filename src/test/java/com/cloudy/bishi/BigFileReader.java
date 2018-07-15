package com.cloudy.bishi;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ljy_cloudy on 2018/7/6.
 */
public class BigFileReader {
    /**
     * 每次读取大小
     */
    private static int size = 1048*1000*100;

    public static void main(String[] args)  {
        String path = "G:\\BaiduNetdiskDownload\\python基础班\\day6\\kaifangX.txt";
//        File file = new File("G:\\BaiduNetdiskDownload\\python基础班\\day6\\kaifangX.txt");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            long t1 = System.currentTimeMillis();
            byte[] bytes = new byte[size];
            int result = fis.read(bytes);

            while (result != -1) {
                bytes = null;
                bytes = new byte[size];
                long start = System.currentTimeMillis();
                result = fis.read(bytes);
                long end = System.currentTimeMillis();
                System.out.println((end - start) + " ms, " + ((end - start) / 1000) + " s");
            }
            long t2 = System.currentTimeMillis();
            System.out.println("total: "+(t2-t1)+" ms");
            System.out.println("finished!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
