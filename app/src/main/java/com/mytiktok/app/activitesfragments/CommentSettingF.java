package com.mytiktok.app.activitesfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mytiktok.app.databinding.FragmentCommentSettingBinding;
import com.mytiktok.app.interfaces.FragmentCallBack;
import com.mytiktok.app.models.CommentModel;
import com.mytiktok.app.R;
import com.mytiktok.app.simpleclasses.Functions;
import com.mytiktok.app.simpleclasses.Variables;


public class CommentSettingF extends BottomSheetDialogFragment implements View.OnClickListener{


    FragmentCommentSettingBinding binding;
    CommentModel item;
    FragmentCallBack callBack;

    public CommentSettingF(CommentModel item, FragmentCallBack callBack) {
        this.item=item;
        this.callBack=callBack;
    }

    public CommentSettingF() {
        //Required Empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_comment_setting, container, false);
        InitControl();
        return binding.getRoot();
    }

    private void InitControl() {
        Functions.hideSoftKeyboard(getActivity());
        binding.tvPinComment.setOnClickListener(this);
        binding.tvCopy.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);

        if (item.videoOwnerId.equals(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,"")))
        {

            if (item.comment_id.equals(item.pin_comment_id))
            {
                binding.tvPinComment.setText(binding.getRoot().getContext().getString(R.string.unpin_comment));
            }
            else
            {
                binding.tvPinComment.setText(binding.getRoot().getContext().getString(R.string.pin_comment));
            }
            binding.tvPinComment.setVisibility(View.VISIBLE);
            binding.tvDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.tvPinComment.setVisibility(View.GONE);
            if (item.userId.equals(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,"")))
            {
                binding.tvDelete.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.tvDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvPinComment:
            {
                performAction("pinComment");
            }
            break;
            case R.id.tvCopy:
            {
                performAction("copyText");
            }
            break;
            case R.id.tvDelete:
            {
                performAction("deleteComment");
            }
            break;
        }
    }

    private void performAction(String action) {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isShow",true);
        bundle.putString("action",action);
        callBack.onResponce(bundle);
        dismiss();
    }
}