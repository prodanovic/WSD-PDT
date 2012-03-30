
package disambiguate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import util.Arguments;
import util.FileUtil;
import util.Log;
import preprocessing.CzechStemmerLight;
import preprocessing.OldIndexer;
import preprocessing.PDT1Reader;
import util.TextStatistic;

public class RandomIndexingExperiment {

	
	
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

	public static void mergeAllTermVariants() throws IOException{
		String wholePDT1 = FileUtil.extractTextFromFile(new File("pdt1_0//pdt1_cleaned"), "Windows-1250");
		wholePDT1=wholePDT1.replaceAll("á", "a");		
		wholePDT1=wholePDT1.replaceAll("é", "e");
		wholePDT1=wholePDT1.replaceAll("í", "i");
		wholePDT1=wholePDT1.replaceAll("ý", "y");
		wholePDT1=wholePDT1.replaceAll("ú", "u");
		wholePDT1=wholePDT1.replaceAll("ù", "u");
		wholePDT1=wholePDT1.replaceAll("ò", "o");
		wholePDT1=wholePDT1.replaceAll("ó", "o");
		
		FileUtil.writeTextToFile(wholePDT1, "pdt1_0//pdt1_cleaned", "Windows-1250", false);
	}
	
	public static void stemAll() throws IOException{
		CzechStemmerLight czechStemmerLight = new CzechStemmerLight();
		String wholePDT1 = FileUtil.extractTextFromFile(new File("pdt1_0//pdt1_cleaned"), "Windows-1250");
		String []sentences = wholePDT1.split("\n");
		String[]tokens;
		StringBuilder ssb = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		for(String sentence:sentences){
			ssb = new StringBuilder();
			tokens = sentence.split(" ");
			for(String token:tokens){
				token = czechStemmerLight.stem(token);
				sb.append(token+" ");
			}
			sb.append(ssb.toString().trim()+"\n");
		}
		FileUtil.writeTextToFile(sb.toString(), "pdt1_0//pdt1_cleaned", "Windows-1250", false);
	}
	
	public static void main(String[] args) throws Exception {
		String[]argss={"-logName log_1_6_3"};
		Arguments.parseArguments(argss);
		
		Logger logger=Log.getLogger(Arguments.logName);
		long start =  System.currentTimeMillis();
		
		cleanPDTFile();
		
//		mergeAllTermVariants();
//		System.out.println("mergeAllTermVariants() completed  in: "+(System.currentTimeMillis()-start)/1000);
		
//		stemAll();
//		System.out.println("stemAll() completed  in: "+(System.currentTimeMillis()-start)/1000);
	
		TextStatistic ts = new TextStatistic();
		
		
//......prepared for 10 fold cross-evaluation
		ts.divideIntoTrainAndTestSet("pdt1_0\\pdt1_cleaned",0,"pdt1_0\\training","pdt1_0\\test");
		
//......make lucene index and semantic vectors from them; training file, and No. sentences per lucene Doc
		SVBuilder.indexAndMakeVectorStore("pdt1_0\\training",Arguments.numberOfSentencesInLuceneDoc);
		
//......needed to check if the term is in index; used in Evaluator(..) and to extract meanings
		OldIndexer.getTermsFromLuceneIndex();
		ts.extractMeaningsFromIndex(OldIndexer.getIndexTerms());

		ts.pruneMeaningThatOccurLessThan(2);
		ts.printAllMeaningsToFile("pdt1_0\\meanings");
		ts.printStatistics();
		OldIndexer.getDocumentsFromLuceneIndex();
				
		Evaluator evaluator = new Evaluator( Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, null);
		evaluator.extractTestContext("pdt1_0\\test",Arguments.evaluationContextWindowSize);
//......prediction
		List<String> predictedMeanings = evaluator.predict();

//......evaluation - done in the predict method now
//		evaluator.evaluate(predictedMeanings);
		
		System.out.println();
		System.out.println("===========================================");
		System.out.println("Precision:"+ evaluator.getPrecision());
		System.out.println("Total time for computing:"+(System.currentTimeMillis() - start)/1000 +" sec.");
		logger.severe("Total time for computing:"+(System.currentTimeMillis() - start)/1000 +" sec.");
		logger.severe("Precision:"+ evaluator.getPrecision());
		System.out.println("Number of similarities calculated:"+evaluator.similaritiesCalculated);
		float num = (System.currentTimeMillis() - start)/evaluator.similaritiesCalculated;
		System.out.println("miliseconds per similarity:"+num);
	}

}
