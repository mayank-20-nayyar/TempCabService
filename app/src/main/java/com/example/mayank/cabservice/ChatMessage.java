package com.example.mayank.cabservice;

/**
 * Created by Mayank on 3/5/2017.
 */
public class ChatMessage {
    public boolean left;
    public String message;
    public boolean isCard = false;

    public ChatMessage(boolean left, String message, boolean isCard) {
        super();
        this.left = left;
        this.message = message;
        this.isCard = isCard;
    }
}