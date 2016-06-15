package amada.ramsatna.services;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.android.exoplayer.DecoderInfo;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import amada.ramsatna.R;
import amada.ramsatna.views.MainActivity;

/**
 * Audio Service class acting as an endpoint to fetch the audio files form the API 
 */
public class AudioService {

    private static final String AUDIO_ENDPOINT = "http://3on.ae/clients/DM/audio/";
    private static final String TAG = "AudioService";
    private URL url;
    private MediaPlayer mp;
    private ExoPlayer exoPlayer;
    private int requestedBufferSize = 5000;
    private int RENDERER_COUNT = 1;


    public void getAudio(final String id, final Context ctx) {

        Uri uri = Uri.parse("http://3on.ae/clients/DM/audio/315.caf");

        exoPlayer = ExoPlayer.Factory.newInstance(1, 1000, 5000);

        String userAgent = Util.getUserAgent(ctx, "ExoPlayerDemo");

        DataSource dataSource = new DefaultUriDataSource(ctx, null,userAgent);

        Allocator allocator = new DefaultAllocator(5000);

        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                uri, dataSource, allocator, 5000);

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, new MediaCodecSelector() {
            @Override
            public DecoderInfo getDecoderInfo(String mimeType, boolean requiresSecureDecoder) throws MediaCodecUtil.DecoderQueryException {
                return null;
            }

            @Override
            public String getPassthroughDecoderName() throws MediaCodecUtil.DecoderQueryException {
                return null;
            }
        });

        exoPlayer.prepare(audioRenderer);

        exoPlayer.setPlayWhenReady(true);


              /*  try {
                    String endpoint = AUDIO_ENDPOINT + id + ".caf";
                    url = new URL(endpoint);
                    Log.d(TAG, "run: " + endpoint);
                    mp = new MediaPlayer();
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.setDataSource(ctx, Uri.parse("http://3on.ae/clients/DM/audio/test.m4a"));
                    mp.prepare();
                    mp.start();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } */
    }

    private File mediaFile;

    public void playAudio(final String mediaUrl, final Context ctx) {

        new Thread() {
            @Override
            public void run() {

                try {
                    URLConnection cn = new URL(mediaUrl).openConnection();
                    InputStream is = cn.getInputStream();

                    // create file to store audio
                    mediaFile = new File(ctx.getCacheDir(), "mediafile");
                    FileOutputStream fos = new FileOutputStream(mediaFile);
                    byte buf[] = new byte[16 * 1024];
                    Log.i("FileOutputStream", "Download");

                    // write to file until complete
                    do {
                        int numread = is.read(buf);
                        if (numread <= 0)
                            break;
                        fos.write(buf, 0, numread);
                    } while (true);
                    fos.flush();
                    fos.close();
                    Log.i("FileOutputStream", "Saved");
                    MediaPlayer mp = new MediaPlayer();

                    // create listener to tidy up after playback complete
                    MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            // free up media player
                            mp.release();
                            Log.i("OnCompletionListener", "MediaPlayer Released");
                        }
                    };
                    mp.setOnCompletionListener(listener);
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream fis = new FileInputStream(mediaFile);
                    // set mediaplayer data source to file descriptor of input stream
                    Log.d(TAG, "run: " + fis.getFD().toString());
                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                    Log.i("MediaPlayer", "Start Player");
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

}
