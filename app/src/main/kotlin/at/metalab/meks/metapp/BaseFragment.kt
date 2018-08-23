package at.metalab.meks.metapp

import android.graphics.Point
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewAnimationUtils
import at.metalab.meks.metapp.screeninvader.ScreenInvaderFragment

/**
 * Created by meks on 8/23/18.
 */

abstract class BaseFragment : Fragment() {
    abstract val actionBarTitle : Int
}