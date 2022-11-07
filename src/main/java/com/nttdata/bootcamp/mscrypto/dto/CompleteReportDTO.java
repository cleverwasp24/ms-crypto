package com.nttdata.bootcamp.mscrypto.dto;

import com.nttdata.bootcamp.mscrypto.model.Transaction;
import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import lombok.Data;

import java.util.List;

@Data
public class CompleteReportDTO {

    private CryptoWallet cryptoWallet;
    private List<Transaction> transactions;

}
