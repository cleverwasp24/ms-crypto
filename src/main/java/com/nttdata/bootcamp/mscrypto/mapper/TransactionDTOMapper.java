package com.nttdata.bootcamp.mscrypto.mapper;

import com.nttdata.bootcamp.mscrypto.dto.TransactionDTO;
import com.nttdata.bootcamp.mscrypto.model.Transaction;
import com.nttdata.bootcamp.mscrypto.model.enums.TransactionTypeEnum;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class TransactionDTOMapper {

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    public TransactionDTO convertToDto(Transaction transaction, TransactionTypeEnum type) {
        return modelMapper.map(transaction, TransactionDTO.class);
    }

    public Transaction convertToEntity(TransactionDTO transactionDTO, TransactionTypeEnum type) {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(type.ordinal());

        switch (type) {
            case BUY -> transaction.setDescription("CRYPTO BUY +$ " + transaction.getAmount());
            case SELL -> transaction.setDescription("CRYPTO SELL -$ " + transaction.getAmount());
        }

        return transaction;
    }

    public Transaction generateDestinationWalletTransaction(Transaction transaction) {
        Transaction destinationTransaction = new Transaction();
        destinationTransaction.setCryptoWalletId(transaction.getDestinationCryptoWalletId());
        destinationTransaction.setDestinationCryptoWalletId(transaction.getDestinationCryptoWalletId());
        destinationTransaction.setAmount(transaction.getAmount());
        destinationTransaction.setAmountExchange(transaction.getAmountExchange());
        switch (TransactionTypeEnum.valueOf(transaction.getTransactionType())) {
            case BUY:
                destinationTransaction.setTransactionType(TransactionTypeEnum.SELL.ordinal());
                destinationTransaction.setDescription("CRYPTO SELL -$ " + transaction.getAmount());
                break;
            case SELL:
                destinationTransaction.setTransactionType(TransactionTypeEnum.BUY.ordinal());
                destinationTransaction.setDescription("CRYPTO BUY +$ " + transaction.getAmount());
                break;
        }
        destinationTransaction.setTransactionDate(LocalDateTime.now());
        return destinationTransaction;
    }
}
