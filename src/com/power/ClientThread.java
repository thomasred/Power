package com.power;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
//import com.android.socket.ClientThread.svThread;
/*import android.util.Log;*/

public class ClientThread extends Thread {
	private OutputStream output = null;
	private InputStream input = null;
	private Socket socket;
	private SocketAddress socketAddress;
	public static Handler clientHandler; // ClientThread的handler
	private boolean key = false;        // 設置為false時 傳輸結束
	private svThread SvThread;
	private static Handler To_main;

	public ClientThread( Handler main_handler ) {
		To_main = main_handler ;
		// constructor 
	} 	
	/**
	 * thread 主要運行(新增一個clinet Thread之後，回執行在run裡面的code)
	 */	
	public void run() {
		Message message;
		if( connect() ) { // 判斷是否有連上伺服器
			message = To_main.obtainMessage( 0, "伺服器連結成功!" );
			To_main.sendMessage(message);
			initclientHandler();
			message = To_main.obtainMessage( 0, "連線結束!" );
			To_main.sendMessage(message);
		}
		else {
			message = To_main.obtainMessage( 0, "無法連上伺服器!" );
			To_main.sendMessage(message);
		}
	}
	
	/**
	 * 連接
	 */
	boolean connect() {
		key = true;
		// 轉換editText文字為 ip port
		socketAddress = new InetSocketAddress("10.0.2.2", 8080);
		socket = new Socket();
		try {
			//與Server連線，timeout時間30秒
			socket.connect(socketAddress, 30000);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			
			SvThread = new svThread();
			SvThread.start();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
			//Log.d("Error", "連接失敗!!...");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//Log.d("Error", "連接失敗!!...");
			return false;
		}
	}
	
	/**
	 * clientHandler傳送文字以及結束處理
	 */
	void initclientHandler() {
		// 初始化消息循環隊列，需要在Handler創建之前
		Looper.prepare();
		// 創建client handler 處理程序
		clientHandler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message msg) {
				switch( msg.what ) {
				case 0:
					// MSG = 0 傳輸字串給server 
					try {
						// (msg.obj)轉為字串，getBytes()字串的位元組資料
				        // void write(byte[] b)
						// 從位元陣列中將b.length長度的資料寫入串流中
						output.write(((String) (msg.obj)).getBytes("GBK"));
						// void flush()將串流資料清空，並將資料寫入緩衝區中
						output.flush();
					} catch(IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 1:
					// MSG = 1 client 端程式結束 
					// key設為false結束伺服器監聽
					key = false;
					try {
						input.close();
						output.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// 結束消息循環隊列	
					clientHandler.getLooper().quit();			
				    break;
				default:
					Log.d("MSG:", "clientMSG DFAULT");
					break;
				}
				return true;
			}
		});
		// 啟動子線程消息循環隊列
		Looper.loop();
	}
	
	/**
	 * 監聽伺服器傳來的資料
	 */
	public class svThread extends Thread {
		public void run() {
			// 緩衝區(buffer)來一次存取多個位元資料
			byte[] buffer = new byte[16384];
			while (key) {
				try {
					// readSize 你讀取了多少長度的資料
					int readSize = input.read(buffer);
					if (readSize > 0) {
						String str = new String(buffer, 0, readSize,"GBK");
						//Log.d("Message:", str);
						// 顯示傳來的字串
						Message message = To_main.obtainMessage(1, str);
						To_main.sendMessage(message);
					} else {
						input.close();
						//Log.d("error:", "close connect...");
						Message message = To_main.obtainMessage( 0, "error!" );
						To_main.sendMessage(message);
						if ( socket.isConnected() )
							socket.close();
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			/*try {
				;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // try.*/
		} // while().
	} // class svThread.
} // class ClientThread.