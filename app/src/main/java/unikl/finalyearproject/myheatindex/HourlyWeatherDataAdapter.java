package unikl.finalyearproject.myheatindex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HourlyWeatherDataAdapter extends BaseAdapter {

    Context context;
    ArrayList<WeatherData> weatherData;
    LayoutInflater inflater;

    public HourlyWeatherDataAdapter(Context context, ArrayList<WeatherData> weatherData) {
        this.context = context;
        this.weatherData = weatherData;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.weatherData.size();
    }

    @Override
    public Object getItem(int i) {
        return this.weatherData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.hourly_weather_data_list_item, null);
        }

        TextView time = (TextView)view.findViewById(R.id.tv_time);
        time.setText(weatherData.get(i).getTime());

        TextView temperatureAndHumidity = (TextView)view.findViewById(R.id.tv_temp_humidity);
        temperatureAndHumidity.setText(weatherData.get(i).getTemperature() + " °F, " + weatherData.get(i).getHumidity() + "%");

        TextView heatIndex = (TextView) view.findViewById(R.id.tv_heat_index);
        heatIndex.setText(Double.toString(weatherData.get(i).getHeatIndex()));

        TextView dangerLevel = (TextView) view.findViewById(R.id.tv_classfication);

        return view;
    }
}
