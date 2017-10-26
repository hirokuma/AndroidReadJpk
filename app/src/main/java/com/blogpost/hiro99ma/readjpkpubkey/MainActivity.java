package com.blogpost.hiro99ma.readjpkpubkey;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.blogpost.hiro99ma.nfc.NfcFactory;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView)findViewById(R.id.text_work);
        assert tv != null;
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
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
            if (NfcB.get(tag) != null) {
                //
                IsoDep nfc = IsoDep.get(tag);
                try {
                    byte[] command;
                    byte[] result;
                    nfc.connect();

                    command = NfcFactory.isoDepCase3Short((byte)0xa4, (byte)0x04, (byte)0x0c, new byte[] { (byte)0xD3, (byte)0x92, (byte)0xF0, 0x00, 0x26, 0x01, 0x00, 0x00, 0x00, 0x01 });
                    result = nfc.transceive(command);
                    if (!NfcFactory.isoDepCheckResult(result)) {
                        Log.d(TAG, "select file 1: AP");
                        throw new RuntimeException("fail1");
                    }

                    command = NfcFactory.isoDepCase3Short((byte)0xa4, (byte)0x02, (byte)0x0c, new byte[] { 0x00, 0x0a });
                    result = nfc.transceive(command);
                    if (!NfcFactory.isoDepCheckResult(result)) {
                        Log.d(TAG, "select file 2: PIN");
                        throw new RuntimeException("fail2");
                    }

                    int point = 0;
                    int less = -1;
                    byte[] crt = null;
                    byte p1 = 0x00;
                    byte p2 = 0x00;
                    byte next = (byte)0xff;
                    while (true) {
                        command = NfcFactory.isoDepCase2Short((byte)0xb0, p1, p2, next);
                        result = nfc.transceive(command);
                        if (!NfcFactory.isoDepCheckResult(result)) {
                            Log.d(TAG, "read binary");
                            throw new RuntimeException("fail3");
                        }
                        if (point == 0) {
                            less = (result[2] << 8) | result[3] + 4;
                            crt = new byte[less];
                        }
                        System.arraycopy(result, 0, crt, point, result.length - 2);
                        point += result.length - 2;
                        less -= result.length - 2;
                        if (less == 0) {
                            break;
                        } else if (less < result.length) {
                            next = (byte)less;
                        }
                        p1 = (byte)((point & 0xff00) >> 8);
                        p2 = (byte)(point & 0x00ff);
                    }

                    // これと混ぜられないか
                    // https://developers.google.com/identity/smartlock-passwords/android/overview
                    ASN1InputStream asn1Stream = new ASN1InputStream(new ByteArrayInputStream(crt));
                    final ASN1Primitive prim = asn1Stream.readObject();

                    nfc.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = (TextView)MainActivity.this.findViewById(R.id.text_work);
                            assert tv != null;
                            tv.setText(ASN1Dump.dumpAsString(prim));
                            Toast.makeText(MainActivity.this, android.R.string.ok, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
