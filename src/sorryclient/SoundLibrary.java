package sorryclient;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundLibrary {
	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			// The wrapper thread is unnecessary, unless it blocks on the
			// Clip finishing; see comments.
			public void run() {
				try {
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(url));
					AudioFormat format = inputStream.getFormat();
					DataLine.Info info = new DataLine.Info(Clip.class, format);
					Clip clip = (Clip) AudioSystem.getLine(info);
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}).start();
	}
}
