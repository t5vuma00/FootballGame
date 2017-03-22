package football.game;

import android.app.ActionBar;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class gameActivity extends AppCompatActivity {

    //FootballGame footballGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Piilottaa sovelluksen nimen
        getSupportActionBar().hide();
        //getActionBar().hide();


        /*
        // Asetetaan peli kokonäyttötilaan
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Poistaa titlen näkyvistä
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        //setContentView(R.layout.activity_game);
        setContentView(new FootballGame(this, null));
    }
}


