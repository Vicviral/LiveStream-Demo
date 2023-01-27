package com.victorloveday.livestreamclone.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.victorloveday.livestreamclone.image
import com.victorloveday.livestreamclone.name
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.controllers.ChannelController
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.victorloveday.livestreamclone.BuildConfig
import timber.log.Timber

class LiveStreamViewModel : ViewModel() {

    private val chatClient = ChatClient.instance()
    private val _viewState = MutableLiveData<State>()

    private lateinit var channelController: ChannelController


    val viewState: LiveData<State> = _viewState

    init {
        chatClient.setUser(chatUser, USER_TOKEN, object : InitConnectionListener() {
            override fun onSuccess(data: ConnectionData) {
                channelController = chatClient.channel(CHANNEL_TYPE, CHANNEL_ID)
                requestChannel()
                subscribeToNewMessageEvent()
            }

            override fun onError(error: ChatError) {
                _viewState.postValue(State.Error("User setting error"))
                Timber.e(error)
            }
        })
    }

    fun sendButtonClicked(message: String) {
//        TODO("Not implemented yet")
    }

    companion object {
        private const val USER_ID = "bob"
        private const val CHANNEL_TYPE = "livestream"
        private const val CHANNEL_ID = "livestream-clone-android" // You'll want to make it unique per video
        private const val USER_TOKEN = BuildConfig.USER_TOKEN

        private val chatUser = User(id = USER_ID).apply {
            name = USER_ID
            image = getDummyAvatar(USER_ID)
        }

        private fun getDummyAvatar(id: String) = "https://api.adorable.io/avatars/285/$id.png"
    }


    private fun requestChannel() {
        val channelData = mapOf("name" to "Live stream chat")
        val request = QueryChannelRequest()
            .withData(channelData)
            .withMessages(20)
            .withWatch()

        channelController.query(request).enqueue {
            if (it.isSuccess) {
                _viewState.postValue(State.Messages(it.data().messages))
            } else {
                _viewState.postValue(State.Error("QueryChannelRequest error"))
                Timber.e(it.error())
            }
        }
    }

    private fun subscribeToNewMessageEvent() {
        chatClient.events().subscribe {
            if (it is NewMessageEvent) {
                _viewState.postValue(State.NewMessage(it.message))
            }
        }
    }
}

sealed class State {
    data class Messages(val messages: List<Message>) : State()
    data class NewMessage(val message: Message) : State()
    data class Error(val message: String) : State()
}