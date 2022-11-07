package com.nttdata.bootcamp.mscrypto.service;

import com.nttdata.bootcamp.mscrypto.dto.*;
import com.nttdata.bootcamp.mscrypto.dto.TransactionDTO;
import com.nttdata.bootcamp.mscrypto.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionService {

    Flux<Transaction> findAll();

    Mono<Transaction> create(Transaction transaction);

    Mono<Transaction> findById(Long id);

    Mono<Transaction> update(Long id, Transaction transaction);

    Mono<Void> delete(Long id);

    Mono<String> sellBootcoin(TransactionDTO transactionDTO);

    Flux<Transaction> findAllByCryptoWalletId(Long walletId);

    Flux<Transaction> findAllByCryptoWalletIdDesc(Long walletId);

    Flux<Transaction> findTransactionsWalletMonth(Long walletId, LocalDateTime date);

    Flux<Transaction> findTransactionsWalletPeriod(Long walletId, LocalDateTime start, LocalDateTime end);

    Mono<String> checkFields(Transaction transaction);

    Mono<CompleteReportDTO> generateCompleteReport(Long id, PeriodDTO periodDTO);

    Mono<Transaction> findLastTransactionBefore(Long id, LocalDateTime date);

    Mono<CryptoWalletReportDTO> generateWalletReportCurrentMonth(Long id);

    Mono<CryptoWalletReportDTO> generateWalletReport(Long id, PeriodDTO periodDTO);
}
