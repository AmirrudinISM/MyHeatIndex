package unikl.finalyearproject.myheatindex;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView dangerLevel, heatIndex, healthEffects, recommendedActions;
    ImageView stockImage;
    FusedLocationProviderClient fusedLocationProviderClient;

    public RecommendationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendationFragment newInstance(String param1, String param2) {
        RecommendationFragment fragment = new RecommendationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recommendation, container, false);
        stockImage = rootView.findViewById(R.id.imageView);
        //current heat index
        heatIndex = rootView.findViewById(R.id.tv_weather_heat_index);
        //current risk level
        dangerLevel = rootView.findViewById(R.id.tv_weather_risk_level);
        healthEffects = rootView.findViewById(R.id.health_effects);
        recommendedActions = rootView.findViewById(R.id.recommended_actions);
        LinearProgressIndicator progressBar = rootView.findViewById(R.id.progressBar);

        Bundle bundle = getArguments();
        if (bundle != null) {
            progressBar.setVisibility(View.GONE);
            String dangerLevelVal = bundle.getString("dangerLevel");
            dangerLevel.setText(dangerLevelVal);

            double heatIndexVal = bundle.getDouble("heatIndex");
            heatIndex.setText(Double.toString(heatIndexVal));
            setImageEffectsAndRecommendations(dangerLevelVal);

        }else {

            progressBar.setVisibility(View.VISIBLE);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(getContext());
                            String url = "https://api.open-meteo.com/v1/forecast?latitude="+ location.getLatitude() + "&longitude=" + location.getLongitude() + "&current=temperature_2m,relative_humidity_2m&temperature_unit=fahrenheit&timezone=auto&forecast_days=1";

                            // Request a string response from the provided URL.
                            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                progressBar.setVisibility(View.GONE);
                                                //Extract "current" object
                                                JSONObject currentObject = response.getJSONObject("current");
                                                WeatherData currentWeatherData = new WeatherData("", currentObject.getDouble("temperature_2m"), currentObject.getDouble("relative_humidity_2m"));
                                                heatIndex.setText(Double.toString(currentWeatherData.getHeatIndex()));
                                                dangerLevel.setText(currentWeatherData.dangerLevel());
                                                setImageEffectsAndRecommendations(currentWeatherData.dangerLevel());

                                            }
                                            catch (Exception e){
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Problem in fetching data", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "NETWORK ERROR", Toast.LENGTH_LONG).show();
                                }
                            });
                            queue.add(jsonRequest);
                        }
                    }
                });
            }else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        }
        return rootView;
    }

    void setImageEffectsAndRecommendations(String dangerLevel){
        switch (dangerLevel){
            case "Safe":
                stockImage.setImageResource(R.drawable.construction_worker_safe);
                healthEffects.setText(R.string.effects_safe);
                recommendedActions.setText(R.string.actions_safe);
                break;
            case "Caution":
                stockImage.setImageResource(R.drawable.construction_worker_caution);
                healthEffects.setText(R.string.effects_caution);
                recommendedActions.setText(R.string.actions_caution);
                break;
            case "Extreme Caution":
                stockImage.setImageResource(R.drawable.construction_worker_extreme_caution);
                healthEffects.setText(R.string.effects_extreme_caution);
                recommendedActions.setText(R.string.actions_extreme_caution);
                break;
            case "Danger":
                stockImage.setImageResource(R.drawable.construction_worker_danger);
                healthEffects.setText(R.string.effects_danger);
                recommendedActions.setText(R.string.actions_danger);
                break;
            case "Extreme Danger":
                stockImage.setImageResource(R.drawable.construction_worker_extreme_danger);
                healthEffects.setText(R.string.effects_extreme_danger);
                recommendedActions.setText(R.string.actions_extreme_danger);
                break;
        }
    }
}