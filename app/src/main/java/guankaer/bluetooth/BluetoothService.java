package guankaer.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class BluetoothService extends Service {

    private static Handler handler = new Handler();
    private static Context btContext;
//    private static ImageView btState;

    private static final UUID btUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "34:81:F4:11:0C:14"; // <==要连接的蓝牙设备MAC地址
    private static BluetoothAdapter btAdapter;
    private static BluetoothSocket btSocket;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static boolean Connecting;

    private static long receiveData;
    public static ArrayList<BluetoothDevice> listDevice = new ArrayList<BluetoothDevice>();

    public static boolean isConn;
    public static byte[] byteData;
    public static int flag = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter != null){
            if(!btAdapter.isEnabled()){
                btAdapter.enable();  //这种方法是不做提醒，直接打开蓝牙设备
                Toast.makeText(getApplicationContext(), "正在打开蓝牙", Toast.LENGTH_SHORT).show();
            }else{
                if(inputStream != null){
                    Toast.makeText(getApplicationContext(), "searching bluetooth device", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "searching bluetooth device", Toast.LENGTH_SHORT).show();
                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    if(pairedDevices.size() > 0){
                        for(BluetoothDevice device:pairedDevices){
                            if(device.getAddress().equals(address)){
                                if (btAdapter.isDiscovering()) {
                                    btAdapter.cancelDiscovery();
                                }
                                try{
                                    btSocket = device.createRfcommSocketToServiceRecord(btUUID);
                                    btSocket.connect();
                                    inputStream = btSocket.getInputStream();
                                    outputStream = btSocket.getOutputStream();
                                    Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                                    new ReceiveEcg().start();
                                }catch(IOException e){
                                    Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }else{
                        if(btAdapter.isDiscovering()){
                            btAdapter.cancelDiscovery();
                        }
                        btAdapter.startDiscovery();
                        final BroadcastReceiver Receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getAction();
                                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                                    final BluetoothDevice extraDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                    final ArrayList<BluetoothDevice> listDevices2 = new ArrayList<BluetoothDevice>();
                                    if (extraDevice.getBondState() != BluetoothDevice.BOND_BONDED){
                                        listDevices2.add(extraDevice);
                                    }
                                    for(BluetoothDevice device:listDevices2){
                                        if(device.getAddress().equals(address)){
                                            if(btAdapter.isDiscovering()){
                                                btAdapter.cancelDiscovery();
                                            }
                                            try {
                                                btSocket = device.createRfcommSocketToServiceRecord(btUUID);
                                                btSocket.connect();
                                                inputStream = btSocket.getInputStream();
                                                outputStream = btSocket.getOutputStream();
                                                Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                                                new ReceiveEcg().start();
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(getApplicationContext(), "请打开传感器", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        };
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        this.registerReceiver(Receiver,filter);
                        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                        this.registerReceiver(Receiver,filter);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        handler.removeCallbacks(btNoConn);
        try {
            inputStream.close();
            outputStream.close();
            btSocket.close();
            inputStream = null;
            outputStream = null;
            btSocket = null;
            byteData = null;
        } catch (Exception e) {
        }
    }

    public static void Init(Context context) {
        btContext = context;
        isConn = false;
        Connecting = false;
        btSearch();
    }

    public static void btSearch() {
        try {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!btAdapter.isEnabled()) {
                Intent mIntentOpenBT = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                btContext.startActivity(mIntentOpenBT);
            }
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            btContext.registerReceiver(btReceiver, filter);
            btAdapter.startDiscovery();

//			Toast.makeText(btContext.getApplicationContext(), R.string.bt_search, Toast.LENGTH_SHORT).show();
            //??撠?銝?
            handler.postDelayed(btNoConn, 20000);
        } catch (Exception e) {
        }
    }

    private static BroadcastReceiver btReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 搜索到的不是已经绑定的蓝牙设备
                    if (device.getName().contains("Dual-SPP")) {
                        // 显示在TextView上
                        listDevice.add(device);
                    }
                    System.out.println("Bluetooth：" + listDevice.size());
//					if (device != null) {
//						String deviceName = device.getName();
//						if (deviceName != null && deviceName.length() > 0) {
//							if (deviceName.indexOf("Dual-SPP") >= 0)
//								btConnect(device);
//						}
//					}
                }
            } catch (Exception e) {
            }
        }
    };

    public static void btConnect(final BluetoothDevice device) {

        System.out.println("aaaaaaaa.......");

        if (Connecting)
            return;
        Connecting = true;
        handler.removeCallbacks(btNoConn);

        new Thread() {
            String Error = "";

            public void run() {
                try {
                    btContext.unregisterReceiver(btReceiver);
                    btAdapter.cancelDiscovery();
                    btSocket = device
                            .createInsecureRfcommSocketToServiceRecord(btUUID);
                    btSocket.connect();
                    inputStream = btSocket.getInputStream();
                    outputStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    Error = e.toString();
                }
                handler.post(new Runnable() {
                    public void run() {
                        if (Error.length() > 0)
                            btConnFail();
                        else
                            btConnSuccess(device);
//						Toast.makeText(btContext, device.getAddress(),
//								Toast.LENGTH_SHORT).show();
                        Connecting = false;
                    }
                });
            }
        }.start();
    }


    private static void btConnSuccess(BluetoothDevice device) {
        isConn = true;
//        Toast.makeText(btContext.getApplicationContext(), R.string.bt_success, Toast.LENGTH_SHORT).show();
        flag = 1;
//		btState.setClickable(false);
        receiveData = System.currentTimeMillis();
        // btCount = 0;
        handler.postDelayed(btConnCheck, 1000);
    }


    private static void btConnFail() {

        isConn = false;
//        Toast.makeText(btContext.getApplicationContext(), R.string.bt_fail, Toast.LENGTH_SHORT).show();
//        btReConn();
    }


    private static Runnable btConnCheck = new Runnable() {
        public void run() {
            BluetoothService.getData();
            long idleTime = (System.currentTimeMillis() - receiveData) / 1000;
            if (idleTime > 0.1)
                btConnAbort();
            if (isConn)
                handler.postDelayed(this, 1000);
        }
    };


    private static Runnable btNoConn = new Runnable() {
        public void run() {
            try {
                if (isConn || Connecting)
                    return;
//                Toast.makeText(btContext.getApplicationContext(), R.string.bt_nodevice, Toast.LENGTH_SHORT).show();
                //?曆??啗?蝵?

//                btReConn();
                btContext.unregisterReceiver(btReceiver);
                btAdapter.cancelDiscovery();
            } catch (Exception e) {
            }
        }
    };


    public static void btConnAbort() {
        isConn = false;
//        Toast.makeText(btContext.getApplicationContext(), R.string.bt_abort, Toast.LENGTH_SHORT).show();
//        btReConn();
        try {
            inputStream.close();
            outputStream.close();
            btSocket.close();
            inputStream = null;
            outputStream = null;
            btSocket = null;
            byteData = null;
        } catch (Exception e) {
        }
    }

//    private static void btReConn() {
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                btState.setClickable(true);
//                flag = 0;
//            }
//        }, 3000);
//    }

    public static int getData() {
        int bytes = 0;
        try {
            if (inputStream != null) {
                if (inputStream.available() > 0) {
                    byteData = new byte[inputStream.available()];
//					System.out.println(byteData.length);
                    bytes = inputStream.read(byteData);

                    receiveData = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
        }
        return bytes;
    }

    public class ReceiveEcg extends Thread{
        public void run(){
            getData();
        }
    }
}
