package com.cloudy.service.house;

import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.dto.HouseDTO;
import com.cloudy.web.form.DatatableSearch;
import com.cloudy.web.form.HouseForm;
import com.cloudy.web.form.RentSearch;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface HouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceResult<HouseDTO> update(HouseForm houseForm);

    /**
     * 管理员查询房屋信息
     * @param searchBody
     * @return
     */
    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    /**
     * 查询房源完整信息
     * @param id
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long id);

    /**
     * 移除图片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 修改封面图片
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 增加标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);

    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    ServiceResult updateStatus(Long id, int status);

    /**
     * 查询房屋信息
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);


 }
