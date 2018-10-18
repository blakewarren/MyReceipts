package com.bignerdranch.android.myreceipts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReceiptFragment extends Fragment {

    private static final String ARG_RECEIPT_ID = "receipt_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String ARG_NEW_RECEIPT = "new_receipt";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;

    private Receipt mReceipt;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mShopName;
    private EditText mComments;
    private Button mDateButton;
    private TextView mLocation;
    private Button mLocationButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mDeleteButton;
    private boolean mDeleteEnabled;
    private GoogleApiClient mClient;

    public static ReceiptFragment newInstance(UUID receiptId, boolean newReceipt){
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECEIPT_ID, receiptId);
        args.putBoolean(ARG_NEW_RECEIPT, newReceipt);

        ReceiptFragment fragment = new ReceiptFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final UUID receiptId = (UUID) getArguments().getSerializable(ARG_RECEIPT_ID);
        mDeleteEnabled = getArguments().getBoolean(ARG_NEW_RECEIPT);
        mReceipt = ReceiptLab.get(getActivity()).getReceipt(receiptId);
        mPhotoFile = ReceiptLab.get(getActivity()).getPhotoFile(mReceipt);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);

                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED){
                            return;
                        }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.i("LOCATION", "Got a fix: " + location);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }

    @Override
    public void onStart(){
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onPause(){
        super.onPause();

        ReceiptLab.get(getActivity())
                .updateReceipt(mReceipt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_receipt, container, false);

        mTitleField = v.findViewById(R.id.receipt_title);
        mTitleField.setText(mReceipt.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReceipt.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mShopName = v.findViewById(R.id.receipt_shop_name);
        mShopName.setText(mReceipt.getShopName());
        mShopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReceipt.setShopName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mComments = v.findViewById(R.id.receipt_comments);
        mComments.setText(mReceipt.getComment());
        mComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReceipt.setComment(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = v.findViewById(R.id.receipt_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mReceipt.getDate());
                dialog.setTargetFragment(ReceiptFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mLocation = v.findViewById(R.id.receipt_location);

        mLocationButton = v.findViewById(R.id.receipt_show_location);


        mReportButton = v.findViewById(R.id.receipt_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getReceiptReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.receipt_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();

        mPhotoButton = v.findViewById(R.id.receipt_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.myreceipts.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = v.findViewById(R.id.receipt_photo);
        updatePhotoView();

        mDeleteButton = v.findViewById(R.id.receipt_delete);
        mDeleteButton.setEnabled(mDeleteEnabled);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Receipt receipt = mReceipt;
                ReceiptLab.get(getActivity()).deleteReceipt(receipt);
                getActivity().finish();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DATE){
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReceipt.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.myreceipts.fileprovider", mPhotoFile);

            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(mReceipt.getDate().toString());
    }

    private String getReceiptReport(){
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mReceipt.getDate()).toString();

        String report = getString(R.string.receipt_report, mReceipt.getTitle(), dateString,
                mReceipt.getShopName(), mReceipt.getComment());

        return report;
    }

    private void updatePhotoView(){
        if (mPhotoView == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
