package football.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;

public class chooseSkins extends AppCompatActivity {

    Intent bgMusic;

    ImageView imageViewBall;
    ImageView imageViewCharacter1;
    ImageView imageViewCharacter2;

    //Tehdään gameactivitystä intentti
    Intent gActivity;

    //Muuttujat, joiden perusteella hahmo valitaan
    public int character1 = 0;
    public int character2 = 1;

    private int background = 0;
    private int football = 0;

    public String hahmo1;
    public String hahmo2;

    //Alustetaan taulukko, joka sisältää hahmojen kuvat 0-6
    private int[] textureArrayWin = {
            R.drawable.character1,
            R.drawable.character2,
            R.drawable.character3,
            R.drawable.character4,
            R.drawable.character5,
            R.drawable.character6,
            R.drawable.character7
    };

    //Alustetaan taulukko, joka sisältää taustojen kuvat 0-1
    private int[] backgroundArray = {
            R.drawable.aurinkotausta,
            R.drawable.kaupunkitausta
    };

    //Alustetaan taulukko, joka sisältää jalkapallojen kuvat 0-1
    private int[] footballArray = {
            R.drawable.football1,
            R.drawable.football2
    };

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_skins);

        //bgMusic = new Intent(this, backgroundAudioHandler.class);
        //bgMusic.putExtra("audio", "setupMusic");
        //startService(bgMusic);

        imageViewBall = (ImageView) findViewById(R.id.imageBall);
        imageViewCharacter1 = (ImageView) findViewById(R.id.imageCharacter1);
        imageViewCharacter2 = (ImageView) findViewById(R.id.imageCharacter2);

        tableLayout = (TableLayout) findViewById(R.id.chooseSkins);


        gActivity = new Intent(this, gameActivity.class);


        //Piilottaa sovelluksen nimen
        getSupportActionBar().hide();

        //Piilottaa notification barin
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public void onButtonCharacter1Right(View v)
    {
        character1 += 1;

        if(character1 > (textureArrayWin.length - 1))
        {
            character1 = 0;
        }

        imageViewCharacter1.setImageResource(textureArrayWin[character1]);
    }

    public void onButtonCharacter1Left(View v)
    {
        character1 -=1;

        if(character1 < 0)
        {
            character1 = 6;
        }

        imageViewCharacter1.setImageResource(textureArrayWin[character1]);
    }

    public void onButtonCharacter2Right(View v)
    {
        character2 += 1;

        if(character2 > (textureArrayWin.length - 1))
        {
            character2 = 0;
        }

        imageViewCharacter2.setImageResource(textureArrayWin[character2]);
    }
    public void onButtonCharacter2Left(View v)
    {

        character2 -= 1;

        if(character2 < 0)
        {
            character2 = 6;
        }

        imageViewCharacter2.setImageResource(textureArrayWin[character2]);
    }

    public void onButtonBackgroundRight(View v)
    {
        background += 1;

        if(background > (backgroundArray.length - 1))
        {
            background = 0;
        }
        tableLayout.setBackgroundResource(backgroundArray[background]);
    }

    public void onButtonBackgroundLeft(View v)
    {

        background -= 1;
        if(background < 0)
        {
            background = 1;
        }

        tableLayout.setBackgroundResource(backgroundArray[background]);
    }

    public void onButtonBallRight(View v)
    {

        football += 1;
        if(football> (footballArray.length -1) )
        {
            football = 0;
        }

        Log.d("football", String.valueOf(football));

        imageViewBall.setImageResource(footballArray[football]);
    }

    public void onButtonBallLeft(View v)
    {

        football -= 1;
        if(football< 0)
        {
            football = 1;
        }

        Log.d("football", String.valueOf(football));

        imageViewBall.setImageResource(footballArray[football]);
    }

    public void onButtonContinue(View v)
    {
        //Pysäytetään valikkomusiikki
        //stopService(bgMusic);

        //Lopettaa hahmovalikon, kun se on poissa ruudulta
        finish();

        hahmo1 = String.valueOf(character1);
        hahmo2 = String.valueOf(character2);

        //Pelin aloitus
        gActivity.putExtra("character1", hahmo1);
        gActivity.putExtra("character2", hahmo2);

        //Tallennetaan hahmovalinnat datatiedostoon, joka on käytettävissä eri luokissa
        SharedPreferences pref = getApplicationContext().getSharedPreferences("hahmovalinnat", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("character1", character1);
        editor.putInt("character2", character2);
        editor.putInt("background", background);
        editor.putInt("football", football);
        editor.apply();

        startActivity(gActivity);
    }

    @Override
    protected void onDestroy()
    {
        finish();
        super.onDestroy();
        Log.d("cSkins", "cDestroy");

    }

}
