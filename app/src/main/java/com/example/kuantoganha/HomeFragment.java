package com.example.kuantoganha;

import android.os.AsyncTask;
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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private Spinner spinnerDistrict;
    private BarChart barChart;
    private RecyclerView recyclerView;
    private JobAdapter adapter;
    private List<Job> jobList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Outras inicializações
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


        spinnerDistrict = view.findViewById(R.id.spinnerDistricts);
        barChart = view.findViewById(R.id.barChart);

        // Inicialize jobList com dados recebidos da API
        jobList = new ArrayList<>();
        adapter = new JobAdapter(jobList);
        recyclerView.setAdapter(adapter);

        //setupSpinner();
        loadJobsData();
        setupChart();

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
                            // Processar a resposta e converter para objetos Job
                            List<Job> jobs = parseJobsJson(response);

                            // Atualizar o RecyclerView com os dados dos jobs
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
                String endDate = jsonObject.optString("end_date", "N/A"); // Usando optString para lidar com valores nulos
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


    private void setupChart() {
        // Configurar o gráfico inicialmente, os dados serão atualizados depois
        List<BarEntry> entries = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, "Salário Médio");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Carregar dados do gráfico
        fetchChartData();
    }

    private void fetchChartData() {
        // Implementar a lógica para carregar os dados do gráfico
    }

    private void fetchDistricts() {


    }
}