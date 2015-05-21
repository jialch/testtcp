package com.example.tcptest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class ClientThread extends Thread {
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private Socket socket;
	private SocketAddress socketAddress;
	public static Handler childHandler;
	private boolean key = true;
	PrintInterface printClass;
	private RxThread rxThread;

	public ClientThread(PrintInterface printClass) {

		this.printClass = printClass;
	}

	/**
	 * 连接
	 */
	void connect() {
		key = true;
		socketAddress = new InetSocketAddress(MainActivity._ipText.getText()
				.toString(), Integer.parseInt(MainActivity._porText.getText()
				.toString()));
		socket = new Socket();

		try {

			socket.connect(socketAddress, 5000);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

			printClass.printf("连接成功");

			MainActivity.mainHandler.post(new Runnable() {
				public void run() {

					MainActivity._connectBtn.setText("Close");
				}

			});

			rxThread = new RxThread();
			rxThread.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			printClass.printf("无法连接到服务器");

			// Log.d("Error", "与服务端连接失败...");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block

		}

	}

	void initChildHandler() {

		// 在子线程中创建Handler必须初始化Looper
		Looper.prepare();

		childHandler = new Handler() {
			/**
			 * 子线程消息处理中心
			 */
			public void handleMessage(Message msg) {

				// 接收主线程及其他线程的消息并处理...
				switch (msg.what) {
				case 0:

					try {
						outputStream.write(((String) (msg.obj)).getBytes());
						outputStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 1:

					key = false;
					try {
						inputStream.close();
						outputStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					childHandler.getLooper().quit();// 结束消息队列

					break;

				default:
					break;
				}

			}
		};

		// 启动该线程的消息队列
		Looper.loop();

	}

	public void run() {
		connect();
		initChildHandler();
		printClass.printf("与服务器断开连接");

	}

	public class RxThread extends Thread {

		public void run() {

			// printClass.printf("启动接收线程");
			byte[] buffer = new byte[1024];

			while (key) {

				try {
					int readSize = inputStream.read(buffer);
					if (readSize > 0) {
						// String str = new String(buffer, 0, readSize);
						byte[] buffer_new = new byte[readSize];
						for (int i = 0; i < readSize; i++) {
							buffer_new[i] = buffer[i];
						}
						String str = bytesToHexString(buffer_new);
						// Log.d("Message:", str);
						printClass.printf(str);

					} else {

						inputStream.close();
						Log.d("error:", "close connect...");
						printClass.printf("与服务器断开连接");
						break;

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (socket.isConnected())
					socket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
}