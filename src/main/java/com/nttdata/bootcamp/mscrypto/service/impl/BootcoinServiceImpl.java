package com.nttdata.bootcamp.mscrypto.service.impl;

import com.mongodb.Block;
import com.nttdata.bootcamp.mscrypto.dto.BootcoinDTO;
import com.nttdata.bootcamp.mscrypto.infrastructure.BootcoinRepository;
import com.nttdata.bootcamp.mscrypto.mapper.BootcoinDTOMapper;
import com.nttdata.bootcamp.mscrypto.model.Bootcoin;
import com.nttdata.bootcamp.mscrypto.model.Bootcoin;
import com.nttdata.bootcamp.mscrypto.service.BootcoinService;
import com.nttdata.bootcamp.mscrypto.service.DatabaseSequenceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Log4j2
@Service
public class BootcoinServiceImpl implements BootcoinService {

    @Autowired
    private BootcoinRepository bootcoinRepository;

    @Autowired
    private DatabaseSequenceService databaseSequenceService;

    private BootcoinDTOMapper bootcoinDTOMapper = new BootcoinDTOMapper();

    @Override
    public Flux<Bootcoin> findAll() {
        log.info("Listing all bootcoin");
        return bootcoinRepository.findAll();
    }

    @Override
    public Mono<Bootcoin> create(Bootcoin bootcoin) {
        log.info("Creating bootcoin: " + bootcoin.toString());
        return bootcoinRepository.save(bootcoin);
    }

    @Override
    public Mono<Bootcoin> findById(Long id) {
        log.info("Searching bootcoin by id: " + id);
        return bootcoinRepository.findById(id);
    }

    @Override
    public Mono<Bootcoin> update(Long id, Bootcoin bootcoin) {
        log.info("Updating bootcoin with id: " + id + " with : " + bootcoin.toString());
        return bootcoinRepository.findById(id).flatMap(a -> {
            bootcoin.setId(id);
            return bootcoinRepository.save(bootcoin);
        });
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.info("Deleting bootcoin with id: " + id);
        return bootcoinRepository.deleteById(id);
    }

    @Override
    public Mono<String> createBootcoin(BootcoinDTO bootcoinDTO) {
        log.info("Creating bootcoin: " + bootcoinDTO.toString());
        Bootcoin bootcoin = bootcoinDTOMapper.convertToEntity(bootcoinDTO);
        return databaseSequenceService.generateSequence(Bootcoin.SEQUENCE_NAME).flatMap(sequence -> {
            bootcoin.setId(sequence);
            return bootcoinRepository.save(bootcoin)
                    .flatMap(b -> Mono.just("Bootcoin created! " + bootcoinDTOMapper.convertToDto(b)));
        });
    }

    @Override
    public Mono<Bootcoin> getLastBootcoin() {
        return bootcoinRepository.findAll().elementAt(0);
    }
}
