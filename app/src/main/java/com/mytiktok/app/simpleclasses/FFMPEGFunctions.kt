package com.mytiktok.app.simpleclasses

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.mytiktok.app.Constants
import com.mytiktok.app.interfaces.FragmentCallBack
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import java.io.File


object FFMPEGFunctions {

    @JvmStatic
    fun getFilePath(activity: Activity):String{
        return Common.getFilePath(activity, Common.VIDEO);
    }

    fun createImageVideo(activity: Activity,photoPaths: ArrayList<String>,
                         callback:FragmentCallBack) {
        var output = Common.getFilePath(activity, Common.VIDEO)

        val query = combineImagesToVideo(activity,photoPaths, output)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                var message=logMessage.text
                Log.d("FFMPEG_","process: ${message}")
                if (message.contains("size=") && message.contains("time="))
                {
                    message = Functions.decodeFFMPEGMessage(message)
                    val bundle=Bundle()
                    bundle.putString("action","process")
                    bundle.putString("message",message)
                    callback.onResponce(bundle)
                }

            }

            override fun success() {
                Log.d(Constants.tag,"success: ")
                val bundle=Bundle()
                bundle.putString("action","success")
                bundle.putString("path",output)
                callback.onResponce(bundle)
            }

            override fun cancel() {
                Log.d(Constants.tag,"cancel: ")
                val bundle=Bundle()
                bundle.putString("action","cancel")
                callback.onResponce(bundle)
            }

