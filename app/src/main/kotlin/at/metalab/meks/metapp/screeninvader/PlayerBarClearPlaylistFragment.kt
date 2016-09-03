package at.metalab.meks.metapp.screeninvader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.metalab.meks.metapp.R
import org.jetbrains.anko.find

/**
 * Created by meks on 03.09.2016.
 */
class PlayerBarClearPlaylistFragment() : PlayerBarBaseFragment() {

    lateinit var mConfirmClearButton: View
    lateinit var mCancelClearButton: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_playerbar_clear_playlist, container, false)
        setRevealAnimation(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mConfirmClearButton = find(R.id.playerbar_button_confirm_clear_playlist)
        mCancelClearButton = find(R.id.playerbar_button_cancel_clear_playlist)

        mConfirmClearButton.setOnClickListener(activity as ScreenInvaderActivtiy)
        mCancelClearButton.setOnClickListener(activity as ScreenInvaderActivtiy)
    }

    override fun getType(): FragmentType {
        return FragmentType.CLEAR
    }

    override fun onFragmentViewUpdated(type: ScreenInvaderActivtiy.View, enabled: Boolean) {
    }
}