package jian.com.ipctest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ying on 17-11-5.
 */


public class ThirdActivity extends AppCompatActivity {
        public static final String BUNDLE_TAG = "BUNDLE";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_third);
//            Intent intent =getIntent();
//            Bundle bundle = intent.getBundleExtra(BUNDLE_TAG);
//            int id = bundle.getInt("id");
//            Toast.makeText(ThirdActivity.this,"id="+id,Toast.LENGTH_LONG).show();
        }


}
