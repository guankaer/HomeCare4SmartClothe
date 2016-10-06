package guankaer.homecare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import guankaer.bluetooth.Bluetooth;


public class MainActivity extends AppCompatActivity{

    private ImageView Monitor, Records, Connect, Settings;

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
        Bluetooth.Init(this);
    }

    private void setOnclick(final ImageView btn){
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (v.getId()){
                    case R.id.monitoring:
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

}
