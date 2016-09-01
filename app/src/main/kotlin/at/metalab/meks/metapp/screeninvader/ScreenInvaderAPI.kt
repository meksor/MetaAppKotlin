package at.metalab.meks.metapp.screeninvader

import android.os.Looper
import android.util.Log

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject

import java.net.URI
import java.net.URISyntaxException

/**
 * Created by meks on 21.07.2016.
 */

class ScreenInvaderAPI() {

    object COMMANDS {

        /*
         * Constants for Commands WITHOUT params
        */

        const val PLAYER_PAUSE : String = "playerPause"

        const val PLAYER_PLAY : String = "playerPlay"

        const val PLAYER_NEXT : String = "playerNext"

        const val PLAYER_PREVIOUS : String = "playerPrevious"

        const val SHAIRPORT_START: String = "shairportStart"

        const val SHAIRPORT_STOP : String = "shairportStop"

        /*
         * Constants for Commands WITH params
         */

        const val VOLUME_SET : String = "/sound/volume"

    }

    private var mWebSocketClient: WebSocketClient? = null

    private val currentItemUrl: String? = null
    var playerPaused: Boolean = false
    var shairportActive: Boolean = false
    var playlistIsReady: Boolean = false

    private var playerObject: JSONObject? = null
    private var soundObject: JSONObject? = null
    lateinit var playlistArray: JSONArray

    fun sendSICommand(command: String, param: String) {
        val fullcommand = "[\"publish\", \"$command\",\"W\",\" $param\"]"
        try {
            if (mWebSocketClient!!.getConnection() != null) {
                mWebSocketClient!!.send(fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendSICommand(command: String) {
        val fullcommand = "[\"publish\", \"$command\",\"W\" ]"
        try {
            if (mWebSocketClient!!.getConnection() != null) {
                mWebSocketClient!!.send(fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //WebSockets! foar Screeninvader
    fun connectWebSocket() {
        val uri: URI
        try {
            uri = URI("ws://10.20.30.40:8080")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }

        mWebSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(serverHandshake: ServerHandshake) {
                Log.i("Websocket", "Opened")
                Looper.prepare()
                mWebSocketClient!!.send("setup")
            }

            override fun onMessage(s: String) {
                parseMessage(s)
            }

            override fun onClose(i: Int, s: String, b: Boolean) {
                Log.i("Websocket", "Closed " + s)
            }

            override fun onError(e: Exception) {
                Log.i("Websocket", "Error " + e.message)
            }
        }
        mWebSocketClient!!.connect()
        mWebSocketClient!!.readyState
    }

    private fun parseMessage(s: String) {
        val syncObj: JSONObject
        val event: JSONArray

        if (s.startsWith("{")) {
            val result = s.replace("\n".toRegex(), "")
            try {
                syncObj = JSONObject(result)
                fullSync(syncObj)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            try {
                event = JSONArray(s)
                when (event.getString(0)) {
                    "notifySend" -> {} //TODO; notify the user
                    "playerTimePos" -> {}
                    "/player/paused" -> {
                        playerPaused = event.getString(2) == "true"
                    }
                    "/shairport/active" -> {
                        shairportActive = event.getString(2) == "true"
                    }
                    else -> return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun fullSync(syncObj: JSONObject) {
        try {
            shairportActive = syncObj.getJSONObject("shairport").getString("active") == "true"
            playlistArray = syncObj.getJSONObject("playlist").getJSONArray("items")
            playerObject = syncObj.getJSONObject("player")
            soundObject = syncObj.getJSONObject("sound")
            playlistIsReady = true
            //updatePlaylist(itemArray);
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}