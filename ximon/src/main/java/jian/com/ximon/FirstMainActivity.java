package jian.com.ximon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jian.com.ximon.function.Function;
import jian.com.ximon.function.FunctionAdapter;

public class FirstMainActivity extends BaseActivity {
    private FunctionAdapter functionAdapter;
    private List<Function> functions = new ArrayList<>();
    private RecyclerView recyclerViewFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initFunction();
    }

    //通过映射来创建activity，方便修改功能
    private void initFunction() {
        Function function;
        recyclerViewFunction = findViewById(R.id.recyclerView);
        //需要新增加功能直接在这里添加
        function = new Function("广告推送", R.drawable.ic_pull, "jian.com.ximon.machine.MachineActivity", R.color.primary_dark);
        functions.add(function);
        function = new Function(getResources().getString(R.string.monitor_control), R.drawable.ic_monitor, "com.thecamhi.main.MainActivity", R.color.colorAccent);
        functions.add(function);
        function = new Function(getResources().getString(R.string.charge_control), R.drawable.ic_charge, "jian.com.ximon.charge.ChargeActivity", R.color.spark_secondary_color);
        functions.add(function);
        function = new Function(getResources().getString(R.string.lamp_control), R.drawable.ic_lamp, "jian.com.ximon.lamp.LampActivity", R.color.primary_dark);
        functions.add(function);
        function = new Function(getResources().getString(R.string.weather_control), R.drawable.icweather, "jian.com.ximon.weather.WeatherActivity", R.color.colorGreen);
        functions.add(function);
        function = new Function(getResources().getString(R.string.light_control), R.drawable.icweather, "jian.com.ximon.light.LightActivity", R.color.colorAccent);
        functions.add(function);
        functionAdapter = new FunctionAdapter(functions, this);
        recyclerViewFunction.setAdapter(functionAdapter);
        recyclerViewFunction.setLayoutManager(new GridLayoutManager(this, 3));
        functionAdapter.setOnFunctionClick(new FunctionAdapter.onFunctionClick() {
            @Override
            public void onClick(Function function, int position) {
                try {
                    Class mClass = Class.forName(function.getClassName());
                    Intent intent = new Intent(FirstMainActivity.this, mClass);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
