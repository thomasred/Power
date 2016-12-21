package com.power;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.content.Context;

import android.graphics.Color;
import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import java.util.ArrayList;
import java.util.List;
/*import android.util.Log;*/

public class MainActivity extends Activity implements OnTabChangeListener, OnClickListener{
	public  static TextView _txt,txt_home_volt,txt_home_current,txt_home_power,txt_home_cost;
	public  static TextView txt_light_volt,txt_light_curr;
	public  static TextView txt_fan_volt,txt_fan_curr;
	public  static TextView txt_air_volt,txt_air_curr;
	public  static TextView txt_computer_volt,txt_computer_curr;
    private static Button btnhome, btnfan, btncomputer, btnlight, btnair, btnchart;
    private static ImageButton imageBtn_refresh,imageBtn_light,imageBtn_fan,imageBtn_air,imageBtn_compu;     
    public static Handler mainHandler;  // 改變手機畫面處理者
	private ClientThread thread;        // 監聽線程       
	private boolean connect_OK = false; // 用來得知與server是否connect
	private String[] Power_data ; // 儲存傳送過來的資料
	private int chart_dot = 12;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity_main_init();
		initMainHandler();
		imageBtn_refresh.performClick();
	}
	
	@Override
	public void onTabChanged(String arg0) {
		//分頁切換發生時，會執行此方法
		Toast.makeText(MainActivity.this, "切換到分頁的頁籤標記:"+arg0, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 初始化UI_Handler
	 */  
	void initMainHandler() {
		mainHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg){ 
				String tmp ; // 存放server傳送過來的字串
				switch (msg.what){
				case 0:  // 在case0當中，我們會顯示來自clientHandler傳來的字串
					tmp = (String)msg.obj; // 傳來的字串存入tmp
					Toast.makeText(MainActivity.this, tmp, Toast.LENGTH_SHORT).show();
					_txt.setText(tmp);
					if(tmp.equals("伺服器連結成功!")){
						connect_OK = true;
						imageBtn_refresh.performClick(); // 連線成功，索取資料
					}
					break;
				case 1:
					tmp = (String)msg.obj; // 傳來的字串存入tmp
					Toast.makeText(MainActivity.this, tmp, Toast.LENGTH_SHORT).show();
					// 將傳送過來的tmp字串做切割，放入Power_data字串陣列方便處理
					Power_data = tmp.split(" "); 
					txt_home_volt.setText("Voltage:" + Power_data[0]);
					txt_home_current.setText("Current:" + Power_data[1]);
					txt_home_power.setText("Power:" + Power_data[2]);
					txt_home_cost.setText("Cost:" + Power_data[3]);
					break;
				default:
					break;     
				}				
				return true;
			}
		});
	}//*/
	
	/**
	 * 按鍵監聽
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId() ){
		case R.id.btn_chart : // 折線圖
			jumpTochart();
		    break; 
		case R.id.btnhome : // 返回鍵
			jumpToMain();
			imageBtn_refresh.performClick();
		    break;   
		case R.id.btnlight : // 燈泡鍵
		    jumpTolight();
		    break;    
		case R.id.btnfan : //風扇鍵
		    jumpTofan();
		    break;    
		case R.id.btnair : //冷氣鍵
		    jumpToair();
		    break;    
		case R.id.btncomputer : //電腦鍵
		    jumpTocomputer();
		    break;    
		case R.id.imageBtn_light : // 燈泡鍵
		    jumpTolight();
		    break;    
		case R.id.imageBtn_fan : //風扇鍵
		    jumpTofan();
		    break;    
		case R.id.imageBtn_air : //冷氣鍵
		    jumpToair();
		    break;    
		case R.id.imageBtn_compu : //電腦鍵
		    jumpTocomputer();
		    break;		    
		case R.id.imageBtn_refresh://抓資料鍵
	    	// 取得網路連線的狀態
			NetworkInfo mNetworkInfo = ((ConnectivityManager) 
					 getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();

			// 如果未連線的話，mNetworkInfo會等於null
			if(mNetworkInfo == null) 
				Toast.makeText(MainActivity.this, "無網路連線!", Toast.LENGTH_LONG).show();
			else if( connect_OK )  // 與伺服器有連接，讓伺服器傳最新資料過來
	    		send("#start"); // 發送開始消息(給電腦端知道) 
	    	else {                 // 與伺服器進行連接
	    		Toast.makeText(MainActivity.this, "連線中", Toast.LENGTH_SHORT).show();
	            thread = new ClientThread(mainHandler);//建立連線處理執行緒
	            thread.start();	            
	    	}
	        break;
	    default:
	        break;    
		}
	}
    /**你好! 我是初始化設置**/
	void activity_main_init(){
		setContentView(R.layout.main);
		_txt = (TextView) findViewById(R.id.text_homepage);
		txt_home_volt = (TextView) findViewById(R.id.text_home_volt);
		txt_home_current = (TextView) findViewById(R.id.text_home_current);
		txt_home_power = (TextView) findViewById(R.id.text_home_power);
		txt_home_cost = (TextView) findViewById(R.id.text_all_cost);
                
		//利用findViewById找出Button和TabHost元件
	    TabHost host=(TabHost)findViewById(android.R.id.tabhost);
	    host.setup();//建立分頁視窗
	    //在TabHost中加入每一個分頁
	    TabSpec spec=host.newTabSpec("tab1");//產生一個分頁，並指定一個標記
	    spec.setContent(R.id.tab1);//設定這個分頁畫面的根結點
		spec.setIndicator("首頁");//設定分頁標籤文字
		host.addTab(spec);//在分頁視窗中將此分頁加入
		spec=host.newTabSpec("tab2");//產生一個分頁，並指定一個標記
		spec.setContent(R.id.tab2);//設定這個分頁畫面的根結點
		spec.setIndicator("電器");//設定分頁標籤文字
		//設定分頁標籤文字和圖示
		host.addTab(spec);//在分頁視窗中將此分頁加入
	    host.setCurrentTab(0);// 設定目前畫面顯示"首頁"分頁
	    host.setOnTabChangedListener(this); //設定分頁切換事件
	    
	    imageBtn_light=(ImageButton)findViewById(R.id.imageBtn_light);
	    imageBtn_light.setOnClickListener(this); //設定按鈕按下事件
		imageBtn_fan=(ImageButton)findViewById(R.id.imageBtn_fan);
	    imageBtn_fan.setOnClickListener(this);
	    imageBtn_air=(ImageButton)findViewById(R.id.imageBtn_air);
	    imageBtn_air.setOnClickListener(this);
	    imageBtn_compu=(ImageButton)findViewById(R.id.imageBtn_compu);
	    imageBtn_compu.setOnClickListener(this);
	    
	    imageBtn_refresh=(ImageButton)findViewById(R.id.imageBtn_refresh);
	    imageBtn_refresh.setOnClickListener(this);
	    btnchart = (Button) findViewById(R.id.btn_chart);
		btnchart.setOnClickListener(this);
	}
	
	/**你好! 我是切換畫面function**/
	public void jumpTolight(){
		setContentView(R.layout.light);
		btnhome = (Button) findViewById(R.id.btnhome);
		btnhome.setOnClickListener(this);
		imageBtn_fan=(ImageButton)findViewById(R.id.imageBtn_fan);
	    imageBtn_fan.setOnClickListener(this);
	    imageBtn_air=(ImageButton)findViewById(R.id.imageBtn_air);
	    imageBtn_air.setOnClickListener(this);
	    imageBtn_compu=(ImageButton)findViewById(R.id.imageBtn_compu);
	    imageBtn_compu.setOnClickListener(this);
	    
	    btnfan=(Button)findViewById(R.id.btnfan);
	    btnfan.setOnClickListener(this);
	    btnair=(Button)findViewById(R.id.btnair);
	    btnair.setOnClickListener(this);
	    btncomputer=(Button)findViewById(R.id.btncomputer);
	    btncomputer.setOnClickListener(this);
		txt_light_volt = (TextView) findViewById(R.id.text_light_volt);
		txt_light_volt.setText("Voltage:" + Power_data[4]);
		txt_light_curr = (TextView) findViewById(R.id.text_light_curr);
		txt_light_curr.setText("Current:" + Power_data[5]);
	}
	
	public void jumpTofan(){
		setContentView(R.layout.fan);
		btnhome = (Button) findViewById(R.id.btnhome);
		btnhome.setOnClickListener(this);
		imageBtn_light=(ImageButton)findViewById(R.id.imageBtn_light);
	    imageBtn_light.setOnClickListener(this); //設定按鈕按下事件
		imageBtn_air=(ImageButton)findViewById(R.id.imageBtn_air);
	    imageBtn_air.setOnClickListener(this);
	    imageBtn_compu=(ImageButton)findViewById(R.id.imageBtn_compu);
	    imageBtn_compu.setOnClickListener(this);
	    
	    btnlight=(Button)findViewById(R.id.btnlight);
	    btnlight.setOnClickListener(this);
	    btnair=(Button)findViewById(R.id.btnair);
	    btnair.setOnClickListener(this);
	    btncomputer=(Button)findViewById(R.id.btncomputer);
	    btncomputer.setOnClickListener(this);
		txt_fan_volt = (TextView) findViewById(R.id.text_fan_volt);
		txt_fan_volt.setText("Voltage:" + Power_data[6]);
		txt_fan_curr = (TextView) findViewById(R.id.text_fan_curr);
		txt_fan_curr.setText("Current:" + Power_data[7]);
		
	}
	
	public void jumpToair(){
		setContentView(R.layout.aircon);
		btnhome = (Button) findViewById(R.id.btnhome);
		btnhome.setOnClickListener(this);
		imageBtn_light=(ImageButton)findViewById(R.id.imageBtn_light);
	    imageBtn_light.setOnClickListener(this); //設定按鈕按下事件
		imageBtn_fan=(ImageButton)findViewById(R.id.imageBtn_fan);
	    imageBtn_fan.setOnClickListener(this);
	    imageBtn_compu=(ImageButton)findViewById(R.id.imageBtn_compu);
	    imageBtn_compu.setOnClickListener(this);
	    
	    btnfan=(Button)findViewById(R.id.btnfan);
	    btnfan.setOnClickListener(this);
	    btnlight=(Button)findViewById(R.id.btnlight);
	    btnlight.setOnClickListener(this);
	    btncomputer=(Button)findViewById(R.id.btncomputer);
	    btncomputer.setOnClickListener(this);
		txt_air_volt = (TextView) findViewById(R.id.text_air_volt);
		txt_air_volt.setText("Voltage:" + Power_data[8]);
		txt_air_curr = (TextView) findViewById(R.id.text_air_curr);
		txt_air_curr.setText("Current:" + Power_data[9]);
		
	}
	
	public void jumpTocomputer(){
		setContentView(R.layout.computer);
		btnhome = (Button) findViewById(R.id.btnhome);
		btnhome.setOnClickListener(this);
		imageBtn_light=(ImageButton)findViewById(R.id.imageBtn_light);
	    imageBtn_light.setOnClickListener(this); //設定按鈕按下事件
		imageBtn_fan=(ImageButton)findViewById(R.id.imageBtn_fan);
	    imageBtn_fan.setOnClickListener(this);
	    imageBtn_air=(ImageButton)findViewById(R.id.imageBtn_air);
	    imageBtn_air.setOnClickListener(this);
	    
	    btnfan=(Button)findViewById(R.id.btnfan);
	    btnfan.setOnClickListener(this);
	    btnair=(Button)findViewById(R.id.btnair);
	    btnair.setOnClickListener(this);
	    btncomputer=(Button)findViewById(R.id.btncomputer);
	    btncomputer.setOnClickListener(this);
		txt_computer_volt = (TextView) findViewById(R.id.text_computer_volt);
		txt_computer_volt.setText("Voltage:" + Power_data[10]);
		txt_computer_curr = (TextView) findViewById(R.id.text_computer_curr);
		txt_computer_curr.setText("Current:" + Power_data[11]);
	}
	
	/**你好! 我是切換畫面function**/
	public void jumpToMain(){
		activity_main_init();
	}
	
	/**你好! 我是折線圖**/
	public void jumpTochart(){
		setContentView(R.layout.chart);
		RelativeLayout chart_view = (RelativeLayout)findViewById(R.id.view);
		btnhome = (Button) findViewById(R.id.btnhome);
		btnhome.setOnClickListener(this);
		
		String[] titles = new String[] { "折線1", "折線2" }; // 定義折線的名稱
        List<double[]> x = new ArrayList<double[]>(); // 點的x坐標
        List<double[]> y = new ArrayList<double[]>(); // 點的y坐標
        // 數值X,Y坐標值輸入
        x.add(new double[] { 1, 3, 5, 7, 9, 11 });
        x.add(new double[] { 0, 2, 4, 6, 8, 10 });
        y.add(new double[] { 3, 14, 8, 22, 16, 18 });
        y.add(new double[] { 20, 18, 15, 12, 10, 8 });
        XYMultipleSeriesDataset dataset = buildDatset(titles, x, y); // 儲存座標值

        int[] colors = new int[] { Color.BLUE, Color.GREEN };// 折線的顏色
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND }; // 折線點的形狀
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "折線圖展示", "X軸名稱", "Y軸名稱", 0, 12, 0, 25, Color.BLACK);// 定義折線圖
        View chart = ChartFactory.getLineChartView(this, dataset, renderer);
        chart_view.addView(chart);//*/
		
	}
	
	
	
    
    // 定義折線圖名稱
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
            String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setChartTitle(title); // 折線圖名稱
        renderer.setChartTitleTextSize(24); // 折線圖名稱字形大小
        renderer.setXTitle(xTitle); // X軸名稱
        renderer.setYTitle(yTitle); // Y軸名稱
        renderer.setXAxisMin(xMin); // X軸顯示最小值
        renderer.setXAxisMax(xMax); // X軸顯示最大值
        renderer.setXLabelsColor(Color.BLACK); // X軸線顏色
        renderer.setYAxisMin(yMin); // Y軸顯示最小值
        renderer.setYAxisMax(yMax); // Y軸顯示最大值
        renderer.setAxesColor(axesColor); // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK); // Y軸線顏色
        renderer.setLabelsColor(Color.BLACK); // 設定標籤顏色
        renderer.setMarginsColor(Color.WHITE); // 設定背景顏色
        renderer.setShowGrid(true); // 設定格線
    }
	
    // 定義折線圖的格式
    private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //將座標變成線加入圖中顯示
        }
        return renderer;
    }
	
	// 資料處理
    private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
            List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = titles.length; // 折線數量
        Integer t = new Integer(length);
        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < length; i++) {
            // XYseries對象,用於提供繪製的點集合的資料
            XYSeries series = new XYSeries(titles[i]); // 依據每條線的名稱新增
            double[] xV = xValues.get(i); // 獲取第i條線的資料
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // 有幾個點

            for (int k = 0; k < seriesLength; k++) // 每條線裡有幾個點
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }//*/
        return dataset;
    }
    
    /**
	 * 傳送文字訊息
	 */
	private void send(String str){
		//public final Message obtainMessage (int what, Object obj)
		Message message = ClientThread.clientHandler.obtainMessage( 0, str );
		ClientThread.clientHandler.sendMessage( message );
	}

	/**
	 * 結束設置
	 */
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if( connect_OK ) { // 如果沒有連線，是不能發送消息的
		    send("#end");  // 發送結束連線的消息(給電腦端知道)      
		    
		    //public final Message obtainMessage (int what)
		    Message message = ClientThread.clientHandler.obtainMessage(1);
		    ClientThread.clientHandler.sendMessage( message );
		}
		//Log.d("Close", "close");
		MainActivity.this.finish();
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}