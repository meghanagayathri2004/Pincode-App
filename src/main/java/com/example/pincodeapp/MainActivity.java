package com.example.pincodeapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.os.StrictMode;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
public class MainActivity extends AppCompatActivity {
    EditText editTextPincode;
    Button buttonFetch;
    TextView textViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextPincode = findViewById(R.id.editTextPincode);
        buttonFetch = findViewById(R.id.buttonFetch);
        textViewResult = findViewById(R.id.textViewResult);
        // Alow network on main thread (for demo only, not for production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pincode = editTextPincode.getText().toString().trim();
                if (pincode.isEmpty()) {
                    textViewResult.setText("Please enter a pincode");
                    return;
                }
                fetchLocationFromPincode(pincode);
            }
        });
    }
    private void fetchLocationFromPincode(String pincode) {
        try {
            URL url = new URL("https://api.postalpincode.in/pincode/" + pincode);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONArray outerArray = new JSONArray(builder.toString());
            JSONObject obj = outerArray.getJSONObject(0);
            JSONArray postOffices = obj.getJSONArray("PostOffice");
            if (postOffices.length() > 0) {
                JSONObject details = postOffices.getJSONObject(0);
                String district = details.getString("District");
                String state = details.getString("State");
                textViewResult.setText("City: " + district + "\nState: " + state);
            } else {
                textViewResult.setText("Invalid Pincode");
            }
        } catch (Exception e) {
            e.printStackTrace();
            textViewResult.setText("Error fetching data");
        }
    }
}
