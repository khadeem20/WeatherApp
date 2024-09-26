//retreive weather dat form API - this backend logic will fetch the latest weather
//data from the external API and return it. TYhe will
//display this data to the user

import java.io.IOException;
import java.lang.reflect.AccessFlag.Location;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class WeatherApp {
        //fetches weahter data for given location

        public static JSONObject getWeatherData(String locationName){

            //GET location coordinates using the geolocation API
            JSONArray locationData= getLocationData(locationName);

            //extract latitude and longitude data
            JSONObject location = (JSONObject) locationData.get(0);
            double latitude = (double) location.get("latitude");
            double longitude = (double) location.get("longitude");

            //extract country name
            String country= (String) location.get("country"); 

            //System.out.println("Country name -> " + country);


            // build API request URL with location coordinates
            String urlString = "https://api.open-meteo.com/v1/forecast?" + 
            "latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=GMT";
            

            try{
                //call api and get response
                HttpURLConnection conn = fetchAPIResponse(urlString);

                //check for reponse status
                if (conn.getResponseCode() != 200){
                    System.out.println("Erro: Could not connect to API");
                    return null;
                }
                
                //stor resulting json data
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while(scanner.hasNext()){
                    //read and store into the string builder
                    resultJson.append(scanner.nextLine());

                }

                // close scanner
                scanner.close();

                conn.disconnect();

                //parse through the data
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj= (JSONObject) parser.parse(String.valueOf(resultJson));

                //retrieve hourly date
                JSONObject hourly =  (JSONObject) resultJsonObj.get("hourly");

                //get the index of the current hour inorder to get the curretn hour's data
                JSONArray time = (JSONArray) hourly.get("time");
                int index= findIndexOfCurrentTime(time);
                
                //get temperature
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (double) temperatureData.get(index);

                //get weather code
                JSONArray weathercode = (JSONArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            
                //get humidity
                JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
                long humidity = (long) relativeHumidity.get(index);

                 //get windspeed
                 JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
                 double windspeed = (double) windspeedData.get(index);

                //build a weather json data object from the data collected so far
                JSONObject weatherData = new JSONObject();
                weatherData.put ("temperature", temperature);
                weatherData.put ("weather_condition", weatherCondition);
                weatherData.put ("humidity", humidity);
                weatherData.put ("windspeed", windspeed);
                weatherData.put("country", country);
                weatherData.put("location",locationName);

                //debugging
                //System.out.println("humdity: " + humidity  +  " ;  windspeed: " + windspeed);

                //return the weather object
                return weatherData;

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }

        //Gets geographic goordinates for given location name
        public static JSONArray getLocationData(String locationName){
            // replace any whitespace in location name to + to adhere to the API's request format
            locationName = locationName.replaceAll("\\s" ,"+");
            
            //build API url with location parameter
            String urlString="https://geocoding-api.open-meteo.com/v1/search?name=" +
            locationName + "&count=10&language=en&format=json";

            //System.out.println("Url for location : "+ urlString);
            try{
                //call api and get a response
                HttpURLConnection conn = fetchAPIResponse(urlString);

                System.out.println("Conn status for Location API -> " + conn.getResponseCode());

                //check response status
                if (conn.getResponseCode() != 200){
                    System.out.println("Error: Could not connect to API");
                    return null;
                }else{
                    System.out.println("Succesfully connected to API");
                    //store the API results
                    StringBuilder resultJson = new StringBuilder();
                   

                    Scanner scanner = new Scanner(conn.getInputStream());

                    //read and store the resulting data into our string builder
                    while(scanner.hasNext()){
                        resultJson.append(scanner.nextLine());
                    }

                    scanner.close();

                    //close url connection
                    conn.disconnect();

                    //parse the JSON string into a JSON obj
                    JSONParser parser= new JSONParser();
                    //System.out.println("Raw JSON response from location API: " + resultJson.toString());
                    JSONObject resultsJsonObj= (JSONObject) parser.parse(String.valueOf(resultJson));

                    System.out.println("resulsJSONObj for location API ->  " + resultsJsonObj);

                    //get the list of location data the API generated from the location name
                    JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                    return locationData;
                }

        
            }catch(Exception e){
                e.printStackTrace();
            }

            //incase location cant be found
            System.out.println("Failed to find location");
            return null;
        }

        private static HttpURLConnection fetchAPIResponse(String urlString){
            try{
                //attempt to create connection
                @SuppressWarnings("deprecation")
                URL url = new URL(urlString);

                System.out.println("fetchAPIResponse: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //set request to get
                conn.setRequestMethod("GET");

                //connec to our API
                conn.connect();
                return conn;


            }catch(IOException e){
                e.printStackTrace();
            }

            //couldnt make connection
            System.out.println("Failed in fetchAPIResponse");
            return null;
        }


private static int findIndexOfCurrentTime(JSONArray timeList){
    String currentTime = getCurrentTime();

    //loop throught the time list and find the one that matches our current time
    for(int i = 0 ; i < timeList.size(); i++){
        String time= (String) timeList.get(i);
        if(time.equalsIgnoreCase(currentTime));
            //return the index 
            return i;
    }

    return 0;
    
}

public static String getCurrentTime(){
    //get current date and time
    LocalDateTime currentDateTime = LocalDateTime.now();

    // date must be formated to 2024-09-25T00:00 --> to be readable by the API
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

    //format and print the current date and time
    String formattedDateTime = currentDateTime.format(formatter);
    
    
    return formattedDateTime;

}

//convet the weather code to a mroe readable format
private static String convertWeatherCode(long weathercode){
    String weatherCondition ="";

    if(weathercode == 0L){
        weatherCondition = "Clear";

    }else if(weathercode <= 3L && weathercode > 0L){
        weatherCondition = "Cloudy";

    }else if( (weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L) ) {
         weatherCondition = "Rain";
    }else if(weathercode >= 71L && weathercode > 77L){
         weatherCondition = "Snow";
    }

    return weatherCondition;
}






}



