package apps.vinsa_000.networktesting;


import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";

    private static final String URL_KEY = "URLKey";

    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;
    private String mUrlString;

    public static NetworkFragment getInstance(FragmentManager fragmentManager, ImageView imageView){
        //Look for already created instance of NetworkFragment
        NetworkFragment networkFragment = (NetworkFragment) fragmentManager.findFragmentByTag(TAG);

        //If it doesn't find one then create a NetworkFragment
        if(networkFragment == null){
            networkFragment = new NetworkFragment();

            //Create Bundle and pass the URL String from ImageView
            Bundle args = new Bundle();
            args.putString(URL_KEY, imageView.getTag().toString());
            networkFragment.setArguments(args);

            //Add the Fragment using FragmentManager
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }

        //Return instance of NetworkFragment
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrlString = getArguments().getString(URL_KEY);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        //When attached create Callback
        mCallback = (DownloadCallback)context;
    }

    @Override
    public void onDetach(){
        super.onDetach();

        //When detached destroy callback
        mCallback = null;
    }

    @Override
    public void onDestroy(){
        cancelDownload();
        super.onDestroy();
    }


    public void startDownload(){
        //Cancel any download currently in progress
        cancelDownload();

        //Create a Asynctask Object
        //Pass a Callback
        mDownloadTask = new DownloadTask(mCallback);

        //Start the Asynctask
        mDownloadTask.execute(mUrlString);
    }

    public void cancelDownload(){
        //Cancel any currently running Asynctask
        if(mDownloadTask != null){
            mDownloadTask.cancel(true);
        }
    }

}
