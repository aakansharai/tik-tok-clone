package com.mytiktok.app.simpleclasses

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.banuba.sdk.token.storage.license.BanubaVideoEditor
import com.danikula.videocache.HttpProxyCacheServer
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.mytiktok.app.Constants
import com.mytiktok.app.R
import com.mytiktok.app.activitesfragments.CustomErrorActivity
import com.mytiktok.app.activitesfragments.livestreaming.model.LiveUserModel
import com.mytiktok.app.activitesfragments.livestreaming.rtc.AgoraEventHandler
import com.mytiktok.app.activitesfragments.livestreaming.rtc.EngineConfig
import com.mytiktok.app.activitesfragments.livestreaming.rtc.EventHandler
import com.mytiktok.app.activitesfragments.livestreaming.stats.StatsManager
import com.mytiktok.app.activitesfragments.livestreaming.utils.FileUtil
import com.mytiktok.app.activitesfragments.livestreaming.utils.PrefManager
import com.mytiktok.app.models.UserOnlineModel
import com.smartnsoft.backgrounddetector.BackgroundDetectorCallback
import com.smartnsoft.backgrounddetector.BackgroundDetectorHandler
import com.volley.plus.VPackages.VolleyRequest
import com.wmocca.com.activitesfragments.videorecording.banuba.VideoEditorModule
import io.agora.rtc.RtcEngine
import io.paperdb.Paper
import java.io.File

/**
 * Created by qboxus on 3/18/2019.
 */
