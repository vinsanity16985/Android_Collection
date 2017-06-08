package apps.vinsa_000.networktesting;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements DownloadCallback {

    private static final String TAG = "MainActivity";
    private static final String URL = "http://thecelebrityspycom.ipage.com/wp-content/uploads/2016/05/Android-logo-qwsd.png";

    private NetworkFragment mNetworkFragment;
    private Spinner spinner;
    private ImageView imageView;

    private boolean isDownloading = false;

    /*
        DOWNLOAD USING OTHER TWO METHODS
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //Call super onCreate to do necessary Android things
        super.onCreate(savedInstanceState);

        //Set the layout for this Activity
        setContentView(R.layout.activity_main);

        //Initialize Spinner and ImageView in order to manipulate later on
        spinner = (Spinner) findViewById(R.id.spinner);
        imageView = (ImageView) findViewById(R.id.imageView);

        //Set a tag containing the URL String to ImageView for later use
        imageView.setTag(URL);

        //Create or grab a single instance of NetworkFragment
        //Pass FragmentManager to handle fragment stuff
        //Pass ImageView to download an image to
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), imageView);

        //Create an ArrayAdapter from download_array using simple_spinner_item layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.download_array, android.R.layout.simple_spinner_item);

        //Set the layout for the Spinner's dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Connect the adapter to the Spinner to populate it with data
        spinner.setAdapter(adapter);
    }

    public void startDownload(View view){
        //Switch checks to see which Spinner dropdown item has been selected by the user
        switch(spinner.getSelectedItem().toString()){
            //If "Asynctask" is selected then show Toast indicating
            case "Asynctask":
                Toast.makeText(this, "Asynctask", Toast.LENGTH_SHORT).show();
                //If not downloading and initialized mNetworkFragment then start downloading
                if(!isDownloading && mNetworkFragment != null){
                    mNetworkFragment.startDownload();
                    Log.d(TAG, "Asynctask Download Started");

                    //Flag to indicate that a download is in progress
                    isDownloading = true;
                }
                break;
            //If "IntentService" is selected then show Toast indicating
            case "IntentService":
                Toast.makeText(this, "IntentService", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "IntentService Download Started");

                break;
            //If "AsynctaskLoader" is selected then show Toast indicating
            case "AsynctaskLoader":
                Toast.makeText(this, "AsynctaskLoader", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "AsynctaskLoader Download Started");

                break;
        }
    }

    @Override
    public void updateFromDownload(String result) {
        //update UI
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        //Create ConnectivityManager by grabbing the CONNECTIVITY_SERVICE System Service
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        //Return the active NetworkInfo from the ConnectivityManager
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode){
            //UI Behavior
            case Progress.ERROR:
                Toast.makeText(this, "Download Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"Download Error");
                break;
            case Progress.CONNECT_SUCCESS:
                Toast.makeText(this, "Co", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Connection Successful");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                Log.i(TAG,"INPUT_STREAM Successful");
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.d(TAG,"Processing INPUT_STREAM");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Log.i(TAG,"Process INPUT_STREAM Success");
                break;
        }
    }

    @Override
    public void finishDownloading() {
        //Change Flag to not downloading
        isDownloading = false;

        //Cancel the download if mNetworkFragment is not already destroyed
        if(mNetworkFragment != null){
            mNetworkFragment.cancelDownload();
        }

        Log.d(TAG, "Finished Download");
    }

    @Override
    public void setImageViewBitmap(Bitmap result){
        imageView.setImageBitmap(result);

        Log.d(TAG, "Bitmap Set");
    }
}
