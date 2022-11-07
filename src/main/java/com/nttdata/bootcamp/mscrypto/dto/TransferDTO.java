package com.nttdata.bootcamp.mscrypto.dto;

import lombok.Data;

@Data
public class TransferDTO {

    private Long walletId;
    private Double amount;
    private Long destinationWalletId;

}
