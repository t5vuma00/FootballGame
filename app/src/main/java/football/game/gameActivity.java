package football.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

public class gameActivity extends AppCompatActivity {

    Intent bgMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Piilottaa sovelluksen nimen
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        //Piilottaa notification barin
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new FootballGame(this, null));

        bgMusic = new Intent(this, backgroundAudioHandler.class);
        bgMusic.putExtra("audio", "inGameMusic");
        bindService(bgMusic, mServerConn, Context.BIND_AUTO_CREATE);
    }

    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onBackPressed(){

        FootballGameThread footballGameThread =  new FootballGameThread(null);
        footballGameThread.setRunning(false);
        stopService(bgMusic);

        Log.d("gameActivity", "onbackpressed");
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopService(bgMusic);

        FootballGameThread footballGameThread =  new FootballGameThread(null);
        footballGameThread.setRunning(false);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(bgMusic);

        Log.d("gDestroy", "gDestroy");

    }
}


