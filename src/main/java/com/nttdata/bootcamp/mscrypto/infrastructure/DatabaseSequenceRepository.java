package com.nttdata.bootcamp.mscrypto.infrastructure;

import com.nttdata.bootcamp.mscrypto.model.DatabaseSequence;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface DatabaseSequenceRepository extends ReactiveMongoRepository<DatabaseSequence, String> {

    Mono<DatabaseSequence> findDatabaseSequenceById(String seqName);

}
