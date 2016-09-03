package at.metalab.meks.metapp.screeninvader

import android.content.Context
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

class ScreenInvaderAPI(val context : Context) {

    object COMMANDS {

        /*
         * Constants for Commands WITHOUT params
        */

        const val PLAYER_PAUSE : String = "playerPause"

        const val PLAYER_PLAY : String = "playerPlay"

        const val PLAYER_NEXT : String = "playerNext"

        const val PLAYER_PREVIOUS : String = "playerPrevious"

        const val PLAYER_JUMP : String = "playerJump"

        const val SHAIRPORT_START: String = "shairportStart"

        const val SHAIRPORT_STOP : String = "shairportStop"

        /*
         * Constants for Commands WITH params
         */

        const val VOLUME_SET : String = "/sound/volume"

    }

    private var mWebSocketClient: WebSocketClient? = null

    private var currentItemUrl: String? = null

    private var playerObject: JSONObject? = null
    private var soundObject: JSONObject? = null
    lateinit var playlistArray: JSONArray

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
                getOnScreenInvaderMessageListener().onWebsocketOpened()
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
                getOnScreenInvaderMessageListener().onWebsocketError(e)
            }
        }
        mWebSocketClient!!.connect()
        mWebSocketClient!!.readyState
    }

    fun sendSICommandPublish(command: String, param: String) {
        val fullcommand = "[\"publish\", \"$command\",\"W\",\"$param\"]"
        try {
            if (mWebSocketClient!!.getConnection() != null) {
                mWebSocketClient!!.send(fullcommand)
                Log.d("Sent:",fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendSICommandPublish(command: String) {
        val fullcommand = "[\"publish\", \"$command\",\"W\" ]"

        try {
            if (mWebSocketClient!!.getConnection() != null) {
                mWebSocketClient!!.send(fullcommand)
                Log.d("Sent:",fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendSICommandTrigger(command: String, param: String) {
        val fullcommand = "[\"trigger\", \"$command\",\"$param\"]"
        try {
            if (mWebSocketClient!!.getConnection() != null) {
                mWebSocketClient!!.send(fullcommand)
                Log.d("Sent:", fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun parseMessage(s: String) {
        val event: JSONArray

        if (s.startsWith("{")) {
            val result = s.replace("\n".toRegex(), "")
                getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.FULL_SYNC, result)
        } else {
            try {
                event = JSONArray(s)
                val eventType = event.getString(0)
                val eventParam = event.getString(2)
                when (eventType) {
                    "notifySend" -> {
                        getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.NOTIFY_SEND, eventParam)
                    }
                    "playerTimePos" -> {
                        getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.PLAYER_TIME_POS, eventParam)
                    }
                    "/player/paused" -> {
                        getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.PLAYER_PAUSE_STATUS, eventParam)
                    }
                    "/shairport/active" -> {
                        getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.SHAIRPORT_ACTIVE_STATUS, eventParam)
                    }
                    "/sound/volume" -> {
                        getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.VOLUME_CHANGED, eventParam)
                    }
                    else -> getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.SHAIRPORT_ACTIVE_STATUS, "Unknown Event Type: " + eventType)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    enum class Message {
        FULL_SYNC,
        NOTIFY_SEND,
        PLAYER_TIME_POS,
        PLAYER_PAUSE_STATUS,
        SHAIRPORT_ACTIVE_STATUS,
        VOLUME_CHANGED
    }

    interface OnScreenInvaderMessageListener {
        fun onScreenInvaderMessage(message : Message, data: String)
        fun onWebsocketError(exception: Exception)
        fun onWebsocketOpened()
    }

    fun getOnScreenInvaderMessageListener() : OnScreenInvaderMessageListener {
        return context as ScreenInvaderActivtiy
    }
}