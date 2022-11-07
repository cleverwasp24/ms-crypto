package com.nttdata.bootcamp.mscrypto.model;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "cryptowallet")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Log4j2
public class CryptoWallet {

    @Transient
    public static final String SEQUENCE_NAME = "cryptowallet_sequence";

    @Id
    private Long id;
    @NonNull
    private Long clientId;
    @NonNull
    @Indexed(unique = true)
    private String cryptoWalletNumber;
    @NonNull
    private Double balance;
    @NonNull
    private Double initialBalance;
    @NonNull
    private String imeiNumber;
    @NonNull
    private String phoneNumber;
    @Nullable
    private Long accountId;
    @Nullable
    private Long walletId;
    @NonNull
    private LocalDateTime creationDate;

}
