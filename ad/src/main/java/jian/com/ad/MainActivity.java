package jian.com.ad;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jian.pus.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import jian.com.ad.bean.LinkFragment;
import jian.com.ad.bean.eventbus.EventCodeNumber;
import jian.com.ad.db.DBManager;
import jian.com.ad.db.FileBean;
import jian.com.ad.db.SharePeferenceUtil;
import jian.com.ad.view.fragment.ImageFragment;
import jian.com.ad.view.fragment.MyPagerAdapter;
import jian.com.ad.view.fragment.VideoFragment;


public class MainActivity extends AppCompatActivity implements VideoFragment.PagerListener {
    private static final int IMAGE = 0;
    private static final int VIDEO = 1;
    private static final int UPTATE_VIEWPAGER = 0;
    public ViewPager mViewPager;
    private List<ListBean> list;
    //适配器
    private MyPagerAdapter mAdapter;
    private final String TAG = "MAINACTIVITY";
    //数据库管理者
    private DBManager dbManager;
    private int autoCurrIndex = 0;//设置当前 第几个图片 被选中
    private List<LinkFragment> fragmentList = new ArrayList<>();
    private long period = 5000;//轮播图展示时长,默认5秒
    private TextView tvCodeNumber;
    //定时轮播图片，需要在主线程里面修改 UI
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    if (msg.arg1 == 0&&fragmentList.size()>2) {
                        //false 当从末页调到首页时，不显示翻页动画效果，
                        mViewPager.setCurrentItem(msg.arg1,false);
                    } else {
                        //如果只有一个广告的话，只需要执行这个，否者viewpger的onPageSelected将不会执行
                        mViewPager.setCurrentItem(msg.arg1);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        dbManager = new DBManager();
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewpager);
        tvCodeNumber = findViewById(R.id.codeNumber);
        initData();

    }

    /**
     * 广告轮播图测试数据
     */
    public void initData() {
        tvCodeNumber.setText(SharePeferenceUtil.getCodeNumber(this));
        list = new ArrayList<>();
        ArrayList<FileBean> ads = dbManager.getAD();
        for (FileBean fileBean : ads) {
            String url = fileBean.getUrl();
            int time = Integer.valueOf(fileBean.getTime()) * 1000;
            ListBean listBean = new ListBean();
            if (url.endsWith(".mp4")) {
                listBean.setBannerName("动画片");
                listBean.setBannerUrl(BASH_PATH + url);
                listBean.setPlayTime(time);
                listBean.setUrlType(VIDEO);//图片类型 视频
                list.add(listBean);
                VideoFragment videoFragment = VideoFragment.newInstance(listBean.getBannerUrl());
                videoFragment.setPagerListener(this);
                LinkFragment linkFragment = new LinkFragment(fileBean.getId(), videoFragment);
                fragmentList.add(linkFragment);

            } else {
                listBean.setBannerName("广告");
                listBean.setBannerUrl(BASH_PATH + url);
                Toast.makeText(this, BASH_PATH + url, Toast.LENGTH_LONG).show();
                listBean.setPlayTime(time);
                listBean.setUrlType(IMAGE);//图片类型 图片
                list.add(listBean);
                ImageFragment imageFragment = ImageFragment.newInstance(listBean.getBannerUrl());
                LinkFragment linkFragment = new LinkFragment(fileBean.getId(), imageFragment);
                fragmentList.add(linkFragment);
            }
        }
        autoBanner();
    }

    //自动切换广播
    private void autoBanner() {
        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                ListBean listBean = list.get(position);
                autoCurrIndex = position;//当前第几页
                period = listBean.getPlayTime();//动态设定轮播图每一页的停留时间
                Log.d("time", period + "position:" + position);
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, period);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        autoCurrIndex = 0;
        mHandler.postDelayed(runnable, 0);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = UPTATE_VIEWPAGER;
            if (autoCurrIndex == fragmentList.size() - 1) {
                autoCurrIndex = -1;
            }
            message.arg1 = autoCurrIndex + 1;
            mHandler.sendMessage(message);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void nextPager() {
        Toast.makeText(this, "wanc", Toast.LENGTH_LONG).show();
        mHandler.post(runnable);
    }

    public static final String BASH_PATH = "http://192.168.0.171:9999";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String json) {
        Gson gson = new Gson();
        Log.d(TAG,json);
        FileBean fileBean = gson.fromJson(json, FileBean.class);
        //当前广告未在数据库中存在的时候进行下面的操作
        if (dbManager.showAdd(fileBean)) {
            dbManager.insertAD(fileBean);
            String url = fileBean.getUrl();
            int time = Integer.valueOf(fileBean.getTime()) * 1000;
            ListBean listBean = new ListBean();
            Log.i(TAG, url);
            if (url.endsWith(".mp4")) {
                listBean.setBannerName("动画片");
                listBean.setBannerUrl(BASH_PATH + url);
                listBean.setPlayTime(time);
                listBean.setUrlType(VIDEO);//图片类型 视频
                list.add(listBean);
                VideoFragment videoFragment = VideoFragment.newInstance(listBean.getBannerUrl());
                videoFragment.setPagerListener(this);
                LinkFragment linkFragment = new LinkFragment(fileBean.getId(), videoFragment);
                fragmentList.add(linkFragment);

            } else {
                listBean.setBannerName("广告");
                listBean.setBannerUrl(BASH_PATH + url);
               Toast.makeText(this, BASH_PATH + url, Toast.LENGTH_LONG).show();
                listBean.setPlayTime(time);
                listBean.setUrlType(IMAGE);//图片类型 图片
                list.add(listBean);
                ImageFragment imageFragment = ImageFragment.newInstance(listBean.getBannerUrl());
                LinkFragment linkFragment = new LinkFragment(fileBean.getId(), imageFragment);
                fragmentList.add(linkFragment);
            }
            Log.d("fragmentList", period + "fragmentList:" +fragmentList.size());
            autoCurrIndex = 0;
            if (fragmentList.size()==2){
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                finish();
            }
            mAdapter.setFragments(fragmentList);
            mAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mAdapter);
        }
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Subscribe
    public void deleteAd(Long id) {
        dbManager.deleteAd(id);
        for (int i = 0, j = fragmentList.size(); i < j; i++) {
            if (fragmentList.get(i).id == id) {
                Log.d(TAG, "remove");
                autoCurrIndex = 0;
                fragmentList.remove(i);
                list.remove(i);
                Log.d(TAG, "remove" + fragmentList.size());
                mAdapter.setFragments(fragmentList);
                mAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mAdapter);
                return;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCodeNumber(EventCodeNumber codeNumber) {
        tvCodeNumber.setText(codeNumber.codeNumber);
    }

}



