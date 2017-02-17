package com.converge.ndbciradionet;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

//    private String WebAddress = "http://www.ndbcnews.com.ph/";
//    private WebView webView;
    WebView webView;
    String Shareurl;

    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    ImageButton b_play;
    boolean prepared = false;
    boolean started = false;
    String steam = "http://194.232.200.156:8000";
    MediaPlayer mediaPlayer;

    //prevent activity to restart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progress bar for web -start
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar.setMax(100);

        webView = (WebView) findViewById(R.id.wv);
        webView.setWebViewClient( new webViewClient()); //changed

        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);

                if (progress==100){
                    frameLayout.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, progress);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl("http://www.ndbcnews.com.ph/");
        progressBar.setProgress(0);

        //progress bar for web -end
//        //web browser and progress bar
//        webView = (WebView) findViewById(R.id.wv);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
//        webView.setWebViewClient(new myWebclient());
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://www.ndbcnews.com.ph/");

        //stream player -start
        b_play = (ImageButton) findViewById(R.id.btnplay);
        b_play.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        new PlayerTask().execute(steam);

        b_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (started){
                    started = false;
                    mediaPlayer.pause();
                    b_play.setImageResource(R.drawable.play);

                }else {
                    started = true;
                    mediaPlayer.start();
                    b_play.setImageResource(R.drawable.pause);


                }

            }
        });

    }

    public class webViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            frameLayout.setVisibility(View.VISIBLE);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Shareurl = url;
//           mTrackUrlChange = url;
        }
    }

    //app bar toolbars and shareContent

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShare = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Shareurl);
        mShare.setShareIntent(shareIntent);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Shareurl);
        mShare.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);

    }

    //key event back = webview.canGoBack
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (webView.canGoBack()){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            b_play.setEnabled(true);
            b_play.setImageResource(R.drawable.play);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(started){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(prepared){
            mediaPlayer.release();
        }
    }
}
