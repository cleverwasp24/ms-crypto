package com.nttdata.bootcamp.mscrypto.service;

import com.nttdata.bootcamp.mscrypto.dto.WalletDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {

    Mono<WalletDTO> findById(Long id);
    Flux<WalletDTO> findAllByClientId(Long id);

}