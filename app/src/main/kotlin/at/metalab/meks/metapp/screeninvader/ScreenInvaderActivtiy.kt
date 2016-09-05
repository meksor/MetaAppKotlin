package at.metalab.meks.metapp.screeninvader

import android.app.FragmentTransaction
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import at.metalab.meks.metapp.BaseDrawerActivity
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.convertDpToPixel
import at.metalab.meks.metapp.screeninvader.pojo.ScreeninvaderObject
import at.metalab.meks.metapp.screeninvader.fragments.PlayerBarBaseFragment
import at.metalab.meks.metapp.screeninvader.fragments.PlayerBarButtonsFragment
import at.metalab.meks.metapp.screeninvader.fragments.PlayerBarClearPlaylistFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by meks on 01.09.2016.
 */
class ScreenInvaderActivtiy : BaseDrawerActivity(),
        View.OnClickListener,
        ScreenInvaderAPI.OnScreenInvaderMessageListener {

    private val mScreenInvaderAPI : ScreenInvaderAPI = ScreenInvaderAPI(this)
    private lateinit var mScreenInvaderObject: ScreeninvaderObject
    private var mLastVolume = "20"

    private lateinit var mProgressBar : ProgressBar
    private lateinit var mVolumeBar: SeekBar
    private lateinit var mPlayerBarMoreLayout: LinearLayout
    private lateinit var mPlaylistRecyclerView : RecyclerView

    private lateinit var mCurrentPlayerbarFragment : PlayerBarBaseFragment

    private object Buttons {
        lateinit var mPlayButton : ImageButton
        lateinit var mMoreButton : ImageButton
        lateinit var mPreviousButton : ImageButton
        lateinit var mNextButton : ImageButton
        lateinit var mShuffleButton : ImageButton
        lateinit var mMuteButton : ImageButton
    }

    var mMoreExpanded : Boolean = false
    var mSyncedWithScreenInvader = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val activityView = inflater.inflate(R.layout.activity_content_screeninvader, mRootLayout, false)
        mRootLayout.addView(activityView)

        mPlayerBarMoreLayout = findViewById(R.id.playerbar_more_layout) as LinearLayout
        mProgressBar = findViewById(R.id.playerbar_progressbar) as ProgressBar

        mPlaylistRecyclerView = findViewById(R.id.screeninvader_recyclerview) as RecyclerView
        mPlaylistRecyclerView.layoutManager = LinearLayoutManager(this)

        mPlaylistRecyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
                return null
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            }
            override fun getItemCount() : Int {
                return 0
            }
        }

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
                updateUiComponent(UiComponent.BUTTON_MUTE)
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
                updateUiComponent(UiComponent.BUTTON_MORE)
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
        if (!mSyncedWithScreenInvader && message == ScreenInvaderAPI.Message.FULL_SYNC){
            onFullSync(data)
        } else if (!mSyncedWithScreenInvader){
            return
        }
        when (message) {
            ScreenInvaderAPI.Message.NOTIFY_SEND -> {

            }
            ScreenInvaderAPI.Message.PLAYER_TIME_POS -> {
                val playerTimePos = parseSimpleGson<ArrayList <String>>(data)
                val sdf = SimpleDateFormat("hh:mm:ss")
                try {
                    val currentTime = sdf.parse(playerTimePos[0].replace("\\", ""))
                    val totalTime = sdf.parse(playerTimePos[1].replace("\\", ""))
                    val percentage = getPercentageLeft(currentTime, totalTime)

                    this@ScreenInvaderActivtiy.runOnUiThread({
                        mProgressBar.isIndeterminate = false
                        mProgressBar.progress = (100 - percentage)
                    })

                } catch (e : ParseException) {
                    Log.i("Unparsable SI Message", data)
                }
            }
            ScreenInvaderAPI.Message.PLAYER_PAUSE_STATUS -> {
                mScreenInvaderObject.player.paused = data
                updateUiComponent(UiComponent.BUTTON_PLAY)
            }
            ScreenInvaderAPI.Message.SHAIRPORT_ACTIVE_STATUS -> {
                mScreenInvaderObject.shairport.active = data
                getFragmentViewUpdateListener().onFragmentViewUpdated(UiComponent.BUTTON_SHAIRPLAY, data.replace(" ", "") == "true")
            }
            ScreenInvaderAPI.Message.VOLUME_CHANGED -> {
                mScreenInvaderObject.sound.volume = data.replace(" ", "")
                mVolumeBar.progress = data.replace(" ", "").toInt()
            }
        }
    }

    private fun onFullSync(data: String){
        val type = object : TypeToken<ScreeninvaderObject>() {}.type
        mScreenInvaderObject = Gson().fromJson<ScreeninvaderObject>(data, type)
        mSyncedWithScreenInvader = true

        onWebsocketConnectionStatusChanged(true)
        updateUiComponent(UiComponent.PLAYLIST_VIEW)
        updateUiComponent(UiComponent.BUTTON_PLAY)
        updateUiComponent(UiComponent.BUTTON_MUTE)
        updateUiComponent(UiComponent.VOLUME_BAR)
    }

    enum class UiComponent {
        BUTTON_PLAY,
        BUTTON_MORE,
        BUTTON_MUTE,
        VOLUME_BAR,
        BUTTON_SHAIRPLAY,
        BUTTON_TORRENTS,
        PLAYLIST_VIEW
    }

    private fun updateUiComponent(component : UiComponent){
        this@ScreenInvaderActivtiy.runOnUiThread({
            when (component) {
                UiComponent.BUTTON_PLAY -> {
                        if (mScreenInvaderObject.player.paused == "true") {
                            Buttons.mPlayButton.setImageDrawable(getDrawable(R.drawable.ic_play_playerbar))
                        } else {
                            Buttons.mPlayButton.setImageDrawable(getDrawable(R.drawable.ic_pause_playerbar))
                        }
                }
                UiComponent.BUTTON_MORE -> {
                    toggleSlidePlayerbarLayout()
                }
                UiComponent.BUTTON_MUTE -> {
                    if (mScreenInvaderObject.sound.volume.replace(" ", "").toInt() > 0) {
                        Buttons.mMuteButton.setImageDrawable(getDrawable(R.drawable.ic_volume_playerbar))
                    } else {
                        Buttons.mMuteButton.setImageDrawable(getDrawable(R.drawable.ic_mute_playerbar))
                    }
                }
                UiComponent.VOLUME_BAR -> {
                    mVolumeBar.progress = mScreenInvaderObject.sound.volume.toInt()
                }
                UiComponent.PLAYLIST_VIEW -> {
                    val adapter = PlaylistAdapter(this, mScreenInvaderObject, mScreenInvaderAPI)
                    mPlaylistRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
                else -> {
                    //TODO: Other things
                }
            }
        })
    }

    override fun onWebsocketOpened() {
    }

    override fun onWebsocketError(exception: Exception) {
        onWebsocketConnectionStatusChanged(false)
        exception.printStackTrace()
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

    private fun onWebsocketConnectionStatusChanged(opened: Boolean) {
        this@ScreenInvaderActivtiy.runOnUiThread({
            Buttons.mShuffleButton.isEnabled = opened
            Buttons.mMoreButton.isEnabled = opened
            Buttons.mPlayButton.isEnabled = opened
            Buttons.mNextButton.isEnabled = opened
            Buttons.mPreviousButton.isEnabled = opened

            Buttons.mShuffleButton.alpha = if (opened) 1f else 0.54f
            Buttons.mMoreButton.alpha = if (opened) 1f else 0.54f
            Buttons.mNextButton.alpha = if (opened) 1f else 0.54f
            Buttons.mPreviousButton.alpha = if (opened) 1f else 0.54f
            Buttons.mPlayButton.background = if (opened) getDrawable(R.drawable.background_circle_blue_ripple)
                                                else getDrawable(R.drawable.background_circle_grey_ripple)
        })
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
        fun onFragmentViewUpdated(type : UiComponent, enabled: Boolean)
    }

    fun getFragmentViewUpdateListener() : FragmentViewUpdateListener {
        return mCurrentPlayerbarFragment
    }
}
