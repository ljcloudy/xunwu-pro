package com.cloudy.service.house;

import com.cloudy.ApplicationTest;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
public class QiNiuServiceImplTest extends ApplicationTest{

    @Autowired
    private QiNiuService qiNiuService;

    @Test
    public void uploadFile() throws Exception {
        String fileName = "C:\\Users\\lenovo\\Desktop\\QQ图片20170814211550.png";
        File file = new File(fileName);

        Assert.assertTrue(file.exists());

        Response response = qiNiuService.uploadFile(file);
        response.isOK();
    }

    @Test
    public void uploadFile1() throws Exception {
    }

    @Test
    public void delete() throws Exception {
        String key = "FgJLjbovAEH0QaStLQFyb5EtPYN6";
        Response response = qiNiuService.delete(key);

        Assert.assertTrue(response.isOK());
    }

}