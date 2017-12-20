package jian.com.ipctest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserManager.mUserId = 2;
        Log.d("MainActivity-----ID",UserManager.mUserId+"");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                Bundle  bundle = new Bundle();
                bundle.putInt("id",10);
                intent.putExtra(SecondActivity.BUNDLE_TAG,bundle);
                startActivity(intent);
            }
        });

    }
    public static String PATH ;
    public static String CACHPATH = "cache";

    @Override
    protected void onResume() {
        super.onResume();
        persistToFile();
    }

    private void persistToFile(){
        PATH =getFilesDir().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = new User(1,"hello",false);
                File dir = new File(PATH,"User");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File cacheFile = new File(PATH,CACHPATH);
                ObjectOutputStream objectOutputStream  = null;
                try {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(cacheFile));
                    objectOutputStream.writeObject(user);
                    Log.d("MainActivity---User","persist user:"+user);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        objectOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

}
