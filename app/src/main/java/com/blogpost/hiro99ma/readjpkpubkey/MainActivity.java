package com.blogpost.hiro99ma.readjpkpubkey;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.blogpost.hiro99ma.nfc.NfcFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcFactory.nfcPause(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean ret = NfcFactory.nfcResume(MainActivity.this, mCallBack);
        if (!ret) {
            Toast.makeText(MainActivity.this, R.string.cannot_nfc, Toast.LENGTH_SHORT).show();
        }
    }

    private NfcAdapter.ReaderCallback mCallBack = new NfcAdapter.ReaderCallback() {
        @Override
        public void onTagDiscovered(Tag tag) {
            runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(MainActivity.this, android.R.string.ok, Toast.LENGTH_SHORT).show();
                  }
            });
        }
    };
}
