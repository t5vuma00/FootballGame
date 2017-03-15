package football.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class gameActivity extends AppCompatActivity {

    //Ball ball;
    //FootballGame fGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);
        setContentView(new FootballGame(this, null));
    }
}

