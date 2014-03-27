import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;



public class Sound {

	private AudioClip soundFile;
	public boolean isLooping; 
	
	Sound (String soundName)
	{	
		File file = new File("sounds/" + soundName);
		isLooping = false;
		try
		{
			soundFile = Applet.newAudioClip(file.toURL());
		}
		catch (Exception e) {}
	}
	
	public void toggle()
	{
		play();		
		stop();
	}
	
	public void play()
	{
		soundFile.stop();
		soundFile.play();
	}
	
	public void stop()
	{
		soundFile.stop();
		isLooping = false;
	}
	
	public void loop()
	{
		soundFile.loop();
		isLooping = true;
	}
	
	  
}
