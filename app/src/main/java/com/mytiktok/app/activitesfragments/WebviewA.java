package com.mytiktok.app.activitesfragments;

import com.mytiktok.app.mainmenu.MainMenuActivity;
import com.mytiktok.app.simpleclasses.AppCompatLocaleActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mytiktok.app.Constants;
import com.mytiktok.app.R;
import com.mytiktok.app.simpleclasses.Functions;
import com.mytiktok.app.simpleclasses.Variables;

public class WebviewA extends AppCompatLocaleActivity implements View.OnClickListener{



    ProgressBar progressBar;
    WebView webView;
    String url = "www.google.com";
    String title,from;
    TextView titleTxt;
    RelativeLayout acceptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_webview);

        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        from = getIntent().getStringExtra("from");
        if (title.equals(getString(R.string.promote_video))) {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
        }


        Functions.printLog(Constants.tag,url);


        findViewById(R.id.goBack).setOnClickListener(this);

        titleTxt = findViewById(R.id.title_txt);
        titleTxt.setText(title);

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress >= 80) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (url.equalsIgnoreCase("closePopup")) {
                    WebviewA.super.onBackPressed();
                }
                return false;
            }
        });


        if(from!=null && from.equals("splash")) {
            acceptBtn = findViewById(R.id.acceptBtn);
            acceptBtn.setOnClickListener(this);
            acceptBtn.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                WebviewA.super.onBackPressed();
                break;

            case R.id.acceptBtn:
                findViewById(R.id.acceptTxt).setVisibility(View.GONE);
                findViewById(R.id.acceptProgress).setVisibility(View.VISIBLE);
                Functions.getSettingsPreference(this).edit().putBoolean(Variables.IsPrivacyPolicyAccept,true).commit();
                Intent intent = new Intent(WebviewA.this, MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
                break;
        }
    }

}
