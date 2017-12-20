package jian.com.ipctest.aidl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;

import jian.com.ipctest.R;

/**
 * Created by ying on 17-11-8.
 */

public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "BookManagerActivity";
    private static final int MESSAGE_NEW_ARRIVED = 1;
    private IBookManager mRemoteBookManager;
    //处理新书信息
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_ARRIVED:

                    Log.d(TAG, "receive new book" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            IBookManager iBookManager = IBookManager.Stub.asInterface(service);
            try {
                mRemoteBookManager = iBookManager;
                List<Book> list = iBookManager.getBookList();
                Log.i(TAG, "列表类型" + list.getClass().getCanonicalName());
                Log.i(TAG, "查询列表" + list.toString());
                Book book = new Book(3, "简的博客");
                iBookManager.addBook(book);
                Log.i(TAG, "add book" + book);
                List<Book> newList = iBookManager.getBookList();
                Log.i(TAG, "查询列表" + newList.toString());
                iBookManager.regiesterListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager=null;
            Log.e(TAG,"binder died");
        }
    };
    private IOnNewBookArrivedListener iOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub(){

        @Override
        public void OnNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_ARRIVED,book)
                    .sendToTarget();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookManagerActivity.this, BookManagerSerVice.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onDestroy() {

        if(mRemoteBookManager!=null&&mRemoteBookManager.asBinder().isBinderAlive()){
            try {
                Log.i(TAG,"unregister listener"+iOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(connection);
        super.onDestroy();

    }
}
