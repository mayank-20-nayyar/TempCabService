package com.example.mayank.cabservice;

import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button call;
    Button alarm;
    Button music;
    Button tableData;
    Map<String,String> contacts = new HashMap<String,String>();
    String receivedMessage = "";
    String toCall = "";
    String toPlay = "";
    public String toSearchTable = "";
    String displayMenu = "";
    EditText ed;
    EditText playSong;

    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    public String[] st = {"weather","book"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.righht);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage(chatText.getText().toString());
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.e("above","chat");
                receivedMessage = chatText.getText().toString();
                sendChatMessage(receivedMessage);
                Log.e("below","chat");
                Log.e("the",receivedMessage);
                decodeMessage(receivedMessage);
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

    }


    void decodeMessage(String message)
    {
        if(message.contains("add button"))
        {
            for(int i=0;i<2 ; i++)
            showButton(st[i],i);
        }
        if(message.contains("date"))
            showDate();
        if(message.contains("time"))
            showTime();
    }

    void showTime()
    {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
                );
        tpd.show(getFragmentManager(), "Timepickerdialog");

    }

    void showDate(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    void showButton(String user, int id)
    {
        final Button myButton = new Button(this);
        myButton.setText(user);
        myButton.setBackgroundResource(R.drawable.button_shape);
        myButton.setId(id);

        LinearLayout ll = (LinearLayout) findViewById(R.id.sec_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(myButton, lp);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                toastMessage();
                LinearLayout ll = (LinearLayout) findViewById(R.id.sec_layout);
                ll.removeAllViews();
                sendChatMessage(st[id]);
            }
        });

    }



    void toastMessage()
    {
        Toast.makeText(getApplicationContext(),"nice",Toast.LENGTH_SHORT).show();
    }

    private boolean sendChatMessage(String message) {
        chatArrayAdapter.add(new ChatMessage(side, message));
        chatText.setText("");
        return true;
    }

    private boolean sendBotMessage(String mes) {
        chatArrayAdapter.add(new ChatMessage(!side, mes));
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }
}
