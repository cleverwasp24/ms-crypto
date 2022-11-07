package com.nttdata.bootcamp.mscrypto.mapper;

import com.nttdata.bootcamp.mscrypto.dto.BootcoinDTO;
import com.nttdata.bootcamp.mscrypto.model.Bootcoin;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class BootcoinDTOMapper {

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    public BootcoinDTO convertToDto(Bootcoin bootcoin) {
        return modelMapper.map(bootcoin, BootcoinDTO.class);
    }

    public Bootcoin convertToEntity(BootcoinDTO bootcoinDTO) {
        return modelMapper.map(bootcoinDTO, Bootcoin.class);
    }
}
