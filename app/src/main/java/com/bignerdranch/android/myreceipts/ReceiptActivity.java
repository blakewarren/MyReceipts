package com.bignerdranch.android.myreceipts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.UUID;

public class ReceiptActivity extends SingleFragmentActivity{

    private static final String EXTRA_RECEIPT_ID = "com.bignerdranch.android.myreceipts.receipt_id";
    private static final String EXTRA_RECEIPT_NEW = "com.bignerdranch.android.myreceipts.delete_enabled";
    private static final String EXTRA_RECEIPT_LATITUDE = "com.bignerdranch.android.myreceipts.current_latitude";
    private static final String EXTRA_RECEIPT_LONGITUDE = "com.bignerdranch.android.myreceipts.current_longitude";

    private static final int REQUEST_ERROR = 0;

    public static Intent newIntent(Context packageContext, UUID receiptId, boolean newReceipt, double receiptLatitude, double receiptLongitude){
        Intent intent = new Intent(packageContext, ReceiptActivity.class);
        intent.putExtra(EXTRA_RECEIPT_ID, receiptId);
        intent.putExtra(EXTRA_RECEIPT_NEW, newReceipt);
        intent.putExtra(EXTRA_RECEIPT_LATITUDE, receiptLatitude);
        intent.putExtra(EXTRA_RECEIPT_LONGITUDE, receiptLongitude);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        UUID receiptId = (UUID) getIntent().getSerializableExtra(EXTRA_RECEIPT_ID);
        boolean newReceipt = getIntent().getBooleanExtra(EXTRA_RECEIPT_NEW, false);
        double receiptLatitude = getIntent().getDoubleExtra(EXTRA_RECEIPT_LATITUDE, 0.00);
        double receiptLongitude = getIntent().getDoubleExtra(EXTRA_RECEIPT_LONGITUDE, 0.00);

        return ReceiptFragment.newInstance(receiptId, newReceipt, receiptLatitude, receiptLongitude);
    }

    @Override
    protected void onResume(){
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if(errorCode != ConnectionResult.SUCCESS){
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR,
                    new DialogInterface.OnCancelListener(){

                        @Override
                        public void onCancel(DialogInterface dialog){
                            //Leave if services are unavailable.
                            finish();
                        }
                    });

            errorDialog.show();
        }
    }
}
