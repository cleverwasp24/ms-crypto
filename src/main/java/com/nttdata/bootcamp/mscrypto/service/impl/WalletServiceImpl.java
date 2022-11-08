package com.nttdata.bootcamp.mscrypto.service.impl;

import com.nttdata.bootcamp.mscrypto.dto.WalletDTO;
import com.nttdata.bootcamp.mscrypto.dto.AccountTransactionDTO;
import com.nttdata.bootcamp.mscrypto.service.AccountService;
import com.nttdata.bootcamp.mscrypto.service.WalletService;
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
public class WalletServiceImpl implements WalletService {

    private final WebClient webClient;

    public WalletServiceImpl(WebClient.Builder webClientBuilder) {
        //microservicio wallet
        this.webClient = webClientBuilder.baseUrl("http://ms-gateway:8088").build();
    }

    @CircuitBreaker(name = "service-wallet", fallbackMethod = "findByIdFallback")
    @TimeLimiter(name = "service-wallet")
    @Override
    public Mono<WalletDTO> findById(Long id) {
        Mono<WalletDTO> walletList = this.webClient.get()
                .uri("/bootcamp/wallet/find/{id}", id)
                .retrieve()
                .bodyToMono(WalletDTO.class);

        log.info("Account obtained from service ms-wallet:" + walletList);
        return walletList;
    }

    @CircuitBreaker(name = "service-wallet", fallbackMethod = "findAllByIdFallback")
    @TimeLimiter(name = "service-wallet")
    @Override
    public Flux<WalletDTO> findAllByClientId(Long id) {
        Flux<WalletDTO> walletList = this.webClient.get()
                .uri("/bootcamp/wallet/findAllByClientId/{id}", id)
                .retrieve()
                .bodyToFlux(WalletDTO.class);

        log.info("Wallet List obtained from service ms-wallet:" + walletList);
        return walletList;
    }

    public Mono<String> findByIdFallback(Long id, Throwable t) {
        log.error("Fallback method for findById (WALLET) executed {}", t.getMessage());
        return Mono.empty();
    }
    public Mono<String> findAllByIdFallback(Long id, Throwable t) {
        log.error("Fallback method for findAllById (WALLET) executed {}", t.getMessage());
        return Mono.empty();
    }

}
