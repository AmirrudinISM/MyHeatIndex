package unikl.finalyearproject.myheatindex;

import java.text.DecimalFormat;

public class WeatherData {
    String time;
    double temperature;
    double humidity;

    public WeatherData(String time, double temperature, double humidity) {
        this.time = time;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    double getHeatIndex(){
        double result;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        result = -42.379
                + (2.04901523 * this.temperature)
                + (10.14333127 * this.humidity)
                - (0.22475541 * this.temperature * this.humidity)
                - (6.83783 * Math.pow(10,-3) * Math.pow(this.temperature,2))
                - (5.481717 * Math.pow(10,-2)* Math.pow(this.humidity,2))
                + (1.22874 * Math.pow(10,-3) *Math.pow(this.temperature,2)*humidity)
                + (8.5282 * Math.pow(10,-4) * this.temperature * Math.pow(this.humidity,2))
                - (1.99 * Math.pow(10,-6) * Math.pow(this.temperature, 2)* Math.pow(this.humidity, 2));

        return Double.parseDouble(decimalFormat.format(result));
    }

    String dangerLevel(){
        double heatIndex = getHeatIndex();

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
