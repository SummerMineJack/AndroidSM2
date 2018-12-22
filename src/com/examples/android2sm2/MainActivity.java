package com.examples.android2sm2;

import java.io.IOException;

import org.bouncycastle.util.encoders.Base64;
import org.summer.utils.ConvertUtils;

import com.examples.android2sm2.code.Pair;
import com.examples.android2sm2.code.SM2Utils;
import com.examples.android2sm2.code.Util;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private Button btnEncode;
	private Button btnDecode;
	private EditText inputData;
	private TextView result;
	private String privateKey;
	private String publicKey;
	private byte[] encodeData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Pair pair = SM2Utils.generateKeyPair();
		privateKey = ConvertUtils.bytes2HexString(Base64.encode(pair.getPrivatekey()));
		publicKey = ConvertUtils.bytes2HexString(Base64.encode(pair.getPublickey()));
		btnDecode = (Button) findViewById(R.id.decode);
		btnEncode = (Button) findViewById(R.id.encdoe);
		inputData = (EditText) findViewById(R.id.encodeData);
		result = (TextView) findViewById(R.id.result);
		btnDecode.setOnClickListener(this);
		btnEncode.setOnClickListener(this);
		result.setText("publicKey:" + publicKey + "\n" + "privateKey:" + privateKey);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.decode:
			try {
				byte[] finalResult = decodeData();
				result.setText(result.getText() + "\n" + new String(finalResult));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case R.id.encdoe:
			try {
				encodeData = encodeData();
				result.setText(result.getText() + "\n" + ConvertUtils.bytes2HexString(encodeData));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	// 解密字符串
	private byte[] decodeData() throws IOException {
		return SM2Utils.decrypt(Base64.decode(Util.hexToByte(privateKey)), encodeData);
	}

	// 加密字符串
	private byte[] encodeData() throws IOException {
		String data = inputData.getText().toString().trim();
		return SM2Utils.encrypt(Base64.decode(Util.hexToByte(publicKey)), data.getBytes());
	}

}
