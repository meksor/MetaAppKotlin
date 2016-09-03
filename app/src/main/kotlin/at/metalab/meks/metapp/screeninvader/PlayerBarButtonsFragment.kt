package at.metalab.meks.metapp.screeninvader

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.metalab.meks.metapp.R
import org.jetbrains.anko.find

/**
 * Created by meks on 03.09.2016.
 */
class PlayerBarButtonsFragment() : PlayerBarBaseFragment() {

    object Buttons {
        lateinit var mReplayButton : View
        lateinit var mTorrentButton : View
        lateinit var mBrowserButton : View
        lateinit var mShairplayButton : View
        lateinit var mClearButton : View
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_playerbar_buttons, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Buttons.mReplayButton = find(R.id.playerbar_button_replay)
        Buttons.mTorrentButton = find(R.id.playerbar_button_torrents)
        Buttons.mBrowserButton = find(R.id.playerbar_button_browser)
        Buttons.mShairplayButton = find(R.id.playerbar_button_airplay)
        Buttons.mClearButton = find(R.id.playerbar_button_clear_playlist)

        Buttons.mReplayButton.setOnClickListener(activity as ScreenInvaderActivtiy)
        Buttons.mTorrentButton.setOnClickListener(activity as ScreenInvaderActivtiy)
        Buttons.mBrowserButton.setOnClickListener(activity as ScreenInvaderActivtiy)
        Buttons.mShairplayButton.setOnClickListener(activity as ScreenInvaderActivtiy)
        Buttons.mClearButton.setOnClickListener(activity as ScreenInvaderActivtiy)
    }

    override fun onFragmentViewUpdated(type: ScreenInvaderActivtiy.View, enabled : Boolean) {
        when (type) {
            ScreenInvaderActivtiy.View.BUTTON_TORRENTS -> {
                Buttons.mTorrentButton.background.setColorFilter(if (enabled) R.color.meta_orange else R.color.meta_dunkelblau, PorterDuff.Mode.SRC_ATOP )
            }
            ScreenInvaderActivtiy.View.BUTTON_SHAIRPLAY -> {
                Buttons.mShairplayButton.background.setColorFilter(if (enabled) R.color.meta_orange else R.color.meta_dunkelblau, PorterDuff.Mode.SRC_ATOP )
            }
        }
    }

    override fun getType(): FragmentType {
        return FragmentType.BUTTONS
    }
}