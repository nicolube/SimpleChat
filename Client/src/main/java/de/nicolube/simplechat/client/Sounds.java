package de.nicolube.simplechat.client;

import lombok.SneakyThrows;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

public class Sounds {
    private static Map<String, Clip> audioClips = new HashMap<>();

    public static void playAudio(String path, float gain) {
        Clip clip = Sounds.audioClips.get(path);
        if (clip == null) {
            clip = cacheAudio(path);
        }
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(((control.getMaximum() - control.getMinimum()) * (gain / 100)) + control.getMinimum());
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    @SneakyThrows
    public static Clip cacheAudio(String path) {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
                AudioSystem.getAudioInputStream(Sounds.class.getResource(path)));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        Sounds.audioClips.put(path, clip);
        return clip;
    }
}
