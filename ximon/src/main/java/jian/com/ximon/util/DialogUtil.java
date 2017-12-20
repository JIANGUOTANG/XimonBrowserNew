package jian.com.ximon.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import jian.com.ximon.R;

/**
 * Created by ying on 17-11-15.
 */

public class DialogUtil {

    private  final String TAG = "DialogUtil";

    private Activity context;



    AlertDialog dialog;

    public  void showEditDialog(Activity context, final int position, final DialogItemClickListener dialogItemClickListener) {
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialer_machine, null);
        LinearLayout linearLayoutEditor = dialogView.findViewById(R.id.linearLayoutEditor);
        LinearLayout linearLayoutDelete = dialogView.findViewById(R.id.linearLayoutDelete);
        linearLayoutEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击编辑
                Log.d(TAG, "编辑" + position);
                dialogItemClickListener.editor();
                dialog.dismiss();
            }
        });
        linearLayoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击编辑
                Log.d(TAG, "删除" + position);
                dialogItemClickListener.delete();
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("操作");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(dialogView);
        builder.setIcon(R.drawable.ic_editor);
        dialog = builder.show();
    }



    public interface DialogItemClickListener {
        void delete();

        void editor();
    }

    public interface SelectListener{
        void url();
        void file();
    }
    public void showAddDialog(Activity context, final SelectListener selectListener){

        LayoutInflater inflater = context.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_ad, null);
        LinearLayout linearLayoutEditor = dialogView.findViewById(R.id.linearLayoutFile);
        LinearLayout linearLayoutDelete = dialogView.findViewById(R.id.linearLayoutUrl);
        linearLayoutEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //通过文件上传
                selectListener.file();
                dialog.dismiss();

            }
        });
        linearLayoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //通过url上传
                selectListener.url();
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("上传");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(dialogView);
        builder.setIcon(R.drawable.ic_upload);
        dialog = builder.show();
    }
    public void showDialog(Activity context,String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);//标题
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setIcon(R.drawable.ic_upload);
        dialog = builder.show();
    }

}
