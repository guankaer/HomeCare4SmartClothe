package guankaer.homecare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import guankaer.bluetooth.Bluetooth;
import guankaer.anychat.*;


public class MainActivity extends AppCompatActivity implements AnyChatBaseEvent{

    private ImageView Monitor, Records, Connect, Settings;
    public AnyChatCoreSDK anyChatSDK;
    private final int LOCALVIDEOAUTOROTATION = 1; // 本地视频自动旋转控制

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

    private void InitSDK() {
        if (anyChatSDK == null) {
            anyChatSDK = AnyChatCoreSDK.getInstance(this);
            anyChatSDK.SetBaseEvent(this);
            anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,LOCALVIDEOAUTOROTATION);
        }
    }

    // 连接服务器消息, bSuccess表示是否连接成功
    public void OnAnyChatConnectMessage(boolean bSuccess){

    }
    // 用户登录消息，dwUserId表示自己的用户ID号，dwErrorCode表示登录结果：0 成功，否则为出错代码
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode){

    }
    // 用户进入房间消息，dwRoomId表示所进入房间的ID号，dwErrorCode表示是否进入房间：0成功进入，否则为出错代码
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode){

    }
    // 房间在线用户消息，进入房间后触发一次，dwUserNum表示在线用户数（包含自己），dwRoomId表示房间ID
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId){

    }
    // 用户进入/退出房间消息，dwUserId表示用户ID号，bEnter表示该用户是进入（TRUE）或离开（FALSE）房间
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter){

    }
    //网络断开消息，该消息只有在客户端连接服务器成功之后，网络异常中断之时触发，dwErrorCode表示连接断开的原因
    public void OnAnyChatLinkCloseMessage(int dwErrorCode){

    }

}
