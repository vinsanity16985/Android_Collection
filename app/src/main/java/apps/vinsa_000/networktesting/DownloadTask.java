package apps.vinsa_000.networktesting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vinsa_000 on 5/29/2017.
 */

public class DownloadTask extends AsyncTask<String, Integer, Bitmap> {

    private DownloadCallback mCallback;

    DownloadTask(DownloadCallback callback) {
        setCallback(callback);
    }

    void setCallback(DownloadCallback callback){
        mCallback = callback;
    }

    //Check to make sure their is Internet in order to continue with download
    protected void onPreExecute(){
        if(mCallback != null){
            //Grab NetworkInfo to check connectivity
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
            if(networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                    && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)){
                //Update UI to show their is no internet
                mCallback.updateFromDownload("No Internet Connection");
                cancel(true);
            }
        }
    }


    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap result = null;

        //Make sure the download hasn't been cancelled and the URL is not empty
        if(!isCancelled() && urls != null && urls.length > 0){
            //Grab URL String
            String urlString = urls[0];

            try{
                //Create URL Object using URL String
                URL url = new URL(urlString);

                //Call downloadURL() and grab its resulting String
                result = downloadURL(url);

                if(result == null){
                    throw new IOException("No Response Recieved");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        if(result != null && mCallback != null){
            mCallback.setImageViewBitmap(result);
        }

        mCallback.finishDownloading();
    }

    private Bitmap downloadURL(URL url) throws IOException{
        //Will handle stream of byte data
        InputStream stream = null;

        //Create a HTTPS Connection to the address specified with the URL
        HttpURLConnection connection = null;

        //Data streamed into here
        Bitmap result = null;

        try{
            //Open the connection
            connection = (HttpURLConnection)url.openConnection();

            //Waits 3000 ms to read data from connection, will throw exception if no data is read
            connection.setReadTimeout(3000);

            //Waits 3000 ms to establish connection, will throw exception if connection is not established
            connection.setConnectTimeout(3000);

            //Set HTTP request method
            connection.setRequestMethod("GET");

            //Set Flag to indicate using connection strictly for Input
            connection.setDoInput(true);

            //Connects
            connection.connect();

            //Let UI know a connection was established successfully
            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);

            //Grab response code from HTTP Connection
            int responseCode = connection.getResponseCode();

            //If a connection is not successful
            if(responseCode != HttpsURLConnection.HTTP_OK){
                throw new IOException("HTTP Error Code: " + responseCode);
            }

            //Grab the byte data input stream
            stream = connection.getInputStream();

            //Let UI know the data stream was grabbed successfully
            publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);

            //Read from stream if it is present
            if(stream != null){
                result  = BitmapFactory.decodeStream(stream);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

}