            override fun failed() {
                Log.d(Constants.tag,"failed: ")
                val bundle=Bundle()
                bundle.putString("action","failed")
                callback.onResponce(bundle)
            }
        })
    }


    fun addImageProcess(stickerPath: String,
                        videoFile:File, outputPath:String,
                        callback:FragmentCallBack) {

        val query = addVideoWaterMark(videoFile.absolutePath, stickerPath, outputPath)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                var message=logMessage.text
                Log.d("FFMPEG_","process: ${message}")
                if (message.contains("size=") && message.contains("time="))
                {
                    message = Functions.decodeFFMPEGMessage(message)
                    val bundle=Bundle()
                    bundle.putString("action","process")
                    bundle.putString("message",message)
                    callback.onResponce(bundle)
                }

            }

            override fun success() {
                Log.d(Constants.tag,"success: ")
                val bundle=Bundle()
                bundle.putString("action","success")
                bundle.putString("path",outputPath)
                callback.onResponce(bundle)
            }

            override fun cancel() {
                Log.d(Constants.tag,"cancel: ")
                val bundle=Bundle()
                bundle.putString("action","cancel")
                callback.onResponce(bundle)
            }

            override fun failed() {
                Log.d(Constants.tag,"failed: ")
                val bundle=Bundle()
                bundle.putString("action","failed")
                callback.onResponce(bundle)
            }
        })
    }


    fun trimVideoProcess(videoFile:File, outputPath:String,
                         startTimeString:String,endTimeString:String,
                         callback: FragmentCallBack) {

        val query = cutVideo(videoFile.absolutePath, startTimeString, endTimeString, outputPath)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                var message=logMessage.text
                Log.d("FFMPEG_","process: ${message}")
                if (message.contains("size=") && message.contains("time="))
                {
                    message = Functions.decodeFFMPEGMessage(message)
                    val bundle=Bundle()
                    bundle.putString("action","process")
                    bundle.putString("message",message)
                    callback.onResponce(bundle)
                }
            }

            override fun success() {
                Log.d("FFMPEG_","success: ")
                val bundle=Bundle()
                bundle.putString("action","success")
                bundle.putString("path",outputPath)
                callback.onResponce(bundle)
            }

            override fun cancel() {
                Log.d("FFMPEG_","cancel: ")
                val bundle=Bundle()
                bundle.putString("action","cancel")
                callback.onResponce(bundle)
            }

            override fun failed() {
                Log.d("FFMPEG_","failed: ")
                val bundle=Bundle()
                bundle.putString("action","failed")
                callback.onResponce(bundle)
            }
        })
    }


    fun compressVideoHighToLowProcess(activity: Activity, videoFile:File,
                                      frameRate:Int,
                                      callback: FragmentCallBack) {
        val outputPath = Common.getFilePath(activity, Common.VIDEO)
        val query = highToLowCompressor(videoFile.absolutePath, outputPath,frameRate)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                var message=logMessage.text
                Log.d("FFMPEG_","process: ${message}")
                if (message.contains("size=") && message.contains("time="))
                {
                    message = Functions.decodeFFMPEGMessage(message)
                    val bundle=Bundle()
                    bundle.putString("action","process")
                    bundle.putString("message",message)
                    callback.onResponce(bundle)
                }

            }

            override fun success() {
                Log.d("FFMPEG_","success: ")
                val bundle=Bundle()
                bundle.putString("action","success")
                bundle.putString("path",outputPath)
                callback.onResponce(bundle)
            }

            override fun cancel() {
                Log.d("FFMPEG_","cancel: ")
                val bundle=Bundle()
                bundle.putString("action","cancel")
                callback.onResponce(bundle)
            }

            override fun failed() {
                Log.d("FFMPEG_","failed: ")
                val bundle=Bundle()
                bundle.putString("action","failed")
                callback.onResponce(bundle)
            }
        })
    }




    fun videoSpeedProcess(context: Context,inputPath:String,speedTabPosition:Int,
                          frameRate:Int,
                          callback: FragmentCallBack) {
        val outputPath = Common.getFilePath(context, Common.VIDEO)
        var setpts:Double=1.0
        var atempo:Double=1.0
        Log.d(Constants.tag,"speedTabPosition: $speedTabPosition")
        when(speedTabPosition)
        {
            0->{
                setpts=2.0
                atempo=0.5
            }
            1->{
                setpts=1.5
                atempo=0.75
            }
            2->{
                setpts=1.0
                atempo=1.0
            }
            3->{
                setpts=0.75
                atempo=1.5
            }
            4->{
                setpts=0.5
                atempo=2.0
            }else->{
            setpts=1.0
            atempo=1.0
        }

        }

        val query = videoMotion(inputPath, outputPath,setpts,atempo,frameRate)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                var message=logMessage.text
                Log.d("FFMPEG_","process: ${message}")
                if (message.contains("size=") && message.contains("time="))
                {
                    message = Functions.decodeFFMPEGMessage(message)
                    val bundle=Bundle()
                    bundle.putString("action","process")
                    bundle.putString("message",message)
                    callback.onResponce(bundle)
                }

            }

            override fun success() {
                Log.d("FFMPEG_","success: ")
                val bundle=Bundle()
                bundle.putString("action","success")
                try {
                    Functions.copyFile(File(outputPath), File(inputPath))
                    Functions.clearFilesCacheBeforeOperation(File(outputPath))
                } catch (e: Exception) {
                    Functions.printLog(Constants.tag, "" + e)
                }
                bundle.putString("path",inputPath)
                callback.onResponce(bundle)
            }

            override fun cancel() {
                Log.d("FFMPEG_","cancel: ")
                val bundle=Bundle()
                bundle.putString("action","cancel")
                callback.onResponce(bundle)
            }

            override fun failed() {
                Log.d("FFMPEG_","failed: ")
                val bundle=Bundle()
                bundle.putString("action","failed")
                callback.onResponce(bundle)
            }
        })
    }





    fun combineImagesToVideo(activity:Activity,photoPaths: ArrayList<String>, output:String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        for (i in 0 until photoPaths.size) {
            val imagePath: String = photoPaths.get(i)
            val duration: Int = (Constants.MAX_TIME_FOR_VIDEO_PICS/photoPaths.size)
            //for input
            inputs.add("-loop")
            inputs.add("1")
            inputs.add("-t")
            inputs.add("$duration")
            inputs.add("-i")
            inputs.add("$imagePath")
        }

        var query: String= ""
        var queryAudio: String = ""
        for (i in 0 until photoPaths.size) {
            query = query?.trim()!!
            query += "[" + i + ":v]scale=${Functions.getPhoneResolution(activity).widthPixels}x${Functions.getPhoneResolution(activity).heightPixels},setdar=${Functions.getPhoneResolution(activity).widthPixels}/${Functions.getPhoneResolution(activity).heightPixels}[" + i + "v];"

            queryAudio = queryAudio?.trim()!!
            queryAudio +="[" + i + "v][" + photoPaths.size + ":a]"
        }
        return getCombineImagesToVideo(inputs, query, queryAudio, photoPaths, output)
    }
    private fun getCombineImagesToVideo(inputs:ArrayList<String>, query: String, queryAudio: String, paths: ArrayList<String>, output: String): Array<String> {
        Log.d(Constants.tag,"inputsquery: ${inputs}")
        val width = 620
        val height = 1102
        inputs.apply {
            add("-f")
            add("lavfi")
            add("-t")
            add("0.1")
            add("-i")
            add("anullsrc")
            add("-filter_complex")
            add(query + queryAudio + "concat=n=" + paths.size + ":v=1:a=1 [v][a]")
            add("-s")
            add("${width}x${height}")
            add("-map")
            add("[v]")
            add("-map")
            add("[a]")
            add("-r")
            add("25")
            add("-vcodec")
            add("mpeg4")
            add("-b:v")
            add("15M")
            add("-b:a")
            add("48000")
            add("-ac")
            add("2")
            add("-ar")
            add("22050")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        Log.d(Constants.tag,"inputsfinal: ${inputs}")
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }


    fun addVideoWaterMark(inputVideo: String, imageInput: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-i")
            add(imageInput)
            add("-filter_complex")
            add("[0:v][1:v]overlay=0:0")
            add("-r")
            add("25")
            add("-vcodec")
            add("mpeg4")
            add("-b:v")
            add("15M")
            add("-c:a")
            add("copy")
            add("-preset")
            add("ultrafast")
            add("-max_muxing_queue_size")
            add("9999")
            add(output)
        }
        Log.d(Constants.tag,"inputs AddImage: ${inputs}")
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }


    fun highToLowCompressor(inputVideo: String, outputVideo: String,frameRate:Int): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-vf")
            add("scale=w='min(620,iw)':h='min(1102,ih)', pad=620:1102:(620-iw)/2:(1102-ih)/2:black")
            add("-r")
            add("${if (frameRate >= 10) frameRate - 5 else frameRate}")
            add("-vcodec")
            add("mpeg4")
            add("-b:v")
            add("15M")
            add("-b:a")
            add("48000")
            add("-ac")
            add("2")
            add("-ar")
            add("22050")
            add("-preset")
            add("ultrafast")
            add(outputVideo)
        }
        Log.d(Constants.tag,"inputs Compression: ${inputs}")
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }


    fun compressVideoProcess(activity: Activity, videoFile:File,
                                      frameRate:Int,
                                      callback: FragmentCallBack) {
        val outputPath = Common.getFilePath(activity, Common.VIDEO)
        val query = compressor(videoFile.absolutePath, outputPath,frameRate)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                var message=logMessage.text
                Log.d("FFMPEG_","process: ${message}")
                if (message.contains("size=") && message.contains("time="))
                {
                    message = Functions.decodeFFMPEGMessage(message)
                    val bundle=Bundle()
                    bundle.putString("action","process")
                    bundle.putString("message",message)
                    callback.onResponce(bundle)
                }

            }

            override fun success() {
                Log.d("FFMPEG_","success: ")
                val bundle=Bundle()
                bundle.putString("action","success")
                bundle.putString("path",outputPath)
                callback.onResponce(bundle)
            }

            override fun cancel() {
                Log.d("FFMPEG_","cancel: ")
                val bundle=Bundle()
                bundle.putString("action","cancel")
                callback.onResponce(bundle)
            }

            override fun failed() {
                Log.d("FFMPEG_","failed: ")
                val bundle=Bundle()
                bundle.putString("action","failed")
                callback.onResponce(bundle)
            }
        })
    }


    fun compressor(inputVideo: String, outputVideo: String,frameRate:Int): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-vf")
            add("scale=w='min(620,iw)':h='min(1102,ih)', pad=620:1102:(620-iw)/2:(1102-ih)/2:black")
            add("-r")
            add("${if (frameRate >= 10) frameRate - 5 else frameRate}")
            add("-vcodec")
            add("mpeg4")
            add("-b:v")
            add("15M")
            add("-b:a")
            add("48000")
            add("-ac")
            add("2")
            add("-ar")
            add("22050")
            add("-preset")
            add("ultrafast")
            add(outputVideo)
        }
        Log.d(Constants.tag,"inputs Compression: ${inputs}")
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }



    fun cutVideo(inputVideoPath: String, startTime: String?, endTime: String?, output: String): Array<String> { Common.getFrameRate(inputVideoPath)
        val inputs: ArrayList<String> = ArrayList()
//        inputs.apply {
//            add("-i")
//            add(inputVideoPath)
//            add("-async")
//            add("1")
//            add("-ss")
//            add(startTime.toString())
//            add("-to")
//            add(endTime.toString())
//            add("-c:v")
//            add("copy")
//            add("-c:a")
//            add("copy")
//            add(output)
//        }

        inputs.apply {
            add("-i")
            add(inputVideoPath)
            add("-async")
            add("1")
            add("-ss")
            add(startTime.toString())
            add("-to")
            add(endTime.toString())
            add("-r")
            add("25")
            add("-vcodec")
            add("mpeg4")
            add("-preset")
            add("ultrafast")
            add("-max_muxing_queue_size")
            add("9999")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }


    fun videoMotion(inputVideo: String, output: String, setpts: Double, atempo: Double,frameRate:Int): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-filter_complex")
            add("[0:v]setpts=${setpts}*PTS[v];[0:a]atempo=${atempo}[a]")
            add("-map")
            add("[v]")
            add("-map")
            add("[a]")
            add("-b:v")
            add("15M")
            add("-b:a")
            add("48000")
            add("-r")
            add("$frameRate")
            add("-vcodec")
            add("mpeg4")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }


}