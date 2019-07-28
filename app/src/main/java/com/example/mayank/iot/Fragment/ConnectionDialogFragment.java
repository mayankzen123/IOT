package com.example.mayank.iot.Fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.mayank.iot.R;
import com.example.mayank.iot.Utility.Util;

/**
 * Created by Administrator on 3/26/2017.
 */
public class ConnectionDialogFragment extends DialogFragment implements View.OnClickListener {

    EditText serverIp, serverPort;
    Button save, cancel;
    TextInputLayout inputServerIp, inputServerPort;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_connection_dialog, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        serverIp = view.findViewById(R.id.host_name);
        serverPort = view.findViewById(R.id.input_port);
        save = view.findViewById(R.id.btn_save);
        cancel = view.findViewById(R.id.btn_cancel);
        inputServerIp = view.findViewById(R.id.input_layout_host);
        inputServerPort = view.findViewById(R.id.input_layout_port);
        setServerDetails();
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setServerDetails() {
        SharedPreferences sharedPreferences = Util.getSharePref(getActivity());
        if (sharedPreferences != null) {
            serverIp.setText(sharedPreferences.getString("serverIp", "192.168"));
            serverPort.setText(sharedPreferences.getString("serverPort", "5560"));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_corner_radius);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_save:
                if (checkIfEmpty())
                    Util.saveDataToSharedPref(getActivity(), serverIp.getText().toString(), serverPort.getText().toString());
                dismissAllowingStateLoss();
                break;
            case R.id.btn_cancel:
                dismissAllowingStateLoss();
                break;
        }
    }

    private boolean checkIfEmpty() {
        if (serverIp.getText().toString().trim().isEmpty()) {
            inputServerIp.setErrorEnabled(true);
            inputServerIp.setError("Please Enter Server Ip");
        } else if (serverPort.getText().toString().trim().isEmpty()) {
            inputServerPort.setErrorEnabled(true);
            inputServerPort.setError("Please Enter Server Port");
        } else {
            inputServerIp.setErrorEnabled(false);
            inputServerPort.setErrorEnabled(false);
            return true;
        }
        return false;
    }
}
