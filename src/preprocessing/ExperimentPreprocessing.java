package preprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import util.Arguments;
import util.FileUtil;

public class ExperimentPreprocessing {

	

	//mark the test set, and put into separate files
	public static void divideIntoTrainAndTestSets(String inputfilePath) throws FileNotFoundException, IOException{
		File file = new File(inputfilePath);
		String trainPath = "pdt1_0//train";
		String testDevPath = "pdt1_0//testDev";
		String testFinalPath = "pdt1_0//testFinal";
		
		 
//......loads and randomizes input sentences.  then it splits them into 3 sets
		Scanner scanner = new Scanner(file, "Windows-1250");
		ArrayList<String> allSentencesList = new ArrayList<String>();
		while(scanner.hasNextLine()){
			String line = scanner.nextLine().trim();
			if(!line.isEmpty()){
				allSentencesList.add(line+"\n");
			}
		}
		
		Collections.shuffle(allSentencesList);
		
		int setSize = allSentencesList.size()/100;
		int startTestDev = 95*setSize;
		int startTestFinal = 96*setSize;
		
		
		File trainFile = new File(trainPath);
		OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream(trainFile.getAbsolutePath()),"Windows-1250");
		for(int i=0 ; i<startTestDev ; i++){
			String sentence = allSentencesList.get(i);
			fWriter.append(sentence);
		}
		fWriter.close();
		
		File testDevFile = new File(testDevPath);
		fWriter = new OutputStreamWriter(new FileOutputStream(testDevFile.getAbsolutePath()),"Windows-1250");
		for(int i=startTestDev ; i<startTestFinal ; i++){
			fWriter.append(allSentencesList.get(i));
		}
		fWriter.close();
		
		File testFinalFile = new File(testFinalPath);
		fWriter = new OutputStreamWriter(new FileOutputStream(testFinalFile.getAbsolutePath()),"Windows-1250");
		for(int i=startTestFinal ; i<allSentencesList.size() ; i++){
			fWriter.append(allSentencesList.get(i));
		}
		fWriter.close();
	}
	
	public void mergeFiles(String inPath1,String inPath2,String outPath) throws IOException{
		StringBuilder stringBuilder = new StringBuilder(); 
		stringBuilder.append(FileUtil.extractTextFromFile(inPath1, "Windows-1250"));
		stringBuilder.append(FileUtil.extractTextFromFile(inPath2, "Windows-1250"));
		FileUtil.writeTextToFile(stringBuilder.toString(), outPath, "Windows-1250", false);
	}
	
	public static void main(String[] args) throws Exception, Exception {
		ExperimentPreprocessing ep = new ExperimentPreprocessing();
		Arguments.lowercase="Y";
		Arguments.stopWordsRemoval="N";
		Arguments.numberOfSentencesInLuceneDoc=1;
		Arguments.numberOfWordsInDocument=-1;
		
//		CzechIndexer ci = new  
//			CzechIndexer("pdt1_0//pdt1_cleaned", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
//		ci.index("index");
		
//		ep.divideIntoTrainAndTestSets("pdt1_0/pdt1_preprocessed");
	}
}
