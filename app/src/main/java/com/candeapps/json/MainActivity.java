package com.candeapps.json;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    private EditText etSefer;
    private EditText etPerek;
    private TextView tvOutput;
    String[] pasukEnglish  = null;
    String pasukHebrew = null;

    private String base_url = "http://www.sefaria.org/api/texts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSefer = (EditText)findViewById(R.id.etSefer);
        etPerek = (EditText)findViewById(R.id.etPerek);
        tvOutput = (TextView)findViewById(R.id.tvOutput);
    }

    public void getText(View v) {
        String sefer = etSefer.getText().toString();
        String perek = etPerek.getText().toString();
        String urlString = base_url + sefer + "." + perek;

        new JSONParserTask().execute(urlString);
    }

    private String[] parseJSON(String in) {
        String[] dataArr = {null, null};

        try {
            JSONObject jsonData = new JSONObject(in);
            JSONArray textArray = jsonData.getJSONArray("text");
            JSONArray heArray = jsonData.getJSONArray("he");
            //pasukEnglish = new String[textArray.length()];
            for (int i = 0; i < textArray.length(); i++) {
                //JSONObject jsonText = textArray.getJSONObject(i);
                //String pasuk = jsonText.toString();
                //pasukEnglish[i] = jsonText.toString();
                dataArr[0] += textArray.getString(i);
                Log.v("JSONthing", dataArr[0]);
            }
            //String[] pasukEnglish = textArray.toString();
            //JSONObject jsonHe = jsonData.getJSONObject(SEFARIA_HE);
            //String pasukHebrew = jsonHe.toString();
            //String pasukHebrew = jsonData.getString("he");
            for (int i = 0; i < heArray.length(); i++){
                dataArr[1] += heArray.getString(i);
                Log.v("JSONthing", dataArr[1]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONthing", "error processing json data");
        }
        return  dataArr;

    }

  /*  private String parseJSONForHebrew(String in) {
        //String dataArr = {null, null};

        try {
            JSONObject jsonData = new JSONObject(in);
            //String[] pasukEnglish = textArray.toString();
            //JSONObject jsonHe = jsonData.getJSONObject(SEFARIA_HE);
            //String pasukHebrew = jsonHe.toString();
            pasukHebrew = jsonData.getString("he");
            Log.v("JSONthing", pasukHebrew);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONthing", "error processing json data");
        }
        return pasukHebrew;
    }*/

    private String fetchSefariaData(String urlString) {
        String sefariaData = null;

        try {
           URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            InputStream stream = conn.getInputStream();
            String data = convertStreamToString(stream);

            String[] dataArr = parseJSON(data);
            if (dataArr[0] != null && dataArr[1] != null) {
                sefariaData = "Text: " + dataArr[0] +
                        "\nhe: " + dataArr[1];
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("ERROR", "malformed url");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR", "io exception");
        }
        return sefariaData;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private class JSONParserTask extends AsyncTask <String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = fetchSefariaData(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                tvOutput.setText(result);
            } else {
                tvOutput.setText("Can't fetch data");
            }
        }
    }
}
