package at.metalab.meks.metapp

import android.app.Activity
import android.os.Bundle
import at.metalab.meks.metapp.screeninvader.ScreenInvaderActivtiy
import org.jetbrains.anko.intentFor

/**
 * Created by meks on 01.09.2016.
 */
class LaunchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(intentFor<ScreenInvaderActivtiy>())
    }

}