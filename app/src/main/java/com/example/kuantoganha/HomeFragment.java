package com.example.kuantoganha;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private BarChart barChart;
    private RecyclerView recyclerView;
    private JobAdapter adapter;
    private List<Job> jobList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        barChart = view.findViewById(R.id.barChart);

        jobList = new ArrayList<>();
        adapter = new JobAdapter(jobList);
        recyclerView.setAdapter(adapter);

        Spinner spinnerDistricts = view.findViewById(R.id.spinnerDistricts);
        spinnerDistricts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DistrictItem selectedDistrict = (DistrictItem) parent.getItemAtPosition(position);
                fetchChartData(selectedDistrict.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada a fazer aqui
            }
        });


        loadJobsData();
        fetchChartData("all");
        loadDistrictsData();
        return view;
    }

    private void loadJobsData() {
        new Thread(() -> {
            try {
                URL url = new URL(Config.urlLastJobs);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = connection.getInputStream();
                    String response = streamToString(responseStream);

                    getActivity().runOnUiThread(() -> {
                        try {
                            List<Job> jobs = parseJobsJson(response);

                            updateRecyclerView(jobs);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getString(R.string.error_processing_data), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), getString(R.string.error_fetching_data), Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String streamToString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private List<Job> parseJobsJson(String jsonResponse) {
        List<Job> jobs = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String district = jsonObject.getString("district");
                String description = jsonObject.getString("description");
                String startDate = jsonObject.getString("start_date");
                String endDate = jsonObject.optString("end_date", "N/A");
                jobs.add(new Job(title, district, description, startDate, endDate));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobs;
    }
    private void updateRecyclerView(List<Job> jobs) {
        JobAdapter jobsAdapter = new JobAdapter(jobs);
        recyclerView.setAdapter(jobsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void fetchChartData(String district) {
        new Thread(() -> {
            try {
                URL url = new URL(Config.urlDistricts + district);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = connection.getInputStream();
                    String response = streamToString(responseStream);

                    getActivity().runOnUiThread(() -> {
                        try {
                            List<JobGraph> jobs = parseChartData(response);
                            updateChart(jobs);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getString(R.string.error_processing_data), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), getString(R.string.error_fetching_data), Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private List<JobGraph> parseChartData(String jsonResponse) {
        List<JobGraph> jobs = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                float salary = (float) jsonObject.getDouble("averagesalary");
                jobs.add(new JobGraph(title, salary));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    private void updateChart(List<JobGraph> jobs) {

        boolean darkTheme = (getActivity().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int textColor = darkTheme ? Color.WHITE : Color.BLACK;

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            JobGraph job = jobs.get(i);
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(i, job.getSalary()));

            BarDataSet dataSet = new BarDataSet(entries, job.getTitle());
            dataSet.setColor(getRandomColor());
            dataSets.add(dataSet);
        }

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);


        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        XAxis topAxis = barChart.getXAxis();
        topAxis.setEnabled(false);


        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        barChart.getAxisLeft().setTextColor(textColor);
        barChart.getAxisRight().setTextColor(textColor);
        barChart.getXAxis().setTextColor(textColor);
        barChart.getLegend().setTextColor(textColor);


        barChart.getDescription().setEnabled(false);


        for (IBarDataSet dataSet : barChart.getData().getDataSets()) {
            dataSet.setValueTextColor(textColor);
            dataSet.setValueTextSize(10f);
        }

        barChart.invalidate();
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private void loadDistrictsData() {
        new Thread(() -> {
            try {
                URL url = new URL(Config.urlSpinner);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = connection.getInputStream();
                    String response = streamToString(responseStream);

                    getActivity().runOnUiThread(() -> {
                        try {
                            List<DistrictItem> districtItems = new ArrayList<>();
                            districtItems.add(new DistrictItem("all", getString(R.string.all_districts)));

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("district");
                                String name = jsonObject.getString("district");
                                districtItems.add(new DistrictItem(id, name));
                            }

                            ArrayAdapter<DistrictItem> adapter = new ArrayAdapter<>(
                                    getActivity(),
                                    android.R.layout.simple_spinner_item,
                                    districtItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            Spinner spinnerDistricts = getActivity().findViewById(R.id.spinnerDistricts);
                            spinnerDistricts.setAdapter(adapter);

                            spinnerDistricts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    DistrictItem selectedDistrict = (DistrictItem) parent.getItemAtPosition(position);
                                    fetchChartData(selectedDistrict.getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // Nada a fazer aqui
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getString(R.string.error_processing_data), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), getString(R.string.error_fetching_data), Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}