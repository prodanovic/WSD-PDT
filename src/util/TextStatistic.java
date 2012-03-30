package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class TextStatistic {


	public HashMap<String, ArrayList<String>> meanings ;
	public String[] trainList;
	public ArrayList<String> testSet;
	public int numberOfMeaningOccurencies = 0;
	public int numberOfMeanings = 0;
	
	public TextStatistic(ArrayList<String> allTokensList ){
		meanings = new HashMap<String, ArrayList<String>>();
		testSet = new ArrayList<String>();
	}
	public TextStatistic(){
		meanings = new HashMap<String, ArrayList<String>>();
		testSet = new ArrayList<String>();
	}
	
	public void extractMeaningsFromTrainingSet(String filePath) throws FileNotFoundException{
		File file = new File(filePath);
		try {
			Scanner scanner = new Scanner(file, "Windows-1250");
			while(scanner.hasNextLine()){
				String sentence = scanner.nextLine();
				String[] tokens = sentence.split(" ");
				for (String token : tokens) {
					if(token.contains("-")){
						numberOfMeaningOccurencies++;
						storeMeaningInHash(token);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void extractMeaningsFromIndex (HashSet<String> indexTerms ) throws FileNotFoundException{
		for (String token : indexTerms) {
			if(token.contains("-")){
				storeMeaningInHash(token);
			}
		}
	}
	
	private boolean  isToken(String token){
		boolean isToken=true;
		String[] regexs = {"","\\s+","[^\\w]+\\w+","\\w+[^\\w]+","[^\\w]+","_\\w+"};
		for(String regex:regexs){
			if(Pattern.matches(regex,token))
				{isToken=false;}
		}
		return isToken;
	}
	
	//mark the test set, and put into separate files
	public void divideIntoTrainAndTestSet(String filePath, int indexOfTestSet, String pathTrain, String pathTest) throws FileNotFoundException, IOException{
		File file = new File(filePath);
		Scanner scanner = new Scanner(file, "Windows-1250");
		ArrayList<String> allSentencesList = new ArrayList<String>();
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
//			line = deleteNonWords(line);
			line = line.toLowerCase();
			if(!line.isEmpty())allSentencesList.add(line+"\n");
		}
		int setSize = allSentencesList.size()/10;
		int startTest = indexOfTestSet*setSize;
		int endTest = startTest+setSize;
		String[] testList = new String[setSize-1];
		trainList = new String[allSentencesList.size()-setSize-1];
		int ind1=0;
		for(int i=0; i<startTest ;i++){
			trainList[ind1++]=allSentencesList.get(i);
		}
		int ind=0;
		for(int i=startTest+1; i<endTest; i++){
			testList[ind++]=allSentencesList.get(i);
			String sentence = allSentencesList.get(i);
			String[] tokens = sentence.split(" ");
			for (String string : tokens) {
				testSet.add(string);	
			}
		}
		for(int i=endTest+1; i<allSentencesList.size() ;i++){ 
			trainList[ind1++]=allSentencesList.get(i);
		}
		File trainFile = new File(pathTrain);
		OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream(trainFile.getAbsolutePath()),"Windows-1250");
		for(String token:trainList) fWriter.append(token);
		fWriter.close();
		
		File testFile = new File(pathTest);
		fWriter = new OutputStreamWriter(new FileOutputStream(testFile.getAbsolutePath()),"Windows-1250");
		for(String token:testList) fWriter.append(token);
		fWriter.close();
		
//		System.out.println("All tokens size: "+allSentencesList.size());
//		System.out.println("Training set size: "+trainList.length);
//		System.out.println("Training indice :[0.."+startTest+", "+(endTest+1)+".."+allSentencesList.size()+"]");
//		for(String s:trainList)System.out.println(s);
//		System.out.println("Test indice :["+(startTest+1)+".."+endTest+"]");
//		System.out.println("Test set size:"+testList.length);
//		for(String s:testList)System.out.println(s);
	}
	
	public void storeMeaningInHash(String meaning){
		String lemma = meaning.substring(0,meaning.indexOf("-"));
		ArrayList<String> lemma_meanings = meanings.get(lemma);
		if(lemma_meanings == null){
			numberOfMeanings++;
			lemma_meanings = new ArrayList<String>();
			lemma_meanings.add(meaning);
			meanings.put(lemma, lemma_meanings);
//			System.out.println("AList dont exist: lemma["+lemma+"] ["+meaning+"]");
		}
		else{
			if(!lemma_meanings.contains(meaning)){
				numberOfMeanings++;
				lemma_meanings.add(meaning);
				meanings.put(lemma, lemma_meanings);
			}
		}
	}
	
	public void printAllMeaningsToFile(String path){
		Iterator<Entry<String, ArrayList<String>>> it = meanings.entrySet().iterator();
		File trainFile = new File(path);
		OutputStreamWriter fWriter;
		try {
			fWriter = new OutputStreamWriter(new FileOutputStream(trainFile.getAbsolutePath()),"Windows-1250");
			while (it.hasNext()) {
		    	Entry<String, ArrayList<String>> pairs = it.next();
		    	ArrayList<String> list =  pairs.getValue();
		    	StringBuilder sb = new StringBuilder();
		    	for(String s:list)sb.append(s+", ");
		    	fWriter.append(pairs.getKey() + " = " +sb.toString()+"\n");
		    }
			fWriter.append("Total: "+meanings.size());
			fWriter.close();
		} catch (Exception e) {e.printStackTrace();
		}
	}

	//now they are not deleted from meanings, they are just disregarded durign evaluation
	@Deprecated
	public void pruneMeaningThatOccurLessThan(int count){
		//delete All One Meaning Entrys
		Iterator<Entry<String, ArrayList<String>>> it = meanings.entrySet().iterator();
		ArrayList<String> deleteKeys = new ArrayList<String>();
		while (it.hasNext()) {
	    	Entry<String, ArrayList<String>> pairs = it.next();
		    if(pairs.getValue().size()< count) {
//		    	pairs.setValue(new ArrayList<String>());
		    	deleteKeys.add(pairs.getKey());
		    }
	    }
		
	    for(String dk:deleteKeys)meanings.remove(dk);
	    
		meanings.remove("");
	}
	
	public void printStatistics(){
		System.out.println("Number of unique meanings: "+meanings.size());
		System.out.println("Number of multiple-meaning occurency: "+numberOfMeaningOccurencies);
		System.out.println("Number of multiple-meanings: "+numberOfMeanings);
	}
	
	public static void testDivideIntoTrainAndTestSet1(){
		TextStatistic ts = new TextStatistic();
		ArrayList<String> allTokensFake = new ArrayList<String>();
		int i=0;
		while(i++<20)allTokensFake.add(new String(i+".token"));
		try {
			ts.divideIntoTrainAndTestSet("pdt1_0\\pdt1_cleaned",9, "pdt1_0\\training", "pdt1_0\\test");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testDivideIntoTrainAndTestSet2(){
		TextStatistic ts = new TextStatistic();
		try {
			ts.divideIntoTrainAndTestSet("pdt1_0\\pdt1_cleaned",1, "pdt1_0\\training", "pdt1_0\\test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) throws Exception {
		long start =  System.currentTimeMillis();
//		testDivideIntoTrainAndTestSet1();
		
		TextStatistic ts = new TextStatistic();
//		String[] testTokens = {"",","," . ","("," ","+-rec","rec-1","'rec","rec?","_rec__","rec-3))"};
//		for(String token: testTokens){
//			System.out.println("["+token+"]:"+ts.isToken(token));
//		}
	
		String regex = "\\s[^\\w+]\\s";
		String regex2 = "\\u0020\\u002E?[\\u0030-\\u0039]+\\u0020"; //\s\.?\d+\s in Windows-1250 encoding
		String regex3 = "\\u0020?[\\u0030-\\u0039]*\\u002E[\\u0030-\\u0039]*\\u0020"; //\s?\d*\.\d*\s
		File fileIn = new File("pdt1_0\\test");
		File fileOut = new File("pdt1_0\\testCleaned");
		
		OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream(fileOut.getAbsolutePath()),"Windows-1250");
		Scanner scanner = new Scanner(fileIn, "Windows-1250");
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			line = line.replaceAll(regex, " ");
			line = line.replaceAll(regex, " ");
			line = line.replaceAll(regex2, " ");
			line = line.replaceAll(regex2, " ");
			line = line.replaceAll(regex3, " ");
//			line = line.replaceFirst(" ", "");
			if(!line.isEmpty())fWriter.append(line+"\n");
		}
		fWriter.close();
		
		System.out.println("Total time for computing:"+(System.currentTimeMillis() - start)+" ms.");
		
		

	}

}
