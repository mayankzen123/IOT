package com.example.mayank.iot.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mayank.iot.MainActivity;
import com.example.mayank.iot.R;
import com.example.mayank.iot.Socket.ClientThread;

/**
 * Created by Administrator on 3/25/2017.
 */
public class SocketFragment extends Fragment implements View.OnClickListener {

    EditText editTextAddress, editTextPort, editTextMsg;
    Button buttonConnect, buttonDisconnect, buttonSend;
    TextView textViewState, textViewRx;

    ClientHandler clientHandler;
    ClientThread clientThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.socket_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        editTextAddress = (EditText) view.findViewById(R.id.address);
        editTextPort = (EditText) view.findViewById(R.id.port);
        editTextMsg = (EditText) view.findViewById(R.id.msgtosend);
        buttonConnect = (Button) view.findViewById(R.id.connect);
        buttonDisconnect = (Button) view.findViewById(R.id.disconnect);
        buttonSend = (Button) view.findViewById(R.id.send);
        textViewState = (TextView) view.findViewById(R.id.state);
        textViewRx = (TextView) view.findViewById(R.id.received);

        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);

        buttonConnect.setOnClickListener(this);
        buttonDisconnect.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        clientHandler = new ClientHandler(this);


    }





    private void updateState(String state) {
        textViewState.setText(state);
    }

    private void updateRxMsg(String rxmsg) {
        textViewRx.append(rxmsg + "\n");
    }

    private void clientEnd() {
        clientThread = null;
        textViewState.setText("clientEnd()");
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.connect:
                clientThread = new ClientThread(
                        editTextAddress.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()),
                        clientHandler);
                clientThread.start();
                buttonConnect.setEnabled(false);
                buttonDisconnect.setEnabled(true);
                buttonSend.setEnabled(true);
                break;
            case R.id.disconnect:
                if (clientThread != null) {
                    clientThread.setRunning("kill", false);
                }
                break;
            case R.id.send:
                if (clientThread != null) {
                    String msgToSend = editTextMsg.getText().toString().trim();
                    clientThread.txMsg(msgToSend);
                }
                break;
        }
    }

    public static class ClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private SocketFragment parent;

        public ClientHandler(SocketFragment parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_STATE:
                    parent.updateState((String) msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String) msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
