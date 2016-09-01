package at.metalab.meks.metapp.screeninvader

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import at.metalab.meks.metapp.BaseDrawerActivity
import at.metalab.meks.metapp.R

/**
 * Created by meks on 01.09.2016.
 */
class ScreenInvaderActivtiy : BaseDrawerActivity(), View.OnClickListener, ScreenInvaderAPI.OnScreenInvaderMessageListener {

    val mScreenInvaderAPI : ScreenInvaderAPI = ScreenInvaderAPI(this)

    object mPlayer {
        var paused = false
        var shairportActive = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val activityView = inflater.inflate(R.layout.activity_content_screeninvader, mRootLayout, false)
        mRootLayout.addView(activityView)

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

    override fun onScreenInvaderMessage(message : ScreenInvaderAPI.Message, param: String) {
        when (message){
            ScreenInvaderAPI.Message.FULL_SYNC -> {}
            ScreenInvaderAPI.Message.NOTIFY_SEND -> {}
            ScreenInvaderAPI.Message.PLAYER_TIME_POS -> {}
            ScreenInvaderAPI.Message.PLAYER_PAUSE_STATUS -> {
                mPlayer.paused = (param == "true")

            }
            ScreenInvaderAPI.Message.SHAIRPORT_ACTIVE_STATUS -> {
                mPlayer.shairportActive = (param == "true")
            }
        }
    }

}
