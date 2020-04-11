package com.example.pusherconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.SocketIOException;
import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView connectPusher;
    TextView connectPusherOutput;
    TextView connectPusherError;
    private Socket socket;
    {
        try{
            socket = IO.socket("https://sawarisadhan.com:6001");
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
    }
    PusherOptions options = new PusherOptions();
    int value = 0;
    int val = 0;
    int va = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        socket.connect();
        connectPusher       = findViewById(R.id.connectPusher);
        connectPusherOutput = findViewById(R.id.connectPusherOutput);
        connectPusherError  = findViewById(R.id.connectPusherError);
        connectPusherOutput.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                connectPusherOutput.setText(Integer.toString(val));
                try{
                    socket.emit("NewMessage");
                }catch (Exception e){
                    e.printStackTrace();
                }
                socket.on("App\\Events\\NewMessage", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String messageObject = "";
                        try {
                            JSONObject data = new JSONObject((String) args[1].toString());
                            Log.d("NewMessage", data.getString("message"));
                            messageObject = data.getString("message");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }finally {
                            Log.d("JSONException","======================"+(String) args[1].toString()+"======================");
//                            Toast.makeText(MainActivity.this, "messageObject", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("NewMessage","Listening to App\\Events\\NewMessage");
                    }
                });
                val++;
            }
        });
        connectPusher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPusher.setText(Integer.toString(value));
                if(socket.connected()){
                    Log.d("tag","------------------------connected------------------------");
                }else{
                    Log.d("tag","------------------------not connected------------------------");
                }
                try{
                    JSONObject object   = new JSONObject();
                    JSONObject auth     = new JSONObject();
                    JSONObject headers  = new JSONObject();

                    socket.emit("NewMessage","hey");
                    try {
                        object.put("channel", "private-new.message.1");
                        object.put("name", "subscribe");

                        headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6Ijg0OTJjZTNkOWY1NWE5ZjU4NTRmYzE0ZjY2M2FhNDEzODAyY2IyM2I0OTY3NGI4MWI2OWU2Y2EyZTBiYjQ4M2QzN2YxNDdmMDcxMTlmMWE0In0.eyJhdWQiOiIyIiwianRpIjoiODQ5MmNlM2Q5ZjU1YTlmNTg1NGZjMTRmNjYzYWE0MTM4MDJjYjIzYjQ5Njc0YjgxYjY5ZTZjYTJlMGJiNDgzZDM3ZjE0N2YwNzExOWYxYTQiLCJpYXQiOjE1ODM5OTEzODIsIm5iZiI6MTU4Mzk5MTM4MiwiZXhwIjoxNjE1NTI3MzgyLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.Rd5dUhn-azfDchdH9HG8YgknkoZYDV_NB_DtC7zwXZBLcaIVeb2wSn4LSsD5BF7Yy0ZzRjMxplmFbdX-MGV3OmqT38-O5Int3stTqZjwXaX5upzFZKJIRUxdy4giHv4XJaRrfiuqsgrkOkZeqnoR7ZnK89Zf39GkPpYm4Bth64yuZ9MG2aGnnH56JhJ27B5aahjZlIKU943DMcWiMCq-gMu6NL5Qdr4JenXJDgXz3UH-UYYVCSHj64ufJTZSZIIK5HEyxG9ov77_EC5PT6AArdqcdgdfUmhISzyHKCyxi2ud-YrVf_tGwtt0SB7EoWbJEPv3kAIGHhI-My9T5pd8zNOJqfDFWSyjCg16nVwPJFsyEsx1EK8xUIxm9oo9JCxplLVZ8QTDRO2n8mgehQa40Q56sS5p2v_tYiDn0FPwfh2zghhJhwrbOw0oQQiM155i-1k2FTb0YX3s94yG_0bm2aj4vazz5INlwqgcrUt4hs-zZ4Om8gi5lWAFImdEkBVjv-WjLj3ciWaT0mnX9eCcV0thDKMCXVXtEvrJ_CO9rYRsfPXuUavIU3gWW3VTL6ATuuicYm0kbNi8HjIivxDuH7-Kj5NkhzP_9WQqW3cRNnPbmIbFh-UBCbh0wqu4phxwDRjJON8tTL-DnKaFfRAIKVQNW7eHJRHIdJhpeeE2BiI");
                        auth.put("headers", headers);
                        object.put("auth", auth);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("subscribe", object, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.e("TAG", "---------------------ECHO SUBSCRIBED--------------------------"); // This event never occurs. I don't know why...
                        }
                    });
                    socket.emit("NewMessage","message");
                    Log.d("hey", "---------------------Logging before socket try----------------------");
                }catch (Exception e){
                    Log.d("hey","----------------------------------------- Catch Exception");
                    e.printStackTrace();
                }
                Log.d("hey", "---------------------Logging before socket catch----------------------");
                value++;
            }
        });
        connectPusherError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPusherError.setText(Integer.toString(va));
                JSONObject data = new JSONObject();
                try {
                    data.put("thread_id","2");
                    data.put("message","hey");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("NewMessage", data);
                socket.emit("App\\Events\\NewMessage", data);

                va++;
            }
        });
    }
    static public boolean isServerReachable(Context context) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL("https://sawarisadhan.com");
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if(urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    public void pusherCode(){
        try{
//                    HttpAuthorizer authorizer = new HttpAuthorizer(PUSHER_AUTH_URL);

            connectPusher.setText(Integer.toString(value));
            options.setCluster("mt1");
//                    options.setHost("192.168.1.89");
//                    options.setHost("ws-mt1.192.168.1.89"); // pusher official
//                    options.setHost("ws-mt1.192.168.1.89"); // pusher official
//                    options.setHost("http://192.168.1.89"); //java.net.UnknownHostException: Host is unresolved: http
            options.setHost("sawarisadhan.com");
            options.setEncrypted(false);
//                    options.setWssPort(443);
//                    options.setWsPort(443);
//                    options.setEncrypted(false);


            //-------------------Reflection--------------------------
//                    System.out.println("-------------------------------------------Reflection-----------------------------");
//                    try{
            Field field = PusherOptions.class.getDeclaredField("URI_SUFFIX");
            field.setAccessible(true);
//                        field.set(options,"?client=java-client&protocol=7&version=2.0.1");
            System.out.println((String)field.get(options)+"-----------URI Suffix-------------");
            Field fieldTwo = PusherOptions.class.getDeclaredField("PUSHER_DOMAIN");
            fieldTwo.setAccessible(true);
            fieldTwo.set(options,"sawarisadhan.com");
            System.out.println((String)fieldTwo.get(options)+"-----------pusher_domain-------------");
//                        Field fieldOne = PusherOptions.class.getDeclaredField("host");
//                        fieldOne.setAccessible(true);
//                        fieldOne.set(options,"192.168.1.89:8000");
//                        System.out.println((String)fieldOne.get(options)+"-----------host-------------");
//                    }catch (Exception e){
//                        e.printStackTrace();
////                        System.out.println();
//                    }


            //----------------Connection Status Pusher---------------------
            System.out.println("-------------------------------------------Status Connection-----------------------------");
            Pusher pusher = new Pusher("ABCDE", options);
            pusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    System.out.println("State changed to " + change.getCurrentState() +
                            " from " + change.getPreviousState());
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    System.out.println("There was a problem connecting! " +
                            "\ncode: " + code +
                            "\nmessage: " + message +
                            "\nException: " + e
                    );
                    if(e != null){
                        System.out.println("There was a problem connecting!"+e.getMessage());
                        e.printStackTrace();
                    };
                }
            }, ConnectionState.ALL);

            //------------------------------Normal Conenction---------------------------------
            System.out.println("-------------------------------------------Normal Connection-----------------------------");
            pusher.connect();
            Channel channel = pusher.subscribe("test");

            channel.bind("NewMessage", new SubscriptionEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    connectPusherOutput.setText("Channel Event Received");
                    value++;
                }
            });
            value++;
        }catch (Exception e){
            connectPusherError.setText(e.toString());
            e.printStackTrace();
        }
    }
}
