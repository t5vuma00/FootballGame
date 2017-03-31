package football.game;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static java.lang.Thread.sleep;

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

    @Override
    public void onBackPressed(){

        FootballGameThread footballGameThread =  new FootballGameThread(null);
        footballGameThread.setRunning(false);
        /*
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        /*
        FootballGame footballGame = new FootballGame(this, null);
        if(footballGame.holder != null){
            footballGame.holder.removeCallback(null);
            finish();
        }*/
        //footballGame.view.is
        //footballGame.getHolder().unlockCanvasAndPost(null);
        //footballGame.getHolder().removeCallback();
        Log.d("gameActivity", "onbackpressed");
        super.onBackPressed();
    }
}


