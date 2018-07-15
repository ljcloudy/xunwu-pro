package com.cloudy.web.controller.admin;

import com.cloudy.base.ApiDataTableResponse;
import com.cloudy.base.ApiResponse;
import com.cloudy.base.HouseOperation;
import com.cloudy.base.HouseStatus;
import com.cloudy.entity.SupportAddress;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.service.house.AddressService;
import com.cloudy.service.house.HouseService;
import com.cloudy.service.house.QiNiuService;
import com.cloudy.web.dto.*;
import com.cloudy.web.form.DatatableSearch;
import com.cloudy.web.form.HouseForm;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by ljy_cloudy on 2018/5/31.
 */
@Controller
public class AdminController {

    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private Gson gson;

    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("/logout/page")
    public String logout() {
        return "logout";
    }

    /**
     * 房源列表页
     *
     * @return
     */
    @GetMapping("/admin/house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    @GetMapping("/admin/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {

        if (id == null || id < 1) {
            return "404";
        }
        ServiceResult<HouseDTO> result = houseService.findCompleteOne(id);
        HouseDTO houseDTO = result.getResult();
        model.addAttribute("house", houseDTO);

        Map<SupportAddress.Level, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());
        model.addAttribute("city", cityAndRegion.get(SupportAddress.Level.CITY));
        model.addAttribute("region", cityAndRegion.get(SupportAddress.Level.REGION));

        HouseDetailDTO houseDetail = houseDTO.getHouseDetail();
        ServiceResult<SubwayDTO> subwayDTOServiceResult = addressService.findSubway(houseDetail.getSubwayLineId());

        if (subwayDTOServiceResult.isSuccess()) {
            model.addAttribute("subway", subwayDTOServiceResult.getResult());
        }

        ServiceResult<SubwayStationDTO> subwayStationDTOServiceResult = addressService.findSubwayStation(houseDetail.getSubwayStationId());

        if (subwayStationDTOServiceResult.isSuccess()) {
            model.addAttribute("station", subwayDTOServiceResult.getResult());
        }

        return "admin/house-edit";
    }

    @PostMapping("/admin/house/edit")
    @ResponseBody
    public ApiResponse updateHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        Map<SupportAddress.Level, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (cityAndRegion.keySet().size() != 2) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult<HouseDTO> result = houseService.update(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }
        ApiResponse response = ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        response.setMessge(result.getMessage());
        return response;
    }

    @PostMapping("/admin/houses")
    @ResponseBody
    public ApiDataTableResponse house(@ModelAttribute DatatableSearch searchBody) {
        ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);

        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);

        response.setDraw(searchBody.getDraw());
        response.setRecordsTotal(result.getTotal());
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());

        return response;
    }

    @GetMapping("/admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    @PostMapping(value = "/admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        try {
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(ret);
            } else {
                return ApiResponse.ofMessage(response.statusCode, response.getInfo());
            }
        } catch (IOException e) {
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片！");
        }

        Map<SupportAddress.Level, SupportAddressDTO> cityAndRegionMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if (cityAndRegionMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult<HouseDTO> serviceResult = houseService.save(houseForm);

        if (serviceResult.isSuccess()) {
            return ApiResponse.ofSuccess(serviceResult.getResult());
        }

        return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
    }

    @DeleteMapping("admin/house/photo")
    @ResponseBody
    public ApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
        ServiceResult result = this.houseService.removePhoto(id);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    @PostMapping("/admin/house/cover")
    @ResponseBody
    public ApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
                                   @RequestParam(value = "target_id") Long targetId) {

        ServiceResult result = houseService.updateCover(coverId, targetId);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 增加标签
     *
     * @param houseId
     * @param tag
     * @return
     */
    @PostMapping("/admin/house/tag")
    @ResponseBody
    public ApiResponse addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                   @RequestParam(value = "tag") String tag) {

        if (houseId < 1 || StringUtils.isEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult result = houseService.addTag(houseId, tag);

        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 增加标签
     *
     * @param houseId
     * @param tag
     * @return
     */
    @DeleteMapping("/admin/house/tag")
    @ResponseBody
    public ApiResponse removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                      @RequestParam(value = "tag") String tag) {

        if (houseId < 1 || StringUtils.isEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult result = houseService.removeTag(houseId, tag);

        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    @PutMapping("/admin/house/operate/{id}/{operation}")
    @ResponseBody
    public ApiResponse operateHouse(@PathVariable(value = "id") Long id,
                                    @PathVariable(value = "operation") int operation) {
        if (id <= 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult result;

        switch (operation) {
            case HouseOperation.PASS:
                result = this.houseService.updateStatus(id, HouseStatus.PASSES.getValue());
                break;
            case HouseOperation.DELETE:
                result = this.houseService.updateStatus(id, HouseStatus.DELETED.getValue());
                break;
            case HouseOperation.PULL_OUT:
                result = this.houseService.updateStatus(id, HouseStatus.NOT_AUDITED.getValue());
                break;
            case HouseOperation.RENT:
                result = this.houseService.updateStatus(id, HouseStatus.RENTED.getValue());
                break;
            default:
                return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);

        }

        if(result.isSuccess()){
            return ApiResponse.ofSuccess(null);
        }
        return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),result.getMessage());
    }


}
