package kr.co.tbell.echeck.constant;

import android.provider.BaseColumns;

public class ColumnContract {

    private ColumnContract() {

    }

    public static class ColumnEntry implements BaseColumns {

        public static final String USER_TABLE_NAME = "user";
        public static final String COLUMN_NICKNAME = "nickname";
        public static final String COLUMN_BEFORE_ELECRT = "elect_before";
        public static final String COLUMN_NOW_PERIOD = "elect_period";
        public static final String COLUMN_USE = "elect_use";
        public static final String COLUMN_HOUSE = "elect_house";
        public static final String COLUMN_HOUSE_COUNT = "house_count";
        public static final String COLUMN_USER_CREATED_AT = "created_at";

        public static final String HOUSE_DISCOUNT_TABLE_NAME = "house";
        public static final String COLUMN_HOUSE_DISCOUNT_YN = "house_discount_yn";
        public static final String COLUMN_HOUSE_DISCOUNT1 = "house_discount1";
        public static final String COLUMN_HOUSE_DISCOUNT2 = "house_discount2";
        public static final String COLUMN_USER_ID = "user_house_id";

        public static final String AGREE_TABLE_NAME = "agree";
        public static final String COLUMN_AGREE_NAME = "agree_name";
        public static final String COLUMN_AGREE_TYPE = "agree_type";
        public static final String COLUMN_AGREE_YN = "agree_yn";
        public static final String COLUMN_AGREE_CREATED_AT = "created_at";

        public static final String ELECT_TABLE_NAME = "elect";
        public static final String COLUMN_ELECT = "elect_amount";
        public static final String COLUMN_CHARGE = "elect_charge";
        public static final String COLUMN_MEASURE = "elect_measure";
        public static final String COLUMN_ELECT_CREATED_AT = "created_at";

        public static final String PRODUCT_TABLE_NAME = "product";
        public static final String COLUMN_NAME = "product_name";
        public static final String COLUMN_PATTERN = "product_pattern";
        public static final String COLUMN_DAY_HOUR = "product_day_hour";
        public static final String COLUMN_PRODUCT_CREATED_AT = "created_at";

        public static final String DELETE_AGREE_DATA = "DELETE FROM agree";
        public static final String DELETE_USER_DATA = "DELETE FROM user";
        public static final String DELETE_HOUSE_DATA = "DELETE FROM house";
        public static final String DELETE_ELECT_DATA = "DELETE FROM elect";
        public static final String DELETE_PRODUCT_DATA = "DELETE FROM product";
    }
}
