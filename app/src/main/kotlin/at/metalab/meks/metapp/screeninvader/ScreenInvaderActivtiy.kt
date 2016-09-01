package at.metalab.meks.metapp.screeninvader

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import at.metalab.meks.metapp.BaseDrawerActivity
import at.metalab.meks.metapp.R

/**
 * Created by meks on 01.09.2016.
 */
class ScreenInvaderActivtiy : BaseDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.activity_content_screeninvader, mRootLayout, true)
    }
}
