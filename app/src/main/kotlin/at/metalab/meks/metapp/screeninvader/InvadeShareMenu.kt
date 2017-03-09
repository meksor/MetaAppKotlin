package at.metalab.meks.metapp.screeninvader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import at.metalab.meks.metapp.R

/**
 * Created by meks on 19.01.17.
 */

class InvadeShareMenu : Activity(), ScreenInvaderAPI.OnScreenInvaderMessageListener {

    lateinit var mUrl : String
    lateinit var mScreeninvaderApi : ScreenInvaderAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI)
        if (networkInfo.isConnected()) {
            //TODO: Check if we know a ScreenInvader on this network
            val intent = intent
            val type = intent.type
            mScreeninvaderApi = ScreenInvaderAPI(this)

            if (type.startsWith("text/")) {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                val pattern = Patterns.WEB_URL
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    mUrl = matcher.group()
                    mScreeninvaderApi.connectWebSocket()
                }
            } //TODO: Add support for other types (file upload)
        } else {
            //TODO: Display a prompt to connect to a WiFi
            Toast.makeText(applicationContext,
                    getString(R.string.no_wifi_toast), Toast.LENGTH_LONG).show()
        }
    }

    //Dummy Screeninvader Interface to Instanciate the API
    override fun onScreenInvaderPlaylistUpdated(item: Int, dataType: String, value: String) {
    }

    override fun onWebsocketError(exception: Exception) {
    }

    override fun onWebsocketOpened() {
        mScreeninvaderApi.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYLIST_LOAD, mUrl)
        finish()
    }

    override fun onScreenInvaderMessage(message: ScreenInvaderAPI.Message, data: String) {
    }

}
