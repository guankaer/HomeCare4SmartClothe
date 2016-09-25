package guankaer.homecare;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import guankaer.bluetooth.BluetoothService;
import guankaer.ecg.EcgProtocol;

public class MonitorActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    private DrawChart ecgChart1,xChart;
    private DrawChart ecgChart2,yChart;
    private DrawChart ecgChart3,zChart;
    private DrawChart ecgTable1,xTable;
    private DrawChart ecgTable2,yTable;
    private DrawChart ecgTable3,zTable;
    private int ecgW, ecgH, gsensorW, gsensorH;
    private DisplayMetrics monitorsizeecg, monitorsizegsensor;

    private static byte header = -91;
    private int sequence = 0;
    private static int timeNow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        initializeVariables();
        BluetoothService.Init(this);
//        handler.post(btTimer);
    }

    private void initializeVariables() {

        monitorsizeecg = new DisplayMetrics();
        monitorsizegsensor = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(monitorsizeecg);
        getWindowManager().getDefaultDisplay().getMetrics(monitorsizegsensor);

        ecgW = monitorsizeecg.widthPixels * 51 / 80;
        ecgH = monitorsizeecg.heightPixels * 24 / 100;
        gsensorW = monitorsizegsensor.widthPixels * 13 / 80;
        gsensorH = monitorsizegsensor.heightPixels * 12 / 100;

        ecgChart1 = (DrawChart) findViewById(R.id.ecgchart1);
        ecgChart2 = (DrawChart) findViewById(R.id.ecgchart2);
        ecgChart3 = (DrawChart) findViewById(R.id.ecgchart3);
        ecgTable1 = (DrawChart) findViewById(R.id.ecgtable1);
        ecgTable2 = (DrawChart) findViewById(R.id.ecgtable2);
        ecgTable3 = (DrawChart) findViewById(R.id.ecgtable3);

        ecgChart1.SetWH(ecgW, ecgH);
        ecgChart2.SetWH(ecgW, ecgH);
        ecgChart3.SetWH(ecgW, ecgH);
        ecgTable1.SetWH(ecgW, ecgH);
        ecgTable2.SetWH(ecgW, ecgH);
        ecgTable3.SetWH(ecgW, ecgH);
        ecgTable1.invalidate();
        ecgTable2.invalidate();
        ecgTable3.invalidate();

        xChart = (DrawChart) findViewById(R.id.xchart);
        yChart = (DrawChart) findViewById(R.id.yhart);
        zChart = (DrawChart) findViewById(R.id.zchart);
        xTable = (DrawChart) findViewById(R.id.xtable);
        yTable = (DrawChart) findViewById(R.id.ytable);
        zTable = (DrawChart) findViewById(R.id.ztable);

        xChart.SetWH(gsensorW, gsensorH);
        yChart.SetWH(gsensorW, gsensorH);
        zChart.SetWH(gsensorW, gsensorH);
        xTable.SetWH(gsensorW, gsensorH);
        yTable.SetWH(gsensorW, gsensorH);
        zTable.SetWH(gsensorW, gsensorH);
        xTable.invalidate();
        yTable.invalidate();
        zTable.invalidate();

    }

}
