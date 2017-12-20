package jian.com.ipctest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";


    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.i(TAG,"接收到消息从客户端："+msg.getData().getString("msg"));
                    //回复消息给客户端
                    Messenger client = msg.replyTo;

                    Message replyMessage = Message.obtain(null,2);

                    Bundle bundle = new Bundle();
                    bundle.putString("reply","我收到了你的消息了，稍后给你信息");
                    replyMessage.setData(bundle);

                    try {
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);

            }
        }
    }
    private final Messenger mMessenger = new Messenger(new MessengerHandler());


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();
    }


}
