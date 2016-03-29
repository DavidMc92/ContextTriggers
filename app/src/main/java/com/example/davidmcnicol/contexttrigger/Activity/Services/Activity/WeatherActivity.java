package com.example.davidmcnicol.contexttrigger.Activity.Services.Activity;

import android.app.Activity;
import android.os.Bundle;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidmcnicol.contexttrigger.R;

/**
 * Created by davidmcnicol on 15/03/16.
 */
public class WeatherActivity extends Activity {


    private String temperature, date, condition, humidity, wind, link;
    private Bitmap icon = null;
    private TextView title, tempText, dateText, conditionText, windText, humidityText,day1,day2, day3, day4;
    private ImageView image;
    private ArrayList<String> weather = new ArrayList<String>();
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);


        image = (ImageView)findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.weather_title);
        dateText = (TextView) findViewById(R.id.dateText);
        tempText = (TextView) findViewById(R.id.tempText);
        conditionText = (TextView) findViewById(R.id.conditionText);
        humidityText = (TextView) findViewById(R.id.humidityText);
        windText = (TextView) findViewById(R.id.windText);
        day1 = (TextView)findViewById(R.id.day1);
        day2 = (TextView)findViewById(R.id.day2);
        day3 = (TextView)findViewById(R.id.day3);
        day4 = (TextView)findViewById(R.id.day4);
//        weatherLink = (TextView)findViewById(R.id.weatherLink);

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        ImageButton report = (ImageButton) findViewById(R.id.reportBtn);


        new retrieve_weatherTask().execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.weather, menu);
//        return true;
//    }



    protected class retrieve_weatherTask extends AsyncTask<Void, String, String> {

        protected void onPreExecute(){
            dialog = new ProgressDialog(WeatherActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading…");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
        // TODO Auto-generated method stub
            String qResult = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://weather.yahooapis.com/forecastrss?w=2295425&u=c&#8221");

            try {
                HttpResponse response = httpClient.execute(httpGet,
                        localContext);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    Reader in = new InputStreamReader(inputStream);
                    BufferedReader bufferedreader = new BufferedReader(in);
                    StringBuilder stringBuilder = new StringBuilder();
                    String stringReadLine = null;
                    while ((stringReadLine = bufferedreader.readLine()) != null) {
                        stringBuilder.append(stringReadLine + "\n");
                    }
                    qResult = stringBuilder.toString();
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG)
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG)
                        .show();
            }

            Document dest = null;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder parser;
            try {
                parser = dbFactory.newDocumentBuilder();
                dest = parser
                        .parse(new ByteArrayInputStream(qResult.getBytes()));
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
                Toast.makeText(WeatherActivity.this, e1.toString(), Toast.LENGTH_LONG)
                        .show();
            } catch (SAXException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG)
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG)
                        .show();
            }

            Node temperatureNode = dest.getElementsByTagName(
                    "yweather:condition").item(0);
            temperature = temperatureNode.getAttributes()
                    .getNamedItem("temp").getNodeValue().toString();
            Node tempUnitNode = dest.getElementsByTagName("yweather:units").item(0);
            temperature = temperature + "°" +tempUnitNode.getAttributes().getNamedItem("temperature").getNodeValue().toString();

            Node dateNode = dest.getElementsByTagName("yweather:forecast")
            .item(0);
            date = dateNode.getAttributes().getNamedItem("date")
                    .getNodeValue().toString();

            Node conditionNode = dest
                    .getElementsByTagName("yweather:condition").item(0);
            condition = conditionNode.getAttributes()
                    .getNamedItem("text").getNodeValue().toString();

            Node humidityNode = dest
                    .getElementsByTagName("yweather:atmosphere").item(0);
            humidity = humidityNode.getAttributes()
                    .getNamedItem("humidity").getNodeValue().toString();
            humidity = humidity + "%";

            Node windNode = dest.getElementsByTagName("yweather:wind").item(0);
            wind = windNode.getAttributes().getNamedItem("speed").getNodeValue().toString();
            Node windUnitNode = dest.getElementsByTagName("yweather:units").item(0);
            wind = wind + " "+windUnitNode.getAttributes().getNamedItem("speed")
                    .getNodeValue().toString();

            String desc = dest.getElementsByTagName("item").item(0)
                    .getChildNodes().item(13).getTextContent().toString();
            StringTokenizer str = new StringTokenizer(desc, "<=>");
            System.out.println("Tokens: " + str.nextToken("=>"));
            String src = str.nextToken();
            System.out.println("src: "+ src);
            String url1 = src.substring(1, src.length() - 2);
            Pattern TAG_REGEX = Pattern.compile("(.+?)<br />");
            Matcher matcher = TAG_REGEX.matcher(desc);
            while (matcher.find()) {
                weather.add(matcher.group(1));
            }

            Pattern links = Pattern.compile("(.+?)<BR/>");
            matcher = links.matcher(desc);
            while(matcher.find()){
                System.out.println("Match Links: "+ (matcher.group(1)));
                link = matcher.group(1);
            }

/* String test = (Html.fromHtml(desc)).toString();
System.out.println("test: "+ test);
StringTokenizer tkn = new StringTokenizer(test);
for(int i=0; i < tkn.countTokens(); i++){
System.out.println("Remaining: "+tkn.nextToken());
}*/

            InputStream in = null;
            try {
// in = OpenHttpConnection(url1);
                int response = -1;
                URL url = new URL(url1);
                URLConnection conn = url.openConnection();

                if (!(conn instanceof HttpURLConnection))
                    throw new IOException("Not an HTTP connection");
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                response = httpConn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    System.out.println("*********************");
                    in = httpConn.getInputStream();
                }
                icon = BitmapFactory.decodeStream(in);
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return qResult;

        }

        protected void onPostExecute(String result) {
            System.out.println("POST EXECUTE");
            if(dialog.isShowing())
                dialog.dismiss();
            tempText.setText("Temperature: "+temperature);
            conditionText.setText("Condition: "+condition);
            dateText.setText("Date: "+date);
            humidityText.setText("Humidity: "+humidity);
            windText.setText("Wind: " + wind);
            image.setImageBitmap(icon);
            day1.setText(weather.get(3));
            day2.setText(weather.get(4));
            day3.setText(weather.get(5));
            day4.setText(weather.get(6));
//            weatherLink.setText(Html.fromHtml(link));

        }
    }
    

}
