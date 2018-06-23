package com.cloudy.service.house;

import com.cloudy.entity.SupportAddress;
import com.cloudy.repository.SupportAddressRepository;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.web.controller.house.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {

        List<SupportAddress> supportAddressList = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());

        List<SupportAddressDTO> supportAddressDTOS = supportAddressList.stream()
                .map(address -> modelMapper.map(address, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return new ServiceMultiResult<>(supportAddressDTOS.size(), supportAddressDTOS);
    }
}
