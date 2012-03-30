package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.lucene.document.Field;

public class PDT1Reader {

	public  HashMap<String, Integer> unique;
	
	public PDT1Reader() {
		unique = new HashMap<String, Integer>(100);
	}

	public PDT1Reader(HashMap<String, Integer> unique) {
		this.unique = unique;
	}
	
	public void extractFromPdtFile(String pdtFilePath, String outputFilePath) {
		File f1 = new File(pdtFilePath);
		File f2 = new File(outputFilePath);
		InputStreamReader fReader = null;
		OutputStreamWriter fWriter = null;
		try{
			fReader = new InputStreamReader(new FileInputStream(f1.getAbsolutePath()),"Windows-1250");
			boolean append = f2.exists() ? true : false;
			fWriter = new OutputStreamWriter(new FileOutputStream(f2.getAbsolutePath(),append),"Windows-1250");
			ArrayList<String> allTokens = new ArrayList<String>(); 
			StringBuilder wholeText = new StringBuilder();
			StringBuilder sb = new StringBuilder();
	    	char c;
	    	int i;
	    	while((i= fReader.read())!= -1){
	    		c = (char)i;
	    		if(c=='<'){
	    			//token tag
	    			String wholeTag = sb.toString();
	    			if(wholeTag.startsWith("f")){
	    				String lemma_ = wholeTag.substring(wholeTag.indexOf(">")+1);
	    				Integer cnt = unique.get(lemma_);
	    				if(cnt == null) cnt = 0;
	    				unique.put(lemma_, ++cnt);
	    				allTokens.add(lemma_);
	    			}
	    			//possible meaning tag
	    			if(wholeTag.startsWith("l>")){
	    				if(wholeTag.contains("-") ){
	    					String val_ = wholeTag.substring(wholeTag.indexOf(">")+1);
	    					if(wholeTag.contains("_")) val_ = val_.substring(0,wholeTag.indexOf("_")-2);
	    					allTokens.add(val_);
	    				}
	    			}
	    			//include punctuation
	    			if(wholeTag.startsWith("d>")){
	    				String val_= wholeTag.substring(wholeTag.indexOf(">")+1);
	    				allTokens.add(val_);
	    			}
	    			//put newline where find sentence identifier
	    			if(wholeTag.startsWith("s id=")){//csts for larger context
//	    				System.err.println("nova recenica");
	    				allTokens.add("\n");
	    			}
	    			sb=new StringBuilder();
	    		}
	    		else { sb.append(c); }
	    	}
	    	// where there is token and tokenMeaning-No, put only tokenMeaning-No
	    	//so that the model can be trained on meanings
	    	for(int j=1; j<allTokens.size(); j++){
	    		String currentToken = allTokens.get(j);
	    		String previousToken = allTokens.get(j-1);
	    		if(currentToken.contains("-") &&  currentToken.length()>1){
	    			wholeText.append(currentToken+" ");
	    		}
	    		else if(!previousToken.contains("-")){
	    			wholeText.append(previousToken+" ");
	    		}
	    	}
	    	fWriter.append(wholeText);
	    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
	    	try {
				fReader.close();
				fWriter.close();
	    	} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}

	
	
	public int getNumberOfTokenOccurencies(){
		int res = 0;
		Iterator<Entry<String, Integer>> it = unique.entrySet().iterator();
		 while (it.hasNext()) {
		    	Entry<String, Integer> item = it.next();
		    	res+=item.getValue();
		 }
		return res;
	}
		
	public static void cleanPDTFile(){
		String pdt1 = "pdt1_0//tr1.p3m";
		String pdt2 = "pdt1_0//tr2.p3m";
		String outputFilePath = "pdt1_0//pdt1_cleaned";
		PDT1Reader reader = new PDT1Reader();
		
		File outputFile = new File(outputFilePath);
		if(outputFile.exists())outputFile.delete();
		
		//cleans from SGML tags and stores in output file
		//if output file already exists, it appends the content of the next input
		reader.extractFromPdtFile(pdt1, outputFilePath);
		reader.extractFromPdtFile(pdt2, outputFilePath);
	}
	
	
	public static void main(String[] args) throws IOException {
//		PDT1Reader citac1 = new PDT1Reader();
//		citac1.extractFromPdtFile("pdt1_0\\tr1.p3m","pdt1_0\\extrSents");
//		
//		PDT1Reader citac2 = new PDT1Reader(citac1.unique);
//		citac2.extractFromPdtFile("pdt1_0\\tr1.p3m","pdt1_0\\extrSents2");
//		System.out.println("Number of Unique Tokens:"+citac2.unique.size());
//		System.out.println("Number of All Token Occurencies:"+citac2.getNumberOfTokenOccurencies());
		
		PDT1Reader.cleanPDTFile();
		
		
//		String m1= "v-1";
//		String m2= "v-2";
//		String m3 = "jan-2";
//		String m4 = "jan-4";
//		
//		citac.storeMeaningInHash(m1);
//		citac.storeMeaningInHash(m2);
//		citac.storeMeaningInHash(m3);
//		citac.storeMeaningInHash(m4);
		
//		citac1.pruneMeaningEntrys();
//		citac.printAllMeanings();
		
	}

}
