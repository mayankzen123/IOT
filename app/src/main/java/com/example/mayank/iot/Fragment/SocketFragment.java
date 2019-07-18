package com.example.mayank.iot.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayank.iot.Adapter.TestAdapter;
import com.example.mayank.iot.MainActivity;
import com.example.mayank.iot.Model.TestCasesModel;
import com.example.mayank.iot.R;
import com.example.mayank.iot.Socket.ClientThread;
import com.example.mayank.iot.Utility.Util;
import java.util.ArrayList;
import java.util.List;

public class SocketFragment extends Fragment implements OnClickListener {
    Button btnNextEntry;
    Button btnPushData;
    Button btnStartMotor;
    ClientHandler clientHandler;
    ClientThread clientThread;
    TextView noData;
    TestAdapter testAdapter;
    ListView testCasesList;
    List<TestCasesModel> testCasesModelList = new ArrayList();
    int testNumber = 1;
    TextView textViewRx;
    TextView textViewState;

    public static class ClientHandler extends Handler {
        public static final int UPDATE_END = 2;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_STATE = 0;
        private SocketFragment parent;

        public ClientHandler(SocketFragment parent2) {
            this.parent = parent2;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    this.parent.updateState((String) msg.obj);
                    return;
                case 1:
                    this.parent.updateRxMsg((String) msg.obj);
                    return;
                case 2:
                    this.parent.clientEnd();
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.socket_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.textViewState = (TextView) view.findViewById(R.id.state);
        this.textViewRx = (TextView) view.findViewById(R.id.received);
        this.btnStartMotor = (Button) view.findViewById(R.id.btn_start_motor);
        this.btnNextEntry = (Button) view.findViewById(R.id.btn_next_entry);
        this.btnPushData = (Button) view.findViewById(R.id.btn_push_Data);
        this.testCasesList = (ListView) view.findViewById(R.id.test_list_view);
        this.noData = (TextView) view.findViewById(R.id.blank);
        this.testAdapter = new TestAdapter(getActivity(), this.testCasesModelList);
        this.testCasesList.setAdapter(this.testAdapter);
        setStateOfButton(false);
        this.clientHandler = new ClientHandler(this);
        this.btnStartMotor.setOnClickListener(this);
        this.btnNextEntry.setOnClickListener(this);
        this.btnPushData.setOnClickListener(this);
    }

    private void setStateOfButton(boolean state) {
        this.btnStartMotor.setEnabled(state);
        this.btnNextEntry.setEnabled(state);
        this.btnPushData.setEnabled(state);
    }

    /* access modifiers changed from: private */
    public void updateState(String state) {
        if (state.equalsIgnoreCase("connected") || state.equalsIgnoreCase("connecting")) {
            MainActivity.menu.findItem(R.id.action_connect).setVisible(false);
            MainActivity.menu.findItem(R.id.action_disconnect).setVisible(true);
            if (state.equalsIgnoreCase("connected")) {
                setStateOfButton(true);
                MainActivity.menu.findItem(R.id.action_sound_on).setVisible(true);
                MainActivity.menu.findItem(R.id.action_details).setVisible(false);
            }
        } else if (state.equalsIgnoreCase("disconnected")) {
            MainActivity.menu.findItem(R.id.action_details).setVisible(true);
            MainActivity.menu.findItem(R.id.action_connect).setVisible(true);
            MainActivity.menu.findItem(R.id.action_disconnect).setVisible(false);
            MainActivity.menu.findItem(R.id.action_sound_on).setVisible(false);
            MainActivity.menu.findItem(R.id.action_sound_off).setVisible(false);
            setStateOfButton(false);
            this.textViewRx.setText("");
            this.noData.setVisibility(View.VISIBLE);
            this.testCasesList.setVisibility(View.GONE);
            this.testNumber = 1;
            this.testCasesModelList.clear();
        }
        ((MainActivity) getActivity()).setCurrentStatus(state);
        this.textViewState.setVisibility(View.VISIBLE);
        this.textViewState.setText(state);
    }

