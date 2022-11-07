package com.nttdata.bootcamp.mscrypto.service;

import com.nttdata.bootcamp.mscrypto.dto.ClientDTO;
import reactor.core.publisher.Mono;

public interface ClientService {

    Mono<ClientDTO> findById(Long id);

}
