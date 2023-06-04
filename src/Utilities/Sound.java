package Utilities;
import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
    
    Clip clip;
    URL soundURL;
    FloatControl floatControl;

    public Sound(String path) {
        this.setFile(path);
        floatControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
    }

    public Sound() {

    }

    public void setFile(String path) {
        // NOTE: fix this. make it so that you'll only call the play() method when you want to play a sound and it has 1 parameter,
        // the file path of the audio
        soundURL = getClass().getResource(path);
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        }
        catch (Exception e ) {

        }
    }

    public void play(String path) {
        setFile(path);
        clip.start();
    }

    public void play() {
        clip.start();
    }

    public void loop(String path) {
        setFile(path);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }


    public void stop() {
        clip.stop();
        clip.getLevel();
    }

    public void adjustVolume(float value) {
        floatControl.setValue(value);
    }
}
