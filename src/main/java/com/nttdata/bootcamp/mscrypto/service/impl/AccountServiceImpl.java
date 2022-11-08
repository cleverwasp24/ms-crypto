package com.nttdata.bootcamp.mscrypto.service.impl;

import com.nttdata.bootcamp.mscrypto.dto.AccountDTO;
import com.nttdata.bootcamp.mscrypto.dto.AccountTransactionDTO;
import com.nttdata.bootcamp.mscrypto.service.AccountService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class AccountServiceImpl implements AccountService {

    private final WebClient webClient;

    public AccountServiceImpl(WebClient.Builder webClientBuilder) {
        //microservicio account
        this.webClient = webClientBuilder.baseUrl("http://ms-gateway:8088").build();
    }

    @CircuitBreaker(name = "service-account", fallbackMethod = "findByIdFallback")
    @TimeLimiter(name = "service-account")
    @Override
    public Mono<AccountDTO> findById(Long id) {
        Mono<AccountDTO> accountList = this.webClient.get()
                .uri("/bootcamp/account/find/{id}", id)
                .retrieve()
                .bodyToMono(AccountDTO.class);

        log.info("Account obtained from service ms-account:" + accountList);
        return accountList;
    }

    @CircuitBreaker(name = "service-account", fallbackMethod = "findAllByIdFallback")
    @TimeLimiter(name = "service-account")
    @Override
    public Flux<AccountDTO> findAllByClientId(Long id) {
        Flux<AccountDTO> accountList = this.webClient.get()
                .uri("/bootcamp/account/findAllByClientId/{id}", id)
                .retrieve()
                .bodyToFlux(AccountDTO.class);

        log.info("Account List obtained from service ms-account:" + accountList);
        return accountList;
    }

    public Mono<String> findByIdFallback(Long id, Throwable t) {
        log.error("Fallback method for findById (ACCOUNT) executed {}", t.getMessage());
        return Mono.empty();
    }
    public Mono<String> findAllByIdFallback(Long id, Throwable t) {
        log.error("Fallback method for findAllById (ACCOUNT) executed {}", t.getMessage());
        return Mono.empty();
    }

}
