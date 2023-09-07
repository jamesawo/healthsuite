package com.hmis.server.hmis.modules.billing.dto;

import java.io.InputStream;

import com.hmis.server.hmis.common.common.service.HmisUtilService;

public enum ReceiptTypeEnum {
    DEPOSIT_RECEIPT {
        final String path = "/receipts/deposit_receipt";

        @Override
        public String filePath(String size) {
            return SRC + path + size + EXT;
        }

        @Override
        public InputStream absoluteFilePath(HmisUtilService util, String size) {
            return util.getAbsoluteFilePath(path + size + EXT);

            /*
             * if (util.isDevOrProdProfile()) {
             * return SRC + path + size + EXT;
             * } else {
             * return util.getAbsoluteFilePath(path + size + EXT);
             * }
             */
        }
    },
    DRUG_BILL_INVOICE {
        final String path = "/invoice/drug_bill_invoice";

        @Override
        public String filePath(String size) {
            return SRC + path + size + EXT;
        }

        @Override
        public InputStream absoluteFilePath(HmisUtilService util, String size) {
            return util.getAbsoluteFilePath(path + size + EXT);
            /*
             * if (util.isDevOrProdProfile()) {
             * return SRC + path + size + EXT;
             * } else {
             * return util.getAbsoluteFilePath(path + size + EXT);
             * }
             */
        }
    },
    DRUG_BILL_RECEIPT {
        @Override
        public String filePath(String size) {
            return null;
        }

        @Override
        public InputStream absoluteFilePath(HmisUtilService util, String size) {
            return null;
        }
    },
    SERVICE_BILL_INVOICE {
        final String path = "/invoice/service_bill_invoice";

        @Override
        public String filePath(String size) {
            return SRC + path + size + EXT;
        }

        @Override
        public InputStream absoluteFilePath(HmisUtilService util, String size) {
            return util.getAbsoluteFilePath(path + size + EXT);
            /*
             * if (util.isDevOrProdProfile()) {
             * return SRC + path + size + EXT;
             * } else {
             * return util.getAbsoluteFilePath(path + size + EXT);
             * }
             */
        }
    },
    SERVICE_BILL_RECEIPT {
        final String path = "/receipts/service_receipt";

        @Override
        public String filePath(String size) {
            return SRC + path + size + EXT;
        }

        @Override
        public InputStream absoluteFilePath(HmisUtilService util, String size) {
            return util.getAbsoluteFilePath(path + size + EXT);
            /*
             * if (util.isDevOrProdProfile()) {
             * return SRC + path + size + EXT;
             * } else {
             * return util.getAbsoluteFilePath(path + size + EXT);
             * }
             */
        }
    };

    public abstract String filePath(String printerSize);

    public abstract InputStream absoluteFilePath(HmisUtilService util, String printerSize);

    public static final String SRC = "src/main/resources";
    public static final String EXT = ".jrxml";
}
