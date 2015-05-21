package com.example.tcptest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		PrintInterface {

	private Button _sendBtn;
	public static Button _connectBtn;
	public static TextView _rxTextView;
	private EditText _txText;
	public static EditText _ipText;
	public static EditText _porText;
	private ScrollView _textScrollView;
	public static Handler mainHandler;
	private String rxTextString = "";
	private ClientThread rxListenerThread;
	private Message message;
	private EditText et_zhongliang, et_danjia, et_jine;

	String str_new = "0000000000000000000000000000";
	String test = "FF146523000362050003291300C8";

	private int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initMainHandler();

	}

	void initMainHandler() {

		mainHandler = new Handler() {

			@Override
			/**
			 * ���߳���Ϣ��������
			 */
			public void handleMessage(Message msg) {

				// �������̵߳���Ϣ

			}

		};

	}

	/**
	 * ��ʼ��
	 */
	void init() {

		_sendBtn = (Button) findViewById(R.id.sendBtn);
		_connectBtn = (Button) findViewById(R.id.connectBtn);
		_ipText = (EditText) findViewById(R.id.ip_editText);
		_porText = (EditText) findViewById(R.id.port_editText);
		_rxTextView = (TextView) findViewById(R.id.rx_textView);
		_txText = (EditText) findViewById(R.id.tx_EditText);
		_textScrollView = (ScrollView) findViewById(R.id.scrollView);
		et_jine = (EditText) findViewById(R.id.et_jine);
		et_zhongliang = (EditText) findViewById(R.id.et_zhongliang);
		et_danjia = (EditText) findViewById(R.id.et_danjia);

		_sendBtn.setOnClickListener(this);
		_connectBtn.setOnClickListener(this);

	}

	/**
	 * ������Ϣ
	 */
	void sendMessage(String str) {

		// ֪ͨ�ͻ����߳� ������Ϣ
		message = ClientThread.childHandler.obtainMessage(0, str);
		ClientThread.childHandler.sendMessage(message);

		printf(str + ">>");
		_txText.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.connectBtn:

			if (_connectBtn.getText().equals("Connect")) {
				printf("��������...");

				rxListenerThread = new ClientThread(this);// �����ͻ����߳�
				rxListenerThread.start();

			} else {

				// �Ͽ�����
				onDestroy();

			}
			break;
		case R.id.sendBtn:

			sendMessage(_txText.getText().toString());// ������Ϣ
			// _rxTextView.setText(radix_point((byte) 0xD4) + "");
			// System.out.println(count_result("002365", radix_point((byte)
			// 0x13))
			// + "");
			printf(test);
			break;

		default:
			break;
		}

	}

	/**
	 * ��16�����ַ���ת�����ֽ�����
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToBytes(String hex) {
		while (hex.length() < 2)
			return null;
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (charToByte(achar[pos]) << 4 | charToByte(achar[pos + 1]));
		}
		return result;
	}

	public static byte charToByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	@Override
	/**
	 * �ı�����ʾ
	 */
	public void printf(final String str) {

		runOnUiThread(new Runnable() {
			public void run() {

				// TODO Auto-generated method stub
				_rxTextView.setText(str);
				if (str.length() == 28) {

					/*
					 * 1.�������������һ��Ϊ�㣬������Ryan���������������������ֱ������������ۼ�����
					 * 2.�����������ʮ�����ޱ仯��Ĭ����������ֵ ����byte����
					 */
					if (str_new.equals(str)) {
						
						flag++;
						System.out.println("===" + flag + "\n");
					} else {
						str_new = str;
						flag = 0;
					}
					// ����ĳ������---�ص�����---С����λ�ã���Ҫ����radix_point�������㣻�ȼ򵥲���ڶ���λ��
					if (flag > 50) {

						String str1 = "" + str.subSequence(8, 10)
								+ str.subSequence(6, 8) + "."
								+ str.subSequence(4, 6);

						String str2 = "" + str.subSequence(16, 18)
								+ str.subSequence(14, 16) + "."
								+ str.subSequence(12, 14);
						String str3 = "" + str.subSequence(24, 26)
								+ str.subSequence(22, 24) + "."
								+ str.subSequence(20, 22);

						show_value(str1, str2, str3);// �ص�����

					}
				}

				_textScrollView.scrollTo(0, _textScrollView.getHeight() + 14);

			}
		});

	}

	/*
	 * �ص�����---ʵ�����������ۡ������ʾ
	 */

	public void show_value(String weight, String price, String money) {
		et_zhongliang.setText(weight);
		et_danjia.setText(price);
		et_jine.setText(money);
	}

	/*
	 * ��С����λ��
	 */
	@SuppressWarnings("unused")
	public byte radix_point(byte flag) {
		byte result;
		result = (byte) (flag & 0x07);

		return result;

	}

	/*
	 * ���ݲ���С����
	 */
	public StringBuffer count_result(String value, byte point) {
		StringBuffer a = new StringBuffer(value);

		if (point == (byte) 0x01) {
			;
		} else if (point == (byte) 0x02) {
			a.insert(3, ".");
		} else if (point == (byte) 0x03) {
			a.insert(4, ".");
		} else if (point == (byte) 0x04) {
			a.insert(5, ".");
		}
		return a;

	}

	/**
	 * BCD��תΪ10���ƴ�(����������)
	 * 
	 * @param: BCD��
	 * @return: 10���ƴ�
	 */
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	// �ֽ�ת��Ϊ�ַ���
	public static String bytesToHexString(byte[] src, int length) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || length <= 0) {
			return null;
		}
		for (int i = 0; i < length; i++) {
			int v = src[i] & 0xFF;
			String hv = " 0x" + Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		ClientThread.childHandler.sendMessage(ClientThread.childHandler
				.obtainMessage(1));

		Log.d("Close", "close");
		_connectBtn.setText("Connect");
		super.onDestroy();
	}

}
