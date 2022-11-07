package com.nttdata.bootcamp.mscrypto.infrastructure;

import com.nttdata.bootcamp.mscrypto.model.Bootcoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootcoinRepository extends ReactiveMongoRepository<Bootcoin, Long> {
}
