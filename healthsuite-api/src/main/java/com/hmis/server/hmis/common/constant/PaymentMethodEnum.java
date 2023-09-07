package com.hmis.server.hmis.common.constant;

import java.util.Arrays;

public enum PaymentMethodEnum {
    CASH {
        @Override
        public String title() {return "CASH";}
    },
    POS {
        @Override
        public String title() {return "POS";}
    },
    ETF {
        @Override
        public String title() {return "ETF";}
    },
    CHEQUE {
        @Override
        public String title() {return "CHEQUE";}
    },
    MOBILE_MONEY {
        @Override
        public String title() {return "MOBILE MONEY";}
    };


    public abstract String title();

    public static PaymentMethodEnum getPaymentMethodEnumValue(String title) {
        return Arrays.stream(values()).filter(value -> value.title().equals(title)).findFirst().orElse(null);
    }


}
