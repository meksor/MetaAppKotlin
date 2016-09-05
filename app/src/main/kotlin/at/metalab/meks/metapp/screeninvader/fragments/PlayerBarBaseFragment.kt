package at.metalab.meks.metapp.screeninvader.fragments

import android.app.Fragment
import android.graphics.Point
import android.view.View
import android.view.ViewAnimationUtils
import at.metalab.meks.metapp.pxFromDp
import at.metalab.meks.metapp.screeninvader.ScreenInvaderActivtiy

/**
 * Created by meks on 03.09.2016.
 */
abstract class PlayerBarBaseFragment : Fragment() , ScreenInvaderActivtiy.FragmentViewUpdateListener {

    enum class FragmentType {
        BUTTONS,
        CLEAR,
        BROWSER
    }

    abstract fun getType() : FragmentType

    override abstract fun onFragmentViewUpdated(type: ScreenInvaderActivtiy.UiComponent, enabled : Boolean)

    open fun setRevealAnimation(view : View) {
        view.visibility = View.INVISIBLE
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)

                val display = activity.windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)

                val cx = view.width / 2
                val cy  = view.height / 2

                val finalRadius = Math.max(view.width, view.height).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius)
                view.visibility = View.VISIBLE
                anim.duration = 300
                anim.start()
            }
        })
    }
}