package at.metalab.meks.metapp.screeninvader

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.screeninvader.pojo.screeninvader.ScreeninvaderObject
import at.metalab.meks.metapp.screeninvader.pojo.youtube.YoutubeVideoObject
import org.jetbrains.anko.backgroundColor

/**
 * Created by meks on 03.09.2016.
 */
class PlaylistAdapter(val context : Context, var mSreeninvaderObject : ScreeninvaderObject,
                      val mScreenInvaderAPI : ScreenInvaderAPI) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    class ViewHolder(val mItemView: View) : RecyclerView.ViewHolder(mItemView) {
        val mThumbnailView = itemView.findViewById(R.id.playlist_row_thumbnail) as ImageView
        val mTitleView = itemView.findViewById(R.id.playlist_row_title) as TextView
        val mDescriptionView = itemView.findViewById(R.id.playlist_row_description) as TextView
        val mRootLayout: View? = itemView.findViewById(R.id.playlist_row_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistAdapter.ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.playlist_row_layout, null)
        view.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.ViewHolder?, position: Int) {
        val reverseList = mSreeninvaderObject.playlist.items.reversed()
        val item = reverseList[position]
        holder!!.mTitleView.text = item.title

        if (item.source.startsWith("https://www.youtube.com/")) {
            RetrieveVideoInfoFromYoutubeUrlTask( object : RetrieveVideoInfoFromYoutubeUrlTask.AsyncResponse {
                override fun processFinish(output: YoutubeVideoObject) {
                    holder.mDescriptionView.text = output.items[0].snippet.description
                    RetrieveThumbnailFromImageUrlTask( object : RetrieveThumbnailFromImageUrlTask.AsyncResponse {
                        override fun processFinish(output: Drawable) {
                            holder.mThumbnailView.setImageDrawable(output)
                        }
                    }).execute(output.items[0].snippet.thumbnails.high.url)
                }
            }).execute(item.source)
        } else {
            holder.mDescriptionView.text = "No Description Available"
            holder.mThumbnailView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cancel_playerbar))
        }

        holder.mItemView.setOnClickListener{
            mScreenInvaderAPI.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYER_JUMP, ((mSreeninvaderObject.playlist.items.size - position) - 1).toString())
        }
        if (mSreeninvaderObject.playlist.index.toInt() == ((mSreeninvaderObject.playlist.items.size - position) - 1)){
            holder.mRootLayout!!.backgroundColor = ContextCompat.getColor(context, R.color.md_grey_300)
        } else {
            holder.mRootLayout!!.backgroundColor = ContextCompat.getColor(context, R.color.md_grey_50)
        }

        holder.mRootLayout.setOnTouchListener(object : PlaylistSwipeListener(context) {
            override fun onSwipeTop() {
                Log.d("Swiped:", "Top")
            }
            override fun onSwipeRight() {
                Log.d("Swiped:", "Right")
            }
            override fun onSwipeLeft() {
                Log.d("Swiped:", "Left")
            }
            override fun onSwipeBottom() {
                Log.d("Swiped:", "Bottom")
            }
        })
    }

    override fun getItemCount(): Int {
        return mSreeninvaderObject.playlist.items.size
    }

}