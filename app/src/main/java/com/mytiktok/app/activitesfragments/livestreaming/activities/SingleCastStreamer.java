package com.mytiktok.app.activitesfragments.livestreaming.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import com.mytiktok.app.R;
import com.mytiktok.app.activitesfragments.EditTextSheetF;
import com.mytiktok.app.activitesfragments.livestreaming.adapter.LiveCommentsAdapter;
import com.mytiktok.app.activitesfragments.livestreaming.fragments.InviteContactsToStreamF;
import com.mytiktok.app.activitesfragments.livestreaming.model.CameraRequestModel;
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveCoinsModel;
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveCommentModel;
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveUserModel;
import com.mytiktok.app.activitesfragments.livestreaming.stats.LocalStatsData;
import com.mytiktok.app.activitesfragments.livestreaming.stats.RemoteStatsData;
import com.mytiktok.app.activitesfragments.livestreaming.stats.StatsData;
import com.mytiktok.app.activitesfragments.livestreaming.ui.VideoGridContainer;
import com.mytiktok.app.interfaces.AdapterClickListener;
import com.mytiktok.app.interfaces.FragmentCallBack;
import com.mytiktok.app.models.StreamShowHeartModel;
import com.mytiktok.app.models.UsersModel;
import com.mytiktok.app.simpleclasses.Functions;
import com.mytiktok.app.simpleclasses.OnSwipeTouchListener;
import com.mytiktok.app.simpleclasses.Variables;
import com.mytiktok.app.simpleclasses.streaminglikes.HeartView;

public class SingleCastStreamer extends RtcBaseActivity implements View.OnClickListener {

    private VideoGridContainer mVideoGridContainer;
    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    DatabaseReference rootref;
    SimpleDraweeView ivGiftCount;
    String userId, userName, userPicture;
    int userRole;
    TextView tvGiftCount;

    View animationCapture;
    LinearLayout tabGiftCount;
    String onlineType,description,secureCode,streamingId;
    int joinStreamPrice;
    boolean dualStreaming;
    LiveUserModel streamerLiveModel;

    ViewFlipper viewflliper;
    RelativeLayout viewOne,viewTwo;
    HeartView streamLikeView;
    Context context;
    boolean isFirstTimeFlip=true;
    View tabMainView;
    public RelativeLayout tabCoinSender;

    public TextView tvCoinCount,tvSender;
    public SimpleDraweeView ivSender;

