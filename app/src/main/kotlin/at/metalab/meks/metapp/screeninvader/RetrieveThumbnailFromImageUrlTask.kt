package at.metalab.meks.metapp.screeninvader

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import java.io.InputStream
import java.net.URL
import java.util.*

/**
 * Created by meks on 08.11.16.
 */

class RetrieveThumbnailFromImageUrlTask(val delegate: AsyncResponse): AsyncTask<String, Void, Drawable>() {

    interface AsyncResponse {
        fun processFinish(output: Drawable)
    }

    override fun doInBackground(vararg url: String?): Drawable? {
        try {
            val inputStream = URL(url[0]).content as InputStream
            val d = Drawable.createFromStream(inputStream, "src name")
            return d
        } catch (e: Exception) {
            Log.e("Thumbnail get failed:", url[0])
            e.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: Drawable){
        delegate.processFinish(result)
    }
}