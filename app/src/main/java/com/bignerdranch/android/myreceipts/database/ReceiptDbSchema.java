package com.bignerdranch.android.myreceipts.database;

public class ReceiptDbSchema {
    public static final class ReceiptTable{
        public static final String NAME = "receipts";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String SHOPNAME = "shopname";
            public static final String COMMENTS = "comments";
            public static final String DATE = "date";
        }
    }
}
