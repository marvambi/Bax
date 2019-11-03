package com.marvambi.degrande.ui.main;

import com.marvambi.degrande.datas.Messages;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class MessageHistoryAdapter<MESSAGE extends Messages> extends MessagesListAdapter {
    public MessageHistoryAdapter(String senderId, ImageLoader imageLoader) {
        super(senderId, imageLoader);
    }

    public MessageHistoryAdapter(String senderId, MessageHolders holders, ImageLoader imageLoader) {
        super(senderId, holders, imageLoader);
    }
}
