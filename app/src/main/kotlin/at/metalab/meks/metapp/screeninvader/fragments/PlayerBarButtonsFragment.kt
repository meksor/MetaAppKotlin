package at.metalab.meks.metapp.screeninvader.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.screeninvader.ScreenInvaderFragment
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
        Buttons.mReplayButton = view!!.find(R.id.playerbar_button_replay)
        Buttons.mTorrentButton = view!!.find(R.id.playerbar_button_torrents)
        Buttons.mBrowserButton = view!!.find(R.id.playerbar_button_browser)
        Buttons.mShairplayButton = view!!.find(R.id.playerbar_button_airplay)
        Buttons.mClearButton = view!!.find(R.id.playerbar_button_clear_playlist)

        Buttons.mReplayButton.setOnClickListener(activity as ScreenInvaderFragment)
        Buttons.mTorrentButton.setOnClickListener(activity as ScreenInvaderFragment)
        Buttons.mBrowserButton.setOnClickListener(activity as ScreenInvaderFragment)
        Buttons.mShairplayButton.setOnClickListener(activity as ScreenInvaderFragment)
        Buttons.mClearButton.setOnClickListener(activity as ScreenInvaderFragment)
    }

    override fun onFragmentViewUpdated(type: ScreenInvaderFragment.UiComponent, enabled : Boolean) {
        when (type) {
            ScreenInvaderFragment.UiComponent.BUTTON_TORRENTS -> {
                Buttons.mTorrentButton.background.setColorFilter(if (enabled) R.color.meta_orange else R.color.meta_dunkelblau, PorterDuff.Mode.SRC_ATOP)
            }
            ScreenInvaderFragment.UiComponent.BUTTON_SHAIRPLAY -> {
                Buttons.mShairplayButton.background.setColorFilter(if (enabled) R.color.meta_orange else R.color.meta_dunkelblau, PorterDuff.Mode.SRC_ATOP)
            }
            else ->{
                //TODO: Implement other buttons
            }
        }
    }

    override fun getType(): FragmentType {
        return FragmentType.BUTTONS
    }
}