package jian.com.ximon.ad.ad;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jian.com.ximon.R;
import jian.com.ximon.ad.ad.bean.AD;
import jian.com.ximon.util.Constant;

/**
 * Created by ying on 17-11-16.
 */

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.MyViewHolder> {

    private final int IMAGE = 1;//图片
    private final int VIDEO = 2;//视频

    private List<AD> adList;
    private Context mContext;

    public AdAdapter(Context context, List<AD> adList) {
        this.adList = adList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == IMAGE) {

            //图片
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_image, parent, false);

        } else {
            //视频
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_video, parent, false);

        }

        return new MyViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final AD ad = adList.get(position);
        if (ad.getFields().getType() == IMAGE) {
            //图片
            Glide.with(mContext).load(Constant.BASH_PATH +ad.getFields().getUrl()).into(holder.imageView);
        } else {
            //视频
            Glide.with(mContext).load(Constant.BASH_PATH + ad.getFields().getUrl()).into(holder.imageVideo);
        }holder.name.setText(ad.getFields().getName());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //长摁
                EventBus.getDefault().post(new ADOnclickEvent(ad,position,ADOnclickEvent.LONGCLICK));
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ADOnclickEvent(ad,position,ADOnclickEvent.CLICK));

            }
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    @Override
    public int getItemViewType(int position) {
        final AD ad = adList.get(position);
        return ad.getFields().getType();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView imageVideo;
        private TextView name;

        public MyViewHolder(View itemView, int type) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            if (type == IMAGE) {
                //图片
                imageView = itemView.findViewById(R.id.image_ad);
            } else {
                //视频
                imageVideo = itemView.findViewById(R.id.image_video);
            }

        }
    }
}
