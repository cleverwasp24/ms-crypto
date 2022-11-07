package com.nttdata.bootcamp.mscrypto.service;

import com.nttdata.bootcamp.mscrypto.dto.CryptoWalletDTO;
import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CryptoWalletService {

    Flux<CryptoWallet> findAll();

    Mono<CryptoWallet> create(CryptoWallet cryptoWallet);

    Mono<CryptoWallet> findById(Long id);

    Mono<CryptoWallet> update(Long id, CryptoWallet cryptoWallet);

    Mono<Void> delete(Long id);

    Mono<String> createCryptoWallet(CryptoWalletDTO cryptoWalletDTO);

    Flux<CryptoWallet> findAllByClientId(Long id);

    Mono<String> checkFields(CryptoWallet cryptoWallet);

}
