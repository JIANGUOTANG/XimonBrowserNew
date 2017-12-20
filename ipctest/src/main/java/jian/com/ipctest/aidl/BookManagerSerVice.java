package jian.com.ipctest.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ying on 17-11-8.
 */

public class BookManagerSerVice extends Service {
    private static final String TAG = "BMS";
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private Binder binder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void regiesterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            //因为内部实现用的是ArrayMap所以不用判断是否存在
            mListenerList.register(listener);

        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "andoird"));
        mBookList.add(new Book(2, "三体"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        Log.d(TAG, "onNewBookArrived,notify listeners:" +N );
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
            if(l!=null){
                try {
                    l.OnNewBookArrived(book);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {
        //每隔五秒模拟添加一本书
        @Override
        public void run() {

            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book" + bookId);
                try {
                    //通知新书
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
