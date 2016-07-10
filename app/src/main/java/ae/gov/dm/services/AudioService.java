package ae.gov.dm.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;


/**
 * Audio Service class acting as an endpoint to fetch the audio files form the API
 */
public class AudioService {

    private static final String AUDIO_ENDPOINT = "https://3on.ae/clients/DM/audio/";
    private static final String TAG = "AudioService";
    private MediaPlayer mp;


    public void getAudio(final String id, final Context ctx) {

        String endpoint = AUDIO_ENDPOINT + id + ".wav";
        try {
            Log.d(TAG, "run: " + endpoint);
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(ctx, Uri.parse(endpoint));
            mp.prepare();
            mp.start();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}




