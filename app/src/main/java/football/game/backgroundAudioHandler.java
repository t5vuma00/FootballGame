package football.game;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class backgroundAudioHandler extends Service {

    MediaPlayer player1;
    MediaPlayer player2;
    AssetFileDescriptor afd;

    private String audioClip = null;

    public IBinder onBind(Intent arg0) {

        //Vastaanotetaan mahdolliset extrat Intentistä, joka aloitti servicen
        Bundle extras = arg0.getExtras();
        if(extras!=null) {
            audioClip = (String) arg0.getExtras().get("audio");
            Log.d("bMusic", audioClip);
        }

        setAudio();

        //Luodaan ja aloitetaan ensimmäisen olion toisto
        player1.start();

        // Valmistelee seuraavan toisto-olion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            player1.setNextMediaPlayer(player2);
        }

        player1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                // When player1 completes, we reset it, and set up player2 to go back to player1 when it's done
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    player2.setNextMediaPlayer(player1);
                }
            }
        });
        player2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Likewise, when player2 completes, we reset it and tell it player1 to user player2 after it's finished again
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    player1.setNextMediaPlayer(player2);
                }
            }
        });

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setAudio()
    {
        if(audioClip.equals("setupMusic"))
        {
            //Tarvitaan callbackeihin
            afd = getResources().openRawResourceFd(R.raw.setupmusic);

            player1 = MediaPlayer.create(this, R.raw.setupmusic);
            player2 = MediaPlayer.create(this, R.raw.setupmusic);
        }

        else if(audioClip.equals("inGameMusic"))
        {
            //Tarvitaan callbackeihin
            afd = getResources().openRawResourceFd(R.raw.ingamemusic);

            player1 = MediaPlayer.create(this, R.raw.ingamemusic);
            player2 = MediaPlayer.create(this, R.raw.ingamemusic);
        }

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        //Vastaanotetaan mahdolliset extrat Intentistä, joka aloitti servicen
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            audioClip = (String) intent.getExtras().get("audio");
            Log.d("bMusic", audioClip);
        }

        setAudio();

        //Luodaan ja aloitetaan ensimmäisen olion toisto
        player1.start();

        // Valmistelee seuraavan toisto-olion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            player1.setNextMediaPlayer(player2);
        }

        player1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                // When player1 completes, we reset it, and set up player2 to go back to player1 when it's done
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    player2.setNextMediaPlayer(player1);
                }
            }
        });
        player2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Likewise, when player2 completes, we reset it and tell it player1 to user player2 after it's finished again
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    player1.setNextMediaPlayer(player2);
                }
            }
        });

        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {

        return null;
    }


    @Override
    public void onDestroy() {
        player1.stop();
        player1.release();

        player2.stop();
        player2.release();
    }

    @Override
    public void onLowMemory() {

    }
}
