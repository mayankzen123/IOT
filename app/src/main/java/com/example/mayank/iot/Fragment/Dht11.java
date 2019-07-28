package com.example.mayank.iot.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mayank.iot.Adapter.Dht11Adapter;
import com.example.mayank.iot.MainActivity;
import com.example.mayank.iot.Model.DHT11Model;
import com.example.mayank.iot.Network.ApiClient;
import com.example.mayank.iot.Network.ApiInterface;
import com.example.mayank.iot.R;
import com.example.mayank.iot.Utility.Util;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mayank on 3/16/2017.
 */
public class Dht11 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String API_KEY = "MHNDD9T1QT0UTN9N";
    private final static int CHANNEL_ID = 189083;
    ListView torsionListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    static List<DHT11Model.FeedsEntity> feedsEntityList;
    static String[] headerName = {"Entry Id", "Date", "Temperature", "Humidity"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_dht11, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        torsionListView = view.findViewById(R.id.dht11_list_view);
        swipeRefreshLayout = view.findViewById(R.id.dht11_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getActivity(), "Please obtain your DHT11 API KEY first from thinkspeak.com", Toast.LENGTH_LONG).show();
            return;
        }

        torsionListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (torsionListView != null && torsionListView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = torsionListView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = torsionListView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchDht11Data();
                                    }
                                }
        );

    }



    private void fetchDht11Data() {
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<DHT11Model> call = apiService.getDhtDetails(CHANNEL_ID, API_KEY);
        call.enqueue(new Callback<DHT11Model>() {

            @Override
            public void onResponse(Call<DHT11Model> call, Response<DHT11Model> response) {
                feedsEntityList = response.body().getFeeds();
                setList(feedsEntityList);
                Log.d(TAG, "Number of Result received: " + feedsEntityList.size());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<DHT11Model> call, Throwable t) {
            }
        });
    }

    private void setList(List<DHT11Model.FeedsEntity> feedsEntities) {
        Dht11Adapter torsionAdapter = new Dht11Adapter(getActivity(), feedsEntities);
        torsionListView.setAdapter(torsionAdapter);
    }

    @Override
    public void onRefresh() {
        fetchDht11Data();
    }

    public void action(Context context, String which) {
        if (which.equalsIgnoreCase("Download")) {
            boolean success = Util.saveDht11ExcelFile(context, "DHT11Data.xls", feedsEntityList, headerName, "DHT11");
            if (success) {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/IOT",
                        "DHT11Data.xls");
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/vnd.ms-excel");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (which.equalsIgnoreCase("Email")) {
            boolean success = Util.saveDht11ExcelFile(context, "DHT11Data.xls", feedsEntityList, headerName, "DHT11");
            if (success) {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/IOT",
                        "DHT11Data.xls");
                Uri path = Uri.fromFile(file);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                String[] to = {""};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DHT11 Result");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        }
    }

}
