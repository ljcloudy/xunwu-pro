package com.cloudy.service.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
public interface QiNiuService {

    Response uploadFile(File file) throws QiniuException;


    Response uploadFile(InputStream inputStream) throws QiniuException;

    Response delete(String key) throws QiniuException;


}