    TextView  tvCurrentJoin,liveUserCount;
    SimpleDraweeView ivMainProfile;
    ImageView ivVideoRequest,ivVerified;
    LinearLayout tabLikeStreaming,tabShareStream,tabMenu,tabEffects;
    TextView tvUserName,tvMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_single_cast_streamer);
        context=SingleCastStreamer.this;
        tvGiftCount=findViewById(R.id.tvGiftCount);
        rootref = FirebaseDatabase.getInstance().getReference();
        rootref.keepSynced(true);

        Intent bundle = getIntent();
        if (bundle != null) {
            userId = bundle.getStringExtra("userId");
            userName = bundle.getStringExtra("userName");
            userPicture = bundle.getStringExtra("userPicture");
            userRole = Constants.CLIENT_ROLE_BROADCASTER;
            onlineType="oneTwoOne";
            description="";
            secureCode="";
            dualStreaming=bundle.getBooleanExtra("dualStreaming",false);
            joinStreamPrice=bundle.getIntExtra("joinStreamPrice",0);
            streamingId=bundle.getStringExtra("streamingId");
        }

        InitControl();
        ActionControl();


        if (userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            rootref.child("LiveStreamingUsers").child(streamingId).keepSynced(true);
            rootref.child("LiveStreamingUsers").child(streamingId).onDisconnect().removeValue();

            addFirebaseNode();
            broadcasterlistenerNode();
            addNodeCameraRequest();
        }
        ListenerCoinNode();
        addLikeStream();
        ListCommentData();
        addStreamInternetConnection();
        setUpScreenData();
    }




    private void setUpScreenData() {
        tvCoinCount.setText(""+Functions.changeValueToInt(Functions.getSharedPreference(context).getString(Variables.U_total_coins_all_time,"0")));
        ivMainProfile.setController(Functions.frescoImageLoad(""+userPicture,ivMainProfile,false));
        tvUserName.setText(""+userName);

        String verified=Functions.getSharedPreference(context).getString(Variables.IS_VERIFIED,"0");
        if (verified.equals("1"))
        {
            ivVerified.setVisibility(View.VISIBLE);
        }
        else
        {
            ivVerified.setVisibility(View.GONE);
        }
    }



    private void InitControl() {
        tvUserName = findViewById(R.id.live_user_name);
        ivVerified=findViewById(R.id.ivVerified);
        tabMenu =findViewById(R.id.tabMenu);
        tabEffects=findViewById(R.id.tabEffects);
        tabLikeStreaming=findViewById(R.id.tabLikeStreaming);
        tabLikeStreaming.setOnClickListener(this);
        ivVideoRequest=findViewById(R.id.ivVideoRequest);
        tvCurrentJoin=findViewById(R.id.tvCurrentJoin);
        ivSender=findViewById(R.id.ivSender);
        tvCoinCount=findViewById(R.id.tvCoinCount);
        tvSender=findViewById(R.id.tvSender);
        tabCoinSender=findViewById(R.id.tabCoinSender);
        ivGiftCount=findViewById(R.id.ivGiftCount);
        tabGiftCount=findViewById(R.id.tabGiftCount);
        animationCapture=findViewById(R.id.animationCapture);
        tabMainView=findViewById(R.id.tabMainView);
        streamLikeView=findViewById(R.id.streamLikeView);
        streamLikeView.setOnClickListener(this);
        viewflliper=findViewById(R.id.viewflliper);

        viewOne=findViewById(R.id.viewOne);
        viewTwo=findViewById(R.id.viewTwo);
        tabShareStream=findViewById(R.id.tabShareStream);
        tabShareStream.setOnClickListener(this);
        liveUserCount=findViewById(R.id.liveUserCount);
        tvMessage=findViewById(R.id.tvMessage);
        tvMessage.setOnClickListener(this);
        ivMainProfile=findViewById(R.id.ivMainProfile);
        initCommentAdapter();

        initUI();
        initData();
    }


    private void ActionControl() {
        findViewById(R.id.cross_btn).setOnClickListener(this);

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
                Log.d(com.mytiktok.app.Constants.tag,"start");

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

                Log.d(com.mytiktok.app.Constants.tag,"end");
                if (viewTwo!=viewflliper.getCurrentView())
                {
                    viewflliper.showNext();
                }

            }
            public void onSwipeBottom() {

            }
            public void onDoubleClick() {

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

        tabEffects.setOnClickListener(this);
        tabMenu.setOnClickListener(this);
        ivVideoRequest.setOnClickListener(this);

    }


    // initailze the adapter
    ArrayList<LiveCommentModel> dataList=new ArrayList<>();
    RecyclerView recyclerView;
    LiveCommentsAdapter adapter;
    public void initCommentAdapter() {
        dataList.clear();
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        adapter = new LiveCommentsAdapter(context, dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {

            }
        });
        recyclerView.setAdapter(adapter);

    }


    ChildEventListener commentChildListener;
    Calendar current_cal;
    public void ListCommentData() {
        current_cal = Calendar.getInstance();
        if(commentChildListener ==null)
        {
            commentChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    LiveCommentModel model = dataSnapshot.getValue(LiveCommentModel.class);
                    dataList.add(model);

                    if (Functions.checkTimeDiffernce(current_cal,model.getCommentTime()))
                    {
                        if(model.getType().equalsIgnoreCase("gift"))
                        {
                            SingleCastStreamer.this.runOnUiThread(new Runnable() {
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
            rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").addChildEventListener(commentChildListener);
        }
    }

    public void removeCommentListener() {
        if (rootref!=null && commentChildListener != null)
        {
            rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").removeEventListener(commentChildListener);
            commentChildListener =null;
        }
    }



    ChildEventListener likeValueEventListener;
    int heartCounter=0;
    private void addLikeStream() {
        if(likeValueEventListener==null)
        {

            likeValueEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        StreamShowHeartModel likeData=snapshot.getValue(StreamShowHeartModel.class);
                        SingleCastStreamer.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                heartCounter=heartCounter+1;
                                heartsShow();
                            }
                        });
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
            rootref.child("LiveStreamingUsers").child(streamingId).child("LikesStream").addChildEventListener(likeValueEventListener);

        }
    }


    ValueEventListener connectCheckListener;
    DatabaseReference connectedRef;
    private Timer timer = new Timer();
    private final long DELAY = 20000;
    private void addStreamInternetConnection() {
        if (connectCheckListener==null)
        {
            connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

            connectCheckListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        Log.d(com.mytiktok.app.Constants.tag, "connected");
                        timer.cancel();
                    } else {
                        Log.d(com.mytiktok.app.Constants.tag, "not connected");
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                onBackPressed();
                                            }
                                        });
                                    }
                                },
                                DELAY
                        );

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(com.mytiktok.app.Constants.tag, "Listener was cancelled");
                }
            };
            connectedRef.addValueEventListener(connectCheckListener);
        }

    }

    public void removeStreamInternetConnection() {
        if (connectedRef != null && connectCheckListener != null) {
            connectedRef.removeEventListener(connectCheckListener);
        }
    }


    ValueEventListener coinValueEventListener;
    ArrayList<LiveCoinsModel> senderCoinsList=new ArrayList<>();
    private void ListenerCoinNode() {
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
        rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").addValueEventListener(coinValueEventListener);
    }



    public void removeCoinListener() {
        if (rootref != null && coinValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").removeEventListener(coinValueEventListener);
        }
    }





    // initialize the views of activity
    private void initUI() {
        boolean isBroadcaster = (userRole == Constants.CLIENT_ROLE_BROADCASTER);

        isAudioActivated=isBroadcaster;
        isVideoActivated=isBroadcaster;

        isbeautyActivated=true;
        rtcEngine().setBeautyEffectOptions(isbeautyActivated,
                com.mytiktok.app.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        rtcEngine().setClientRole(userRole);
        if (isBroadcaster) startBroadcast();
    }


    private void initData() {
        Functions.printLog(com.mytiktok.app.Constants.tag, "initData");
        mVideoDimension = com.mytiktok.app.activitesfragments.livestreaming.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }


    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(Functions.parseInterger(userId));
        mVideoGridContainer.addUserVideoSurface(Functions.parseInterger(userId), surface);
        rtcEngine().enableVideo();
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        removeRtcVideo(Functions.parseInterger(userId));
        mVideoGridContainer.removeUserVideo(Functions.parseInterger(userId));
        rtcEngine().disableVideo();
    }



    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Functions.printLog(com.mytiktok.app.Constants.tag, "onFirstRemoteVideoDecoded");
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        Functions.printLog(com.mytiktok.app.Constants.tag, "renderRemoteUser");
        SurfaceView surface = prepareRtcVideo(uid);
        mVideoGridContainer.addUserVideoSurface(uid, surface);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid);
        mVideoGridContainer.removeUserVideo(uid);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    // check the network quality
    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        Functions.printLog(com.mytiktok.app.Constants.tag, "onRemoteAudioStats");
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            broadcastRemoveListener();
            removeNode();
            removeNodeCameraRequest();
        }
        removeCoinListener();
        removeLikeStream();
        removeCommentListener();
        removeStreamInternetConnection();

    }

    public void removeLikeStream() {
        if (rootref!=null && likeValueEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(streamingId).child("LikesStream").removeEventListener(likeValueEventListener);
            likeValueEventListener =null;
        }


    }

    public void addFirebaseNode() {

        LiveUserModel model=new LiveUserModel();
        model.setStreamingId(""+streamingId);
        model.setUserId(""+userId);
        model.setUserName(""+userName);
        model.setUserPicture(""+userPicture);
        model.setOnlineType(""+onlineType);
        model.setDescription(""+description);
        model.setSecureCode(""+secureCode);
        model.setJoinStreamPrice(""+joinStreamPrice);
        model.setDualStreaming(dualStreaming);
        model.setVideoEnable(true);
        model.setStreamJoinAllow(false);
        model.setUserCoins(""+Functions.getSharedPreference(context).getString(Variables.U_total_coins_all_time,"0"));
        model.setIsVerified(""+Functions.getSharedPreference(context).getString(Variables.IS_VERIFIED,"0"));
        model.setDuetConnectedUserId("");
        rootref.child("LiveStreamingUsers").child(streamingId).setValue(model);
    }

    // when user goes to offline then change the value status on firebase
    public void removeNode() {
        rootref.child("LiveStreamingUsers").child(streamingId).removeValue();
    }




    // check the current live user status eighter user is live or not when users goes offline this callback will hit
    ValueEventListener broadcastValueEventListener;
    public void broadcasterlistenerNode() {

        broadcastValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               SingleCastStreamer.this.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (dataSnapshot.exists()) {
                           streamerLiveModel = dataSnapshot.getValue(LiveUserModel.class);

                           tvCoinCount.setText(""+Functions.changeValueToInt(streamerLiveModel.getUserCoins()));
                           liveUserCount.setText(Functions.getSuffix(""+dataSnapshot.child("JoinStream").getChildrenCount()));


                           Log.d(com.mytiktok.app.Constants.tag,"streamerUserCoin: "+streamerLiveModel.getUserCoins());

                           Functions.getSharedPreference(context).edit()
                                   .putString(Variables.U_total_coins_all_time, ""+streamerLiveModel.getUserCoins()).commit();
                       }
                       else
                       {
                           Toast.makeText(context, context.getString(R.string.your_live_channel_is_close), Toast.LENGTH_SHORT).show();
                           onBackPressed();
                       }

                   }
               });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootref.child("LiveStreamingUsers").child(streamingId).addValueEventListener(broadcastValueEventListener);
    }

    public void broadcastRemoveListener() {
        if (rootref != null && broadcastValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(streamingId).removeEventListener(broadcastValueEventListener);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tabMenu:
            {
                ShowDailogForJoinBroadcast();
            }
            break;
            case R.id.tabEffects:
            {
                performBeautify();
            }
            break;
            case R.id.ivVideoRequest:
            {
                showCameraRequest();
            }
            break;
            case R.id.tabShareStream:
            {
                inviteFriendsForStream();
            }
            break;
            case R.id.cross_btn:
            {
                onBackPressed();
            }
            break;
            case R.id.tvMessage:
            {
                sendComment();
            }
            break;
            case R.id.tabLikeStreaming:
            {

            }
            break;
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
                        addMessages("comment");
                    }
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("replyStr","");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "EditTextSheetF");
    }

    private void inviteFriendsForStream() {
        InviteContactsToStreamF f = new InviteContactsToStreamF(streamingId,"single",new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {

                }
            }
        });
        f.show(getSupportFragmentManager(), "InviteContactsToStreamF");
    }

    private void showCameraRequest() {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.camera_request_broadcast_view);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        RelativeLayout tabAccept = alertDialog.findViewById(R.id.tabAccept);
        RelativeLayout tabReject = alertDialog.findViewById(R.id.tabReject);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                sendCameraRequest("2");
            }
        });
        tabReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                sendCameraRequest("0");

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void sendCameraRequest(String type) {
        CameraRequestModel model=new CameraRequestModel();
        model.setRequestState(type);
        rootref.child("LiveStreamingUsers").child(streamingId).child("CameraRequest")
                .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete())
                {
                    Toast.makeText(context, context.getString(R.string.camera_request_sended), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void heartsShow()
    {
        streamLikeView.addHeart(new Random().nextInt(5));
    }



    boolean isAudioActivated=true,isVideoActivated=true,isbeautyActivated=true;
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

        if (!(onlineType.equals("oneTwoOne")))
        {
            if (userRole != Constants.CLIENT_ROLE_BROADCASTER)
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

        rtcEngine().setBeautyEffectOptions(live_btn_mute_video.isActivated(),
                com.mytiktok.app.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);

        tab_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });
        swith_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                rtcEngine().switchCamera();
            }
        });
        live_btn_mute_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isAudioActivated = !isAudioActivated;

                rtcEngine().muteLocalAudioStream(!isAudioActivated);

                if (isAudioActivated)
                {
                    rtcEngine().enableAudio();
                }
                else
                {
                    rtcEngine().disableAudio();
                }
            }
        });
        live_btn_beautification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                performBeautify();
            }
        });

        live_btn_mute_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isVideoActivated=!isVideoActivated;
                if (isVideoActivated) {
                    startBroadcast();
                    rootref.child("LiveStreamingUsers").child(streamingId).child("isVideoEnable").setValue(true);

                } else {
                    stopBroadcast();
                    rootref.child("LiveStreamingUsers").child(streamingId).child("isVideoEnable").setValue(false);

                }

            }
        });
        alertDialog.show();
    }


    private void performBeautify() {
        isbeautyActivated=!isbeautyActivated;
        rtcEngine().setBeautyEffectOptions(isbeautyActivated,
                com.mytiktok.app.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);
    }


    SimpleDraweeView ivGiftProfile,ivGiftItem;
    LinearLayout tabGiftTitle;
    RelativeLayout tabGiftMain;
    View animationGiftCapture,animationResetAnimation;
    TextView tvGiftTitle,tvGiftCountTitle,tvSendGiftCount;
    public void ShowGiftAnimation(LiveCommentModel item) {
        ivGiftProfile=findViewById(R.id.ivGiftProfile);
        tabGiftTitle=findViewById(R.id.tabGiftTitle);
        tabGiftMain=findViewById(R.id.tabGiftMain);
        animationResetAnimation=findViewById(R.id.animationResetAnimation);
        tvGiftTitle=findViewById(R.id.tvGiftTitle);
        tvGiftCountTitle=findViewById(R.id.tvGiftCountTitle);
        ivGiftItem=findViewById(R.id.ivGiftItem);
        tvSendGiftCount=findViewById(R.id.tvSendGiftCount);
        animationGiftCapture=findViewById(R.id.animationGiftCapture);

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
        player = MediaPlayer.create(getApplicationContext(), R.raw.gift_tone);
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
    public void addMessages(String type) {

        final String key = rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(this).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(this).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(this).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment(""+ tvMessage.getText().toString());
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").child(key).setValue(commentItem);

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }


    @Override
    public void onBackPressed() {
        finish();
    }


    ValueEventListener  cameraRequestEventListener;
    private void addNodeCameraRequest() {
        if(cameraRequestEventListener==null)
        {
            cameraRequestEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        CameraRequestModel model=snapshot.getValue(CameraRequestModel.class);
                        if (model.getRequestState().equals("1"))
                        {
                            ivVideoRequest.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_camera_request_r));
                            ivVideoRequest.setVisibility(View.VISIBLE);
                            showCameraRequest();
                        }
                        else
                        if (model.getRequestState().equals("2"))
                        {
                            ivVideoRequest.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_camera_request_a));
                            ivVideoRequest.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            ivVideoRequest.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootref.child("LiveStreamingUsers").child(streamingId).child("CameraRequest").addValueEventListener(cameraRequestEventListener);

        }
    }

    private void removeNodeCameraRequest() {
        if (rootref!=null && cameraRequestEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(streamingId).child("CameraRequest").removeEventListener(cameraRequestEventListener);
            cameraRequestEventListener =null;
        }
    }

}
