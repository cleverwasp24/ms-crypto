package com.nttdata.bootcamp.mscrypto.service;

import com.nttdata.bootcamp.mscrypto.dto.BootcoinDTO;
import com.nttdata.bootcamp.mscrypto.model.Bootcoin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcoinService {

    Flux<Bootcoin> findAll();

    Mono<Bootcoin> create(Bootcoin bootcoin);

    Mono<Bootcoin> findById(Long id);

    Mono<Bootcoin> update(Long id, Bootcoin bootcoin);

    Mono<Void> delete(Long id);

    Mono<String> createBootcoin(BootcoinDTO bootcoinDTO);

    Mono<Bootcoin> getLastBootcoin();

}
