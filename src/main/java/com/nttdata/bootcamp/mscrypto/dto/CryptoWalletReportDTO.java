package com.nttdata.bootcamp.mscrypto.dto;

import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CryptoWalletReportDTO {

    private CryptoWallet cryptoWallet;
    private List<DailyBalanceDTO> dailyBalances = new ArrayList<>();

}
