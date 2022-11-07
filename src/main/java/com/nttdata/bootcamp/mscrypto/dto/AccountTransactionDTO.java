package com.nttdata.bootcamp.mscrypto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountTransactionDTO {

    private Long accountId;
    private Double amount;


}
