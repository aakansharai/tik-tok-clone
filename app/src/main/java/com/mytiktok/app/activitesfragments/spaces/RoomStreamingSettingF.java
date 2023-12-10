package com.mytiktok.app.activitesfragments.spaces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mytiktok.app.R;
import com.mytiktok.app.activitesfragments.spaces.models.HomeUserModel;
import com.mytiktok.app.databinding.FragmentRoomStreamingSettingBinding;
import com.mytiktok.app.interfaces.FragmentCallBack;
import com.mytiktok.app.simpleclasses.Functions;
import com.mytiktok.app.simpleclasses.Variables;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;

import java.util.ArrayList;


public class RoomStreamingSettingF extends BottomSheetDialogFragment implements View.OnClickListener{


    FragmentRoomStreamingSettingBinding binding;
    FragmentCallBack callBack;
    ArrayList<HomeUserModel> currentUserList;
    HomeUserModel myUserModel;

    public RoomStreamingSettingF(ArrayList<HomeUserModel> currentUserList, FragmentCallBack callBack) {
        this.currentUserList =currentUserList;
        this.callBack = callBack;
    }

    public RoomStreamingSettingF() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_room_streaming_setting, container, false);
        InitControl();
        return binding.getRoot();
    }

    private void InitControl() {

        binding.tvShareRoom.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tvShareRoom);
        binding.tvEndRoom.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tvEndRoom);
        binding.tvUserShareRoom.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tvUserShareRoom);
        binding.tvUserReportRoomTitle.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tvUserReportRoomTitle);

        setupSctreenData();
    }

    private void setupSctreenData() {
        setupButtonLogic();

    }

    private void setupButtonLogic() {
        for(HomeUserModel myModel:currentUserList)
        {
            if (myModel.getUserModel().getId().equals(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")))
            {
                myUserModel=myModel;
            }
        }


        //moderator
        if (myUserModel!=null && myUserModel.getUserRoleType().equals("1"))
        {
            binding.tabOwner.setVisibility(View.VISIBLE);
            binding.tabOther.setVisibility(View.GONE);
        }
        else if (myUserModel!=null && myUserModel.getUserRoleType().equals("2"))
            {
                binding.tabOwner.setVisibility(View.GONE);
                binding.tabOther.setVisibility(View.VISIBLE);
            }
            else
            //user
            {
                binding.tabOwner.setVisibility(View.GONE);
                binding.tabOther.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvShareRoom:
            {
                perFormAction("ShareRoom");
            }
            break;
            case R.id.tvEndRoom:
            {
                perFormAction("EndRoom");
            }
            break;
            case R.id.tvUserShareRoom:
            {
                perFormAction("UserShareRoom");
            }
            break;

            case R.id.tvUserReportRoomTitle:
            {
                perFormAction("UserReportRoomTitle");
            }
            break;
        }
    }

    private void perFormAction(String action) {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isShow",true);
        bundle.putString("action",action);
        callBack.onResponce(bundle);
        dismiss();
    }
}