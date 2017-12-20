/**
  * Copyright 2017 bejson.com 
  */
package jian.com.ximon.weather.bean;
import java.util.List;

/**
 * Auto-generated: 2017-12-06 13:49:52
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class HeWeather6 {

    private Basic basic;
    private List<Daily_forecast> daily_forecast;
    private List<Hourly> hourly;
    private List<Lifestyle> lifestyle;
    private Now now;
    private String status;
    public void setBasic(Basic basic) {
         this.basic = basic;
     }
     public Basic getBasic() {
         return basic;
     }

    public void setDaily_forecast(List<Daily_forecast> daily_forecast) {
         this.daily_forecast = daily_forecast;
     }
     public List<Daily_forecast> getDaily_forecast() {
         return daily_forecast;
     }

    public void setHourly(List<Hourly> hourly) {
         this.hourly = hourly;
     }
     public List<Hourly> getHourly() {
         return hourly;
     }

    public void setLifestyle(List<Lifestyle> lifestyle) {
         this.lifestyle = lifestyle;
     }
     public List<Lifestyle> getLifestyle() {
         return lifestyle;
     }

    public void setNow(Now now) {
         this.now = now;
     }
     public Now getNow() {
         return now;
     }

    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }


}