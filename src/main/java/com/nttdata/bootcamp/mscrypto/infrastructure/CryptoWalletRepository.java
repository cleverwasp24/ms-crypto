package com.nttdata.bootcamp.mscrypto.infrastructure;

import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CryptoWalletRepository extends ReactiveMongoRepository<CryptoWallet, Long> {

    Flux<CryptoWallet> findAllByClientId(Long id);

}
