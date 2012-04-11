package experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.apache.lucene.index.CorruptIndexException;

import preprocessing.CzechIndexer;
import preprocessing.ExperimentPreprocessing;
import preprocessing.LinguisticPreprocessing;
import preprocessing.PDT1Reader;
import util.Arguments;
import util.FileUtil;
import util.Log;
import util.Arguments.MATRIX_TYPE;
import disambiguate.Evaluator;

public class Experiment {

	static Logger logger;
	
	public static void main(String[] args) {
		System.out.println("Welcome to the WSD system!");
		while(true){
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please insert system parameters (leave blank if you want to use default arguments):");
			try {
				String inputArguments= reader.readLine();
				if(!inputArguments.equals(""))
					Arguments.parseArguments(inputArguments.split(" "));
				
				logger=Log.getLogger(Arguments.initLogName());
				
				logger.fine(Arguments.modelNameForLog());
				System.out.println("Please give the location of the file[in SGML format] you wish to train the system on (leave blank if you want to skip training):");
				String inputFilePath = reader.readLine();
				if(!inputFilePath.equals("")){
					logger.fine(Arguments.preprocessingParamsForLog());
					extractTheCorpusAndTrain(inputFilePath);
					preprocess();
				}
				
				System.out.println("Please give the location of the test file (ambiguous words should be marked in format \"word-number\"):");
				String testFilePath = reader.readLine();
				if(!testFilePath.equals("")){
					evaluate(testFilePath);
				}

			} catch (NumberFormatException e) {
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (IOException e) {
			} catch (Exception e) {
				System.out.println("Evaluation exception:");
				e.printStackTrace();
			}	
		}
	}

	public static void extractTheCorpusAndTrain(String inputPath) throws FileNotFoundException, IOException{
		long start = System.currentTimeMillis();
		PDT1Reader.cleanInputFile(inputPath);
		long time = (System.currentTimeMillis()-start)/1000;
		System.out.println("Extracted the corpus from input file in "+time+" seconds.");
	}
	
	public static void preprocess() throws FileNotFoundException, IOException{
		long start = System.currentTimeMillis();
//		ExperimentPreprocessing.divideIntoTrainAndTestSets("temp/sgml_cleaned");
		LinguisticPreprocessing.removeNonWords("temp/sgml_cleaned","temp/sgml_cleaned_");
		if(Arguments.stopWordsRemoval.equalsIgnoreCase("y"))LinguisticPreprocessing.stopWordRemoval("temp/sgml_cleaned_","temp/sgml_cleaned_");
		if(Arguments.lowercase.equalsIgnoreCase("y"))LinguisticPreprocessing.lowercase("temp/sgml_cleaned_","temp/sgml_cleaned_");
		if(Arguments.mergeLexicalVariants.equalsIgnoreCase("y"))LinguisticPreprocessing.margeAllCzechVariants("temp/sgml_cleaned_","temp/sgml_cleaned_");
		if(Arguments.stemming.equalsIgnoreCase("y"))LinguisticPreprocessing.stemCzechTerms("temp/sgml_cleaned_","temp/sgml_cleaned_");
		long time = (System.currentTimeMillis()-start)/1000;
		System.out.println("Preprocessing done in "+time+" seconds.");
	}
	
	public static void evaluate(String testFilePath) throws Exception{
		long start = System.currentTimeMillis();
		Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, "temp/sgml_cleaned");
		evaluator.extractTestContext(testFilePath,Arguments.evaluationContextWindowSize);
		logger.fine(evaluator.testSetStatsForLog());
		
		evaluator.predict();
		logger.fine(evaluator.evaluationStatsForLog());
		long time = (System.currentTimeMillis()-start)/1000;
		System.out.println("Evaluation done in "+time+" seconds.");
		System.out.println("F-measure on the test set is "+evaluator.getFMeasure(0.5f)+"%. " +
				"More detailed results are to be found in log "+Arguments.initLogName()+"\n\n\n");
	}
	
	

}
