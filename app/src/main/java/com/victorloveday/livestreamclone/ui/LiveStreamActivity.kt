package com.victorloveday.livestreamclone.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.victorloveday.livestreamclone.adapters.MessageListAdapter
import com.victorloveday.livestreamclone.hideKeyboard
import com.victorloveday.livestreamclone.showToast
import io.getstream.chat.android.client.models.Message
import io.victorloveday.livestreamclone.R
import kotlinx.android.synthetic.main.activity_main.*

class LiveStreamActivity : AppCompatActivity(R.layout.activity_main) {
    private val adapter = MessageListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadMockVideoStream()
        messagesListRV.adapter = adapter

        val viewModel: LiveStreamViewModel by viewModels()
        viewModel.viewState.observe(this, Observer { state ->
            when (state) {
                is State.Messages -> updateMessageList(state.messages)
                is State.NewMessage -> updateMessageList(adapter.currentList + state.message)
                is State.Error -> showToast(state.message)
            }
        })

        sendMessageButton.setOnClickListener {
            viewModel.sendButtonClicked(messageInput.text.toString())
            messageInput.setText("")
            messageInput.clearFocus()
            messageInput.hideKeyboard()
        }
    }

    private fun updateMessageList(messages: List<Message>) {
        adapter.submitList(messages)
        adapter.notifyDataSetChanged()
        val scrollTarget = adapter.itemCount
        messagesListRV.scrollToPosition(scrollTarget) // always scroll to the bottom of the messages
    }

    private fun loadMockVideoStream() {
        val playerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // Replace videoId value with your desired Youtube videoId
                youTubePlayer.loadVideo(videoId = "XYqrrpvTtU8", startSeconds = 0f)
            }
        }
        val playerOptions = IFramePlayerOptions.Builder().controls(0).rel(0).build()
        mockLiveStreamView.initialize(playerListener, false, playerOptions)
    }

}