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
    public static Handler mainHandler;  // ���ܤ���e���B�z��
	private ClientThread thread;        // ��ť�u�{       
	private boolean connect_OK = false; // �Ψӱo���Pserver�O�_connect
	private String[] Power_data ; // �x�s�ǰe�L�Ӫ����
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
		//���������o�ͮɡA�|���榹��k
		Toast.makeText(MainActivity.this, "��������������ҼаO:"+arg0, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * ��l��UI_Handler
	 */  
	void initMainHandler() {
		mainHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg){ 
				String tmp ; // �s��server�ǰe�L�Ӫ��r��
				switch (msg.what){
				case 0:  // �bcase0���A�ڭ̷|��ܨӦ�clientHandler�ǨӪ��r��
					tmp = (String)msg.obj; // �ǨӪ��r��s�Jtmp
					Toast.makeText(MainActivity.this, tmp, Toast.LENGTH_SHORT).show();
					_txt.setText(tmp);
					if(tmp.equals("���A���s�����\!")){
						connect_OK = true;
						imageBtn_refresh.performClick(); // �s�u���\�A�������
					}
					break;
				case 1:
					tmp = (String)msg.obj; // �ǨӪ��r��s�Jtmp
					Toast.makeText(MainActivity.this, tmp, Toast.LENGTH_SHORT).show();
					// �N�ǰe�L�Ӫ�tmp�r�갵���ΡA��JPower_data�r��}�C��K�B�z
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
	 * �����ť
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId() ){
		case R.id.btn_chart : // ��u��
			jumpTochart();
		    break; 
		case R.id.btnhome : // ��^��
			jumpToMain();
			imageBtn_refresh.performClick();
		    break;   
		case R.id.btnlight : // �O�w��
		    jumpTolight();
		    break;    
		case R.id.btnfan : //������
		    jumpTofan();
		    break;    
		case R.id.btnair : //�N����
		    jumpToair();
		    break;    
		case R.id.btncomputer : //�q����
		    jumpTocomputer();
		    break;    
		case R.id.imageBtn_light : // �O�w��
		    jumpTolight();
		    break;    
		case R.id.imageBtn_fan : //������
		    jumpTofan();
		    break;    
		case R.id.imageBtn_air : //�N����
		    jumpToair();
		    break;    
		case R.id.imageBtn_compu : //�q����
		    jumpTocomputer();
		    break;		    
		case R.id.imageBtn_refresh://������
	    	// ���o�����s�u�����A
			NetworkInfo mNetworkInfo = ((ConnectivityManager) 
					 getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();

			// �p�G���s�u���ܡAmNetworkInfo�|����null
			if(mNetworkInfo == null) 
				Toast.makeText(MainActivity.this, "�L�����s�u!", Toast.LENGTH_LONG).show();
			else if( connect_OK )  // �P���A�����s���A�����A���ǳ̷s��ƹL��
	    		send("#start"); // �o�e�}�l����(���q���ݪ��D) 
	    	else {                 // �P���A���i��s��
	    		Toast.makeText(MainActivity.this, "�s�u��", Toast.LENGTH_SHORT).show();
	            thread = new ClientThread(mainHandler);//�إ߳s�u�B�z�����
	            thread.start();	            
	    	}
	        break;
	    default:
	        break;    
		}
	}
    /**�A�n! �ڬO��l�Ƴ]�m**/
	void activity_main_init(){
		setContentView(R.layout.main);
		_txt = (TextView) findViewById(R.id.text_homepage);
		txt_home_volt = (TextView) findViewById(R.id.text_home_volt);
		txt_home_current = (TextView) findViewById(R.id.text_home_current);
		txt_home_power = (TextView) findViewById(R.id.text_home_power);
		txt_home_cost = (TextView) findViewById(R.id.text_all_cost);
                
		//�Q��findViewById��XButton�MTabHost����
	    TabHost host=(TabHost)findViewById(android.R.id.tabhost);
	    host.setup();//�إߤ�������
	    //�bTabHost���[�J�C�@�Ӥ���
	    TabSpec spec=host.newTabSpec("tab1");//���ͤ@�Ӥ����A�ë��w�@�ӼаO
	    spec.setContent(R.id.tab1);//�]�w�o�Ӥ����e�����ڵ��I
		spec.setIndicator("����");//�]�w�������Ҥ�r
		host.addTab(spec);//�b�����������N�������[�J
		spec=host.newTabSpec("tab2");//���ͤ@�Ӥ����A�ë��w�@�ӼаO
		spec.setContent(R.id.tab2);//�]�w�o�Ӥ����e�����ڵ��I
		spec.setIndicator("�q��");//�]�w�������Ҥ�r
		//�]�w�������Ҥ�r�M�ϥ�
		host.addTab(spec);//�b�����������N�������[�J
	    host.setCurrentTab(0);// �]�w�ثe�e�����"����"����
	    host.setOnTabChangedListener(this); //�]�w���������ƥ�
	    
	    imageBtn_light=(ImageButton)findViewById(R.id.imageBtn_light);
	    imageBtn_light.setOnClickListener(this); //�]�w���s���U�ƥ�
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
	
	/**�A�n! �ڬO�����e��function**/
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
	    imageBtn_light.setOnClickListener(this); //�]�w���s���U�ƥ�
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
	    imageBtn_light.setOnClickListener(this); //�]�w���s���U�ƥ�
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
	    imageBtn_light.setOnClickListener(this); //�]�w���s���U�ƥ�
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
	
	/**�A�n! �ڬO�����e��function**/
	public void jumpToMain(){
		activity_main_init();
	}
	
	/**�A�n! �ڬO��u��**/
	public void jumpTochart(){
		setContentView(R.layout.chart);
		RelativeLayout chart_view = (RelativeLayout)findViewById(R.id.view);
		btnhome = (Button) findViewById(R.id.btnhome);
		btnhome.setOnClickListener(this);
		
		String[] titles = new String[] { "��u1", "��u2" }; // �w�q��u���W��
        List<double[]> x = new ArrayList<double[]>(); // �I��x����
        List<double[]> y = new ArrayList<double[]>(); // �I��y����
        // �ƭ�X,Y���Эȿ�J
        x.add(new double[] { 1, 3, 5, 7, 9, 11 });
        x.add(new double[] { 0, 2, 4, 6, 8, 10 });
        y.add(new double[] { 3, 14, 8, 22, 16, 18 });
        y.add(new double[] { 20, 18, 15, 12, 10, 8 });
        XYMultipleSeriesDataset dataset = buildDatset(titles, x, y); // �x�s�y�Э�

        int[] colors = new int[] { Color.BLUE, Color.GREEN };// ��u���C��
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND }; // ��u�I���Ϊ�
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "��u�Ϯi��", "X�b�W��", "Y�b�W��", 0, 12, 0, 25, Color.BLACK);// �w�q��u��
        View chart = ChartFactory.getLineChartView(this, dataset, renderer);
        chart_view.addView(chart);//*/
		
	}
	
	
	
    
    // �w�q��u�ϦW��
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
            String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setChartTitle(title); // ��u�ϦW��
        renderer.setChartTitleTextSize(24); // ��u�ϦW�٦r�Τj�p
        renderer.setXTitle(xTitle); // X�b�W��
        renderer.setYTitle(yTitle); // Y�b�W��
        renderer.setXAxisMin(xMin); // X�b��̤ܳp��
        renderer.setXAxisMax(xMax); // X�b��̤ܳj��
        renderer.setXLabelsColor(Color.BLACK); // X�b�u�C��
        renderer.setYAxisMin(yMin); // Y�b��̤ܳp��
        renderer.setYAxisMax(yMax); // Y�b��̤ܳj��
        renderer.setAxesColor(axesColor); // �]�w���жb�C��
        renderer.setYLabelsColor(0, Color.BLACK); // Y�b�u�C��
        renderer.setLabelsColor(Color.BLACK); // �]�w�����C��
        renderer.setMarginsColor(Color.WHITE); // �]�w�I���C��
        renderer.setShowGrid(true); // �]�w��u
    }
	
    // �w�q��u�Ϫ��榡
    private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //�N�y���ܦ��u�[�J�Ϥ����
        }
        return renderer;
    }
	
	// ��ƳB�z
    private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
            List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = titles.length; // ��u�ƶq
        Integer t = new Integer(length);
        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < length; i++) {
            // XYseries��H,�Ω󴣨�ø�s���I���X�����
            XYSeries series = new XYSeries(titles[i]); // �̾ڨC���u���W�ٷs�W
            double[] xV = xValues.get(i); // �����i���u�����
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // ���X���I

            for (int k = 0; k < seriesLength; k++) // �C���u�̦��X���I
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }//*/
        return dataset;
    }
    
    /**
	 * �ǰe��r�T��
	 */
	private void send(String str){
		//public final Message obtainMessage (int what, Object obj)
		Message message = ClientThread.clientHandler.obtainMessage( 0, str );
		ClientThread.clientHandler.sendMessage( message );
	}

	/**
	 * �����]�m
	 */
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if( connect_OK ) { // �p�G�S���s�u�A�O����o�e������
		    send("#end");  // �o�e�����s�u������(���q���ݪ��D)      
		    
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