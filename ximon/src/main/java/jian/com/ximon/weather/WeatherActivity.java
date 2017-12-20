package jian.com.ximon.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;
import jian.com.ximon.BaseActivity;
import jian.com.ximon.R;
import jian.com.ximon.weather.bean.Daily_forecast;
import jian.com.ximon.weather.bean.JsonRootBean;
import jian.com.ximon.weather.bean.Now;

/**
 * Created by 11833 on 2017/12/5.
 */

public class WeatherActivity extends BaseActivity{
 private final String Tag ="WeatherActivity";
 private NowHwWeatherView  nowHwWeatherView;//天气视图
 private TextView tvCond_txt;//显示天气状况
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_weather);
        initView();
        setupToolbar("天气");
        getWeather("珠海");
    }

    private void initView() {
        nowHwWeatherView = findViewById(R.id.weatherView);
        tvCond_txt = findViewById(R.id.tvCond_txt);
    }

    private void getWeather(String location){
        RestClient.builder()
                .url("https://free-api.heweather.com/s6/weather")
                .params("key","ab016cfe68bb43fcacf71ac01e7ad063")
                .params("location",location)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(Tag,response);
                        Gson gson = new Gson();
                        JsonRootBean bean = gson.fromJson(response, JsonRootBean.class);
                        final Now now = bean.getHeWeather6().get(0).getNow();//获得当前的天气情况
                        Daily_forecast forecast = bean.getHeWeather6().get(0).getDaily_forecast().get(0);//获得当天的天气情况
//
                        final int current = Integer.parseInt(now.getTmp());
                        final int maxTemp = Integer.parseInt(forecast.getTmp_max());
                        final int minTemp = Integer.parseInt(forecast.getTmp_min());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nowHwWeatherView.setTemp(current,maxTemp,minTemp);
                                nowHwWeatherView.setBitmap(now.getCond_txt());
                                tvCond_txt.setText(now.getCond_txt());
                            }
                        });


                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .build()
                .get();
    }
}
