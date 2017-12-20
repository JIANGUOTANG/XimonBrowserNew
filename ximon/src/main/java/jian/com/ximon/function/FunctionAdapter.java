package jian.com.ximon.function;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jian.com.ximon.R;


/**
 * Created by ying on 17-10-9.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    private List<Function> functions;
    private Context context;

    public FunctionAdapter(List<Function> functions, Context context) {
        this.functions = functions;
        this.context = context;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_funtion, parent, false);
        return new FunctionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FunctionViewHolder holder, final int position) {
        final Function function = functions.get(position);
        AppCompatImageView imageView = holder.imageView;
        imageView.setImageResource(function.getIdSvgIcon());
        holder.textView.setText(function.getText());
        if (function.getColor()!=0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, function.getColor()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFunctionClick != null) {
                    onFunctionClick.onClick(function, position);
                }
            }
        });

    }

    private onFunctionClick onFunctionClick;

    public void setOnFunctionClick(FunctionAdapter.onFunctionClick onFunctionClick) {
        this.onFunctionClick = onFunctionClick;
    }

    public interface onFunctionClick {
        void onClick(Function function, int position);
    }

    @Override
    public int getItemCount() {
        return functions.size();
    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private AppCompatImageView imageView;

        public FunctionViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvFunction);
            imageView = itemView.findViewById(R.id.imgFunction);
        }
    }
}
