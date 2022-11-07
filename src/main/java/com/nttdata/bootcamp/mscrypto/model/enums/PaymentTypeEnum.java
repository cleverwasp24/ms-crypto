package com.nttdata.bootcamp.mscrypto.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum PaymentTypeEnum {

    ACCOUNT(0),

    WALLET(1);

    private int value;
    private static Map map = new HashMap();

    private PaymentTypeEnum(int value) {
        this.value = value;
    }

    static {
        for (PaymentTypeEnum transactionType : PaymentTypeEnum.values()) {
            map.put(transactionType.value, transactionType);
        }
    }

    public static PaymentTypeEnum valueOf(int transactionType) {
        return (PaymentTypeEnum) map.get(transactionType);
    }

    public int getValue() {
        return value;
    }


}
