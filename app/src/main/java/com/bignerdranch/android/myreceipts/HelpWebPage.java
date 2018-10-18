package com.bignerdranch.android.myreceipts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HelpWebPage extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpwebpage);

        mWebView = findViewById(R.id.receipt_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest request){
                return false;
            }
        });

        mWebView.loadUrl("https://en.wikipedia.org/wiki/Receipt");
    }

    @Override
    public void onBackPressed(){
        if (mWebView.canGoBack()){
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
