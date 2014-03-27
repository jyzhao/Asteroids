import java.util.HashMap;
import java.util.Map;


public class SoundsContainer {
	Map<String, Sound> soundDic = new HashMap<String, Sound>();
	
	
	public SoundsContainer(String[] files) {
		// TODO Auto-generated constructor stub
		for (int i = 0 ; i < files.length ; i ++)
		{
			soundDic.put(files[i].split("_")[1].split("\\.")[0], new Sound(files[i]));
			//System.out.println();
		}
	}
	
	public void stopAll()
	{
		for (String key : soundDic.keySet()) {
		    soundDic.get(key).stop();
		    //System.out.println("Stopping: " + key); 
		}
		//System.out.println("----------");
	}
	
	public void play(String name)
	{
		stopAll();
		soundDic.get(name).play();
	}
	
	
}
