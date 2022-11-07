package com.nttdata.bootcamp.mscrypto.service.impl;

import com.nttdata.bootcamp.mscrypto.dto.CryptoWalletDTO;
import com.nttdata.bootcamp.mscrypto.infrastructure.CryptoWalletRepository;
import com.nttdata.bootcamp.mscrypto.mapper.CryptoWalletDTOMapper;
import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import com.nttdata.bootcamp.mscrypto.service.ClientService;
import com.nttdata.bootcamp.mscrypto.service.DatabaseSequenceService;
import com.nttdata.bootcamp.mscrypto.service.CryptoWalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class CryptoWalletServiceImpl implements CryptoWalletService {

    @Autowired
    private CryptoWalletRepository cryptoWalletRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DatabaseSequenceService databaseSequenceService;

    private CryptoWalletDTOMapper cryptoWalletDTOMapper = new CryptoWalletDTOMapper();

    @Override
    public Flux<CryptoWallet> findAll() {
        log.info("Listing all crypto wallets");
        return cryptoWalletRepository.findAll();
    }

    @Override
    public Mono<CryptoWallet> create(CryptoWallet cryptoWallet) {
        log.info("Creating crypto wallet: " + cryptoWallet.toString());
        return cryptoWalletRepository.save(cryptoWallet);
    }

    @Override
    public Mono<CryptoWallet> findById(Long id) {
        log.info("Searching crypto wallet by id: " + id);
        return cryptoWalletRepository.findById(id);
    }

    @Override
    public Mono<CryptoWallet> update(Long id, CryptoWallet cryptoWallet) {
        log.info("Updating crypto wallet with id: " + id + " with : " + cryptoWallet.toString());
        return cryptoWalletRepository.findById(id).flatMap(a -> {
            cryptoWallet.setId(id);
            return cryptoWalletRepository.save(cryptoWallet);
        });
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.info("Deleting crypto wallet with id: " + id);
        return cryptoWalletRepository.deleteById(id);
    }

    @Override
    public Mono<String> createCryptoWallet(CryptoWalletDTO cryptoWalletDTO) {
        log.info("Creating crypto wallet: " + cryptoWalletDTO.toString());
        CryptoWallet cryptoWallet = cryptoWalletDTOMapper.convertToEntity(cryptoWalletDTO);
        //Validar los datos del monedero crypto
        return checkFields(cryptoWallet)
                //Validar que el cliente exista
                .switchIfEmpty(clientService.findById(cryptoWallet.getClientId()).flatMap(c -> {
                    return databaseSequenceService.generateSequence(CryptoWallet.SEQUENCE_NAME).flatMap(sequence -> {
                        cryptoWallet.setId(sequence);
                        return cryptoWalletRepository.save(cryptoWallet)
                                .flatMap(w -> Mono.just("Wallet created! " + cryptoWalletDTOMapper.convertToDto(w)));
                    });
                }).switchIfEmpty(Mono.error(new IllegalArgumentException("Client not found"))));
    }

    @Override
    public Flux<CryptoWallet> findAllByClientId(Long id) {
        log.info("Listing all crypto wallets by client id");
        return cryptoWalletRepository.findAllByClientId(id);
    }

    @Override
    public Mono<String> checkFields(CryptoWallet cryptoWallet) {
        if (cryptoWallet.getCryptoWalletNumber() == null || cryptoWallet.getCryptoWalletNumber().trim().equals("")) {
            return Mono.error(new IllegalArgumentException("Crypto wallet number cannot be empty"));
        }
        if (cryptoWallet.getBalance() == null || cryptoWallet.getBalance() < 0) {
            return Mono.error(new IllegalArgumentException("New crypto wallet balance must be equal or greater than 0"));
        }
        if (cryptoWallet.getPhoneNumber() == null || cryptoWallet.getPhoneNumber().trim().equals("")) {
            return Mono.error(new IllegalArgumentException("Phone number cannot be empty"));
        }
        if (cryptoWallet.getImeiNumber() == null || cryptoWallet.getImeiNumber().trim().equals("")) {
            return Mono.error(new IllegalArgumentException("Imei number cannot be empty"));
        }
        return Mono.empty();
    }

}
