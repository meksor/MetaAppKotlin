package at.metalab.meks.metapp.screeninvader

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import at.metalab.meks.metapp.BaseDrawerActivity
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.screeninvader.pojo.ScreeninvaderObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by meks on 01.09.2016.
 */
class ScreenInvaderActivtiy : BaseDrawerActivity(), View.OnClickListener, ScreenInvaderAPI.OnScreenInvaderMessageListener {

    val mScreenInvaderAPI : ScreenInvaderAPI = ScreenInvaderAPI(this)

    lateinit var mProgressBar : ProgressBar
    lateinit var mPlayButton : FloatingActionButton
    lateinit var screenInvaderObject : ScreeninvaderObject

    object mPlayer {
        var paused = false
        var shairportActive = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val activityView = inflater.inflate(R.layout.activity_content_screeninvader, mRootLayout, false)
        mRootLayout.addView(activityView)

        mProgressBar = findViewById(R.id.playerbar_progressbar) as ProgressBar
        mPlayButton = findViewById(R.id.playerbar_button_play) as FloatingActionButton
        mProgressBar
        mScreenInvaderAPI.connectWebSocket()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.playerbar_button_play -> {
                mScreenInvaderAPI.sendSICommand(
                        if (!mPlayer.paused)
                            ScreenInvaderAPI.COMMANDS.PLAYER_PAUSE
                        else
                            ScreenInvaderAPI.COMMANDS.PLAYER_PLAY)
            }
            R.id.playerbar_button_previous -> {
                mScreenInvaderAPI.sendSICommand(ScreenInvaderAPI.COMMANDS.PLAYER_PREVIOUS)
            }
            R.id.playerbar_button_next -> {
                mScreenInvaderAPI.sendSICommand(ScreenInvaderAPI.COMMANDS.PLAYER_NEXT)
            }
        }
    }

    override fun onScreenInvaderMessage(message : ScreenInvaderAPI.Message, data: String) {
        when (message){
            ScreenInvaderAPI.Message.FULL_SYNC -> {onFullSync(data)}

            ScreenInvaderAPI.Message.NOTIFY_SEND -> {

            }

            ScreenInvaderAPI.Message.PLAYER_TIME_POS -> {
                val type = object : TypeToken<Array<String>>() {}.type
                val playerTimePos = Gson().fromJson<Array<String>>(data, type)

                val sdf = SimpleDateFormat("hh:mm:ss", Locale.GERMAN)
                val currentTime = sdf.parse(playerTimePos[0].replace("\\", ""))
                val totalTime = sdf.parse(playerTimePos[1].replace("\\", ""))
                val percentage = getPercentageLeft(currentTime, totalTime)

                Log.d("progress:", percentage.toString())

            }
            ScreenInvaderAPI.Message.PLAYER_PAUSE_STATUS -> {
                screenInvaderObject.player.paused = (data == "true")

            }
            ScreenInvaderAPI.Message.SHAIRPORT_ACTIVE_STATUS -> {
                mPlayer.shairportActive = (data == "true")
            }
        }
    }

    fun onFullSync(data: String){
        val type = object : TypeToken<ScreeninvaderObject>() {}.type
        screenInvaderObject = Gson().fromJson<ScreeninvaderObject>(data, type)
    }

    fun getPercentageLeft(current: Date, end: Date): Int {
        val now = current.time
        val s = 0
        val e = end.time
        if (s >= e || now >= e) {
            return 0
        }
        if (now <= s) {
            return 100
        }
        return ((e - now) * 100 / (e - s)).toInt()
    }

}
