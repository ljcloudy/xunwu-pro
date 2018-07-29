package com.cloudy.service.search;

import com.cloudy.ApplicationTest;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.web.form.RentSearch;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by ljy_cloudy on 2018/7/28.
 */
public class SearchServiceImplTest extends ApplicationTest {
    @Autowired
    private SearchService searchService;

    @Test
    public void index() throws Exception {
        Long houseId = 15L;
        boolean index = searchService.index(houseId);
        Assert.assertTrue(index);
    }

    @Test
    public void remove() throws Exception {
        Long houseId = 15L;
        searchService.remove(houseId);
    }

    @Test
    public void queryTest(){
        RentSearch rentSearch = new RentSearch();
        rentSearch.setCityEnName("bj");
        rentSearch.setRegionEnName("*");
        rentSearch.setKeywords("富力城");
        ServiceMultiResult<Long> result = searchService.query(rentSearch);
        Assert.assertTrue(result.getTotal() > 0);
    }

}