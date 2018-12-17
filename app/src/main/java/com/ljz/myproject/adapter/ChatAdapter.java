package com.ljz.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljz.myproject.R;
import com.ljz.myproject.entity.ChatInfo;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatInfo> list;
    private Context context;

    public List<ChatInfo> getList() {
        return list;
    }

    public void setList(List<ChatInfo> list) {
        this.list = list;
    }

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getTag();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatInfo.TAG_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_left, null);
            return new ChatLeftHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_right, null);
            return new ChatRightHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (list.get(position).getTag() == ChatInfo.TAG_LEFT) {
            ChatLeftHolder chatLeftHolder = (ChatLeftHolder) holder;
            chatLeftHolder.getTvName().setText(list.get(position).getName());
            chatLeftHolder.getTvContent().setText(list.get(position).getContent());
        } else {
            ChatRightHolder chatRightHolder = (ChatRightHolder) holder;
            chatRightHolder.getTvName().setText(list.get(position).getName());
            chatRightHolder.getTvContent().setText(list.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatLeftHolder extends RecyclerView.ViewHolder {

        private TextView tvContent;
        private TextView tvName;

        private ChatLeftHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_left);
            tvName = itemView.findViewById(R.id.tv_device);
        }

        private TextView getTvContent() {
            return tvContent;
        }

        public void setTvContent(TextView tvContent) {
            this.tvContent = tvContent;
        }

        private TextView getTvName() {
            return tvName;
        }

        public void setTvName(TextView tvName) {
            this.tvName = tvName;
        }
    }

    public class ChatRightHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;
        private TextView tvName;

        private ChatRightHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_right);
            tvName = itemView.findViewById(R.id.tv_device);
        }

        private TextView getTvContent() {
            return tvContent;
        }

        public void setTvContent(TextView tvContent) {
            this.tvContent = tvContent;
        }

        private TextView getTvName() {
            return tvName;
        }

        public void setTvName(TextView tvName) {
            this.tvName = tvName;
        }
    }
}
