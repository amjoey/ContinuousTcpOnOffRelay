package com.amjoey.continuoustcponoffrelay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import simpletcp.ContinuousTcpClient;
import simpletcp.TcpUtils;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    public static final int TCP_PORT = 9001;

    private Button buttonConnect,buttonRefresh;
    private ToggleButton tButton;
    private TextView textViewStatus;
    private EditText editTextIpAddress;

    private ContinuousTcpClient continuousTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = findViewById(R.id.textViewStatus);
        editTextIpAddress = findViewById(R.id.editTextIpAddress);
        buttonConnect = findViewById(R.id.buttonConnect);
        buttonRefresh = findViewById(R.id.buttonRefresh);
        tButton = (ToggleButton) findViewById(R.id.toggleButton1);

        TcpUtils.forceInputIp(editTextIpAddress);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (buttonConnect.getText().toString().equals("Open")) {
                    buttonConnect.setEnabled(false);
                    editTextIpAddress.setEnabled(false);

                    String ip = editTextIpAddress.getText().toString();
                    continuousTcpClient.connect(ip, TCP_PORT, new ContinuousTcpClient.ConnectionCallback() {
                        public void onConnectionFailed(String ip, Exception e) {
                            textViewStatus.setText("Disconnect");
                            buttonConnect.setEnabled(true);
                            editTextIpAddress.setEnabled(true);
                        }

                        public void onConnected(String hostName, String hostAddress) {
                            textViewStatus.setText("Connect");
                            editTextIpAddress.setEnabled(false);
                            buttonConnect.setEnabled(true);
                            buttonConnect.setText("Close");
                        }
                    });
                } else if (buttonConnect.getText().toString().equals("Close")) {
                    continuousTcpClient.disconnect();
                    textViewStatus.setText("Disconnect");
                    editTextIpAddress.setEnabled(true);
                    buttonConnect.setText("Open");
                }
            }
        });

        tButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    continuousTcpClient.send("LEDON\r");
                } else {
                    continuousTcpClient.send("LEDOFF\r");
                }
                //return false;
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                continuousTcpClient.send("REFRESH\r");
            }
        });

        continuousTcpClient = new ContinuousTcpClient(TCP_PORT, new ContinuousTcpClient.TcpConnectionListener() {
            public void onDisconnected() {
                textViewStatus.setText("Disconnect");
                editTextIpAddress.setEnabled(true);
                buttonConnect.setEnabled(true);
                buttonConnect.setText("Open");
            }

            public void onDataReceived(String message, String ip) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if(message.equals("1")){
                    tButton.setChecked(true);

                }else if(message.equals("0")){
                    tButton.setChecked(false);

                }
            }

            public void onConnected(String hostName, String hostAddress, Socket s) {
                textViewStatus.setText("Connect");
                editTextIpAddress.setText(hostAddress);
                editTextIpAddress.setEnabled(false);
                buttonConnect.setText("Close");
            }
        });
    }
    public void onResume() {
        super.onResume();
        //continuousTcpClient.disconnect();
        textViewStatus.setText("Disconnect");
        editTextIpAddress.setEnabled(true);
        buttonConnect.setText("Open");
        continuousTcpClient.start();
    }

    public void onStop() {
        super.onStop();
        continuousTcpClient.stop();
    }
}
