package experiments;

import java.io.IOException;
import java.util.logging.Logger;

import preprocessing.CzechIndexer;
import preprocessing.ExperimentPreprocessing;
import preprocessing.LinguisticPreprocessing;
import util.Arguments;
import util.Log;
import util.Arguments.MATRIX_TYPE;
import disambiguate.Evaluator;

public class Experiment {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//..........preprocessing		
//		Arguments.lowercase="n";
//		Arguments.stopWordsRemoval="n";
//		Arguments.stemming="n";
//		Arguments.mergeLexicalVariants="n";
////......matrix generation
//		Arguments.numberOfWordsInDocument=-1;//3,2,1
//		Arguments.numberOfSentencesInLuceneDoc = 1;//5,3,1
//		if(Arguments.numberOfWordsInDocument>-1)Arguments.numberOfSentencesInLuceneDoc = 1;
////......matrix type - normalizing frequencies
//		Arguments.matrixType = MATRIX_TYPE.TFIDF.ordinal();
////......evaluation
//		Arguments.upBoarderForNumberOfMeanings = 2;
//		Arguments.evaluationContextWindowSize = 3;
		
		Arguments.parseArguments(args);
		
		boolean isMerge=Arguments.mergeLexicalVariants.equalsIgnoreCase("y");
		boolean isStem=Arguments.mergeLexicalVariants.equalsIgnoreCase("y");
		
		Logger logger=Log.getLogger(Arguments.initLogName());
		logger.fine(Arguments.getLogHeader());
		long start = System.currentTimeMillis();
		
//		PDT1Reader.cleanPDTFile();
		
		ExperimentPreprocessing ep = new ExperimentPreprocessing();
		ep.divideIntoTrainAndTestSets(Arguments.inputFilePath);
		
		if(isMerge){
			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//train","pdt1_0//train");
			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testDev","pdt1_0//testDev");
			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testFinal","pdt1_0//testFinal");
		}
		if(isStem){
			LinguisticPreprocessing.stemCzechTerms("pdt1_0//train_","pdt1_0//train");
			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testDev_","pdt1_0//testDev");
			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testFinal_","pdt1_0//testFinal");
		}
		
		
//......train and test
		logger.fine("\t====train and test====");
		CzechIndexer ci = new  CzechIndexer("pdt1_0//train", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index("index");
		
		Evaluator evaluator = new Evaluator(Arguments.upBoarderForNumberOfMeanings);
		evaluator.extractTestContext("pdt1_0//testDev",Arguments.evaluationContextWindowSize);
		logger.fine(evaluator.testSetStatsForLog());
		evaluator.predict();
		logger.fine("Finished in "+(System.currentTimeMillis()-start)/1000+" seconds.");
		logger.fine(evaluator.evaluationStatsForLog());
		

//......test on unseen data
		logger.fine("\t====test on unseen data====");
		
		ep.mergeFiles("pdt1_0//train", "pdt1_0//testDev", "pdt1_0//train+testDev");
		ci = new  CzechIndexer("pdt1_0//train+testDev", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index("index");
		evaluator = new Evaluator(Arguments.upBoarderForNumberOfMeanings);
		evaluator.extractTestContext("pdt1_0//testFinal",Arguments.evaluationContextWindowSize);
		logger.fine(evaluator.testSetStatsForLog());
		evaluator.predict();
		logger.fine("Finished in "+(System.currentTimeMillis()-start)/1000+" seconds.");
		logger.fine(evaluator.evaluationStatsForLog());
		
	}

	

}
