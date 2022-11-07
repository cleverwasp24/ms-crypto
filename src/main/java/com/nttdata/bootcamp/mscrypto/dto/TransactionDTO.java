package com.nttdata.bootcamp.mscrypto.dto;

import lombok.Data;

@Data
public class TransactionDTO {

    private Long cryptoWalletId;
    private Double amount;
    private Long destinationCryptoWalletId;
    private Integer paymentType;
    private Long accountId;
    private Long walletId;

}
