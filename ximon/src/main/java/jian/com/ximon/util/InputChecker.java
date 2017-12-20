package jian.com.ximon.util;

import android.widget.EditText;

/**
 * Created by ying on 17-11-15.
 */

public class InputChecker {
    /**
     * 检查输入是否正确
     * @param editTexts
     * @return
     */
   public static boolean isCorrect(EditText... editTexts){
        boolean  isCorrect=true;
        for(EditText editText:editTexts){
            if (editText.getText().toString().length()<=0){
                isCorrect =false;
            }

        }
       return isCorrect;
   }

}

