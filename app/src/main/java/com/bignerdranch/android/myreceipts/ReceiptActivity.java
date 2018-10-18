package com.bignerdranch.android.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


import java.util.UUID;

public class ReceiptActivity extends SingleFragmentActivity{

    private static final String EXTRA_RECEIPT_ID = "com.bignerdranch.android.myreceipts.receipt_id";

    public static Intent newIntent(Context packageContext, UUID receiptId){
        Intent intent = new Intent(packageContext, ReceiptActivity.class);
        intent.putExtra(EXTRA_RECEIPT_ID, receiptId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        UUID receiptId = (UUID) getIntent().getSerializableExtra(EXTRA_RECEIPT_ID);
        return ReceiptFragment.newInstance(receiptId);
    }
}
