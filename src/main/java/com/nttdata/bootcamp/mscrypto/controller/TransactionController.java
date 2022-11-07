package com.nttdata.bootcamp.mscrypto.controller;

import com.nttdata.bootcamp.mscrypto.dto.TransactionDTO;
import com.nttdata.bootcamp.mscrypto.model.Transaction;
import com.nttdata.bootcamp.mscrypto.service.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/bootcamp/cryptoWalletTransaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = "/findAllTransactions")
    @ResponseBody
    public Flux<Transaction> findAllTransactions() {
        return transactionService.findAll();
    }

    @PostMapping(value = "/sellBootcoin")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> withdraw(@RequestBody TransactionDTO transactionDTO) {
        return transactionService.sellBootcoin(transactionDTO);
    }

    @GetMapping(value = "/find/{id}")
    @ResponseBody
    public Mono<ResponseEntity<Transaction>> findTransactionById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(transaction -> ResponseEntity.ok().body(transaction))
                .onErrorResume(e -> {
                    log.info("Transaction not found " + id, e);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/update/{id}")
    @ResponseBody
    public Mono<ResponseEntity<Transaction>> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return transactionService.update(id, transaction)
                .map(t -> new ResponseEntity<>(t, HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseBody
    public Mono<Void> deleteByIdTransaction(@PathVariable Long id) {
        return transactionService.delete(id);
    }

    @GetMapping(value = "/findAllByCryptoWalletId/{id}")
    @ResponseBody
    public Flux<Transaction> findAllByCryptoWalletId(@PathVariable Long id) {
        return transactionService.findAllByCryptoWalletId(id);
    }

}
