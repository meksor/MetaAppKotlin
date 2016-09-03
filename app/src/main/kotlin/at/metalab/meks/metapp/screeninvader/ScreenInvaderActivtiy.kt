package at.metalab.meks.metapp.screeninvader

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import at.metalab.meks.metapp.BaseDrawerActivity
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.convertDpToPixel
import at.metalab.meks.metapp.pxFromDp
import at.metalab.meks.metapp.screeninvader.pojo.ScreeninvaderObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.header_nav_activity_base.view.*
import org.jetbrains.anko.image
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by meks on 01.09.2016.
 */
class ScreenInvaderActivtiy : BaseDrawerActivity(), View.OnClickListener, ScreenInvaderAPI.OnScreenInvaderMessageListener {

    val mScreenInvaderAPI : ScreenInvaderAPI = ScreenInvaderAPI(this)
    lateinit var mScreenInvaderObject: ScreeninvaderObject
    var lastVolume = "20"

    lateinit var mProgressBar : ProgressBar
    lateinit var mVolumeBar: SeekBar
    lateinit var mPlayButton : ImageButton
    lateinit var mMuteButton : ImageButton
    lateinit var mMoreButton : ImageButton
    lateinit var mPlayerBarMoreLayout: LinearLayout

    var mMoreExpanded : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val activityView = inflater.inflate(R.layout.activity_content_screeninvader, mRootLayout, false)
        mRootLayout.addView(activityView)

        mProgressBar = findViewById(R.id.playerbar_progressbar) as ProgressBar
        mPlayButton = findViewById(R.id.playerbar_button_play) as ImageButton
        mMoreButton = findViewById(R.id.playerbar_button_more) as ImageButton
        mMuteButton = findViewById(R.id.playerbar_button_mute) as ImageButton
        mPlayerBarMoreLayout = findViewById(R.id.playerbar_more_layout) as LinearLayout

