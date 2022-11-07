package com.nttdata.bootcamp.mscrypto.service;

import com.nttdata.bootcamp.mscrypto.dto.AccountDTO;
import com.nttdata.bootcamp.mscrypto.dto.AccountTransactionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    Mono<AccountDTO> findById(Long id);
    Flux<AccountDTO> findAllByClientId(Long id);
    Mono<String> cardPurchase(AccountTransactionDTO transactionDTO);
    Mono<String> cardDeposit(AccountTransactionDTO transactionDTO);

}
