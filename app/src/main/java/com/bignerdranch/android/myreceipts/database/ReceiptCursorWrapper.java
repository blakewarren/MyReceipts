package com.bignerdranch.android.myreceipts.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.myreceipts.Receipt;
import com.bignerdranch.android.myreceipts.database.ReceiptDbSchema.ReceiptTable;

import java.util.Date;
import java.util.UUID;

public class ReceiptCursorWrapper extends CursorWrapper {
    public ReceiptCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Receipt getReceipt(){
        String uuidString = getString(getColumnIndex(ReceiptTable.Cols.UUID));
        String title = getString(getColumnIndex(ReceiptTable.Cols.TITLE));
        String shopname = getString(getColumnIndex(ReceiptTable.Cols.SHOPNAME));
        String comments = getString(getColumnIndex(ReceiptTable.Cols.COMMENTS));
        long date = getLong(getColumnIndex(ReceiptTable.Cols.DATE));

        Receipt receipt = new Receipt(UUID.fromString(uuidString));
        receipt.setTitle(title);
        receipt.setShopName(shopname);
        receipt.setComment(comments);
        receipt.setDate(new Date(date));

        return receipt;
    }
}
