package com.ljz.myproject.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ljz.myproject.R;
import com.ljz.myproject.entity.BlueTooth;
import java.util.List;

public class BlueToothAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BlueTooth> list;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BlueToothAdapter(Context context) {
        this.context = context;
    }

    public void setWifiData(List<BlueTooth> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BlueTooth.TAG_NORMAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_blue_tooth, parent, false);
            return new BlueToothHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_toast, parent, false);
            return new ToastHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getTag();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (list.get(position).getTag() == BlueTooth.TAG_NORMAL) {

            BlueToothHolder blueToothHolder = (BlueToothHolder) holder;
            BlueTooth result = list.get(position);

            blueToothHolder.getTvLevel().setText(result.getRssi());
            blueToothHolder.getTvName().setText(result.getName());
            blueToothHolder.getTvMac().setText(result.getMac());

            blueToothHolder.getRlClick().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(position);
                }
            });
        } else {
            ToastHolder blueToothHolder = (ToastHolder) holder;
            BlueTooth result = list.get(position);
            blueToothHolder.getTvToast().setText(result.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else return list.size();
    }

    public class BlueToothHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvLevel;
        private TextView tvMac;
        private RelativeLayout rlClick;

        private BlueToothHolder(View itemView) {
            super(itemView);
            tvMac = itemView.findViewById(R.id.tv_mac);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvName = itemView.findViewById(R.id.tv_name);
            rlClick = itemView.findViewById(R.id.rl_click);
        }

        private RelativeLayout getRlClick() {
            return rlClick;
        }

        public void setRlClick(RelativeLayout rlClick) {
            this.rlClick = rlClick;
        }

        private TextView getTvName() {
            return tvName;
        }

        public void setTvName(TextView tvName) {
            this.tvName = tvName;
        }

        private TextView getTvLevel() {
            return tvLevel;
        }

        public void setTvLevel(TextView tvLevel) {
            this.tvLevel = tvLevel;
        }

        private TextView getTvMac() {
            return tvMac;
        }

        public void setTvMac(TextView tvMac) {
            this.tvMac = tvMac;
        }
    }

    public class ToastHolder extends RecyclerView.ViewHolder{
        private TextView tvToast;
        private ToastHolder(View itemView) {
            super(itemView);
            tvToast = itemView.findViewById(R.id.tv_toast);
        }

        private TextView getTvToast() {
            return tvToast;
        }

        public void setTvToast(TextView tvToast) {
            this.tvToast = tvToast;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
