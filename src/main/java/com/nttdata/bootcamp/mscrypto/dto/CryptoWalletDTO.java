package com.nttdata.bootcamp.mscrypto.dto;

import lombok.Data;

@Data
public class CryptoWalletDTO {

    private Long clientId;
    private String walletNumber;
    private Double balance;
    private Long debitCardId;
    private String imeiNumber;
    private String phoneNumber;

}
