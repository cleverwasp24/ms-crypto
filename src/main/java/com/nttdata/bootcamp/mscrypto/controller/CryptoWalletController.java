package com.nttdata.bootcamp.mscrypto.controller;

import com.nttdata.bootcamp.mscrypto.dto.*;
import com.nttdata.bootcamp.mscrypto.model.Transaction;
import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import com.nttdata.bootcamp.mscrypto.service.TransactionService;
import com.nttdata.bootcamp.mscrypto.service.CryptoWalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/bootcamp/cryptoWallet")
public class CryptoWalletController {

    @Autowired
    CryptoWalletService cryptoWalletService;

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = "/findAllCryptoWallets")
    @ResponseBody
    public Flux<CryptoWallet> findAllCryptoWallets() {
        return cryptoWalletService.findAll();
    }

    @GetMapping(value = "/findAllCryptoWalletsByClientId/{id}")
    @ResponseBody
    public Flux<CryptoWallet> findAllCryptoWalletsByClientId(@PathVariable Long id) {
        return cryptoWalletService.findAllByClientId(id);
    }

    @PostMapping(value = "/createCryptoWallet")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createCryptoWallet(@RequestBody CryptoWalletDTO savingsCryptoWalletDTO) {
        return cryptoWalletService.createWallet(savingsCryptoWalletDTO);
    }

    @GetMapping(value = "/find/{id}")
    @ResponseBody
    public Mono<ResponseEntity<CryptoWallet>> findCryptoWalletById(@PathVariable Long id) {
        return cryptoWalletService.findById(id)
                .map(cryptoWallet -> ResponseEntity.ok().body(cryptoWallet))
                .onErrorResume(e -> {
                    log.info("Wallet not found " + id, e);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/update/{id}")
    @ResponseBody
    public Mono<ResponseEntity<CryptoWallet>> updateCryptoWallet(@PathVariable Long id, @RequestBody CryptoWallet cryptoWallet) {
        return cryptoWalletService.update(id, cryptoWallet)
                .map(a -> new ResponseEntity<>(a, HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseBody
    public Mono<Void> deleteByIdWallet(@PathVariable Long id) {
        return cryptoWalletService.delete(id);
    }

    @GetMapping(value = "/findAllByClientId/{id}")
    @ResponseBody
    public Flux<CryptoWallet> findAllByClientId(@PathVariable Long id) {
        return cryptoWalletService.findAllByClientId(id);
    }

    @GetMapping(value = "/getDailyBalanceReportCurrentMonth/{id}")
    @ResponseBody
    public Mono<CryptoWalletReportDTO> getDailyBalanceReportCurrentMonth(@PathVariable Long id) {
        return cryptoWalletService.findById(id)
                .flatMap(cryptoWallet -> transactionService.generateWalletReportCurrentMonth(cryptoWallet.getId()))
                .switchIfEmpty(Mono.error(new Exception("CryptoWallet not found")));
    }

    @GetMapping(value = "/getDailyBalanceReport/{id}")
    @ResponseBody
    public Mono<CryptoWalletReportDTO> getDailyBalanceReport(@PathVariable Long id, @RequestBody PeriodDTO periodDTO) {
        return cryptoWalletService.findById(id)
                .flatMap(cryptoWallet -> transactionService.generateWalletReport(cryptoWallet.getId(), periodDTO))
                .switchIfEmpty(Mono.error(new Exception("CryptoWallet not found")));
    }

    @GetMapping(value = "/getLatestTenTransactions/{id}")
    @ResponseBody
    public Flux<Transaction> getLatestTenTransactions(@PathVariable Long id) {
        return cryptoWalletService.findById(id)
                .flatMapMany(cryptoWallet -> transactionService.findAllByWalletIdDesc(cryptoWallet.getId()).take(10))
                .switchIfEmpty(Mono.error(new Exception("CryptoWallet not found")));
    }

    @GetMapping(value = "/getCompleteReport/{id}")
    @ResponseBody
    public Mono<CompleteReportDTO> getCompleteReport(@PathVariable Long id, @RequestBody PeriodDTO periodDTO) {
        return cryptoWalletService.findById(id)
                .flatMap(cryptoWallet -> transactionService.generateCompleteReport(cryptoWallet.getId(), periodDTO))
                .switchIfEmpty(Mono.error(new Exception("CryptoWallet not found")));
    }

}
