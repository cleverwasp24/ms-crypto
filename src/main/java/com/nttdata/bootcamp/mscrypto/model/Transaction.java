package com.nttdata.bootcamp.mscrypto.model;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {

    @Transient
    public static final String SEQUENCE_NAME = "transaction_sequence";

    @Id
    private Long id;
    @NonNull
    private Long cryptoWalletId;
    @Nullable
    private Long destinationCryptoWalletId;
    @NonNull
    private Integer transactionType;
    @NonNull
    private Integer paymentType;
    @NonNull
    private String description;
    @NonNull
    private Double amount;
    @Nullable
    private Double amountExchange;
    @NonNull
    private Long bootcoinId;
    @NonNull
    private Double newBalance;
    @NonNull
    private LocalDateTime transactionDate;

}
