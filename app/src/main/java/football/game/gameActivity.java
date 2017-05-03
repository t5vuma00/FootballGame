package football.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class gameActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Intent bgMusic;
    FootballGame footballGame;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String key_endgame = "endgame";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Piilottaa sovelluksen nimen
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        //Piilottaa notification barin
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Luodaan muuttuja johon footballgame pääsee käsiksi
        createSharedPreferences();

        //PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener();


        footballGame = new FootballGame(this,null);
        setContentView(footballGame);
        //= (ImageView)findViewById(R.id.jalat) ;


        /*
         bgMusic = new Intent(this, backgroundAudioHandler.class);
         bgMusic.putExtra("audio", "inGameMusic");
         bindService(bgMusic, mServerConn, Context.BIND_AUTO_CREATE);*/
        //firstTimeStarting = false;


    }
    /*
    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };*/


    @Override
    public void onBackPressed(){
        stopFootballGame();

        //stopService(bgMusic);
        setContentView(R.layout.layout_pause);

        Log.d("gameActivity", "onbackpressed");
        //super.onBackPressed();
        //finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        FootballGameThread footballGameThread = new FootballGameThread(null);
        footballGameThread.setRunning(true);

    }

    @Override
    protected void onPause(){
        super.onPause();
        //stopService(bgMusic);

        stopFootballGame();
    }

    @Override
    protected void onStop(){
        super.onStop();

        //stopService(bgMusic);

        stopFootballGame();

        //finish();
    }

    //Pelaaja haluaa jatkaa peliä kun peli on vielä kesken
    public void continueGame(View view){
        //stopService(bgMusic);
        //stopFootballGame();
        setContentView(footballGame);
    }

    //Mennään main menuun
    public void goMainMenu(View view){
        //stopService(bgMusic);
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void resetBall(View view){
        footballGame.setStartCoordinates();
        footballGame.nullSpeeds();
        setContentView(footballGame);
    }

    //Pelaaja haluaa pelata uudelleen
    public void playAgain(View view){
        setContentView(footballGame);
        editor.putBoolean(key_endgame, false);
        editor.apply();
    }

    //Pysäytetään jalkapallopeli
    private void stopFootballGame(){
        FootballGameThread footballGameThread =  new FootballGameThread(null);
        footballGameThread.setRunning(false);
    }

    //Luodaan muuttuja joka näkyy muissakin activityissä
    public void createSharedPreferences(){

        sharedPreferences = getApplicationContext().getSharedPreferences("endgame", MODE_PRIVATE);
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = sharedPreferences.edit();
        editor.putBoolean(key_endgame, false);
        editor.apply();

    }

    //Katsotaan muuttuuko jaetun muuttujan arvo
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(key_endgame)){
            //Jos jaetun muuttujan arvo muuttuu todeksi tiedetään että peli loppui ja pitää laittaa lopetusnäyttö
            boolean booleanEndgame = sharedPreferences.getBoolean(key_endgame, false);
            if(booleanEndgame){
                setContentView(R.layout.layout_endgame);
            }
        }
    }
}