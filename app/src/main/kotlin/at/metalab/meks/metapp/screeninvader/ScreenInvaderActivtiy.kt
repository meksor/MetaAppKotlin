package at.metalab.meks.metapp.screeninvader

import android.app.FragmentTransaction
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import at.metalab.meks.metapp.BaseDrawerActivity
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.convertDpToPixel
import at.metalab.meks.metapp.screeninvader.pojo.ScreeninvaderObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by meks on 01.09.2016.
 */
class ScreenInvaderActivtiy : BaseDrawerActivity(),
        View.OnClickListener,
        ScreenInvaderAPI.OnScreenInvaderMessageListener {

    val mScreenInvaderAPI : ScreenInvaderAPI = ScreenInvaderAPI(this)
    lateinit var mScreenInvaderObject: ScreeninvaderObject
    var mLastVolume = "20"

    lateinit var mProgressBar : ProgressBar
    lateinit var mVolumeBar: SeekBar
    lateinit var mPlayerBarMoreLayout: LinearLayout

    lateinit var mCurrentPlayerbarFragment : PlayerBarBaseFragment

    object Buttons {
        lateinit var mPlayButton : ImageButton
        lateinit var mMoreButton : ImageButton
        lateinit var mPreviousButton : ImageButton
        lateinit var mNextButton : ImageButton
        lateinit var mShuffleButton : ImageButton

        lateinit var mMuteButton : ImageButton
    }

    var mMoreExpanded : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val activityView = inflater.inflate(R.layout.activity_content_screeninvader, mRootLayout, false)
        mRootLayout.addView(activityView)

        mPlayerBarMoreLayout = findViewById(R.id.playerbar_more_layout) as LinearLayout
        mProgressBar = findViewById(R.id.playerbar_progressbar) as ProgressBar

        Buttons.mPlayButton = findViewById(R.id.playerbar_button_play) as ImageButton
        Buttons.mMoreButton = findViewById(R.id.playerbar_button_more) as ImageButton
        Buttons.mMuteButton = findViewById(R.id.playerbar_button_mute) as ImageButton
        Buttons.mPreviousButton = findViewById(R.id.playerbar_button_previous) as ImageButton
        Buttons.mNextButton = findViewById(R.id.playerbar_button_next) as ImageButton
        Buttons.mShuffleButton = findViewById(R.id.playerbar_button_shuffle) as ImageButton

        Buttons.mMoreButton.setOnClickListener(this)
        Buttons.mMuteButton.setOnClickListener(this)
        Buttons.mPlayButton.setOnClickListener(this)
        Buttons.mPreviousButton.setOnClickListener(this)
        Buttons.mNextButton.setOnClickListener(this)
        Buttons.mShuffleButton.setOnClickListener(this)

        mVolumeBar = findViewById(R.id.playerbar_volume_seekbar) as SeekBar
        mVolumeBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar : SeekBar) {
            }

            override fun onStartTrackingTouch(seekBar : SeekBar) {
            }

            override fun onProgressChanged(seekBar : SeekBar, progress : Int, fromUser : Boolean) {
                updateUiComponent(View.BUTTON_MUTE)
                if (fromUser) {
                    mScreenInvaderAPI.sendSICommandTrigger(ScreenInvaderAPI.COMMANDS.VOLUME_SET, progress.toString())
                }
            }
        })
        mScreenInvaderAPI.connectWebSocket()
        showFragment(PlayerBarBaseFragment.FragmentType.BUTTONS)
    }

    override fun onClick(p0: android.view.View?) {

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
                updateUiComponent(View.BUTTON_MORE)
            }
            R.id.playerbar_button_mute -> {
                if (mScreenInvaderObject.sound.volume.replace(" ","").toInt() > 0){
                    mLastVolume = mScreenInvaderObject.sound.volume.replace(" ","")
                    mScreenInvaderAPI.sendSICommandTrigger(ScreenInvaderAPI.COMMANDS.VOLUME_SET, "0")
                } else {
                    mScreenInvaderAPI.sendSICommandTrigger(ScreenInvaderAPI.COMMANDS.VOLUME_SET, mLastVolume)
                }
            }
            R.id.playerbar_button_airplay -> {
                mScreenInvaderAPI.sendSICommandPublish(
                        if (mScreenInvaderObject.shairport.active != "true")
                            ScreenInvaderAPI.COMMANDS.SHAIRPORT_STOP
                        else
                            ScreenInvaderAPI.COMMANDS.SHAIRPORT_START)
            }
            R.id.playerbar_button_replay -> {
                mScreenInvaderAPI.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYER_JUMP, "0")
            }
            R.id.playerbar_button_clear_playlist -> {
                showFragment(PlayerBarBaseFragment.FragmentType.CLEAR)
            }
            R.id.playerbar_button_confirm_clear_playlist -> {
                //TODO: Clear Playlist
            }
            R.id.playerbar_button_cancel_clear_playlist -> {
                showFragment(PlayerBarBaseFragment.FragmentType.BUTTONS)
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
                updateUiComponent(View.BUTTON_PLAY)
            }
            ScreenInvaderAPI.Message.SHAIRPORT_ACTIVE_STATUS -> {
                mScreenInvaderObject.shairport.active = data
                getFragmentViewUpdateListener().onFragmentViewUpdated(View.BUTTON_SHAIRPLAY, data.replace(" ","") == "true")
            }
            ScreenInvaderAPI.Message.VOLUME_CHANGED -> {
                mScreenInvaderObject.sound.volume = data.replace(" ","")
                mVolumeBar.progress = data.replace(" ","").toInt()
            }
        }
    }

    private fun onFullSync(data: String){
        val type = object : TypeToken<ScreeninvaderObject>() {}.type
        mScreenInvaderObject = Gson().fromJson<ScreeninvaderObject>(data, type)

        updateUiComponent(View.BUTTON_PLAY)
        updateUiComponent(View.BUTTON_MUTE)
        updateUiComponent(View.VOLUME_BAR)
    }

    enum class View {
        BUTTON_PLAY,
        BUTTON_MORE,
        BUTTON_MUTE,
        VOLUME_BAR,
        BUTTON_SHAIRPLAY,
        BUTTON_TORRENTS,
    }

    private fun updateUiComponent(component : View){
        when(component){
            View.BUTTON_PLAY -> {
                this@ScreenInvaderActivtiy.runOnUiThread( {
                    if (mScreenInvaderObject.player.paused == "true"){
                        Buttons.mPlayButton.setImageDrawable(getDrawable(R.drawable.ic_play_playerbar))
                    } else {
                        Buttons.mPlayButton.setImageDrawable(getDrawable(R.drawable.ic_pause_playerbar))
                    }
                })
            }
            View.BUTTON_MORE -> {
                toggleSlidePlayerbarLayout()
            }
            View.BUTTON_MUTE -> {
                if (mScreenInvaderObject.sound.volume.replace(" ","").toInt() > 0){
                    Buttons.mMuteButton.setImageDrawable(getDrawable(R.drawable.ic_volume_playerbar))
                } else {
                    Buttons.mMuteButton.setImageDrawable(getDrawable(R.drawable.ic_mute_playerbar))
                }
            }
            View.VOLUME_BAR -> {
                mVolumeBar.progress = mScreenInvaderObject.sound.volume.toInt()
            }
        }
    }

    override fun onWebsocketOpened() {
    }

    override fun onWebsocketError(exception: Exception) {
    }


    private fun toggleSlidePlayerbarLayout() {
        if (!mMoreExpanded){
            mPlayerBarMoreLayout.animate()
                    .translationYBy(convertDpToPixel(144f))
                    .translationY(0f)
                    .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong()).start()
            Buttons.mMoreButton.animate().rotationBy(180f).start()
            mMoreExpanded = true

        } else {
            mPlayerBarMoreLayout.animate()
                    .translationYBy(0f)
                    .translationY(convertDpToPixel(144f))
                    .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong()).start()
            Buttons.mMoreButton.animate().rotationBy(180f).start()
            mMoreExpanded = false
        }
    }

    private fun showFragment(type : PlayerBarBaseFragment.FragmentType){
        when (type){
            PlayerBarBaseFragment.FragmentType.BUTTONS -> mCurrentPlayerbarFragment = PlayerBarButtonsFragment()
            PlayerBarBaseFragment.FragmentType.CLEAR -> mCurrentPlayerbarFragment = PlayerBarClearPlaylistFragment()
            PlayerBarBaseFragment.FragmentType.BROWSER -> TODO()
        }
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.playerbar_fragment_container, mCurrentPlayerbarFragment)
        transaction.commitAllowingStateLoss()
    }

    private fun getPercentageLeft(current: Date, end: Date): Int {
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

    private fun <T>parseSimpleGson(data : String) : T{
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson<T>(data, type)
    }

    interface FragmentViewUpdateListener {
        fun onFragmentViewUpdated(type : View, enabled: Boolean)
    }

    fun getFragmentViewUpdateListener() : FragmentViewUpdateListener {
        return mCurrentPlayerbarFragment
    }
}
