package guankaer.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;
import android.content.BroadcastReceiver;

import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import guankaer.homecare.*;

public class BluetoothService extends Service {

    public static BluetoothAdapter btAdapter = null;
    private static Context btContext;
    private BluetoothSocket mmSocket = null;
    public static OutputStream mOut = null;
    public static InputStream mIn = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private static String address = "00:12:11:07:01:16"; // <==要连接的蓝牙设备MAC地址
//    private static String address = "34:81:F4:11:0C:14";
    private static String address = "34:81:F4:11:09:E4";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }

    public static void Init(Context context){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()){
            Intent mIntentOpenBT = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(mIntentOpenBT);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter != null){
            if(!btAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
            }else{
                if(mIn != null){
                    Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();
                    Intent bluetooth_intent = new Intent();
                    bluetooth_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    bluetooth_intent.setClass(getApplicationContext(),MonitorActivity.class);
                    startActivity(bluetooth_intent);
                }else{
                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    if(pairedDevices.size() > 0){
                        //搜索已经配对的设备
                        Toast.makeText(getApplicationContext(), "搜索已配对蓝牙设备", Toast.LENGTH_SHORT).show();
                        for(BluetoothDevice device:pairedDevices){
                            if(device.getAddress().equals(address)){
                                if(btAdapter.isDiscovering()){
                                    btAdapter.cancelDiscovery();
                                }
                                try{
                                    mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                                    mmSocket.connect();
                                    mIn = mmSocket.getInputStream();
                                    mOut = mmSocket.getOutputStream();
                                    Toast.makeText(getApplicationContext(), "1连接成功", Toast.LENGTH_SHORT).show();
                                    Intent bluetooth_intent = new Intent();
                                    bluetooth_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    bluetooth_intent.setClass(getApplicationContext(),MonitorActivity.class);
                                    startActivity(bluetooth_intent);
//                                    new ReceiveEcg().start();
                                }catch(IOException e){
                                    Toast.makeText(getApplicationContext(), "1连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }else{
                        //搜索未配对的设备
                        Toast.makeText(getApplicationContext(), "搜索未配对蓝牙设备", Toast.LENGTH_SHORT).show();
                        if(btAdapter.isDiscovering()) {
                            btAdapter.cancelDiscovery();
                        }
                        btAdapter.startDiscovery();
                        final BroadcastReceiver Receiver = new BroadcastReceiver(){
                            @Override
                            public void onReceive(Context arg0, Intent arg1) {
                                String action = arg1.getAction();
                                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                                    final BluetoothDevice extraDevice = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
                                                mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                                                mmSocket.connect();
                                                mIn = mmSocket.getInputStream();
                                                mOut = mmSocket.getOutputStream();
                                                Toast.makeText(getApplicationContext(), "2连接成功", Toast.LENGTH_SHORT).show();
                                                Intent bluetooth_intent = new Intent();
                                                bluetooth_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                bluetooth_intent.setClass(getApplicationContext(),MonitorActivity.class);
                                                startActivity(bluetooth_intent);
//                                                new ReceiveEcg().start();
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "2连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(getApplicationContext(), "请打开蓝牙心电传感器", Toast.LENGTH_SHORT).show();
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
    public void onDestory(){
        super.onDestroy();
    }
}
