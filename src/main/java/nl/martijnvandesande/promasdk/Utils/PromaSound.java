package nl.martijnvandesande.promasdk.Utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by martijn.vandersande on 8/21/17.
 */

public class PromaSound {

    private static PromaSound instance = null;

    private SoundPool soundPool = null;

    protected PromaSound() {
        // Exists only to defeat instantiation.
    }
    public static PromaSound getInstance() {
        if(instance == null) {
            instance = new PromaSound();
        }
        return instance;
    }

    @SuppressWarnings("deprecation")
    public SoundPool getSoundPool(){

        if(soundPool == null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                soundPool = new SoundPool.Builder()
                        .setAudioAttributes(attributes)
                        .setMaxStreams(20)
                        .build();
            } else {

                soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
            }
        }

        return soundPool;
    }

}
