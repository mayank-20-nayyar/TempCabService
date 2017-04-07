package com.example.mayank.cabservice;

/**
 * Created by Mayank on 3/4/2017.
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView timeStamp;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    public boolean isCard = false;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar calobj = Calendar.getInstance();
        String time_stamp = df.format(calobj.getTime());


        if (chatMessageObj.left && chatMessageObj.isCard == false) {
            Log.e("the mes",chatMessageObj.message + " " + chatMessageObj.isCard + "");
            row = inflater.inflate(R.layout.left, parent, false);
        }else if (chatMessageObj.left == false && chatMessageObj.isCard == false){
            Log.e("the mes",chatMessageObj.message + " " + chatMessageObj.isCard + "");
            row = inflater.inflate(R.layout.righht, parent, false);
        }
        else if(chatMessageObj.left == true && chatMessageObj.isCard == true)
        {
            Log.e("the mes",chatMessageObj.message + " " + chatMessageObj.isCard + "");
            row = inflater.inflate(R.layout.card,parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        timeStamp = (TextView)row.findViewById(R.id.time_stamp);

        chatText.setText(chatMessageObj.message);
        timeStamp.setText(time_stamp);

        return row;
    }
    void setCardFlag(boolean val)
    {
        isCard = val;
        Log.e("isCard", isCard + "");
    }
}