    /* access modifiers changed from: private */
    public void updateRxMsg(String rxmsg) {
        if (rxmsg.trim().equalsIgnoreCase("Sound On")) {
            rxmsg = "Sound sensor is now ON";
            MainActivity.menu.findItem(R.id.action_sound_on).setVisible(false);
            MainActivity.menu.findItem(R.id.action_sound_off).setVisible(true);
        } else if (rxmsg.trim().equalsIgnoreCase("Sound Off")) {
            rxmsg = "Sound sensor is now OFF";
            MainActivity.menu.findItem(R.id.action_sound_on).setVisible(true);
            MainActivity.menu.findItem(R.id.action_sound_off).setVisible(false);
        } else if (rxmsg.trim().equalsIgnoreCase("Motor ON")) {
            this.btnStartMotor.setText("STOP MOTOR");
            this.btnStartMotor.setBackgroundColor(Color.parseColor("#FF0000"));
            rxmsg = "Motor Started";
        } else if (rxmsg.trim().equalsIgnoreCase("Motor OFF")) {
            this.btnStartMotor.setText("START MOTOR");
            this.btnStartMotor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            rxmsg = "Motor Stopped";
        } else if (rxmsg.trim().toLowerCase().contains("count")) {
            this.noData.setVisibility(View.GONE);
            this.testCasesList.setVisibility(View.VISIBLE);
            refreshListView(rxmsg);
        } else if (rxmsg.trim().equalsIgnoreCase("push")) {
            ConnectionDialogFragment connectionDialogFragment = (ConnectionDialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag("SOCKET");
            if (connectionDialogFragment != null && connectionDialogFragment.isVisible()) {
                connectionDialogFragment.dismissAllowingStateLoss();
            }
            rxmsg = "Data Pushed Successfully";
            Toast.makeText(getActivity(), rxmsg, Toast.LENGTH_LONG).show();
            this.textViewRx.setText("");
            this.noData.setVisibility(View.VISIBLE);
            this.testCasesList.setVisibility(View.GONE);
            this.testNumber = 1;
            this.testCasesModelList.clear();
        }
        this.textViewRx.append(rxmsg + "\n");
    }

    private void refreshListView(String rxmsg) {
        this.testCasesModelList.add(new TestCasesModel(this.testNumber, rxmsg.split("#")[1]));
        this.testAdapter.refreshItem(this.testCasesModelList);
        this.testNumber++;
    }

    /* access modifiers changed from: private */
    public void clientEnd() {
        this.clientThread = null;
        updateState("Disconnected");
    }

    public void connect() {
        SharedPreferences preferences = Util.getSharePref(getActivity());
        if (preferences != null) {
            String serverIp = preferences.getString("serverIp", "defaultIp");
            String port = preferences.getString("serverPort", "defaultPort");
            if (serverIp.equalsIgnoreCase("defaultIp") || port.equalsIgnoreCase("defaultPort")) {
                openServerDetailsDialog();
                return;
            }
            this.clientThread = new ClientThread(serverIp, Integer.parseInt(port), this.clientHandler);
            this.clientThread.start();
            return;
        }
        openServerDetailsDialog();
    }

    public void disconnect() {
        if (this.clientThread != null) {
            MainActivity.menu.findItem(R.id.action_connect).setVisible(true);
            this.clientThread.setRunning("EXIT", false);
        }
    }

    private void openServerDetailsDialog() {
        Toast.makeText(getActivity(), "Please Input Server Details", Toast.LENGTH_LONG).show();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("DialogType", "ServerDetails");
        ConnectionDialogFragment connectionDialogFragment = new ConnectionDialogFragment();
        connectionDialogFragment.setArguments(bundle);
        connectionDialogFragment.show(fragmentManager, "SOCKET");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_motor /*2131493014*/:
                if (this.btnStartMotor.getText().toString().equalsIgnoreCase("START MOTOR")) {
                    this.clientThread.sendCommand("STARTMOTOR");
                    return;
                } else {
                    this.clientThread.sendCommand("STOPMOTOR");
                    return;
                }
            case R.id.btn_next_entry /*2131493015*/:
                this.clientThread.sendCommand("NEXTENTRY");
                return;
            case R.id.btn_push_Data /*2131493016*/:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("DialogType", "PushData");
                ConnectionDialogFragment connectionDialogFragment = new ConnectionDialogFragment();
                connectionDialogFragment.setArguments(bundle);
                connectionDialogFragment.show(fragmentManager, "PUSHDATA");
                return;
            default:
                return;
        }
    }

    public void soundCommand(boolean status) {
        if (status) {
            this.clientThread.sendCommand("SOUNDON");
        } else {
            this.clientThread.sendCommand("SOUNDOFF");
        }
    }

    public void pushData(String pushdata) {
        this.clientThread.sendCommand(pushdata);
    }
}
