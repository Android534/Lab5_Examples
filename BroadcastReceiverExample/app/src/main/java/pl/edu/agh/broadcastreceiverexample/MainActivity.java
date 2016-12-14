package pl.edu.agh.broadcastreceiverexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etReceivedBroadcast;
    Button btnSendBroadcast;
    @Override
    public void onResume() {
        super.onResume();
        //registerReceiver(myReceiver, myIntentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etReceivedBroadcast = (EditText) findViewById(R.id.etReceivedBroadcast);
        btnSendBroadcast = (Button) findViewById(R.id.btnSendBroadcast);
        btnSendBroadcast.setOnClickListener(this);

        MyApplication myApplication = (MyApplication) this.getApplicationContext();
        myApplication.mainActivity = this;
    }

    @Override
    public void onPause(){
        //unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent("pl.edu.agh.USER_ACTION");

        sendBroadcast(intent);
    }
}
