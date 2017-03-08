package com.example.mayank.cabservice;

import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
/*
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
*/

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    String receivedMessage = "";
    private ChatArrayAdapter chatArrayAdapter;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    public String[] st = {"weather","book ride"};
    private CardArrayAdapter cardArrayAdapter;
    private ListView listView;
    public int flag;
    public String dropLocation;
    public String pickUpLocation;
    public int count = 0;
    public String UserId = null;
    MaxentTagger tagger = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);


        try {
            tagger = new MaxentTagger(
                    "taggers/left3words-wsj-0-18.tagger");
        } catch (IOException e) {
            Log.e("taggger",e + "");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e("clas", "not found");
            e.printStackTrace();
        }
        Log.e("done with tagging","yo");


        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.righht);
        listView.setAdapter(chatArrayAdapter);

        sendBotMessage("Enter the Drop location");

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
        if(flag == 0) {
            String tempDroplocation = extractLocation(message,flag);
            String[] tempDropArray = tempDroplocation.split("\\s");

            if(tempDropArray.length < 3) {
                dropLocation = tempDroplocation;
                sendBotMessage("Enter Pickup Location");
                flag++;
            }
            else{
                flag = 0;
                sendBotMessage(tempDroplocation);
            }
        }
        else if(flag == 1){
            String tempPickUpLocation = extractLocation(message,flag);
            String[] tempPickUpArray = tempPickUpLocation.split("\\s");

            if(tempPickUpArray.length < 2)
            {
                pickUpLocation = tempPickUpLocation;
                sendJson(dropLocation,pickUpLocation);

                sendBotMessage("The drop location is: " + dropLocation + ".");
                sendBotMessage("The pick up location is: " + pickUpLocation + ".");
                sendBotMessage("The Id is: " + UserId + ".");
                sendBotMessage("Thanks for booking through us.");
                sendBotMessage("Press 1 to make a new booking");
                flag ++;

            }
            else{
                flag = 1;
                sendBotMessage(tempPickUpLocation);
            }
        }
        if(message.equals("1"))
        {
            sendBotMessage("Enter the Drop Location");
            flag = 0;
        }

        if(message.contains("add button"))
        {
            for(int i=0;i<2 ; i++)
            showButton(st[i],i);
        }
        if(message.contains("date"))
            showDate();
        if(message.contains("time"))
            showTime();
        /*if(message.contains("card"))
            showCard();
*/

    }

    String extractLocation(String mes, int flag)
    {
        String posTaggedMes = postagger(mes);
        String[] posArray = posTaggedMes.trim().split("\\s+");
        String[] innerArray;
        Log.e("tagged", posTaggedMes);
        if(posArray.length == 1)
        {
            innerArray = posArray[0].split("/");
            if(innerArray[1].equals("NN"))
                return innerArray[0];
            else
                return "This is not the proper destination location. Please enter correct destination.";
        }
        else {
                for (int i = 0; i < posArray.length; i++) {
                    innerArray = posArray[i].split("/");
                    Log.e("here", innerArray[0] + innerArray[1] + flag + "");
                    if (innerArray[1].equals("TO") && innerArray[0].equals("to") && flag == 0 )
                    {
                        innerArray = posArray[i+1].split("/");
                        if(innerArray[1].equals("NN") || innerArray[1].equals("NNS") || innerArray[1].equals("NNP"))
                            return innerArray[0];
                        else
                            return "This is not the proper destination location. Please enter correct destination.";
                    }
                    if (innerArray[1].equals("IN") && innerArray[0].equals("from") && flag == 1)
                    {
                        innerArray = posArray[i+1].split("/");
                        if(innerArray[1].equals("NN") || innerArray[1].equals("NNS") || innerArray[1].equals("NNP"))
                            return innerArray[0];
                        else
                            return "This is not the proper pick up location. Please enter correct pick up location.";
                    }
                    if (innerArray[1].equals("TO") && innerArray[0].equals("to") && flag != 0)
                        return "We already have your drop location. Please enter your pick up location.";

                    if (innerArray[1].equals("IN") && innerArray[0].equals("from") && flag == 0)
                        return "The destination location is required first. Please enter the pick up location when asked.";
            }
        }

        return null;
    }

    String postagger(String mes)
    {
        String posTagged = null;

        posTagged = tagger.tagString(mes);
        return posTagged;
    }

    /*void openNLP()
    {
        String paragraph = "Hi. How are you? This is Mike.";

        // always start with a model, a model is learned from training data
        InputStream is = null;
        try {
            is = new FileInputStream("en-sent.bin");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        SentenceModel model = null;
        try {
            model = new SentenceModel(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(paragraph);

        Log.e("one",sentences[0]);
        Log.e("two",sentences[1]);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    void sendJson(final String dLocation, final String picLocation)
    {
        String[] net = {dLocation,picLocation};
        new Network().execute(net);

    }

    /*void showCard()
    {
        listView = (ListView) findViewById(R.id.card_listView);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

        for (int i = 0; i < 3; i++) {
            Card card = new Card("Card " + (i+1) + " Line 1", "Card " + (i+1) + " Line 2");
            cardArrayAdapter.add(card);
        }
        listView.setAdapter(cardArrayAdapter);

    }
*/


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

class Network extends AsyncTask<String[],Void,String[]>
{
    MainActivity ma;
    @Override
    protected String[] doInBackground(String[]... params) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        currentDateTimeString = format.format(Date.parse(currentDateTimeString));
        Log.e("d",currentDateTimeString);
        String dropLocation = params[0][0];
        String pickUpLocation = params[0][1];
        Log.e("in","thread");
        Looper.prepare(); //For Preparing Message Pool for the child Thread
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        JSONObject json = new JSONObject();

        try {
            Log.e("pic", dropLocation);
            Log.e("pic", pickUpLocation);
            Log.e("in","try");
            HttpPost post = new HttpPost("http://35.166.44.35:8080/testapi/webapi/trips/trip/book");
            Log.e("after","post");
            json.put("t_mobileno","987898789");
            json.put("t_bidredius","5");
            json.put("t_bidamount","120");
            json.put("t_status","New");
            json.put("t_type","Bid");
            json.put("t_bookdatetime",currentDateTimeString);
            json.put("t_driverid","2");
            json.put("tu_userid","7");
            json.put("tv_vehicalid","1");
            json.put("t_picplace", pickUpLocation);
            json.put("t_dropplace",dropLocation );
            json.put("t_totalfare","2042");
            json.put("t_picdatetime","");
            json.put("t_dropdatetime","");
            json.put("rating","2");

            StringEntity se = new StringEntity( json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            Log.e("after","entity");
            response = client.execute(post);
            Log.e("code", response.getStatusLine().getStatusCode() + "");
            Log.e("response",response + "");

                    /*Checking response */
            if(response!=null){
                Log.e("in","res");
                String result = EntityUtils.toString(response.getEntity()); //Get the data in the entity
                Log.e("the result",result);
                JSONObject jsonObject = new JSONObject(result);
            }

        } catch(Exception e) {
            Log.e("ex", e + "");
            e.printStackTrace();

        }

        return null;
    }
}