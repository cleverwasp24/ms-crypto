package com.nttdata.bootcamp.mscrypto.controller;

import com.nttdata.bootcamp.mscrypto.dto.BootcoinDTO;
import com.nttdata.bootcamp.mscrypto.model.Bootcoin;
import com.nttdata.bootcamp.mscrypto.service.BootcoinService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/bootcamp/bootcoin")
public class BootcoinController {

    @Autowired
    BootcoinService bootcoinService;

    @GetMapping(value = "/findAllBootcoins")
    @ResponseBody
    public Flux<Bootcoin> findAllBootcoins() {
        return bootcoinService.findAll();
    }

    @PostMapping(value = "/createBootcoin")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createBootcoin(@RequestBody BootcoinDTO bootcoinDTO) {
        return bootcoinService.createBootcoin(bootcoinDTO);
    }

    @GetMapping(value = "/find/{id}")
    @ResponseBody
    public Mono<ResponseEntity<Bootcoin>> findBootcoinById(@PathVariable Long id) {
        return bootcoinService.findById(id)
                .map(bootcoin -> ResponseEntity.ok().body(bootcoin))
                .onErrorResume(e -> {
                    log.info("Bootcoin not found " + id, e);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/update/{id}")
    @ResponseBody
    public Mono<ResponseEntity<Bootcoin>> updateBootcoin(@PathVariable Long id, @RequestBody Bootcoin bootcoin) {
        return bootcoinService.update(id, bootcoin)
                .map(a -> new ResponseEntity<>(a, HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseBody
    public Mono<Void> deleteByIdWallet(@PathVariable Long id) {
        return bootcoinService.delete(id);
    }

    @GetMapping(value = "/getLastBootcoin")
    @ResponseBody
    public Mono<ResponseEntity<Bootcoin>> getLastBootcoin() {
        return bootcoinService.getLastBootcoin()
                .map(bootcoin -> ResponseEntity.ok().body(bootcoin))
                .onErrorResume(e -> {
                    log.info("Bootcoin not found", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
