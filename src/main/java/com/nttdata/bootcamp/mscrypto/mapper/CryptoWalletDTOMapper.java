package com.nttdata.bootcamp.mscrypto.mapper;

import com.nttdata.bootcamp.mscrypto.dto.*;
import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class CryptoWalletDTOMapper {
    
    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    public CryptoWallet convertToDto(CryptoWallet cryptoWallet) {
        return modelMapper.map(cryptoWallet, CryptoWallet.class);
    }
    public CryptoWallet convertToEntity(CryptoWalletDTO cryptoWalletDTO) {
        CryptoWallet cryptoWallet = modelMapper.map(cryptoWalletDTO, CryptoWallet.class);
        cryptoWallet.setInitialBalance(cryptoWallet.getBalance());
        cryptoWallet.setCreationDate(LocalDateTime.now());
        return cryptoWallet;
    }
    
}
