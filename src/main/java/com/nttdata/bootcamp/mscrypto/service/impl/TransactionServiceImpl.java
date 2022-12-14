package com.nttdata.bootcamp.mscrypto.service.impl;

import com.nttdata.bootcamp.mscrypto.dto.*;
import com.nttdata.bootcamp.mscrypto.infrastructure.TransactionRepository;
import com.nttdata.bootcamp.mscrypto.mapper.TransactionDTOMapper;
import com.nttdata.bootcamp.mscrypto.model.Transaction;
import com.nttdata.bootcamp.mscrypto.model.CryptoWallet;
import com.nttdata.bootcamp.mscrypto.model.enums.PaymentTypeEnum;
import com.nttdata.bootcamp.mscrypto.model.enums.TransactionTypeEnum;
import com.nttdata.bootcamp.mscrypto.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CryptoWalletService cryptoWalletService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BootcoinService bootcoinService;

    @Autowired
    private DatabaseSequenceService databaseSequenceService;

    private TransactionDTOMapper transactionDTOMapper = new TransactionDTOMapper();

    @Override
    public Flux<Transaction> findAll() {
        log.info("Listing all transactions");
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> create(Transaction transaction) {
        log.info("Creating transaction: " + transaction.toString());
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> findById(Long id) {
        log.info("Searching transaction by id: " + id);
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> update(Long id, Transaction transaction) {
        log.info("Updating transaction with id: " + id + " with : " + transaction.toString());
        return transactionRepository.findById(id).flatMap(a -> {
            transaction.setId(id);
            return transactionRepository.save(transaction);
        });
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.info("Deleting transaction with id: " + id);
        return transactionRepository.deleteById(id);
    }

    /**
     * This method sells Bootcoin to another crypto wallet
     *
     * @param transactionDTO
     * @return
     */
    @Override
    public Mono<String> sellBootcoin(TransactionDTO transactionDTO) {
        log.info("Selling bootcoin: " + transactionDTO.toString() + " destination wallet: " + transactionDTO.getDestinationCryptoWalletId());
        Transaction transaction = transactionDTOMapper.convertToEntity(transactionDTO, TransactionTypeEnum.SELL);
        //Validar los datos de la transferencia
        return checkFields(transaction)
                //Si los datos son correctos, validar que el monedero origen exista
                .switchIfEmpty(cryptoWalletService.findById(transaction.getCryptoWalletId()).flatMap(originWallet -> {
                    //Si el monedero origen existe, verificar que el monedero origen tenga el saldo suficiente para realizar la transferencia
                    return cryptoWalletService.findById(transaction.getDestinationCryptoWalletId()).flatMap(destinationWallet -> {
                        originWallet.setBalance(originWallet.getBalance() - transaction.getAmount());
                        if (originWallet.getBalance() < 0) {
                            return Mono.error(new IllegalArgumentException("Insufficient balance to sell"));
                        }
                        transaction.setNewBalance(originWallet.getBalance());
                        destinationWallet.setBalance(destinationWallet.getBalance() + transaction.getAmount());
                        //Crear una transacci??n en el monedero destino
                        Transaction destinationWalletTransaction = transactionDTOMapper.generateDestinationWalletTransaction(transaction);
                        destinationWalletTransaction.setNewBalance(destinationWallet.getBalance());
                        //Calcular precio de bootcoin en ese momento
                        return bootcoinService.getLastBootcoin().flatMap(b -> {
                            transaction.setBootcoinId(b.getId());
                            transaction.setAmountExchange(transaction.getAmount() * b.getPrice());
                            switch (PaymentTypeEnum.valueOf(transaction.getPaymentType())) {
                                case ACCOUNT:
                                    //Verificar que la cuenta exista
                                    return accountService.findById(originWallet.getAccountId()).flatMap(ac -> {
                                        //verificar que la cuenta de la cuenta destino tenga saldo suficiente
                                        return accountService.findById(destinationWallet.getAccountId()).flatMap(da -> {
                                            //Si la cuenta destino tiene saldo suficiente, realizar la transferencia
                                            ac.setBalance(ac.getBalance() - transaction.getAmountExchange());
                                            if (ac.getBalance() < 0) {
                                                return Mono.error(new IllegalArgumentException("Insufficient balance in the buyer account"));
                                            } else {
                                                //Actualizar monedero origen
                                                return cryptoWalletService.update(originWallet.getId(), originWallet)
                                                        //Generar id de la transacci??n y registrar la transacci??n en el monedero origen
                                                        .flatMap(oa -> databaseSequenceService.generateSequence(Transaction.SEQUENCE_NAME).flatMap(id -> {
                                                            transaction.setId(id);
                                                            return transactionRepository.save(transaction);
                                                        }))
                                                        //Actualizar monedero destino
                                                        .flatMap(ot -> cryptoWalletService.update(destinationWallet.getId(), destinationWallet))
                                                        //Generar id de la transacci??n y registrar la transacci??n en el monedero destino
                                                        .flatMap(dw -> databaseSequenceService.generateSequence(Transaction.SEQUENCE_NAME).flatMap(id -> {
                                                            destinationWalletTransaction.setId(id);
                                                            return transactionRepository.save(destinationWalletTransaction);
                                                        }))
                                                        .flatMap(dt -> Mono.just("Bootcoin sold, new balance: " + originWallet.getBalance()));
                                            }
                                        });
                                    });
                                case WALLET:
                                    //Verificar que exista el wallet

                                    //Actualizar monedero origen
                                    return cryptoWalletService.update(originWallet.getId(), originWallet)
                                            //Generar id de la transacci??n y registrar la transacci??n en el monedero origen
                                            .flatMap(oa -> databaseSequenceService.generateSequence(Transaction.SEQUENCE_NAME).flatMap(id -> {
                                                transaction.setId(id);
                                                return transactionRepository.save(transaction);
                                            }))
                                            //Actualizar monedero destino
                                            .flatMap(ot -> cryptoWalletService.update(destinationWallet.getId(), destinationWallet))
                                            //Generar id de la transacci??n y registrar la transacci??n en el monedero destino
                                            .flatMap(da -> databaseSequenceService.generateSequence(Transaction.SEQUENCE_NAME).flatMap(id -> {
                                                destinationWalletTransaction.setId(id);
                                                return transactionRepository.save(destinationWalletTransaction);
                                            }))
                                            .flatMap(dt -> Mono.just("Bootcoin sold, new balance: " + originWallet.getBalance()));
                                default:
                                    return Mono.error(new IllegalArgumentException("Payment type not valid!"));
                            }
                        });
                        //Si el monedero destino no existe, se cancela la transacci??n
                    }).switchIfEmpty(Mono.error(new IllegalArgumentException("Destination crypto wallet not found")));
                    //Si el monedero origen no existe, se cancela la transacci??n
                }).switchIfEmpty(Mono.error(new IllegalArgumentException("Origin Crypto Wallet not found"))));
    }

    /**
     * This method finds all transactions by wallet id
     *
     * @param walletId
     * @return
     */
    @Override
    public Flux<Transaction> findAllByCryptoWalletId(Long walletId) {
        log.info("Listing all transactions by wallet id");
        return transactionRepository.findAllByCryptoWalletId(walletId);
    }

    /**
     * This method finds all transactions by wallet id in descending order by date
     *
     * @param walletId
     * @return
     */
    @Override
    public Flux<Transaction> findAllByCryptoWalletIdDesc(Long walletId) {
        log.info("Listing all transactions by wallet id order by date desc");
        return transactionRepository.findAllByCryptoWalletIdOrderByTransactionDateDesc(walletId);
    }

    /**
     * This method finds all transactions by wallet id in the current month
     *
     * @param walletId
     * @param date
     * @return
     */
    @Override
    public Flux<Transaction> findTransactionsWalletMonth(Long walletId, LocalDateTime date) {
        return transactionRepository.findAllByCryptoWalletIdAndTransactionDateBetween(walletId,
                date.withDayOfMonth(1).with(LocalTime.MIN), date.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX));
    }

    /**
     * This method finds all transactions by wallet id in a range of dates
     *
     * @param walletId
     * @param start
     * @param end
     * @return
     */
    @Override
    public Flux<Transaction> findTransactionsWalletPeriod(Long walletId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findAllByCryptoWalletIdAndTransactionDateBetween(walletId, start, end);
    }

    /**
     * This method validates the fields of the transaction
     *
     * @param transaction
     * @return
     */
    @Override
    public Mono<String> checkFields(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            return Mono.error(new IllegalArgumentException("Wallet transaction amount must be greater than 0"));
        }
        return Mono.empty();
    }

    /**
     * This method generates a complete report of the transactions of a wallet
     *
     * @param id
     * @param periodDTO
     * @return
     */
    @Override
    public Mono<CompleteReportDTO> generateCompleteReport(Long id, PeriodDTO periodDTO) {
        log.info("Generating complete report in a period: " + periodDTO.getStart() + " - " + periodDTO.getEnd());
        Mono<CompleteReportDTO> completeReportDTOMono = Mono.just(new CompleteReportDTO());
        Mono<CryptoWallet> walletMono = cryptoWalletService.findById(id);
        Flux<Transaction> transactionFlux = findTransactionsWalletPeriod(id, periodDTO.getStart(), periodDTO.getEnd());
        return completeReportDTOMono.flatMap(r -> walletMono.map(wallet -> {
            r.setCryptoWallet(wallet);
            return r;
        }).flatMap(r2 -> transactionFlux.collectList().map(transactions -> {
            r2.setTransactions(transactions);
            return r2;
        })));
    }

    /**
     * This method finds the last transaction of a wallet made before a specific date
     * Este metodo encuentra la ??ltima transacci??n realizada en un monedero antes de una fecha
     *
     * @param id
     * @param date
     * @return
     */
    @Override
    public Mono<Transaction> findLastTransactionBefore(Long id, LocalDateTime date) {
        return transactionRepository.findByCryptoWalletIdAndTransactionDateBeforeOrderByTransactionDateDesc(id, date)
                .flatMap(t -> Mono.just(t))
                //if it is empty take the wallet opening balance and creation date
                .switchIfEmpty(cryptoWalletService.findById(id).flatMap(a -> {
                    Transaction transaction = new Transaction();
                    transaction.setNewBalance(a.getInitialBalance());
                    transaction.setTransactionDate(a.getCreationDate());
                    return Mono.just(transaction);
                }));
    }

    @Override
    public Mono<CryptoWalletReportDTO> generateWalletReportCurrentMonth(Long id) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).with(LocalTime.MIN);
        log.info("Generating wallet report for current month: " + start + " - " + now);
        return generateWalletReport(id, new PeriodDTO(start, now));
    }

    @Override
    public Mono<CryptoWalletReportDTO> generateWalletReport(Long id, PeriodDTO periodDTO) {
        log.info("Generating wallet report in a period: " + periodDTO.getStart() + " - " + periodDTO.getEnd());
        Mono<CryptoWalletReportDTO> walletReportDTOMono = Mono.just(new CryptoWalletReportDTO());
        Mono<CryptoWallet> walletMono = cryptoWalletService.findById(id);
        Mono<Transaction> firstBefore = findLastTransactionBefore(id, periodDTO.getStart());
        Flux<Transaction> transactionFlux = findTransactionsWalletPeriod(id, periodDTO.getStart(), periodDTO.getEnd());
        return walletReportDTOMono.flatMap(r -> walletMono.map(wallet -> {
                    r.setCryptoWallet(wallet);
                    return r;
                }))
                .flatMap(r -> transactionFlux.collectList().map(tl -> {
                    tl = tl.stream().collect(
                                    Collectors.groupingBy(t -> t.getTransactionDate().toLocalDate(),
                                            Collectors.collectingAndThen(
                                                    Collectors.maxBy(
                                                            Comparator.comparing(Transaction::getTransactionDate)),
                                                    transaction -> transaction.get())))
                            .values().stream().collect(Collectors.toList());
                    //Add all transactions to the report as daily balances
                    tl.forEach(t -> r.getDailyBalances().add(new DailyBalanceDTO(t.getTransactionDate().toLocalDate(), t.getNewBalance())));
                    return r;
                }))
                .flatMap(r -> firstBefore.map(t -> {
                    //If transaction list does not contain a transaction on the start date, add it
                    if (r.getDailyBalances().stream().noneMatch(ta -> ta.getDate().equals(periodDTO.getStart().toLocalDate()))) {
                        if (t.getTransactionDate().toLocalDate().equals(periodDTO.getStart().toLocalDate())) {
                            r.getDailyBalances().add(new DailyBalanceDTO(t.getTransactionDate().toLocalDate(), t.getNewBalance()));
                        } else {
                            r.getDailyBalances().add(new DailyBalanceDTO(periodDTO.getStart().toLocalDate(), 0.00));
                        }
                    }
                    return r;
                }))
                //Fill missingDays in the transaction list
                .flatMap(r -> {
                    long days = ChronoUnit.DAYS.between(periodDTO.getStart().toLocalDate(), periodDTO.getEnd().toLocalDate());
                    HashMap<LocalDate, Double> map = new HashMap<>();
                    r.getDailyBalances().forEach(t -> map.put(t.getDate(), t.getBalance()));
                    for (int i = 1; i <= days; i++) {
                        LocalDate date = periodDTO.getStart().toLocalDate().plusDays(i);
                        if (!map.containsKey(date)) {
                            map.put(date, map.get(date.minusDays(1)));
                        }
                    }
                    r.setDailyBalances(new ArrayList<>());
                    map.forEach((k, v) -> r.getDailyBalances().add(new DailyBalanceDTO(k, v)));
                    //Sort the list by date
                    r.getDailyBalances().sort(Comparator.comparing(DailyBalanceDTO::getDate));
                    return Mono.just(r);
                });
    }

}
