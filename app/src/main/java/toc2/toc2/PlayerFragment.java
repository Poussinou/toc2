package toc2.toc2;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private PlayerService playerService;
    public boolean playerServiceBound = false;
    private Context appContext;
    public int speed = NavigationActivity.SPEED_INITIAL;

    private ServiceConnection playerConnection = null;

    private MetronomeFragment metrFrag = null;

    /** Defines callbacks for service binding, passed to bindService() */
    //private final ServiceConnection playerConnection = new ServiceConnection() {
    //    @Override
    //    public void onServiceConnected(ComponentName className, IBinder service) {

            //NavigationActivity act = (NavigationActivity) getActivity();
    //        if(appContext != null) {
    //            // We've bound to LocalService, cast the IBinder and get LocalService instance
    //            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
    //            playerService = binder.getService();
    //            playerServiceBound = true;
    //            playerService.changeSpeed(speed);
    //            final int sound = R.raw.hhp_dry_a;
    //            playerService.changeSound(sound);
    //            startPlayer();
    //        }
    //    }

    //    @Override
    //    public void onServiceDisconnected(ComponentName arg0) {
    //        playerServiceBound = false;
    //    }
    //};

    public PlayerFragment() {
        // Required empty public constructor


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("Metronome", "PlayerFragment:onCreate");
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);
        return null;
    }


    @Override
    public void onDestroy() {
        Log.v("Metronome", "PlayerFragment:onDestroy");
        if(playerServiceBound) {
            appContext.unbindService(playerConnection);
            playerServiceBound = false;
        }
        super.onDestroy();
    }

    //public boolean playerServiceBound(){
    //    return playerServiceBound;
    //}

    public PlayerService bindAndStartPlayer(Context context){
        Log.v("Metronome", "PlayerFragment:bindAndStartPlayer");
        appContext = context;

        if(!playerServiceBound) {

            playerConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {
                    Log.v("Metronome", "PlayerService:onServiceConnected");
                    //NavigationActivity act = (NavigationActivity) getActivity();
                    if (appContext != null) {
                        // We've bound to LocalService, cast the IBinder and get LocalService instance
                        PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
                        playerService = binder.getService();
                        playerServiceBound = true;
                        playerService.changeSpeed(speed);
                        final int sound = R.raw.hhp_dry_a;
                        playerService.changeSound(sound);
                        startPlayer();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                    Log.v("Metronome", "PlayerService:onServiceDisconnected");
                    playerServiceBound = false;
                }
            };

            Intent serviceIntent = new Intent(appContext, PlayerService.class);
            appContext.bindService(serviceIntent, playerConnection, Context.BIND_AUTO_CREATE);
        }
        return playerService;
    }

    public void startPlayer() {
        if (playerServiceBound) {
            playerService.startPlay();
            updateMetronomeFragment();
            //metrFrag.playpauseButton.setImageResource(R.drawable.ic_pause);
        }
    }

    public void stopPlayer() {
        if (playerServiceBound) {
            playerService.stopPlay();
            updateMetronomeFragment();
            //metrFrag.playpauseButton.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateMetronomeFragment() {
        Log.v("Metronome", "PlayerFragment:updateMetronomeFragment");
        if(metrFrag != null){
            metrFrag.updateView(playerService);
        }
    }

    public void togglePlayerIfAvailable() {
        if(playerServiceBound) {
            if(playerService.getPlayerStatus() == PlayerService.PLAYER_STARTED) {
                stopPlayer();
            }
            else if(playerService.getPlayerStatus() == PlayerService.PLAYER_STOPPED){
                startPlayer();
            }
        }
    }

    public void togglePlayer(Context context) {

        if(!playerServiceBound) {
            playerService = bindAndStartPlayer(context);
            //bindToPlayerService(); // this also starts player
        }
        else {
            if(playerService.getPlayerStatus() == PlayerService.PLAYER_STARTED) {
                stopPlayer();
            }
            else if(playerService.getPlayerStatus() == PlayerService.PLAYER_STOPPED){
                startPlayer();
            }
        }
    }

    public void changeSpeed(int val) {
        speed = val;
        if (playerServiceBound) {
            playerService.changeSpeed(val);
        }
    }

    public void setMetronomeFragment(MetronomeFragment metronomeFragment){
        metrFrag = metronomeFragment;
        updateMetronomeFragment();
    }

    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                         Bundle savedInstanceState) {
    //    TextView textView = new TextView(getActivity());
    //    textView.setText(R.string.hello_blank_fragment);
    //    return textView;
    //}

}