package com.mytiktok.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mytiktok.app.R;
import com.mytiktok.app.databinding.ItemPromotionAudienceSelectionLayoutBinding;
import com.mytiktok.app.interfaces.AdapterClickListener;
import com.mytiktok.app.models.HomeModel;
import com.mytiktok.app.models.HomeSelectionModel;
import com.mytiktok.app.models.PromotionAudiencesModel;
import com.mytiktok.app.simpleclasses.Functions;

import java.util.ArrayList;

public class PromotionAudiencesAdapter extends RecyclerView.Adapter<PromotionAudiencesAdapter.CustomViewHolder> {

    public Context context;
    private ArrayList<PromotionAudiencesModel> dataList;

    AdapterClickListener adapterClickListener;

    public PromotionAudiencesAdapter(Context context, ArrayList<PromotionAudiencesModel> dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.adapterClickListener = adapterClickListener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        ItemPromotionAudienceSelectionLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),R.layout.item_promotion_audience_selection_layout, viewGroup,false);
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        PromotionAudiencesModel item=dataList.get(i);
        if (item.isSelected())
        {
            holder.binding.ivSelection.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(),R.drawable.ic_circle_selection));
        }
        else
        {
            holder.binding.ivSelection.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(),R.drawable.ic_un_selected));
        }

        holder.binding.tvTitle.setText(""+item.getName());
        holder.bind(i, item, adapterClickListener);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        ItemPromotionAudienceSelectionLayoutBinding binding;

        public CustomViewHolder(ItemPromotionAudienceSelectionLayoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(final int position, final PromotionAudiencesModel item, final AdapterClickListener listener) {
            binding.tabSelection.setOnClickListener(v -> {
                adapterClickListener.onItemClick(v, position, item);

            });
        }

    }


}
