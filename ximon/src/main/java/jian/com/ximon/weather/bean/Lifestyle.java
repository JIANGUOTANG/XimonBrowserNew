/**
  * Copyright 2017 bejson.com 
  */
package jian.com.ximon.weather.bean;

/**
 *
 * 生活指数
 */
public class Lifestyle {

    private String brf;//生活指数简介
    private String txt;//生活指数详细描述
    private String type;//生活指数类型 comf：舒适度指数、cw：洗车指数、drsg：穿衣指数、flu：感冒指数、sport：运动指数、trav：旅游指数、uv：紫外线指数、air：空气污染扩散条件指数
    public void setBrf(String brf) {
         this.brf = brf;
     }
     public String getBrf() {
         return brf;
     }

    public void setTxt(String txt) {
         this.txt = txt;
     }
     public String getTxt() {
         return txt;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

}