package football.game;

import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private boolean muteOn = false;
    private String audio = null;
    Intent bgMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Piilottaa sovelluksen nimen
        getSupportActionBar().hide();

        //Piilottaa notification barin
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //määritellään service intentiksi
        bgMusic = new Intent(this, backgroundAudioHandler.class);

        //Aloittaa musiikin toistamisen
        audio = "setupMusic";
        setAudio();

        setContentView(R.layout.activity_main);
    }

    protected void startGame(View view){
        stopService(bgMusic);
        Intent intent = new Intent(this, gameActivity.class);
        startActivity(intent);
        //finish();
    }

    protected void setAudio()
    {
        if(muteOn == true)
        {
            stopService(bgMusic);
            Log.d("Musiikki", "Lopetus");
        }

        else if(muteOn == false)
        {
            //Aloittaa musiikin toistamisen
            bgMusic.putExtra("audio", audio);
            startService(bgMusic);
            //bindService(bgMusic, mServerConn, Context.BIND_AUTO_CREATE);
            //this.startService(bgMusic);
            Log.d("Musiikki", "Aloitus");
        }
    }

    protected void setMuteOn(View view)
    {
        //jos mute päällä
        if(muteOn)
        {
            Log.d("muteOn", "setFalse");
            muteOn = false;
        }
        //jos mute pois
        else if(!muteOn)
        {
            Log.d("muteOn", "setTrue");
            muteOn = true;
        }
        setAudio();
    }

    @Override
    public void onBackPressed()
    {
        stopService(bgMusic);
        Log.d("BACK", "mBack");
        finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopService(bgMusic);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(bgMusic);
    }
}
