package com.nttdata.bootcamp.mscrypto.infrastructure;

import com.nttdata.bootcamp.mscrypto.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, Long> {

    Flux<Transaction> findAllByCryptoWalletId(Long cryptoWalletId);

    Flux<Transaction> findAllByCryptoWalletIdOrderByTransactionDateDesc(Long cryptoWalletId);

    Flux<Transaction> findAllByCryptoWalletIdAndTransactionDateBetween(Long cryptoWalletId, LocalDateTime start, LocalDateTime end);

    Mono<Transaction> findByCryptoWalletIdAndTransactionDateBeforeOrderByTransactionDateDesc(Long cryptoWalletId, LocalDateTime date);

}
