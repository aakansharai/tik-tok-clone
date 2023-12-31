package com.mytiktok.app.activitesfragments.livestreaming.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mytiktok.app.Constants;
import com.mytiktok.app.R;
import com.mytiktok.app.activitesfragments.EditTextSheetF;
import com.mytiktok.app.activitesfragments.livestreaming.activities.LiveUsersA;
import com.mytiktok.app.activitesfragments.livestreaming.activities.MultiViewLiveA;
import com.mytiktok.app.activitesfragments.livestreaming.adapter.LiveCommentsAdapter;
import com.mytiktok.app.activitesfragments.livestreaming.model.CameraRequestModel;
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveCoinsModel;
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveCommentModel;
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveUserModel;
import com.mytiktok.app.activitesfragments.profile.analytics.DateOperations;
import com.mytiktok.app.activitesfragments.sendgift.StickerGiftF;
import com.mytiktok.app.activitesfragments.sendgift.StickerModel;
import com.mytiktok.app.apiclasses.ApiLinks;
import com.mytiktok.app.interfaces.AdapterClickListener;
import com.mytiktok.app.interfaces.FragmentCallBack;
import com.mytiktok.app.models.StreamJoinModel;
import com.mytiktok.app.models.StreamShowHeartModel;
import com.mytiktok.app.models.UserModel;
import com.mytiktok.app.models.UserOnlineModel;
import com.mytiktok.app.models.UsersModel;
import com.mytiktok.app.simpleclasses.DataParsing;
import com.mytiktok.app.simpleclasses.Functions;
import com.mytiktok.app.simpleclasses.OnSwipeTouchListener;
import com.mytiktok.app.simpleclasses.Variables;
import com.mytiktok.app.simpleclasses.streaminglikes.HeartView;
import com.chaos.view.PinView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MultipleStreamerListF extends Fragment implements View.OnClickListener{

    View view;
    Context context;
    LiveUserModel item;
    MultiViewLiveA activity;
    DatabaseReference rootref;
    FrameLayout tabStreamView,tabOfflineView;
    RelativeLayout tabLockStream;
    ViewFlipper viewflliper;
    RelativeLayout viewOne,viewTwo;
    SimpleDraweeView ivGiftCount;
    TextView tvGiftCount;
    View animationCapture;
    LinearLayout tabGiftCount;
    HeartView streamLikeView;
    boolean isLikeStream=true;

    View tabMainView;

    TextView liveUserCount, tvCurrentJoin;
    TextView tvUserName;
    SimpleDraweeView ivMainProfile;
    public SimpleDraweeView ivSender;
    public TextView tvCoinCount,tvSender;
    public RelativeLayout tabCoinSender;


    RelativeLayout tabStartLive;
    PinView pinView;
    TextView tvLockUserName,btnfollow;
    SimpleDraweeView ivProfile,ivSmallProfile;

    boolean isFirstTimeFlip=true;
    boolean isSendHeart=true;
    LinearLayout tabLikeStreaming,tabInviteAll,tabGift,tabCoHost;
    ImageView ivVerified;
    public TextView tvMessage;

    int userId;

    public MultipleStreamerListF(LiveUserModel item, MultiViewLiveA activity) {
        this.item = item;
        this.activity = activity;
    }

    public MultipleStreamerListF() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_multiple_streamer_list, container, false);

        userId=Functions.parseInterger(Functions.getSharedPreference(context).getString(Variables.U_ID,""));

        return view;
    }

    private void ActionControl() {

        final Animation inAnim = AnimationUtils.loadAnimation(context, R.anim.in_from_right);
        final Animation outAnim = AnimationUtils.loadAnimation(context, R.anim.out_to_left);
        final Animation inPrevAnim = AnimationUtils.loadAnimation(context, R.anim.in_from_left);
        final Animation outPrevAnim = AnimationUtils.loadAnimation(context, R.anim.out_to_right);


        tabMainView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

                viewflliper.setInAnimation(inPrevAnim);
                viewflliper.setOutAnimation(outPrevAnim);
                Log.d(Constants.tag,"start");

                if (viewTwo==viewflliper.getCurrentView())
                {

                        viewflliper.showPrevious();
                }
                else
                {
                    viewflliper.showPrevious();
                }

            }
            public void onSwipeLeft() {
                viewflliper.setInAnimation(inAnim);
                viewflliper.setOutAnimation(outAnim);
                Log.d(Constants.tag,"end");

                    viewflliper.showNext();



            }
            public void onSwipeBottom() {

            }

            @Override
            public void onDoubleClick(MotionEvent e) {
                if (isSendHeart)
                {
                    isSendHeart=true;
                    addLikeIntoStream();
                }
            }

            public void onSingleClick(){

            }
        });

        if (isFirstTimeFlip)
        {
            isFirstTimeFlip=false;
            if (viewOne==viewflliper.getCurrentView())
            {
                viewflliper.showNext();
            }
        }

        tabGift.setOnClickListener(this);
        tabInviteAll.setOnClickListener(this);
        tabCoHost.setOnClickListener(this);

        view.findViewById(R.id.tabLiveUser).setOnClickListener(this);


        addJoinedMessage();

    }

    Handler joinHandler;
    Runnable joinRunnable;
    public void addJoinedMessage(){

        removejoinCallback();

        joinHandler=new Handler();
        joinRunnable=new Runnable() {
            @Override
            public void run() {
                if(!isJoinded) {
                    addMessages("joined", tvMessage.getText().toString());
                }
            }
        };
        joinHandler.postDelayed(joinRunnable,3000);


    }

    public void removejoinCallback(){
        if(joinHandler!=null && joinRunnable!=null){
            joinHandler.removeCallbacks(joinRunnable);
        }
    }



    ArrayList<UsersModel> taggedUserList = new ArrayList<>();
    private void sendComment() {
        EditTextSheetF fragment = new EditTextSheetF("OwnComment",taggedUserList, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    if (bundle.getString("action").equals("sendComment"))
                    {
                        taggedUserList= (ArrayList<UsersModel>) bundle.getSerializable("taggedUserList");
                        String message=bundle.getString("message");
                        tvMessage.setText(message);
                        addMessages("comment",tvMessage.getText().toString());
                    }
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("replyStr","");
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "EditTextSheetF");
    }

    private void addLikeIntoStream() {
        StreamShowHeartModel likeData=new StreamShowHeartModel();
        likeData.setUserId(""+ Functions.getSharedPreference(context).getString(Variables.U_ID,""));
        likeData.setOtherUserId(""+item.getUserId());
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("LikesStream").push().setValue(likeData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                {
                    isSendHeart=true;
                }
            }
        });
    }

    private void InitControl() {
        context=view.getContext();
        rootref = FirebaseDatabase.getInstance().getReference();
        tabStreamView=view.findViewById(R.id.tabStreamView);
        tabMainView=view.findViewById(R.id.tabMainView);
        tabOfflineView=view.findViewById(R.id.tabOfflineView);
        tabLockStream=view.findViewById(R.id.tabLockStream);
        tvGiftCount=view.findViewById(R.id.tvGiftCount);
        ivGiftCount=view.findViewById(R.id.ivGiftCount);
        tabGiftCount=view.findViewById(R.id.tabGiftCount);
        animationCapture=view.findViewById(R.id.animationCapture);
        streamLikeView=view.findViewById(R.id.streamLikeView);

        btnfollow=view.findViewById(R.id.btnfollow);
        btnfollow.setOnClickListener(this);
        tvCurrentJoin=view.findViewById(R.id.tvCurrentJoin);
        liveUserCount=view.findViewById(R.id.liveUserCount);
        tabGift =view.findViewById(R.id.tabGift);
        tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setOnClickListener(this);
        tvUserName = view.findViewById(R.id.tvMainUserName);
        ivMainProfile=view.findViewById(R.id.ivMainProfile);
        tabLikeStreaming=view.findViewById(R.id.tabLikeStreaming);
        tabLikeStreaming.setOnClickListener(this);
        tvCoinCount=view.findViewById(R.id.tvCoinCount);
        tvSender=view.findViewById(R.id.tvSender);
        tabCoinSender=view.findViewById(R.id.tabCoinSender);
        ivSender=view.findViewById(R.id.ivSender);
        ivVerified=view.findViewById(R.id.ivVerified);


        view.findViewById(R.id.cross_btn).setOnClickListener(this);
        view.findViewById(R.id.cross_btn2).setOnClickListener(this);

        viewflliper=view.findViewById(R.id.viewflliper);

        viewOne=view.findViewById(R.id.viewOne);
        viewTwo=view.findViewById(R.id.viewTwo);
        tabCoHost=view.findViewById(R.id.tabCoHost);
        tabInviteAll=view.findViewById(R.id.tabInviteAll);

        setUpSecureCodeScreen();
        initCommentAdapter();


        checkUserStatus();
    }

    private void setUpSecureCodeScreen() {
        pinView=view.findViewById(R.id.pinView);
        tabStartLive=view.findViewById(R.id.tabStartLive);
        tabStartLive.setOnClickListener(this);
        ivProfile=view.findViewById(R.id.ivProfile);
        tvLockUserName=view.findViewById(R.id.tvUserName);
        ivSmallProfile=view.findViewById(R.id.ivSmallProfile);
    }


    ChildEventListener commentChildListener;
    Calendar current_cal;
    boolean isJoinded;
    public void ListCommentData() {
        current_cal = Calendar.getInstance();
        if(commentChildListener ==null)
        {

            LiveCommentModel model=new LiveCommentModel();
            model.setType("alert");
            model.setComment(view.getContext().getString(R.string.streaming_welcome_txt));
            dataList.add(model);
            adapter.notifyDataSetChanged();

            commentChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    LiveCommentModel model = dataSnapshot.getValue(LiveCommentModel.class);


                    if(isJoinded || (model.getType().equals("joined") &&
                            model.userId.equals(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")))){

                        isJoinded=true;
                        dataList.add(model);

                        if(model.getType().equalsIgnoreCase("gift"))
                        {
                            if (Functions.checkTimeDiffernce(current_cal,model.getCommentTime())) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ShowGiftAnimation(model);
                                    }
                                });
                            }
                        }

                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(dataList.size() - 1);

                    }

                    if(model.getType().equals("selfInviteForStream") && model.userId.equals(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""))){
                        cameraRequestTime=model.getCommentTime();
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").addChildEventListener(commentChildListener);
        }
    }

    public void removeCommentListener() {
        if (rootref!=null && commentChildListener != null) {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").removeEventListener(commentChildListener);
            commentChildListener =null;
        }
    }



    // send the comment to the live user
    public void addLiveStreamingShareMessage(String type) {

        final String key = rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(context).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(context).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment("");
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").child(key).setValue(commentItem);


        CameraRequestModel model=new CameraRequestModel();
        model.setRequestState("1");
        rootref.child("LiveStreamingUsers").child(item.getStreamingId())
                .child("CameraRequest")
                .child(Functions.getSharedPreference(context).getString(Variables.U_ID,"0"))
                .setValue(model);
        
    }


    ValueEventListener  cameraRequestEventListener;
    boolean isCameraConnect=false;
    private void addNodeCameraRequest() {
        if(cameraRequestEventListener==null)
        {

            cameraRequestEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        CameraRequestModel model=snapshot.getValue(CameraRequestModel.class);
                        if (model.getRequestState().equals("2"))
                        {
                            Toast.makeText(context, context.getString(R.string.camera_request_granted), Toast.LENGTH_SHORT).show();
                            isCameraConnect=true;
                        }
                        else
                        if(model.getRequestState().equals("1"))
                        {
                            isCameraConnect=false;
                        }
                        else
                        {
                            isCameraConnect=false;
                            stopBroadcast(activity.userRole);
                            Toast.makeText(context, context.getString(R.string.camera_request_rejected), Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    isCameraConnect=false;
                }
            };
            rootref.child("LiveStreamingUsers").child(item.getStreamingId())
                    .child("CameraRequest")
                    .child(Functions.getSharedPreference(context).getString(Variables.U_ID,"0"))
                    .addValueEventListener(cameraRequestEventListener);

        }
    }

    private void removeNodeCameraRequest() {
        if (rootref!=null && cameraRequestEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId())
                    .child("CameraRequest")
                    .child(Functions.getSharedPreference(context).getString(Variables.U_ID,"0"))
                    .removeEventListener(cameraRequestEventListener);

            cameraRequestEventListener =null;
        }
    }



    // initailze the adapter
    ArrayList<LiveCommentModel> dataList=new ArrayList<>();
    RecyclerView recyclerView;
    LiveCommentsAdapter adapter;
    public void initCommentAdapter() {
        dataList.clear();

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        adapter = new LiveCommentsAdapter(view.getContext(), dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                LiveCommentModel itemUpdate=dataList.get(pos);
                if (itemUpdate.getType().equals("shareStream"))
                {
                    inviteFriendsForStream();
                }

            }
        });

        recyclerView.setAdapter(adapter);

    }


    boolean previousIsVideoEnable=true;
    private void setUpScreenData() {
        tvCoinCount.setText(""+Functions.changeValueToInt(item.getUserCoins()));
        tvUserName.setText(item.getUserName());
        ivMainProfile.setController(Functions.frescoImageLoad(item.getUserPicture(),ivMainProfile,false));

        if (item.getIsVerified().equals("1"))
        {
            ivVerified.setVisibility(View.VISIBLE);
        }
        else
        {
            ivVerified.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(item.getSecureCode()))
        {
            tabStreamView.setVisibility(View.VISIBLE);
            tabLockStream.setVisibility(View.GONE);
//            connectStream
            Log.d(Constants.tag,"Stream: emptySecure");
            activity.refreshStreamingConnection(item.getStreamingId());
        }
        else
        {
            if (LiveUsersA.unlockStream.containsKey(item.getUserId()))
            {
                tabStreamView.setVisibility(View.VISIBLE);
                tabLockStream.setVisibility(View.GONE);
//                connectStream
                Log.d(Constants.tag,"Stream: secure");
                activity.refreshStreamingConnection(item.getStreamingId());
            }
            else
            {
                tabStreamView.setVisibility(View.GONE);
                tabLockStream.setVisibility(View.VISIBLE);
                setupLockScreenData();
            }

        }

        if(item.getUserId().equalsIgnoreCase(Variables.sharedPreferences.getString(Variables.U_ID,"")))
        {
            tabGift.setVisibility(View.GONE);
        }
        else {
            tabGift.setVisibility(View.VISIBLE);
        }

        if (item.isDualStreaming())
        {

            if (item.isStreamJoinAllow())
            {
                tabCoHost.setVisibility(View.VISIBLE);
                addNodeCameraRequest();
            }
            else
            {
                tabCoHost.setVisibility(View.GONE);
                removeNodeCameraRequest();
            }
        }
        else
        {
            tabCoHost.setVisibility(View.GONE);
        }


         Functions.printLog(Constants.tag,"item.isVideoEnable():"+item.isVideoEnable() + " previousIsVideoEnable:"+previousIsVideoEnable);
        if(activity.mVideoGridContainer!=null) {

            if (item.isVideoEnable() && !previousIsVideoEnable) {
                SurfaceView surface = activity.prepareRtcVideo(Functions.parseInterger(item.getUserId()));
                activity.mVideoGridContainer.addUserVideoSurface(Functions.parseInterger(item.getUserId()), surface);
                previousIsVideoEnable=true;
            }

            else if(!item.isVideoEnable() && previousIsVideoEnable) {
                activity.removeRtcVideo(Functions.parseInterger(item.getUserId()));
                activity.mVideoGridContainer.removeUserVideo(Functions.parseInterger(item.getUserId()));
                previousIsVideoEnable=false;

            }

        }


    }




    private void setupLockScreenData() {
        tvLockUserName.setText(item.getUserName());
        ivProfile.setController(Functions.frescoBlurImageLoad(item.getUserPicture(), view.getContext(),75));
        ivSmallProfile.setController(Functions.frescoImageLoad(item.getUserPicture(),ivSmallProfile,false));
    }



    public void openViewrsList() {
        ViewersListF viewersListF =ViewersListF.newInstance(item.streamingId, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {

            }
        });
        viewersListF.show(getChildFragmentManager(), "");
    }



    ValueEventListener  coinValueEventListener;
    ArrayList<LiveCoinsModel> senderCoinsList=new ArrayList<>();
    private void ListenerCoinNode() {
        if(coinValueEventListener==null)
        {
            coinValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    senderCoinsList.clear();
                    if (dataSnapshot.exists())
                    {
                        for(DataSnapshot joinSnapsot:dataSnapshot.getChildren())
                        {
                            if (!(TextUtils.isEmpty(joinSnapsot.getValue().toString())))
                            {
                                LiveCoinsModel model=joinSnapsot.getValue(LiveCoinsModel.class);
                                senderCoinsList.add(model);
                            }
                        }
                        double maxCoins=0;
                        LiveCoinsModel highCoinSender = null;
                        if (senderCoinsList.size()>0)
                        {
                            tabCoinSender.setVisibility(View.VISIBLE);
                            maxCoins=Double.valueOf(senderCoinsList.get(0).getSendedCoins());
                            highCoinSender=senderCoinsList.get(0);
                        }
                        else
                        {
                            tabCoinSender.setVisibility(View.GONE);
                        }
                        for (LiveCoinsModel item:senderCoinsList)
                        {
                            if (Double.valueOf(item.getSendedCoins())>maxCoins)
                            {
                                maxCoins=Double.valueOf(item.getSendedCoins());
                                highCoinSender=item;
                            }
                        }
                        if (highCoinSender!=null)
                        {
                            tvSender.setText(highCoinSender.getUserName());
                            ivSender.setController(Functions.frescoImageLoad(highCoinSender.getUserPic(),ivSender,false));
                        }


                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").addValueEventListener(coinValueEventListener);

        }
    }

    public void removeCoinListener() {
        if (rootref!=null && coinValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").removeEventListener(coinValueEventListener);
            coinValueEventListener=null;
        }
    }

    private void AddJoinNode() {
        StreamJoinModel model=new StreamJoinModel();
        model.setUserId(Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
        model.setUserName(Functions.getSharedPreference(context).getString(Variables.U_NAME, ""));
        model.setUserPic(Functions.getSharedPreference(context).getString(Variables.U_PIC, ""));

        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("JoinStream")
                .child(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))
                .setValue(model);
    }



    private void startBroadcast(int role) {
        Log.d(Constants.tag,"Stream: startBroadcast as "+role);
        Log.d(Constants.tag,"Stream: startBroadcast with compare "+activity.userRole);
        Functions.printLog(Constants.tag,"userId"+userId);

        SurfaceView surface = activity.startBroadcast(item.getStreamingId(),role);
        activity.mVideoGridContainer.addUserVideoSurface(userId, surface);

    }


    private void stopBroadcast(int role) {
        Log.d(Constants.tag,"Stream: stopBroadcast as "+role);
        Log.d(Constants.tag,"Stream: stopBroadcast with compare "+activity.userRole);
        Functions.printLog(Constants.tag,"userId"+userId);
        activity.stopBroadcast(role);
        activity.mVideoGridContainer.removeUserVideo(userId);
    }



    // when user goes to offline then change the value status on firebase
    public void removeJoinNode() {
        if(rootref!=null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId())
                    .child("CameraRequest")
                     .child(Functions.getSharedPreference(context).getString(Variables.U_ID,"0")).removeValue();

            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("duetConnectedUserId").setValue("");

            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("JoinStream")
                    .child(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))
                    .removeValue();

        }
    }








    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cross_btn:
            {
                getActivity().onBackPressed();
            }
            break;

            case R.id.cross_btn2:
            {
                getActivity().onBackPressed();
            }
            break;

            case R.id.tabLikeStreaming:
            {
                if (isSendHeart)
                {
                    isSendHeart=true;
                    addLikeIntoStream();
                }
            }
            break;
            case R.id.tabGift:
            {
                ShowGiftSheet();
            }
            break;
            case R.id.tabInviteAll:
            {
                inviteFriendsForStream();
            }
            break;
            case R.id.tabCoHost:
            {

                if (isCameraConnect)
                {
                    ShowDailogForJoinBroadcast();
                }
                else
                {
                    sendCameraRequest();
                }

            }
            break;
            case R.id.tabStartLive:
            {
                if (item.getSecureCode().equals(pinView.getText().toString()))
                {
                    LiveUsersA.unlockStream.put(item.getUserId(),pinView.getText().toString());
                    tabStreamView.setVisibility(View.VISIBLE);
                    tabLockStream.setVisibility(View.GONE);
//                    connectStream
                    Log.d(Constants.tag,"Stream: breakeSecure");
                    activity.refreshStreamingConnection(item.getStreamingId());
                }

            }
            break;
            case R.id.tvMessage:
            {
                sendComment();
            }
            break;

            case R.id.btnfollow:
            {
                if (Functions.checkLoginUser(getActivity()))
                    followUnFollowUser();
            }
            break;

            case R.id.tabLiveUser:
               // openViewrsList();
                break;
        }
    }

    private void followUnFollowUser() {
        Functions.callApiForFollowUnFollow(getActivity(),
                Functions.getSharedPreference(context).getString(Variables.U_ID, ""),
                item.getUserId(),
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {

                        callApiForGetAllvideos(item.getUserId(),item.getUserName());
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

    }

    // get the profile details of user
    boolean isRunFirstTime = false;

    private void callApiForGetAllvideos(String userId,String userName) {

        JSONObject parameters = new JSONObject();
        try {

            if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false))
            {
                if (userId!=null && userName!=null)
                {
                    if (userId.equals(Functions.getSharedPreference(context).getString(Variables.U_ID, "")))
                    {
                        parameters.put("user_id", userId);
                    }
                    else
                    {
                        parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                        parameters.put("other_user_id", userId);
                    }

                }
                else
                {
                    parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                    parameters.put("username", userName);
                }
            }
            else
            {
                if (userId!=null && userName!=null)
                {
                    parameters.put("user_id", userId);
                }
                else
                {
                    parameters.put("username", userName);
                }
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }


        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showUserDetail, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                isRunFirstTime = true;
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msg = jsonObject.optJSONObject("msg");

                        UserModel userDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));
                        String follow_status = userDetailModel.getButton().toLowerCase();
                        if (!userDetailModel.getId().
                                equals(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))) {

                            if (follow_status.equalsIgnoreCase("following")) {
                                btnfollow.setVisibility(View.GONE);
                            } else if (follow_status.equalsIgnoreCase("friends")) {
                                btnfollow.setVisibility(View.GONE);
                            }
                            else if (follow_status.equalsIgnoreCase("follow back")){

                                btnfollow.setVisibility(View.VISIBLE);
                                btnfollow.setText(getString(R.string.follow_back));
                            }
                            else {
                                btnfollow.setVisibility(View.VISIBLE);
                                btnfollow.setText(getString(R.string.follow));
                            }


                        }


                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }


    String cameraRequestTime;
    private void sendCameraRequest() {
        if(cameraRequestTime!=null) {
            Long difference = DateOperations.INSTANCE.getTimeDifference(cameraRequestTime, DateOperations.INSTANCE.getTimeZoneFormat());

            if (difference > (5 * 60 * 1000)) {
                addLiveStreamingShareMessage("selfInviteForStream");
            } else {
                Toast.makeText(context, context.getString(R.string.you_can_send_join_request_after) + " " + Functions.getTimeWithAdditionalSecond("mm:ss", (int) (((5 * 60 * 1000) -difference) / 1000)), Toast.LENGTH_SHORT).show();

            }
        }else {
            addLiveStreamingShareMessage("selfInviteForStream");
        }

    }


    private void inviteFriendsForStream() {
        InviteContactsToStreamF f = new InviteContactsToStreamF(item.getStreamingId(),"multiple",new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {

                }
            }
        });
        f.show(getChildFragmentManager(), "InviteContactsToStreamF");
    }


    public void heartsShow()
    {
        streamLikeView.addHeart(new Random().nextInt(5));
    }


    boolean isAudioActivated=false,isVideoActivated=false,isbeautyActivated=false;
    public void ShowDailogForJoinBroadcast() {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.live_join_broadcast_view);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        ImageView swith_camera_btn=alertDialog.findViewById(R.id.swith_camera_btn);
        ImageView live_btn_mute_audio=alertDialog.findViewById(R.id.live_btn_mute_audio);
        ImageView live_btn_beautification=alertDialog.findViewById(R.id.live_btn_beautification);
        ImageView live_btn_mute_video=alertDialog.findViewById(R.id.live_btn_mute_video);
        RelativeLayout tab_cancel = alertDialog.findViewById(R.id.tab_cancel);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);
        LinearLayout tabClient=alertDialog.findViewById(R.id.tabClient);
        LinearLayout tabSwitch=alertDialog.findViewById(R.id.tabSwitch);

        if (!item.isDualStreaming())
        {
            if (!(item.getOnlineType().equals("oneTwoOne")))
            {
                tabClient.setVisibility(View.GONE);
                tabSwitch.setVisibility(View.GONE);
            }
        }


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        live_btn_mute_audio.setActivated(isAudioActivated);
        live_btn_mute_video.setActivated(isVideoActivated);
        live_btn_beautification.setActivated(isbeautyActivated);

        activity.setBeautyEffectOptions(isbeautyActivated);

        tab_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                getActivity().onBackPressed();
            }
        });
        swith_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                activity.switchCamera();
            }
        });
        live_btn_mute_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isAudioActivated=!isAudioActivated;
                activity.muteLocalAudioStream(!isAudioActivated);


            }
        });
        live_btn_beautification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isbeautyActivated=!isbeautyActivated;
                activity.setBeautyEffectOptions(isbeautyActivated);
            }
        });
        live_btn_mute_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isVideoActivated = !isVideoActivated;
                if (isVideoActivated) {
                    activity.userRole=io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;
                    startBroadcast(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
                } else {

                    activity.userRole=io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE;
                    stopBroadcast(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
                }
                Log.d(Constants.tag,"activity.userRole: "+activity.userRole);
            }
        });
        alertDialog.show();
    }




    public void ShowGiftSheet() {
        StickerGiftF giftFragment = new StickerGiftF(item.getUserId(), item.getUserName(), item.getUserPicture(), new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    StickerModel model= (StickerModel) bundle.getSerializable("Data");
                    String counter=bundle.getString("count");
                    addGiftComment("gift",counter,model);
                    Log.d(Constants.tag,"Test : "+item.getUserCoins());

                    if(item!=null)
                    {
                        double userCoins=Double.valueOf(item.getUserCoins());
                        double userGift=(Long.valueOf(counter)*Double.valueOf(model.coins));
                        userGift=userCoins+userGift;
                        HashMap map = new HashMap();
                        map.put("userCoins",""+userGift);
                        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).updateChildren(map);
                    }

                }
            }
        });
        giftFragment.show(getChildFragmentManager(), "");
    }

    SimpleDraweeView ivGiftProfile,ivGiftItem;
    LinearLayout tabGiftTitle;
    RelativeLayout tabGiftMain;
    View animationGiftCapture,animationResetAnimation;
    TextView tvGiftTitle,tvGiftCountTitle,tvSendGiftCount;
    public void ShowGiftAnimation(LiveCommentModel item) {
        ivGiftProfile=view.findViewById(R.id.ivGiftProfile);
        tabGiftTitle=view.findViewById(R.id.tabGiftTitle);
        tabGiftMain=view.findViewById(R.id.tabGiftMain);
        animationResetAnimation=view.findViewById(R.id.animationResetAnimation);
        tvGiftTitle=view.findViewById(R.id.tvGiftTitle);
        tvGiftCountTitle=view.findViewById(R.id.tvGiftCountTitle);
        ivGiftItem=view.findViewById(R.id.ivGiftItem);
        tvSendGiftCount=view.findViewById(R.id.tvSendGiftCount);
        animationGiftCapture=view.findViewById(R.id.animationGiftCapture);

        String[] str=item.getComment().split("=====");

        Uri imageUri = Uri.parse(str[2]);

        ivGiftProfile.setController(Functions.frescoImageLoad(item.getUserPicture(),ivGiftProfile,false));

        ivGiftItem.setController(Functions.frescoImageLoad(""+imageUri,ivGiftItem,false));
        tvGiftTitle.setText(item.getUserName());
        tvGiftCountTitle.setText(getString(R.string.gave_you_a)+" "+str[1]);
        tvSendGiftCount.setText("X "+str[0]);

        tabGiftMain.animate().alpha(1).translationX(animationGiftCapture.getX()).setDuration(3000).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tabGiftMain.animate().translationY(animationCapture.getY()).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tabGiftMain.clearAnimation();
                                tabGiftMain.animate().alpha(0).translationY(animationResetAnimation.getY()).translationX(animationResetAnimation.getX()).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        tabGiftMain.clearAnimation();
                                    }
                                }).start();
                            }
                        }).start();
                    }


                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        PlayGiftSound();
                    }
                }).start();


    }

    MediaPlayer player;
    Handler handler;
    private void PlayGiftSound() {
        handler=new Handler(Looper.getMainLooper());
        player = MediaPlayer.create(context, R.raw.gift_tone);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setVolume(100,100);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        handler.postDelayed(runnable,2000);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            onTuneStop();
        }
    };

    public void onTuneStop() {

        if(player!=null && player.isPlaying()){
            player.stop();
        }
        if (handler!=null)
        {
            handler.removeCallbacks(runnable);
        }

    }


    // send the comment to the live user
    public void addMessages(String type,String msg) {

        final String key = rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(context).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(context).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment(msg);
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").child(key).setValue(commentItem);

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }



    // send the comment to the live user
    public void addGiftComment(String type, String count, StickerModel model) {

        final String key = rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(context).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(context).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment(count+"====="+model.name+"====="+model.getImage());
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").child(key).setValue(commentItem);
        LiveCoinsModel coinsModel=new LiveCoinsModel();
        coinsModel.setUserId(my_id);
        coinsModel.setUserName(my_name);
        coinsModel.setUserPic(my_image);
        coinsModel.setSendedCoins(""+(Double.valueOf(count)*Double.valueOf(model.coins)));
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    LiveCoinsModel preModel=snapshot.getValue(LiveCoinsModel.class);
                    double totalCoins=Double.valueOf(preModel.getSendedCoins());
                    totalCoins=totalCoins+((Double.valueOf(count)*Double.valueOf(model.coins)));

                    HashMap<String,Object> updateMap=new HashMap<>();
                    updateMap.put("sendedCoins",""+totalCoins);

                    rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").child(my_id).updateChildren(updateMap);
                }
                else
                {
                    rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").child(my_id).setValue(coinsModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }



    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    InitControl();
                    ActionControl();
                }
            },200);
        }
    }



    ValueEventListener userLiveStatusListener;
    private void checkUserStatus() {
        if (userLiveStatusListener==null)
        {
            userLiveStatusListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (snapshot.exists())
                            {

                                String duetConnectedUserId=snapshot.child("duetConnectedUserId").getValue().toString();
                                ArrayList<StreamJoinModel> jointUserList=new ArrayList<>();
                                for (DataSnapshot childShot:snapshot.child("JoinStream").getChildren())
                                {
                                    if (!(TextUtils.isEmpty(childShot.getValue().toString())))
                                    {
                                        StreamJoinModel model=childShot.getValue(StreamJoinModel.class);
                                        if (!(duetConnectedUserId.equals(model.userId)))
                                        {
                                            jointUserList.add(model);
                                        }
                                    }
                                }
                                liveUserCount.setText(Functions.getSuffix(""+jointUserList.size()));


                                Functions.printLog(Constants.tag,snapshot.toString());
                                item = snapshot.getValue(LiveUserModel.class);

                                tabStreamView.setVisibility(View.VISIBLE);
                                tabOfflineView.setVisibility(View.GONE);

                                Log.d(Constants.tag,"Stream: userChange");


                                item.setVideoEnable((boolean) snapshot.child("isVideoEnable").getValue());
                                Functions.printLog(Constants.tag,"item.setVideoEnable"+item.isVideoEnable());

                                setUpScreenData();

                                lounchStreamerCam();



                            }
                            else
                            {
                                tabStreamView.setVisibility(View.GONE);
                                tabOfflineView.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tabStreamView.setVisibility(View.GONE);
                    tabOfflineView.setVisibility(View.VISIBLE);
                }};
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).addValueEventListener(userLiveStatusListener);
        }

    }
    private void removeUserStatus() {
        if (rootref!=null && userLiveStatusListener!=null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).removeEventListener(userLiveStatusListener);
            userLiveStatusListener=null;
        }
    }




    private void lounchStreamerCam() {
        InitNodeListener();
        addBlockStatusStream();

    }

    private void InitNodeListener() {

        joinStream();
        AddJoinNode();
        ListenerCoinNode();
        ListCommentData();
        addLikeStream();
        addStreamerOnlineStatus();
        callApiForGetAllvideos(item.getUserId(),item.getUserName());
    }

    ValueEventListener  blockValueEventListener;
    private void addBlockStatusStream() {
        if(blockValueEventListener==null)
        {

            blockValueEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String blockStatus= (String) snapshot.child("blockState").getValue();
                        performBlockAction(blockStatus);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("BlockStreaming")
                    .child(Functions.getSharedPreference(context).getString(Variables.U_ID,""))
            .addValueEventListener(blockValueEventListener);

        }
    }

    private void performBlockAction(String blockStatus) {
        if (blockStatus.equals("1"))
        {
            Toast.makeText(context, context.getString(R.string.your_are_blocked_on_this_stream), Toast.LENGTH_SHORT).show();
            tabStreamView.setVisibility(View.GONE);
            tabOfflineView.setVisibility(View.VISIBLE);
            removeNodeListener();
        }
        else
        {
            checkUserStatus();
        }
    }

    private void removeBlockStatusStream() {
        if (rootref!=null && blockValueEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("BlockStreaming")
                    .child(Functions.getSharedPreference(context).getString(Variables.U_ID,"")).removeEventListener(blockValueEventListener);
            blockValueEventListener =null;
        }
    }


    ChildEventListener  likeValueEventListener;
    int heartCounter=0;
    private void addLikeStream() {
        if(likeValueEventListener==null) {
            likeValueEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        StreamShowHeartModel likeData=snapshot.getValue(StreamShowHeartModel.class);

                        if (getActivity()!=null)
                        {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    heartCounter=heartCounter+1;
                                    heartsShow();
                                }
                            });
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("LikesStream").addChildEventListener(likeValueEventListener);
        }
    }

    public void removeLikeStream() {
        if (rootref!=null && likeValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("LikesStream").removeEventListener(likeValueEventListener);
            likeValueEventListener =null;
        }
    }


    private void joinStream() {
        boolean isBroadcaster = false;
        isAudioActivated=isBroadcaster;
        isVideoActivated=isBroadcaster;
        isbeautyActivated=true;

        activity.setBeautyEffectOptions(isbeautyActivated);
        activity.mVideoGridContainer = view.findViewById(R.id.live_video_grid_layout);
        activity.mVideoGridContainer.setStatsManager(activity.setStatsManager());
        activity.setClientRole(activity.userRole);
        if (isBroadcaster) {
            startBroadcast(activity.userRole);
        }
        activity.mVideoDimension = activity.getconfigDimenIndex();
    }

    private Timer timer = new Timer();
    private final long DELAY = 20000;
    ChildEventListener  streamerOnlineListener;
    private void addStreamerOnlineStatus() {
        if(streamerOnlineListener==null) {

            streamerOnlineListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        UserOnlineModel itemUpdate=snapshot.getValue(UserOnlineModel.class);
                        if (item.getUserId().equalsIgnoreCase(itemUpdate.getUserId()))
                        {
                            if (timer!=null)
                            {
                                Functions.cancelIndeterminentLoader();
                                timer.cancel();
                            }
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserOnlineModel itemUpdate=snapshot.getValue(UserOnlineModel.class);
                                if (item.getUserId().equalsIgnoreCase(itemUpdate.getUserId()))
                                {
                                    Functions.showIndeterminentLoader(getActivity(),itemUpdate.getUserName()+" "+context.getString(R.string.single_is_week)
                                    ,false,false);
                                    timer.cancel();
                                    timer = new Timer();
                                    timer.schedule(
                                            new TimerTask() {
                                                @Override
                                                public void run() {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Functions.cancelIndeterminentLoader();
                                                            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).removeValue();
                                                        }
                                                    });
                                                }
                                            },
                                            DELAY
                                    );
                                }

                            }
                        });

                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootref.child(Variables.onlineUser).addChildEventListener(streamerOnlineListener);

        }
    }

    private void removeStreamerOnlineStatus() {
        if (rootref!=null && streamerOnlineListener != null)
        {
            rootref.child(Variables.onlineUser).removeEventListener(streamerOnlineListener);
            streamerOnlineListener =null;
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        removeNodeListener();
        removeBlockStatusStream();
    }


    private void removeNodeListener() {

        activity.removeStreamingConnection();
        removeUserStatus();
        removeJoinNode();
        removeCoinListener();
        removeCommentListener();
        removeLikeStream();
        removeStreamerOnlineStatus();
        removeNodeCameraRequest();
        removejoinCallback();

    }


}