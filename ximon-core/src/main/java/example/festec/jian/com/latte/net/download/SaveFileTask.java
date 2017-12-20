package example.festec.jian.com.latte.net.download;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.InputStream;

import example.festec.jian.com.latte.app.Latte;
import example.festec.jian.com.latte.net.callback.IRequest;
import example.festec.jian.com.latte.net.callback.ISuccess;
import example.festec.jian.com.latte.util.file.FileUtil;
import okhttp3.ResponseBody;

/**
 * Created by ying on 17-8-20.
 */

public class SaveFileTask extends AsyncTask<Object, Void, File> {
    private final IRequest REQUEST;
    private final ISuccess ISUCCESS;

    public SaveFileTask(IRequest REQUEST, ISuccess ISUCCESS) {
        this.REQUEST = REQUEST;
        this.ISUCCESS = ISUCCESS;
    }

    @Override
    protected File doInBackground(Object... objects) {
        String downloadDir = (String) objects[0];
        String extension = (String) objects[1];
        final ResponseBody body = (ResponseBody) objects[2];
        final String name = (String) objects[3];

        //获得输入流
        final InputStream is = body.byteStream();
        if (downloadDir == null || downloadDir.equals("")) {
            downloadDir = "down_loads";

        }
        if (extension == null || extension.equals("")) {
            extension = "";
        }
          if(name==null){
              //把后缀改成大写来命名
              FileUtil.writeToDisk(is,downloadDir,extension.toLowerCase(),extension);
          }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if(ISUCCESS!=null){
            ISUCCESS.onSuccess(file.getPath());

        }
        if(REQUEST!=null){
            REQUEST.onRequestEnd();
        }
        autoInstallApk(file);
    }

    /**
     * apk自动安装
     */
    private void autoInstallApk(File file){
        //getExten.sipon
        if(FileUtil.getExtension(file.getPath()).equals("apk"))
        {
             final Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            Latte.getApplicationContext().startActivity(intent);
        }
    }
}
