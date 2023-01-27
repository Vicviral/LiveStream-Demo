package com.victorloveday.livestreamclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.victorloveday.livestreamclone.image
import com.victorloveday.livestreamclone.loadUrl
import com.victorloveday.livestreamclone.name
import io.getstream.chat.android.client.models.Message
import io.victorloveday.livestreamclone.R
import kotlinx.android.synthetic.main.item_comment.view.*

/**
 *Created by Victor Loveday on 1/27/23
 */
class MessageListAdapter(): ListAdapter<Message, MessageViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindMessage(getItem(position))
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }

}

class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindMessage(message: Message) {
        message.apply {
            itemView.avatar.loadUrl(user.image, R.drawable.ic_person_white_24dp)
            itemView.userName.text = user.name
            itemView.message.text = text
        }
    }
}