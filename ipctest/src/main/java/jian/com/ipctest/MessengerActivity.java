package jian.com.ipctest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import jian.com.ipctest.service.MessengerService;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG  = "MessengerActivity";
    private Messenger mService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService =new Messenger(iBinder);
            Message msg = Message.obtain(null,1);
            Bundle data = new Bundle();
            data.putString("msg","hello jian,I am client");
            msg.setData(data);
            //这个将需要接收服务端回复的Messenger通过Message的replyTo参数传递给服务端
            msg.replyTo = mGetReplyMessager;
            try{
                //通过Messenger发送消息
                mService.send(msg);
            }catch (RemoteException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private Messenger mGetReplyMessager = new Messenger(new MessengerHandler());
    //  处理服务端返回的消息
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    Log.i(TAG,"接收服务端的消息："+msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //绑定Service
                Intent intent = new Intent(MessengerActivity.this, MessengerService.class);
                bindService(intent,connection, Context.BIND_AUTO_CREATE);
            }
        });


    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();

    }
}
