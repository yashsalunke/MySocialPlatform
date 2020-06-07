package com.example.mysocialplatform

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysocialplatform.models.Post
import kotlinx.android.synthetic.main.item_post.view.*


class PostsAdapter (val context: Context,val posts : List<Post>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    override fun getItemCount() = posts.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Bind(posts[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun Bind(post: Post) {

            itemView.tvUsername.text = post.User?.name
            itemView.tvDescription.text = post.description
            Glide.with(context).load(post.image_url).into(itemView.ivPost)
            itemView.tvRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creation_time_ms)


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false)
        return ViewHolder(view)
    }
}

