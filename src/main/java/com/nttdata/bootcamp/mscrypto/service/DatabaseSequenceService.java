package com.nttdata.bootcamp.mscrypto.service;

import reactor.core.publisher.Mono;

public interface DatabaseSequenceService {

    Mono<Long> generateSequence(String seqName);

}
