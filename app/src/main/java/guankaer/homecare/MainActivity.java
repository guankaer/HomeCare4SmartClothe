package guankaer.homecare;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import guankaer.bluetooth.Bluetooth;
import guankaer.bluetooth.BluetoothService;


public class MainActivity extends AppCompatActivity {

    private ImageView Monitor, Records, Connect, Settings;
//    final Intent BluetoothServiceIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Monitor = (ImageView) findViewById(R.id.monitoring);
        Records = (ImageView) findViewById(R.id.records);
        Connect = (ImageView) findViewById(R.id.connect);
        Settings = (ImageView) findViewById(R.id.settings);
        setOnclick(Monitor);
        setOnclick(Records);
        setOnclick(Connect);
        setOnclick(Settings);
//        BluetoothServiceIntent.setAction("guankaer.bluetooth.BluetoothService");
//        BluetoothService.Init(this);
        Bluetooth.Init(this);
    }

    private void setOnclick(final ImageView btn){
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (v.getId()){
                    case R.id.monitoring:
//                        startService(BluetoothServiceIntent);
//                        initConnect();
                        Intent monitoring = new Intent(MainActivity.this,MonitorActivity.class);
                        startActivityForResult(monitoring,1);
                        MainActivity.this.finish();
                        break;
                    case R.id.records:
                        Intent records = new Intent(MainActivity.this,RecordActivity.class);
                        startActivityForResult(records,2);
                        MainActivity.this.finish();
                        break;
                    case R.id.connect:
                        Intent connect = new Intent(MainActivity.this,Connect.class);
                        startActivityForResult(connect,3);
                        MainActivity.this.finish();
                        break;
                    case R.id.settings:
                        Intent settings = new Intent(MainActivity.this,Settings.class);
                        startActivityForResult(settings,4);
                        MainActivity.this.finish();
                        break;
                }
            }
        });
    }

    public void initConnect(){
        if (Bluetooth.flag == 0) {
            Bluetooth.btSearch();
            for (BluetoothDevice btDevice : Bluetooth.listDevice) {
                if (btDevice.getAddress().equals(Bluetooth.address)) {
                    Bluetooth.btConnect(btDevice);
                }
            }
        }else if (Bluetooth.flag == 1) {
            Bluetooth.btConnAbort();
        }
    }
}
