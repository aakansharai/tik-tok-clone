package com.mytiktok.app.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mytiktok.app.models.FollowingModel;
import com.mytiktok.app.R;
import com.mytiktok.app.simpleclasses.Functions;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.CustomViewHolder> {

    public Context context;
    ArrayList<FollowingModel> datalist;
    boolean isSelf;
    String fromFrag;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, FollowingModel item);
    }

    public OnItemClickListener listener;

    public FollowingAdapter(Context context,boolean isSelf,String fromFrag, ArrayList<FollowingModel> arrayList, OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.isSelf=isSelf;
        this.listener = listener;
        this.fromFrag=fromFrag;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_following, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView userImage;
        TextView userName;
        TextView userId;
        TextView actionTxt;
        ImageView ivCross;
        RelativeLayout mainlayout;

        public CustomViewHolder(View view) {
            super(view);
            ivCross=view.findViewById(R.id.ivCross);
            userImage = view.findViewById(R.id.user_image);
            userName = view.findViewById(R.id.user_name);
            userId = view.findViewById(R.id.user_id);
            mainlayout=view.findViewById(R.id.mainlayout);
            actionTxt = view.findViewById(R.id.action_txt);
        }

        public void bind(final int pos, final FollowingModel item, final OnItemClickListener listener) {


            mainlayout.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            actionTxt.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            ivCross.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });



        }


    }



    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        FollowingModel item = datalist.get(i);

        holder.userName.setText(item.username);

        holder.userImage.setController(Functions.frescoImageLoad(item.getProfile_pic(),R.drawable.ic_user_icon,holder.userImage,false));

        if (isSelf)
        {
            if (fromFrag.equalsIgnoreCase("following"))
            {
                if (item.notificationType.equalsIgnoreCase("1"))
                {
                    holder.ivCross.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_live_notification));
                }
                else
                if (item.notificationType.equalsIgnoreCase("0"))
                {
                    holder.ivCross.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_mute_notification));
                }

            }else
            if (fromFrag.equalsIgnoreCase("fan"))
            {
                holder.ivCross.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_option));
            }else
            {
                holder.ivCross.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_cross));
            }

        }

        if (item.isFollow)
        {
            holder.ivCross.setVisibility(View.GONE);
        }
        else
        {
            holder.ivCross.setVisibility(View.VISIBLE);
        }


        holder.userId.setText(item.first_name + " " + item.last_name);
        holder.actionTxt.setText(item.follow_status_button);

        if (item.follow_status_button != null &&
                (item.follow_status_button.equalsIgnoreCase("follow") || item.follow_status_button.equalsIgnoreCase("follow back"))) {
            holder.actionTxt.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_background));
            holder.actionTxt.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));

        } else if (item.follow_status_button != null &&
                (item.follow_status_button.equalsIgnoreCase("following") || item.follow_status_button.equalsIgnoreCase("friends"))) {
            holder.actionTxt.setBackground(ContextCompat.getDrawable(context, R.drawable.d_gray_border));
            holder.actionTxt.setTextColor(ContextCompat.getColor(context, R.color.black));

        } else if (item.follow_status_button != null && item.follow_status_button.equalsIgnoreCase("0")) {
            holder.actionTxt.setVisibility(View.GONE);
        }


        holder.bind(i, datalist.get(i), listener);

    }


}