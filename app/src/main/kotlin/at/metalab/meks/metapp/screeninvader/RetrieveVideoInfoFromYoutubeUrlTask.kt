package at.metalab.meks.metapp.screeninvader

import android.os.AsyncTask
import android.util.Log
import at.metalab.meks.metapp.screeninvader.pojo.youtube.YoutubeVideoObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URL
import java.util.*

/**
 * Created by meks on 08.11.16.
 */
class RetrieveVideoInfoFromYoutubeUrlTask(val delegate: AsyncResponse): AsyncTask<String, Void, YoutubeVideoObject>() {

    interface AsyncResponse {
        fun processFinish(output: YoutubeVideoObject)
    }

    override fun doInBackground(vararg url: String?): YoutubeVideoObject? {
        try {
            val videoUrlQueryMap = getQueryMap(URL(url[0]).query)
            Log.d("Got query map for:", videoUrlQueryMap.toString())

            //YOUTUBE_API_KEY is defined in an uncommited file
            val apiVideoUrl = "https://www.googleapis.com/youtube/v3/videos?id=" + videoUrlQueryMap["v"] + "&key=" + YOUTUBE_API_KEY + "&part=snippet&fields=items(snippet(description,thumbnails))"

            val videoInfoJSON = URL(apiVideoUrl).readText()
            val type = object : TypeToken<YoutubeVideoObject>() {}.type
            val videoObject = Gson().fromJson<YoutubeVideoObject>(videoInfoJSON, type)
            return videoObject
        } catch (e: Exception) {
            Log.e("Get video info failed:", url[0])
            e.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: YoutubeVideoObject){
        if (result.items.size > 0) delegate.processFinish(result)
    }

    fun getQueryMap(query: String): Map<String, String> {
        val params = query.split("&".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
        val map = HashMap<String, String>()
        for (param in params) {
            val name = param.split("=".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()[0]
            val value = param.split("=".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()[1]
            map.put(name, value)
        }
        return map
    }

}
