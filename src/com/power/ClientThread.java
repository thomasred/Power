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
	public static Handler clientHandler; // ClientThread��handler
	private boolean key = false;        // �]�m��false�� �ǿ鵲��
	private svThread SvThread;
	private static Handler To_main;

	public ClientThread( Handler main_handler ) {
		To_main = main_handler ;
		// constructor 
	} 	
	/**
	 * thread �D�n�B��(�s�W�@��clinet Thread����A�^����brun�̭���code)
	 */	
	public void run() {
		Message message;
		if( connect() ) { // �P�_�O�_���s�W���A��
			message = To_main.obtainMessage( 0, "���A���s�����\!" );
			To_main.sendMessage(message);
			initclientHandler();
			message = To_main.obtainMessage( 0, "�s�u����!" );
			To_main.sendMessage(message);
		}
		else {
			message = To_main.obtainMessage( 0, "�L�k�s�W���A��!" );
			To_main.sendMessage(message);
		}
	}
	
	/**
	 * �s��
	 */
	boolean connect() {
		key = true;
		// �ഫeditText��r�� ip port
		socketAddress = new InetSocketAddress("10.0.2.2", 8080);
		socket = new Socket();
		try {
			//�PServer�s�u�Atimeout�ɶ�30��
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
			//Log.d("Error", "�s������!!...");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//Log.d("Error", "�s������!!...");
			return false;
		}
	}
	
	/**
	 * clientHandler�ǰe��r�H�ε����B�z
	 */
	void initclientHandler() {
		// ��l�Ʈ����`�����C�A�ݭn�bHandler�Ыؤ��e
		Looper.prepare();
		// �Ы�client handler �B�z�{��
		clientHandler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message msg) {
				switch( msg.what ) {
				case 0:
					// MSG = 0 �ǿ�r�굹server 
					try {
						// (msg.obj)�ର�r��AgetBytes()�r�ꪺ�줸�ո��
				        // void write(byte[] b)
						// �q�줸�}�C���Nb.length���ת���Ƽg�J��y��
						output.write(((String) (msg.obj)).getBytes("GBK"));
						// void flush()�N��y��ƲM�šA�ñN��Ƽg�J�w�İϤ�
						output.flush();
					} catch(IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 1:
					// MSG = 1 client �ݵ{������ 
					// key�]��false�������A����ť
					key = false;
					try {
						input.close();
						output.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// ���������`�����C	
					clientHandler.getLooper().quit();			
				    break;
				default:
					Log.d("MSG:", "clientMSG DFAULT");
					break;
				}
				return true;
			}
		});
		// �Ұʤl�u�{�����`�����C
		Looper.loop();
	}
	
	/**
	 * ��ť���A���ǨӪ����
	 */
	public class svThread extends Thread {
		public void run() {
			// �w�İ�(buffer)�Ӥ@���s���h�Ӧ줸���
			byte[] buffer = new byte[16384];
			while (key) {
				try {
					// readSize �AŪ���F�h�֪��ת����
					int readSize = input.read(buffer);
					if (readSize > 0) {
						String str = new String(buffer, 0, readSize,"GBK");
						//Log.d("Message:", str);
						// ��ܶǨӪ��r��
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