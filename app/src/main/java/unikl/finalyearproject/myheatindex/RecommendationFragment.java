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

    TextView dangerLevel, heatIndex;
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


        Bundle bundle = getArguments();
        if (bundle != null) {
            String dangerLevelVal = bundle.getString("dangerLevel");
            //dangerLevel = rootView.findViewById(R.id.tv_danger_level);
            dangerLevel.setText(dangerLevelVal);

            double heatIndexVal = bundle.getDouble("heatIndex");
            //heatIndex = rootView.findViewById(R.id.tv_heat_index);
            //heatIndex.setText(Double.toString(heatIndexVal));


        }else {
            LinearProgressIndicator progressBar = rootView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation( location.getLatitude(), location.getLongitude(), 1);
                                Toast.makeText(getContext(), addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

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

                                                //current heat index
                                                //heatIndex = rootView.findViewById(R.id.tv_heat_index);
                                                //heatIndex.setText(Double.toString(currentWeatherData.getHeatIndex()));

                                                //current risk level
                                                //dangerLevel = rootView.findViewById(R.id.tv_danger_level);
                                                dangerLevel.setText(currentWeatherData.dangerLevel());

                                                //get imageview
                                                //ImageView weatherIcon = rootView.findViewById(R.id.imageView);

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
}