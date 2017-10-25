package com.blogpost.hiro99ma.nfc;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.util.Log;


/**
 * NFC関係の処理を抜き出した
 *
 * @author hiroshi
 *
 */
public class NfcFactory {

    private final static String TAG = "NfcFactory";

    private final static int READ_TAG_TYPE = NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

    /**
     * onResume()時の動作
     *
     * @param activity		現在のActivity。だいたいthisを渡すことになる。
     * @return				true:NFCタグ検出の準備ができた<br />
     * 						false:できなかった
     */
    public static boolean nfcResume(Activity activity, NfcAdapter.ReaderCallback callback) {
        //NFC
        NfcManager mng = (NfcManager)activity.getSystemService(Context.NFC_SERVICE);
        if (mng == null) {
            Log.e(TAG, "no NfcManager");
            return false;
        }
        NfcAdapter adapter = mng.getDefaultAdapter();
        if (adapter == null) {
            Log.e(TAG, "no NfcService");
            return false;
        }

        adapter.enableReaderMode(activity, callback, READ_TAG_TYPE, null);

        return true;
    }

    /**
     * onPause()時の動作
     *
     * @param activity		現在のActivity。だいたいthisを渡すことになる。
     */
    public static void nfcPause(Activity activity) {
        NfcManager mng = (NfcManager)activity.getSystemService(Context.NFC_SERVICE);
        if (mng == null) {
            Log.e(TAG, "no NfcManager");
            return;
        }
        NfcAdapter adapter = mng.getDefaultAdapter();
        if (adapter == null) {
            Log.e(TAG, "no NfcService");
            return;
        }

        if (activity.isFinishing()) {
            adapter.disableReaderMode(activity);
        }
    }

    public static byte[] isoDepCase2Short(byte ins, byte p1, byte p2, byte le) {
        return new byte[] { 0x00, ins, p1, p2, le };
    }

    public static byte[] isoDepCase3Short(byte ins, byte p1, byte p2, byte[] data) {
        byte[] result = new byte[5 + data.length];
        result[0] = 0x00;		//CLA
        result[1] = ins;
        result[2] = p1;
        result[3] = p2;
        result[4] = (byte)data.length;
        System.arraycopy(data, 0, result, 5, data.length);
        return result;
    }

    public static boolean isoDepCheckResult(byte[] result) {
        return (result.length >= 2) && (result[result.length - 2] == (byte) 0x90) && (result[result.length - 1] == 0x00);
    }
}
