package jian.com.ximon.ad.ad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import jian.com.ximon.BaseActivity;
import jian.com.ximon.R;
import jian.com.ximon.ad.ad.bean.AD;
import jian.com.ximon.ad.file.UploadFileActivity;
import jian.com.ximon.util.Constant;
import jian.com.ximon.util.DialogUtil;

/**
 * Created by ying on 17-11-15.
 */

public class AdActivity extends BaseActivity implements AdView, DialogUtil.SelectListener {

    private RecyclerView recyclerViewAD;
    public static final String TAG = "AdActivity";
    private AdPresenter adPresenter;
    private AdAdapter adAdapter;
    private int machine_id = 0;
    private FloatingActionButton floatingActionBtSend;//推送按钮
    private Button btAdd;//添加广告按钮
    private final int FILEREQUESTCODE = 1;
    private final int URLREQUESTCODE = 2;
    private int selectPosition;//当前选中的位置
    List<AD> adList;//广告列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        initView();
        setupToolbar("广告");
        Intent intent = getIntent();
        machine_id = intent.getIntExtra("machine_id", 0);
        adPresenter = new AdPresenter(this,this, machine_id);
        adPresenter.loadMachine();
    }

    private void initView() {
        recyclerViewAD = findViewById(R.id.recyclerView_machineList);
        floatingActionBtSend = findViewById(R.id.float_bt_send);
        btAdd = findViewById(R.id.btAddMachine);
        btAdd.setText("添加广告");
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil dialogUtil = new DialogUtil();
                dialogUtil.showAddDialog(AdActivity.this, AdActivity.this);
            }
        });
        floatingActionBtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击推送按钮
                if(machine_id==0){
                    //集体推送
                }else {
                    //单个发送
                    adPresenter.sendAd(machine_id);

                }
            }
        });
    }

    @Override
    public void startLoading() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void finishLoading(AdEvent event) {
        //加载广告列表完成
        adList = event.adList;
        Log.d(TAG, adList.size() + "jian");
        recyclerViewAD.setLayoutManager(new LinearLayoutManager(this));
        adAdapter = new AdAdapter(this, adList);
        recyclerViewAD.setAdapter(adAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    //长按弹出功能选择界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemLongClick(final ADOnclickEvent event) {
        DialogUtil dialogUtil = new DialogUtil();
        selectPosition = event.position;//获得选中的位置
        if (event.currentClick == ADOnclickEvent.LONGCLICK) {
            //长按
            dialogUtil.showEditDialog(this, event.position, new DialogUtil.DialogItemClickListener() {

                @Override
                public void delete() {
                    //删除
                    Log.i(TAG, "delete");
                    adPresenter.deleteAd(event.ad.getPk());
                }

                @Override
                public void editor() {
                    //编辑
                }
            });
        } else {
            //点击
            if (event.ad.getFields().getType() == 1) {
                //预览图片
                Intent intent = new Intent(AdActivity.this, ImagePreViewActivity.class);
                //传递图片的路径
                intent.putExtra("url", Constant.BASH_PATH + event.ad.getFields().getUrl());
                startActivity(intent);
            } else {
                //预览视频
                Intent intent = new Intent(AdActivity.this, VideoPreViewActivity.class);
                //传递图片的路径
                intent.putExtra("url", Constant.BASH_PATH + event.ad.getFields().getUrl());
                startActivity(intent);
            }

        }
    }

    @Override
    public void url() {
        //点击通过url上传
        Intent intent = new Intent(AdActivity.this, UploadFileActivity.class);
        intent.putExtra("machine_id", machine_id);
        startActivityForResult(intent, URLREQUESTCODE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteSuccess(String response) {
        adList.remove(selectPosition);//当前列表删除
        adAdapter.notifyDataSetChanged();
    }

    @Override
    public void file() {
        //点击通过file上传
        Intent intent = new Intent(AdActivity.this, UploadFileActivity.class);
        intent.putExtra("machine_id", machine_id);
        startActivityForResult(intent, FILEREQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            adPresenter.loadMachine();
        }
    }
}
