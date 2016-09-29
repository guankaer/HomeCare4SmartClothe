package guankaer.homecare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import guankaer.bluetooth.*;


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
    private int sum_1 = 0;
    private int sum_2 = 0;
    private int sum_3 = 0;
    private int count = 0;
    private static int timeNow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        initializeVariables();
//        startService(new Intent(this, BluetoothService.class));
//        initConnect();
        handler.post(btTimer);
        timeNow = 0;
    }

    public void initConnect(){
        Bluetooth.btSearch();
        for (BluetoothDevice btDevice : Bluetooth.listDevice) {
            if (btDevice.getAddress().equals(Bluetooth.address)) {
                Bluetooth.btConnect(btDevice);
            }
        }

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


    byte[] BluetoothTemp;
    private Runnable btTimer = new Runnable() {
        public void run() {
            try {
//                if (!Bluetooth.isConn && cv.startNow)
//                    cv.stop();
                int bytes = Bluetooth.getData();
                if (Bluetooth.byteData != null) {

                    BluetoothTemp = Bluetooth.byteData;

                    if (BluetoothTemp.length % 18 == 0 && BluetoothTemp.length != 0) {
                        for (int j = 0; j < BluetoothTemp.length / 18; j++) {
                            int tempSequence = Bluetooth.From2ComplementtoUnsigned(BluetoothTemp[j * 18 + 2]) +
                                    Bluetooth.From2ComplementtoUnsigned(BluetoothTemp[j * 18 + 3]) * 256;
                            if (BluetoothTemp[j * 18] == header && BluetoothTemp[j * 18 + 1] == header &&
                                    (tempSequence > sequence || sequence - tempSequence == 999)) {
//                                gsensorMode(BluetoothTemp[j * 18 + 16], BluetoothTemp[j * 18 + 17]); //display G-sensor stat
                                for (int k = 0; k < 2; k++) {
                                    combineLowHigh(BluetoothTemp, j, k);//combine low byte and high byte
                                    if (++count >= 2) {

                                        timeNow++;

                                        //saveEMDRawData(sum_3);
//                                        if (timeNow % 2 == 0 ) { // downsample
//                                            if (modeshow == "ECG") {
//
//                                            } else {
//                                                drawChart1.prepareLine2(sum_1);
//                                                drawChart2.prepareLine2(sum_2);
//                                                drawChart3.prepareLine2(sum_3);
//                                            }
//                                        }
                                        count = 0;
                                        sum_1 = 0;
                                        sum_2 = 0;
                                        sum_3 = 0;
                                    }
                                }
                                sequence = tempSequence;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(MonitorActivity.this, e.toString(),
                        Toast.LENGTH_LONG).show();
            }
//            if (!change)
                handler.post(this);
        }
    };

    private void combineLowHigh(byte[] temp, int j, int k) {
        int value_1 = 0;
        int value_2 = 0;
        int value_3 = 0;

//        if (modeshow == "ECG") {
            value_1 = Bluetooth
                    .From2ComplementtoUnsigned(temp[j * 18 + k + 4]);
            value_2 = Bluetooth
                    .From2ComplementtoUnsigned(temp[j * 18 + k + 6]);
            value_3 = Bluetooth
                    .From2ComplementtoUnsigned(temp[j * 18 + k + 8]);
//        } else {
//            value_1 = (temp[j * 18 + k + 10]);
//            value_2 = (temp[j * 18 + k + 12]);
//            value_3 = (temp[j * 18 + k + 14]);
//        }

        sum_1 = sum_1 + (value_1 << ((count % 2) * 8));
        sum_2 = sum_2 + (value_2 << ((count % 2) * 8));
        sum_3 = sum_3 + (value_3 << ((count % 2) * 8));
    }

    @Override
    protected void onDestroy() {
        try {
            handler.removeCallbacks(btTimer);
            Bluetooth.onDestroy();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

}
