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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.material.appbar.MaterialToolbar;
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
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvResponse, currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    MaterialToolbar appBar;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        ListView listView = rootView.findViewById(R.id.weather_list);
        TextView currentLocation = rootView.findViewById(R.id.tv_current_location);
        LinearProgressIndicator progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation( location.getLatitude(), location.getLongitude(), 1);
                            currentLocation.setText(addresses.get(0).getLocality());
                            Toast.makeText(getContext(), addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        String url = "https://api.open-meteo.com/v1/forecast?latitude="+ location.getLatitude() +"&longitude="+ location.getLongitude() +"&current=temperature_2m,relative_humidity_2m,weather_code&hourly=temperature_2m,relative_humidity_2m,weather_code&temperature_unit=fahrenheit&timezone=auto&forecast_days=1";

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
                                            TextView currentHeatIndex = rootView.findViewById(R.id.tv_weather_heat_index);
                                            currentHeatIndex.setText(Double.toString(currentWeatherData.getHeatIndex()));

                                            //current risk level
                                            TextView currentDangerLevel = rootView.findViewById(R.id.tv_weather_risk_level);
                                            currentDangerLevel.setText(currentWeatherData.dangerLevel());

                                            //get imageview
                                            ImageView weatherIcon = rootView.findViewById(R.id.imageView);


                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                                            LocalDateTime dateTime = LocalDateTime.parse(currentObject.getString("time"), formatter);
                                            int hour = dateTime.getHour();

                                            int weatherCode = currentObject.getInt("weather_code");
                                            if(weatherCode == 0 || weatherCode == 1){
                                                if( isBetween(hour,7,19)){
                                                    weatherIcon.setImageResource(R.drawable.sunny_48px);
                                                }
                                                else{
                                                    weatherIcon.setImageResource(R.drawable.clear_night_48px);
                                                }
                                            }
                                            else if(isBetween(hour, 50, 59)){
                                                weatherIcon.setImageResource(R.drawable.rainy_48px);
                                            } else if (isBetween(hour, 95,99)) {
                                                weatherIcon.setImageResource(R.drawable.thunderstorm_48px);
                                            }
                                            else {
                                                if( isBetween(hour,7,19)){
                                                    weatherIcon.setImageResource(R.drawable.partly_cloudy_day_48px);
                                                }
                                                else{
                                                    weatherIcon.setImageResource(R.drawable.nights_stay_48px);
                                                }
                                            }


                                            //current temp & humidity
                                            TextView currentTempHumidity = rootView.findViewById(R.id.tv_current_temp_humidity);
                                            currentTempHumidity.setText(currentWeatherData.getTemperature() + " °F, " + currentWeatherData.getHumidity() + "%");

                                            // Extract the "hourly" object
                                            JSONObject hourlyObject = response.getJSONObject("hourly");

                                            // Extract the "temperature_2m" array
                                            JSONArray temperatureArray = hourlyObject.getJSONArray("temperature_2m");
                                            JSONArray humidityArray = hourlyObject.getJSONArray("relative_humidity_2m");
                                            JSONArray timeArray = hourlyObject.getJSONArray("time");

                                            String timeSubstring = "";
                                            ArrayList<WeatherData> weatherData = new ArrayList<>();
                                            for (int i = 0; i < temperatureArray.length(); i++) {
                                                dateTime = LocalDateTime.parse(timeArray.getString(i), formatter);

                                                // Extracting the time substring
                                                timeSubstring = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                                                weatherData.add(new WeatherData(timeSubstring, temperatureArray.getDouble(i), humidityArray.getDouble(i)));
                                            }

                                            HourlyWeatherDataAdapter hourlyWeatherDataAdapter = new HourlyWeatherDataAdapter(getContext(), weatherData);
                                            listView.setAdapter(hourlyWeatherDataAdapter);

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
                                tvResponse.setText("That didn't work!");
                            }
                        });
                        queue.add(jsonRequest);
                    }
                }
            });
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }






        return rootView;
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

}