package at.metalab.meks.metapp.screeninvader

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import at.metalab.meks.metapp.MainActivity
import org.java_websocket.WebSocket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_10
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray

import java.net.URI
import java.net.URISyntaxException

/**
 * Created by meks on 21.07.2016.
 */

class ScreenInvaderAPI(val context: Context, val msgListener: OnScreenInvaderMessageListener) {

    object COMMANDS {

        /*
         * Constants for Commands WITHOUT params"
        */

        const val PLAYER_PAUSE: String = "playerPause"

        const val PLAYER_PLAY: String = "playerPlay"

        const val PLAYER_NEXT: String = "playerNext"

        const val PLAYER_PREVIOUS: String = "playerPrevious"

        const val PLAYER_JUMP: String = "playerJump"

        const val SHAIRPORT_START : String = "shairportStart"

        const val SHAIRPORT_STOP: String = "shairportStop"

        /*
         * Constants for Commands WITH params
         */

        const val VOLUME_SET: String = "/sound/volume"

        const val PLAYLIST_REMOVE: String = "playlistRemove"

        const val PLAYLIST_LOAD: String = "showUrl"

    }

    private var mWebSocketClient: WebSocketClient? = null

    //WebSockets! foar Screeninvader
    fun connectWebSocket() {
        val uri: URI
        try {
            uri = URI("ws://10.20.30.40:8080")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }

        mWebSocketClient = object : WebSocketClient(uri, Draft_10(), null, 1 / 1000000) {
            override fun onOpen(serverHandshake: ServerHandshake) {
                Looper.prepare()
                Log.i("Websocket", "Opened")
                getOnScreenInvaderMessageListener().onWebsocketOpened()
                if (context is MainActivity) mWebSocketClient!!.send("setup") //If API is being started from MainActivity, send setup to get intial Screeninvader Object
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
            if (mWebSocketClient!!.connection != null) {
                mWebSocketClient!!.send(fullcommand)
                Log.d("Sent:", fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendSICommandPublish(command: String) {
        val fullcommand = "[\"publish\", \"$command\",\"W\" ]"

        try {
            if (mWebSocketClient!!.connection != null) {
                mWebSocketClient!!.send(fullcommand)
                Log.d("Sent:", fullcommand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendSICommandTrigger(command: String, param: String) {
        val fullcommand = "[\"trigger\", \"$command\",\"$param\"]"
        try {
            if (mWebSocketClient!!.connection != null) {
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
                if (eventType.startsWith("/playlist/items/")) {
                    val parts = eventType.split("/")
                    if (parts.size > 4 && parts[4] != "." && eventParam != "") {
                        try {
                            getOnScreenInvaderMessageListener().onScreenInvaderPlaylistUpdated(parts[3].substring(1).toInt(), parts[4], eventParam)
                        } catch (e: NumberFormatException) {
                            Log.e("Unknown Event Type: ", eventType)
                            e.printStackTrace()
                        }
                    }
                } else {
                    when (eventType) {
                        "notifySend" -> {
                            getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.NOTIFY_SEND, eventParam)
                            Log.d("Got Notify:", eventParam)
                        }
                        "notifyLong" -> {
                            getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.NOTIFY_LONG, eventParam)
                            Log.d("Got Notify:", eventParam)
                        }
                        "notifyException" -> {
                            getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.NOTIFY_EXCEPTION, eventParam)
                            Log.d("Got Notify:", eventParam)
                        }
                        "playerTimePos" -> {
                            getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.PLAYER_TIME_POS, eventParam)
                        }
                        "playlistRemove" -> {
                            getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.PLAYLIST_ITEM_REMOVED, eventParam)
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
                        "/playlist/index" -> {
                            getOnScreenInvaderMessageListener().onScreenInvaderMessage(Message.PLAYLIST_INDEX_CHANGED, eventParam)
                        }
                        else -> Log.e("Unknown Event Type: ", eventType)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                //:P
            }
        }
    }

    enum class Message {
        FULL_SYNC,
        NOTIFY_SEND,
        NOTIFY_LONG,
        NOTIFY_EXCEPTION,
        PLAYER_TIME_POS,
        PLAYER_PAUSE_STATUS,
        SHAIRPORT_ACTIVE_STATUS,
        VOLUME_CHANGED,
        PLAYLIST_INDEX_CHANGED,
        PLAYLIST_SIZE_CHANGED,
        PLAYLIST_ITEM_REMOVED
    }

    interface OnScreenInvaderMessageListener {
        fun onScreenInvaderMessage(message: Message, data: String)
        fun onScreenInvaderPlaylistUpdated(item: Int, dataType: String, value: String)
        fun onWebsocketError(exception: Exception)
        fun onWebsocketOpened()
    }

    fun getOnScreenInvaderMessageListener(): OnScreenInvaderMessageListener {
        if (context is MainActivity || context is InvadeShareMenu) {
            return msgListener
        } else {
            throw(UnsupportedOperationException())
        }
    }
}