class TicTic : Application(), ActivityLifecycleCallbacks,
    BackgroundDetectorHandler.OnVisibilityChangedListener {
    private var proxy: HttpProxyCacheServer? = null
    private var backgroundDetectorHandler: BackgroundDetectorHandler? = null

    var videoEditor: BanubaVideoEditor? = null

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
        initAdsView()
        VolleyRequest.initalizeSdk(this)
        appLevelContext = applicationContext
        registerActivityLifecycleCallbacks(this)
        backgroundDetectorHandler = BackgroundDetectorHandler(
            BackgroundDetectorCallback(
                BackgroundDetectorHandler.ON_ACTIVITY_RESUMED,
                this
            )
        )
        Fresco.initialize(
            applicationContext, ImagePipelineConfigUtils.getDefaultImagePipelineConfig(
                applicationContext
            )
        )
        Paper.init(applicationContext)
        FirebaseApp.initializeApp(applicationContext)
        addFirebaseToken()
        setUserOnline()
        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        }
        if (exoDatabaseProvider == null) {
            exoDatabaseProvider = StandaloneDatabaseProvider(applicationContext)
        }
        if (simpleCache == null) {
            simpleCache =
                SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor!!, exoDatabaseProvider!!)
            if (simpleCache!!.cacheSpace >= 400207768) {
                freeMemory()
            }
        }
        initCrashActivity()
        initConfig()
        Functions.createNoMediaFile(applicationContext)

        videoEditor = BanubaVideoEditor.initialize(Constants.banubaToken)
        if (videoEditor == null) {
            Log.e(TAG, ERR_SDK_NOT_INITIALIZED)
        } else {
            VideoEditorModule().initialize(this@TicTic)
        }

    }

    private fun initAdsView() {
        MobileAds.initialize(this) { }
    }

    fun initCrashActivity() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
            .enabled(true)
            .showErrorDetails(true)
            .showRestartButton(true)
            .logErrorOnRestart(true)
            .trackActivities(true)
            .minTimeBetweenCrashesMs(2000)
            .restartActivity(CustomErrorActivity::class.java)
            .errorActivity(CustomErrorActivity::class.java)
            .apply()
    }

    var onlineEventListener: ChildEventListener? = null
    var streamingEventListener: ChildEventListener? = null
    var rootref: DatabaseReference? = null
    private fun setUserOnline() {
        rootref = FirebaseDatabase.getInstance().reference
        addOnlineListener()
        addStreamingListener()
    }

    fun addStreamingListener() {
        if (streamingEventListener == null) {
            streamingEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (!TextUtils.isEmpty(snapshot.value.toString())) {
                        val model = snapshot.getValue(
                            LiveUserModel::class.java
                        )
                        allLiveStreaming[model!!.getStreamingId()] = model
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if (!TextUtils.isEmpty(snapshot.value.toString())) {
                        val model = snapshot.getValue(
                            LiveUserModel::class.java
                        )
                        allLiveStreaming.remove(model!!.getStreamingId())
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            rootref!!.child(Variables.LiveStreaming).addChildEventListener(streamingEventListener!!)
        }
    }

    fun removeStreamingListener() {
        if (rootref != null && streamingEventListener != null) {
            rootref!!.child(Variables.LiveStreaming).removeEventListener(streamingEventListener!!)
            streamingEventListener = null
        }
    }

    fun addOnlineListener() {
        if (onlineEventListener == null) {
            addOnlineStatus()
            onlineEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (!TextUtils.isEmpty(snapshot.value.toString())) {
                        val item = snapshot.getValue(
                            UserOnlineModel::class.java
                        )
                        allOnlineUser[item!!.getUserId()] = item
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if (!TextUtils.isEmpty(snapshot.value.toString())) {
                        val item = snapshot.getValue(
                            UserOnlineModel::class.java
                        )
                        allOnlineUser.remove(item!!.getUserId())
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            rootref!!.child(Variables.onlineUser).addChildEventListener(onlineEventListener!!)
        }
    }

    fun removeOnlineListener() {
        if (rootref != null && onlineEventListener != null) {
            removeOnlineStatus()
            rootref!!.child(Variables.onlineUser).removeEventListener(onlineEventListener!!)
            onlineEventListener = null
        }
    }

    private fun removeOnlineStatus() {
        if (Functions.getSharedPreference(applicationContext)
                .getBoolean(Variables.IS_LOGIN, false)
        ) {
            rootref!!.child(Variables.onlineUser).child(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_ID, "0")!!
            )
                .removeValue().addOnCompleteListener {
                    Log.d(
                        Constants.tag, "removeOnlineStatus: " + Functions.getSharedPreference(
                            applicationContext
                        ).getString(Variables.U_ID, "0")
                    )
                }
        }
    }

    private fun addOnlineStatus() {
        if (Functions.getSharedPreference(applicationContext)
                .getBoolean(Variables.IS_LOGIN, false)
        ) {
            val onlineModel = UserOnlineModel()
            onlineModel.setUserId(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_ID, "0")
            )
            onlineModel.setUserName(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_NAME, "")
            )
            onlineModel.setUserPic(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_PIC, "")
            )
            rootref!!.child(Variables.onlineUser).child(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_ID, "0")!!
            ).onDisconnect().removeValue()
            rootref!!.child(Variables.onlineUser).child(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_ID, "0")!!
            ).keepSynced(true)
            rootref!!.child(Variables.onlineUser).child(
                Functions.getSharedPreference(
                    applicationContext
                ).getString(Variables.U_ID, "0")!!
            ).setValue(onlineModel)
                .addOnCompleteListener {
                    Log.d(
                        Constants.tag,
                        "addOnlineStatus: " + onlineModel.getUserId()
                    )
                }
        }
    }

    fun addFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Log.d(Constants.tag, "token: $token")
                val editor = Functions.getSharedPreference(
                    applicationContext
                ).edit()
                editor.putString(Variables.DEVICE_TOKEN, "" + token)
                editor.commit()
            })
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(applicationContext)
            .maxCacheSize((1024 * 1024 * 1024).toLong())
            .maxCacheFilesCount(50)
            .cacheDirectory(
                File(
                    Functions.getAppFolder(
                        applicationContext
                    ) + "videoCache"
                )
            )
            .build()
    }

    // check how much memory is available for cache video
    fun freeMemory() {
        try {
            val dir = cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }

    // delete the cache if it is full
    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    private var mRtcEngine: RtcEngine? = null
    private val mGlobalConfig = EngineConfig()
    private val mHandler = AgoraEventHandler()
    private val mStatsManager = StatsManager()
    private fun initConfig() {
        try {
            mRtcEngine = RtcEngine.create(applicationContext, getString(R.string.agora_app_id), mHandler)
            mRtcEngine!!.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine!!.enableVideo()
            mRtcEngine!!.setLogFile(FileUtil.initializeLogFile(applicationContext))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val pref = PrefManager.getPreferences(
            applicationContext
        )

        mGlobalConfig.videoDimenIndex = pref.getInt(
            com.mytiktok.app.activitesfragments.livestreaming.Constants.PREF_RESOLUTION_IDX,
            com.mytiktok.app.activitesfragments.livestreaming.Constants.DEFAULT_PROFILE_IDX
        )

        val showStats = pref.getBoolean(
            com.mytiktok.app.activitesfragments.livestreaming.Constants.PREF_ENABLE_STATS,
            false
        )

        mGlobalConfig.setIfShowVideoStats(false)
        mStatsManager.enableStats(false)
        mGlobalConfig.mirrorLocalIndex = pref.getInt(
            com.mytiktok.app.activitesfragments.livestreaming.Constants.PREF_MIRROR_LOCAL,
            0
        )
        mGlobalConfig.mirrorRemoteIndex = pref.getInt(
            com.mytiktok.app.activitesfragments.livestreaming.Constants.PREF_MIRROR_REMOTE,
            0
        )
        mGlobalConfig.mirrorEncodeIndex = pref.getInt(
            com.mytiktok.app.activitesfragments.livestreaming.Constants.PREF_MIRROR_ENCODE,
            0
        )
    }

    fun engineConfig(): EngineConfig {
        return mGlobalConfig
    }

    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun statsManager(): StatsManager {
        return mStatsManager
    }

    fun registerEventHandler(handler: EventHandler?) {
        mHandler.addHandler(handler)
    }

    fun removeEventHandler(handler: EventHandler?) {
        mHandler.removeHandler(handler)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        backgroundDetectorHandler!!.onActivityResumed(activity)
        Functions.RegisterConnectivity(activity) { requestType, response ->
            if (response.equals("disconnected", ignoreCase = true)) {
                removeOnlineListener()
                removeStreamingListener()
                Functions.showToastOnTop(
                    activity,
                    null,
                    activity.getString(R.string.your_network_is_unstable)
                )
            } else {
                addOnlineListener()
                addStreamingListener()
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        backgroundDetectorHandler!!.onActivityPaused(activity)
        Functions.unRegisterConnectivity(activity)
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onAppGoesToBackground(context: Context?) {
        Log.d(Constants.tag, "onAppGoesToBackground")
        removeOnlineListener()
        removeStreamingListener()
    }

    override fun onAppGoesToForeground(context: Context?) {
        Log.d(Constants.tag, "onAppGoesToForeground")
        addOnlineListener()
        addStreamingListener()
    }

    companion object {
        @JvmField
        var appLevelContext: Context? = null
        var simpleCache: SimpleCache? = null
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: DatabaseProvider? = null
        var exoPlayerCacheSize = (100 * 1024 * 1024).toLong()
        var allOnlineUser = HashMap<String, UserOnlineModel?>()
        @JvmField
        var allLiveStreaming = HashMap<String, LiveUserModel?>()

        // below code is for cache the videos in local
        @JvmStatic
        fun getProxy(context: Context): HttpProxyCacheServer {
            val app = context.applicationContext as TicTic
            return try {
                if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy!!
            } catch (e: Exception) {
                app.newProxy()
            }
        }


        const val TAG = "BanubaVideoEditor"
        // Please set your license token for Banuba Video Editor SDK
        const val ERR_SDK_NOT_INITIALIZED = "Banuba Video Editor SDK is not initialized: license token is unknown or incorrect.\nPlease check your license token or contact Banuba"
        const val ERR_LICENSE_REVOKED = "License is revoked or expired. Please contact Banuba https://www.banuba.com/faq/kb-tickets/new"

    }
}