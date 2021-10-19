package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsURLs=new ArrayList<String>();
    ArrayList<String> celebsNames=new ArrayList<String>();
    String []answers=new String[4];
    int locationofCorrectAnswer=0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    int chosenCeleb=0;
    ImageView imageView;

    public void chosen(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationofCorrectAnswer)))
        {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Wrong! It was "+celebsNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();

        newQuesion();
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            catch(Exception e)
            {
               e.printStackTrace();
               return null;
            }
        }
    }


   public static class DownloadTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        Log.i("celebtag", "CELEB url = " + urls[0]);
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        URL url;
        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            return result.toString();
        } catch (MalformedURLException e) {
            result.append("Error: MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}

        public void newQuesion()
         {
           try {
               Random rand = new Random();

               chosenCeleb = rand.nextInt(celebsURLs.size());
               ImageDownloader imagetask = new ImageDownloader();

               Bitmap celebImage = imagetask.execute(celebsURLs.get(chosenCeleb)).get();

               imageView.setImageBitmap(celebImage);

               locationofCorrectAnswer = rand.nextInt(4);
               int incorrectAnswerLocation;
               for (int i = 0; i < 4; i++) {
                   if (i == locationofCorrectAnswer)
                       answers[i] = celebsNames.get(chosenCeleb);
                   else {
                       incorrectAnswerLocation = rand.nextInt(celebsURLs.size());
                       while (incorrectAnswerLocation == chosenCeleb) {
                           incorrectAnswerLocation = rand.nextInt(celebsURLs.size());
                       }
                       answers[i] = celebsNames.get(incorrectAnswerLocation);
                   }
               }
               button1.setText(answers[0]);
               button2.setText(answers[1]);
               button3.setText(answers[2]);
               button4.setText(answers[3]);
           }
           catch (Exception e){
               e.printStackTrace();
           }

         }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);


       DownloadTask task=new DownloadTask();
        String result=null;

        try {
            result=task.execute("https://www.imdb.com/list/ls052283250/").get();


            String[] splitresult=result.split("<div class=\"footer filmosearch\">");


            Pattern p=Pattern.compile("height=\"209\"\nsrc=\"(.*?)\"");
            Matcher m=p.matcher(splitresult[0]);
            while(m.find())
            {
             celebsURLs.add(m.group(1));
            }
            p=Pattern.compile("img alt=\"(.*?)\"");
            m=p.matcher(splitresult[0]);

            while(m.find())
            {
                celebsNames.add(m.group(1));
            }
           newQuesion();

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    }
