package util;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

	public static Logger logg = null;
	
	public static Logger getLogger(String logName){
		try {
			if(logg==null){
				logg = Logger.getLogger("global"); 
				logg.setLevel(Level.ALL);
				File dir = new File("logs");
				if(!dir.exists())dir.mkdir();
				FileHandler fh =new FileHandler("logs\\"+logName);
				fh.setFormatter(new SimpleFormatter());
				logg.addHandler(fh);
			}
			return logg;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		
	}
	
}
