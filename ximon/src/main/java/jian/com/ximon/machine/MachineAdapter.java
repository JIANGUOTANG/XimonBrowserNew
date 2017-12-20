package jian.com.ximon.machine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jian.com.ximon.R;

/**
 * Created by ying on 17-11-14.
 */

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.MyViewHolder> {
    private List<Machine> machines;

    public MachineAdapter(List<Machine> machines) {
        this.machines = machines;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_machine,
                parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Machine machine = machines.get(position);
        holder.tvAddress.setText(machine.getFields().getAddress());
        holder.tvName.setText(machine.getFields().getAddress());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //长按
                EventBus.getDefault().post(new MachineOnclickEvent(machine,position,MachineOnclickEvent.LONG_CLICK));
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //单击
                EventBus.getDefault().post(new MachineOnclickEvent(machine,position,MachineOnclickEvent.CLICK));

            }
        });
    }

    @Override
    public int getItemCount() {
        return machines.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);

        }
    }
}
