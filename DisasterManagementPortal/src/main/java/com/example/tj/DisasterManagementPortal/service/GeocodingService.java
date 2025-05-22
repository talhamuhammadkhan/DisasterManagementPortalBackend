package com.example.tj.DisasterManagementPortal.service;

import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private OkHttpClient client = new OkHttpClient();

    public Coordinates getCoordinates(String address) throws Exception {
        System.out.print(address);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=(Enter your Google Maps API key here)";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String jsonResponse = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            System.out.print(results);
            
            if (results.length() > 0) {
                JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                return new Coordinates(lat, lng);
            } else {
                throw new Exception("Geocoding API returned no results");
            }
        }
    }

    public static class Coordinates {
        public double lat;
        public double lng;

        public Coordinates(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
