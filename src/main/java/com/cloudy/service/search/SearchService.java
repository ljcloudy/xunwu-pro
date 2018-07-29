package com.cloudy.service.search;

import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.form.RentSearch;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/7/28.
 */
public interface SearchService {

    boolean index(Long houseId);

    boolean remove(Long houseId);

    ServiceMultiResult<Long> query(RentSearch rentSearch);

    /**
     * 推荐关键词
     * @param prefix
     * @return
     */
    ServiceResult<List<String>> suggest(String prefix);

}
