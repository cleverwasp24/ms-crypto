package com.nttdata.bootcamp.mscrypto.model;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bootcoin")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Log4j2
public class Bootcoin {

    @Transient
    public static final String SEQUENCE_NAME = "bootcoin_sequence";

    @Id
    private Long id;
    @NonNull
    private Double price;
    @NonNull
    private LocalDateTime updatedAt;

}
