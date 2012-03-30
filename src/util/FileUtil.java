package util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * The Class FileUtil.
 */
public class FileUtil {

	
	
	public static String extractTextFromFile(String filePath, String encoding) throws IOException {
		if(encoding==null || encoding.equals(""))encoding="UTF-8";
		File file = new File(filePath);
		InputStreamReader fReader = new InputStreamReader(new FileInputStream(file.getAbsolutePath()),encoding);
		StringBuilder sb = new StringBuilder();
    	char c;
    	int i;
		while((i= fReader.read())!= -1){
    		c = (char)i;
    		sb.append(c);
		}
		fReader.close();
		return sb.toString();
	}
	
	/**
	 * Extract text from file.
	 *
	 * @param file the file
	 * @param encoding the encoding
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String extractTextFromFile(File file, String encoding) throws IOException {
		if(encoding==null || encoding.equals(""))encoding="utf-8";
		InputStreamReader fReader = new InputStreamReader(new FileInputStream(file.getAbsolutePath()),encoding);
		StringBuilder sb = new StringBuilder();
    	char c;
    	int i;
		while((i= fReader.read())!= -1){
    		c = (char)i;
    		sb.append(c);
		}
		fReader.close();
		return sb.toString();
	}
	
	/**
	 * Write text to file.
	 *
	 * @param text the text
	 * @param filePath the file path
	 * @param encoding the encoding
	 * @param appendText the append text
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static File writeTextToFile(String text, String filePath, String encoding, boolean appendText) throws IOException{
		File file = new File(filePath);
		if(encoding==null || encoding.equals(""))encoding="utf-8";
		OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream
				(file.getAbsolutePath(),appendText),encoding);
		fWriter.append(text);
		fWriter.close();
		return file;
	}
	
	/**
	 * Write text to file.
	 *
	 * @param text the text
	 * @param file the file
	 * @param encoding the encoding
	 * @param appendText the append text
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static File writeTextToFile(String text, File file, String encoding, boolean appendText) throws IOException{
		if(encoding==null || encoding.equals(""))encoding="UTF-8";
		OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream
				(file.getAbsolutePath(),appendText),encoding);
		fWriter.append(text);
		fWriter.close();
		return file;
	}
	
	
	
	/**
	 * Convert win1252to utf8.
	 *
	 * @param text the text
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String convertWin1252toUtf8(String text) throws IOException{

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("temp"),"Windows-1252");
        BufferedWriter writer = new BufferedWriter(out);
        writer.write(text);
		writer.close();
		BufferedReader reader = new BufferedReader(	new InputStreamReader(new FileInputStream("temp"), "UTF-8"));
		String line = "";
		String utf8 = "";
		while ((line = reader.readLine()) != null) {
			utf8 = utf8.concat(new String(line.getBytes(), "UTF-8"));
		}
		reader.close();
		return utf8;
	}
	
	public static void copyFile(String srFile, String dtFile){
		  try{
		  File f1 = new File(srFile);
		  File f2 = new File(dtFile);
		  InputStream in = new FileInputStream(f1);
		  OutputStream out = new FileOutputStream(f2);

		  byte[] buf = new byte[1024];
		  int len;
		  while ((len = in.read(buf)) > 0){
			  out.write(buf, 0, len);
		  }
		  in.close();
		  out.close();
		  }catch (Exception e) {
			  e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		copyFile("pdt1_0/pdt1_cleaned", "pdt1_0/pdt1_preprocessed");
	}
}
