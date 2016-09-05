package at.metalab.meks.metapp.screeninvader

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import at.metalab.meks.metapp.R
import at.metalab.meks.metapp.screeninvader.pojo.ScreeninvaderObject
import com.google.api.services.youtube.YouTube
import org.jetbrains.anko.backgroundColor

/**
 * Created by meks on 03.09.2016.
 */
class PlaylistAdapter(val context : Context, var mSreeninvaderObject : ScreeninvaderObject, val mScreenInvaderAPI : ScreenInvaderAPI) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    private var youtube: YouTube? = null

    class ViewHolder(val mItemView: View) : RecyclerView.ViewHolder(mItemView) {
        val mThumbnailView = itemView.findViewById(R.id.playlist_row_thumbnail) as ImageView
        val mTitleView = itemView.findViewById(R.id.playlist_row_title) as TextView
        val mDescriptionView = itemView.findViewById(R.id.playlist_row_description) as TextView
        val mRootLayout = itemView.findViewById(R.id.playlist_row_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistAdapter.ViewHolder {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.playlist_row_layout, null)
        view.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.ViewHolder?, position: Int) {
        val reverseList = mSreeninvaderObject.playlist.items.reversed()
        val item = reverseList[position]
        holder!!.mTitleView.text = item.title
        holder.mDescriptionView.text = "Implement me!"
        holder.mThumbnailView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cancel_playerbar))
        holder.mItemView.setOnClickListener{
            mScreenInvaderAPI.sendSICommandPublish(ScreenInvaderAPI.COMMANDS.PLAYER_JUMP, position.toString())
        }
        holder.mRootLayout.backgroundColor =
                if (mSreeninvaderObject.player.url == item.source)
                    ContextCompat.getColor(context, R.color.md_grey_300)
                else
                    ContextCompat.getColor(context, R.color.md_grey_50)
    }

    override fun getItemCount(): Int {
        return mSreeninvaderObject.playlist.items.size
    }
}