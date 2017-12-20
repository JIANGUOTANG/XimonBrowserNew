package jian.com.ipctest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by ying on 17-11-5.
 */


public class SecondActivity extends AppCompatActivity {
        public static final String BUNDLE_TAG = "BUNDLE";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_second);
            Log.d("SecondActivity-----ID",UserManager.mUserId+"");

        }
    public static String PATH = "User";
    public static String CACHPATH = "cache";

    @Override
    protected void onResume() {
        super.onResume();
        recoverFromFile();
    }

    private void recoverFromFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = null;

                File cacheFile = new File(getFilesDir().toString(),CACHPATH);
                if(cacheFile.exists()) {

                    ObjectInputStream objectInputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(new FileInputStream(cacheFile));
                        user = (User) objectInputStream.readObject();
                        Log.d("SecondActivity---User", "persist user:" + user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            objectInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();
    }


}
