package unikl.finalyearproject.myheatindex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HIToolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HIToolFragment extends Fragment {
    TextView result, classification;
    EditText temperature, humidity;
    Button btnResult;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HIToolFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HIToolFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HIToolFragment newInstance(String param1, String param2) {
        HIToolFragment fragment = new HIToolFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_h_i_tool, container, false);

        temperature = rootView.findViewById(R.id.temperature);
        humidity = rootView.findViewById(R.id.humidity);
        btnResult = rootView.findViewById(R.id.calculate);
        result = rootView.findViewById(R.id.tv_heat_index);
        classification = rootView.findViewById(R.id.tv_risk_level);

        btnResult.setOnClickListener(
            view -> {

                if (temperature.getText().toString().isEmpty()|| humidity.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all fields.", Toast.LENGTH_LONG).show();
                } else {
                    double temperatureVal =  Double.parseDouble(temperature.getText().toString());
                    double humidityVal = Double.parseDouble(humidity.getText().toString());
                    double resultVal = calculateHeatIndex(temperatureVal, humidityVal);
                    result.setText(resultVal + " °F");
                    classification.setText("Your risk level is: " + heatIndexClassification(resultVal));
                }
            }
        );

        return rootView;
    }

    double calculateHeatIndex(double temperature, double humidity){
        double result;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        result = -42.379
                + (2.04901523 * temperature)
                + (10.14333127 * humidity)
                - (0.22475541 * temperature * humidity)
                - (6.83783 * Math.pow(10,-3) * Math.pow(temperature,2))
                - (5.481717 * Math.pow(10,-2)* Math.pow(humidity,2))
                + (1.22874 * Math.pow(10,-3) *Math.pow(temperature,2)*humidity)
                + (8.5282 * Math.pow(10,-4) * temperature * Math.pow(humidity,2))
                - (1.99 * Math.pow(10,-6) * Math.pow(temperature, 2)* Math.pow(humidity, 2));

        return Double.parseDouble(decimalFormat.format(result));
    }

    String heatIndexClassification(double heatIndex){
        if ( heatIndex >= 80 && heatIndex < 90) {
            return "Caution";
        }
        else if(heatIndex >= 90 && heatIndex < 103){
            return "Extreme Caution";
        }
        else if(heatIndex >= 103 && heatIndex < 124){
            return "Danger";
        }
        else if(heatIndex >= 124){
            return "Extreme Danger";
        }
        return "Safe";
    }
}