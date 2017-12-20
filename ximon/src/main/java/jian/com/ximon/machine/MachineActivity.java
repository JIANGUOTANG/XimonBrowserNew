package jian.com.ximon.machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import jian.com.ximon.ad.ad.AdActivity;
import jian.com.ximon.util.DialogUtil;

/**
 * Created by ying on 17-11-13.
 */

public class MachineActivity extends BaseActivity implements MachineView {
    private RecyclerView recyclerViewMachine;
    private Button btAddMachine;//添加广告机按钮

    public static final String TAG = "MachineActivity";
    private MachinePresenter machinePresenter;
    private MachineAdapter machineAdapter;//适配器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);
        initView();
        //设置toolbar
        setupToolbar(getResources().getString(R.string.your_machine));
        machinePresenter = new MachinePresenter(this, this);
        machinePresenter.loadMachine();//加载广告机
    }

    private void initView() {
        recyclerViewMachine = findViewById(R.id.recyclerView_machineList);
        btAddMachine = findViewById(R.id.btAddMachine);
        //点击按钮跳转到广告机添加界面
        btAddMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MachineActivity.this, MachineAddActivity.class), 2);
            }
        });
    }

    @Override
    public void startLoading() {
        //开始加载，这里应该有动画
    }

    @Override
    public void deleteSuccess() {
        //删除成功
    }

    @Override
    public void deleteFailure() {
        //删除失败
    }

    private List<Machine> machines;

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void finishLoading(MachineEvent event) {
        //加载结束，返回List
        machines = event.machines;
        Log.d(TAG, machines.size() + "jian");
        machineAdapter = new MachineAdapter(machines);
        //设置为列表的形式显示
        recyclerViewMachine.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMachine.setAdapter(machineAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        //注册EventBus
        EventBus.getDefault().register(this);
        super.onStart();

    }

    @Override
    protected void onStop() {
        //注销EventBus
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemLongClick(final MachineOnclickEvent event) {
        final Fields fields = event.machine.getFields();
        if (event.currentClick == MachineOnclickEvent.LONG_CLICK) {
            //长按弹出功能选择界面
            DialogUtil dialogUtil = new DialogUtil();
            dialogUtil.showEditDialog(this, event.position, new DialogUtil.DialogItemClickListener() {
                @Override
                public void delete() {
                    //删除
                    machinePresenter.delete(event.machine.getPk());
                    machines.remove(event.position);//显示界面的删除
                    machineAdapter.notifyDataSetChanged();
                }

                @Override
                public void editor() {
                    //编辑
                    Intent intent = new Intent(MachineActivity.this, MachineAddActivity.class);
                    intent.putExtra("machine_id",event.machine.getPk());
                    startActivity(intent);
                }
            });
        } else {
            //点击item跳转到广告机对应的广告界面
            //编辑
            Intent intent = new Intent(MachineActivity.this, AdActivity.class);
            intent.putExtra("machine_id", event.machine.getPk());
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            machinePresenter.loadMachine();
        }
    }
}
