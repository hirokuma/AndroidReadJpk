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
}
