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

import com.example.mayank.iot.Adapter.TorsionAdapter;
import com.example.mayank.iot.MainActivity;
import com.example.mayank.iot.Model.TorsionModel;
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
public class TorsionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String API_KEY = "2HZG5C25A6AKOTPH";
    private final static int CHANNEL_ID = 241328;
    ListView torsionListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    static List<TorsionModel.FeedsEntity> feedsEntityList;

    static String headerName[] = {"Entry Id", "Date", "Sample Name", "Count 1", "Count 2", "Count 3", "Average", "Client Result", "Actual Result"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_torsion, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        torsionListView = (ListView) view.findViewById(R.id.torsion_list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.torsion_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getActivity(), "Please obtain your Torsion API KEY first from thinkspeak.com", Toast.LENGTH_LONG).show();
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
                                        fetchTorsionData();
                                    }
                                }
        );

    }




    private void fetchTorsionData() {
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<TorsionModel> call = apiService.getTorsionDetails(CHANNEL_ID, API_KEY);
        call.enqueue(new Callback<TorsionModel>() {

            @Override
            public void onResponse(Call<TorsionModel> call, Response<TorsionModel> response) {
                feedsEntityList = response.body().getFeeds();

                setList(feedsEntityList);
                Log.d(TAG, "Number of Result received: " + feedsEntityList.size());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<TorsionModel> call, Throwable t) {
            }
        });
    }

    private void setList(List<TorsionModel.FeedsEntity> feedsEntities) {
        TorsionAdapter torsionAdapter = new TorsionAdapter(getActivity(), feedsEntities);
        torsionListView.setAdapter(torsionAdapter);
    }

    @Override
    public void onRefresh() {
        fetchTorsionData();

    }

    public void action(Context context, String which) {
        if (which.equalsIgnoreCase("Download")) {
            boolean success = Util.saveTorsionExcelFile(context, "TorsionData.xls", feedsEntityList, headerName, "TORSION");
            if (success) {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/IOT",
                        "TorsionData.xls");
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
            boolean success = Util.saveTorsionExcelFile(context, "TorsionData.xls", feedsEntityList, headerName, "TORSION");
            if (success) {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/IOT",
                        "TorsionData.xls");
                Uri path = Uri.fromFile(file);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {""};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Torsion Result");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        }
    }


}