        mPlayButton.setOnClickListener(this)
        findViewById(R.id.playerbar_button_previous).setOnClickListener(this)
        findViewById(R.id.playerbar_button_next).setOnClickListener(this)
        findViewById(R.id.playerbar_button_shuffle).setOnClickListener(this)
        mMoreButton.setOnClickListener(this)
        mMuteButton.setOnClickListener(this)
        mVolumeBar = findViewById(R.id.playerbar_volume_seekbar) as SeekBar
        mVolumeBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar : SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar : SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar : SeekBar, progress : Int, fromUser : Boolean) {
                updateUiComponent(UiComponent.BUTTON_MUTE)
                if (fromUser) {
                    mScreenInvaderAPI.sendSICommandTrigger(ScreenInvaderAPI.COMMANDS.VOLUME_SET, progress.toString())
                }
            }
        })


        mScreenInvaderAPI.connectWebSocket()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.playerbar_button_play -> {
                mScreenInvaderAPI.sendSICommandPublish(
                        if (mScreenInvaderObject.player.paused != "true")
                            ScreenInvaderAPI.COMMANDS.PLAYER_PAUSE
                        else
                            ScreenInvaderAPI.COMMANDS.PLAYER_PLAY)
            }
            R.id.playerbar_button_previous -> {
                mScreenInvaderAPI.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYER_PREVIOUS)
            }
            R.id.playerbar_button_next -> {
                mScreenInvaderAPI.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYER_NEXT)
            }
            R.id.playerbar_button_shuffle -> {
                if (mScreenInvaderObject.playlist.index.toInt() > 0) {
                    val shuffleValue = Random().nextInt(mScreenInvaderObject.playlist.index.toInt())
                    mScreenInvaderAPI.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYER_JUMP, shuffleValue.toString())
                }
            }
            R.id.playerbar_button_more  -> {
                updateUiComponent(UiComponent.BUTTON_MORE)
            }
            R.id.playerbar_button_mute -> {
                if (mScreenInvaderObject.sound.volume.replace(" ","").toInt() > 0){
                    lastVolume = mScreenInvaderObject.sound.volume.replace(" ","")
                    mScreenInvaderAPI.sendSICommandTrigger(ScreenInvaderAPI.COMMANDS.VOLUME_SET, "0")
                } else {
                    mScreenInvaderAPI.sendSICommandTrigger(ScreenInvaderAPI.COMMANDS.VOLUME_SET, lastVolume)
                }
            }
        }
    }

    override fun onScreenInvaderMessage(message : ScreenInvaderAPI.Message, data: String) {
        when (message){
            ScreenInvaderAPI.Message.FULL_SYNC -> {onFullSync(data)}

            ScreenInvaderAPI.Message.NOTIFY_SEND -> {

            }

            ScreenInvaderAPI.Message.PLAYER_TIME_POS -> {
                val playerTimePos = parseSimpleGson<ArrayList <String>>(data)

                    val sdf = SimpleDateFormat("hh:mm:ss")
                if(playerTimePos[0] != "V:" && playerTimePos[0] != "lib") {
                    val currentTime = sdf.parse(playerTimePos[0].replace("\\", ""))
                    val totalTime = sdf.parse(playerTimePos[1].replace("\\", ""))
                    val percentage = getPercentageLeft(currentTime, totalTime)

                    mProgressBar.isIndeterminate = false
                    mProgressBar.progress = 100 - percentage
                }
            }
            ScreenInvaderAPI.Message.PLAYER_PAUSE_STATUS -> {
                mScreenInvaderObject.player.paused = data
                updateUiComponent(UiComponent.BUTTON_PLAY)
            }
            ScreenInvaderAPI.Message.SHAIRPORT_ACTIVE_STATUS -> {
                mScreenInvaderObject.shairport.active = data
            }
            ScreenInvaderAPI.Message.VOLUME_CHANGED -> {
                mScreenInvaderObject.sound.volume = data.replace(" ","")
                mVolumeBar.progress = data.replace(" ","").toInt()
            }
        }
    }

    fun onFullSync(data: String){
        val type = object : TypeToken<ScreeninvaderObject>() {}.type
        mScreenInvaderObject = Gson().fromJson<ScreeninvaderObject>(data, type)

        updateUiComponent(UiComponent.BUTTON_PLAY)
        updateUiComponent(UiComponent.BUTTON_MUTE)
        updateUiComponent(UiComponent.VOLUME_BAR)
    }

    enum class UiComponent {
        BUTTON_PLAY,
        BUTTON_MORE,
        BUTTON_MUTE,
        VOLUME_BAR,
    }

    fun updateUiComponent(component : UiComponent){
        when(component){
            UiComponent.BUTTON_PLAY -> {
                this@ScreenInvaderActivtiy.runOnUiThread( {
                    if (mScreenInvaderObject.player.paused == "true"){
                        mPlayButton.setImageDrawable(getDrawable(R.drawable.ic_play_playerbar))
                    } else {
                        mPlayButton.setImageDrawable(getDrawable(R.drawable.ic_pause_playerbar))
                    }
                })
            }
            UiComponent.BUTTON_MORE -> {
                toggleSlidePlayerbarLayout()
            }
            UiComponent.BUTTON_MUTE -> {
                if (mScreenInvaderObject.sound.volume.replace(" ","").toInt() > 0){
                    mMuteButton.setImageDrawable(getDrawable(R.drawable.ic_volume_playerbar))
                } else {
                    mMuteButton.setImageDrawable(getDrawable(R.drawable.ic_mute_playerbar))
                }
            }
            UiComponent.VOLUME_BAR -> {
                mVolumeBar.progress = mScreenInvaderObject.sound.volume.toInt()
            }
        }
    }

    fun toggleSlidePlayerbarLayout() {
        if (!mMoreExpanded){
            mPlayerBarMoreLayout.animate()
                    .translationYBy(convertDpToPixel(144f))
                    .translationY(0f)
                    .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong()).start()
            mMoreButton.animate().rotationBy(180f).start()
            mMoreExpanded = true

        } else {
            mPlayerBarMoreLayout.animate()
                    .translationYBy(0f)
                    .translationY(convertDpToPixel(144f))
                    .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong()).start()
            mMoreButton.animate().rotationBy(180f).start()
            mMoreExpanded = false
        }
    }

    fun getPercentageLeft(current: Date, end: Date): Int {
        val sdf = SimpleDateFormat("hh:mm:ss")
        val now = current.time
        val s =  sdf.parse("00:00:00").time
        val e = end.time
        if (s >= e || now >= e) {
            return 0
        }
        if (now <= s) {
            return 100
        }
        return ((e - now) * 100 / (e - s)).toInt()
    }

    fun <T>parseSimpleGson(data : String) : T{
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson<T>(data, type)
    }

}
