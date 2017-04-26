package football.game;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

public class gameActivity extends AppCompatActivity {

    Intent bgMusic;
    Intent sSkins;
    public String character1;
    public String character2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Piilottaa sovelluksen nimen
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        //Piilottaa notification barin
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(new FootballGame(this, null));

        bgMusic = new Intent(this, backgroundAudioHandler.class);
        bgMusic.putExtra("audio", "inGameMusic");
        //bindService(bgMusic, mServerConn, Context.BIND_AUTO_CREATE);
        //startService(bgMusic);



        //Tarkistetaan hahmot
        Intent cSkins = getIntent();
        Log.d("gActivity", "EXTRAS_COMING");

        Bundle extras = cSkins.getExtras();
        if (extras != null){
            character1 = (String) cSkins.getExtras().get("character1");
            character2 = (String) cSkins.getExtras().get("character2");

            Log.d("gActivity C1", character1);
            Log.d("gActivity C2", character2);

            //sSkins.putExtra("character1", character1);
            //sSkins.putExtra("character2", character2);

            //startActivity(sSkins);
        }


/*
        SharedPreferences prefs = getSharedPreferences("hahmovalinnat", MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            String testiCharacter1 = prefs.getString("character1", "No name defined");//"No name defined" is the default value.
            Log.d("testiCharacter12", testiCharacter1);
            //int idName = prefs.getInt("idName", 0); //0 is the default value.
        }
        */

        setContentView(new FootballGame(this, null));
    }


    //Bindausta varten
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
        //stopService(bgMusic);
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
    protected void onResume(){
        super.onResume();
        stopService(bgMusic);
        startService(bgMusic);


        FootballGameThread footballGameThread =  new FootballGameThread(null);
        footballGameThread.setRunning(true);
    }


    @Override
    protected void onDestroy()
    {
        finish();


        FootballGameThread footballGameThread =  new FootballGameThread(null);
        footballGameThread.setRunning(false);
        //stopService(bgMusic);
        super.onDestroy();


        Log.d("gDestroy", "gDestroy");

    }
/*
    @Override
    protected void onStop()
    {
        super.onStop();
        stopService(bgMusic);

        finish();

        Log.d("gSTOP", "gSTOP");

    }
*/

}


