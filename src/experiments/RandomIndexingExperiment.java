package experiments;

import java.util.logging.Logger;

import disambiguate.Evaluator;

import preprocessing.CzechIndexer;
import preprocessing.ExperimentPreprocessing;
import preprocessing.LinguisticPreprocessing;

import util.Arguments;
import util.Log;
import util.Arguments.MATRIX_TYPE;

public class RandomIndexingExperiment {

	
	
	
	
	
	public static void main(String[] args) throws Exception {
		//..........preprocessing		
		Arguments.lowercase="n";
		Arguments.stopWordsRemoval="y";
		Arguments.stemming="n";
		Arguments.mergeLexicalVariants="n";
//......matrix generation
		Arguments.numberOfWordsInDocument=-1;//3,2,1
		Arguments.numberOfSentencesInLuceneDoc = 1;//5,3,1
		if(Arguments.numberOfWordsInDocument>-1)Arguments.numberOfSentencesInLuceneDoc = 1;
//......matrix type - normalizing frequencies
		Arguments.matrixType = MATRIX_TYPE.RI.ordinal();
//......evaluation
		Arguments.upBoarderForNumberOfMeanings = 3;
		Arguments.evaluationContextWindowSize = 3;
		
		Logger logger=Log.getLogger(Arguments.initLogName());
		logger.fine(Arguments.modelNameForLog());
		long start = System.currentTimeMillis();
		
//		PDT1Reader.cleanPDTFile();
		
		ExperimentPreprocessing ep = new ExperimentPreprocessing();
//		ep.divideIntoTrainAndTestSets(Arguments.inputFilePath);
		
		if(Arguments.mergeLexicalVariants.equalsIgnoreCase("y"))LinguisticPreprocessing.mergeCzechTermVariants();
		if(Arguments.stemming.equalsIgnoreCase("y"))LinguisticPreprocessing.stemCzechTerms();
		
		
//......train and test
		logger.fine("\t====train and test====");
				
		Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, null);
		evaluator.extractTestContext("pdt1_0//testDev",Arguments.evaluationContextWindowSize);
		logger.fine(evaluator.testSetStatsForLog());
		evaluator.predict();
		logger.fine("Finished in "+(System.currentTimeMillis()-start)/1000+" seconds.");
		logger.fine(evaluator.evaluationStatsForLog());
		

//......test on unseen data
//		logger.fine("\t====test on unseen data====");
//		ep.mergeFiles("pdt1_0//train", "pdt1_0//testDev", "pdt1_0//train+testDev");
//		ci = new  CzechIndexer("pdt1_0//train+testDev", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
//		ci.index("index");
//		evaluator = new Evaluator(Arguments.upBoarderForNumberOfMeanings);
//		evaluator.extractTestContext("pdt1_0//testFinal",Arguments.evaluationContextWindowSize);
//		logger.fine(evaluator.testSetStatsForLog());
//		evaluator.predict();
//		logger.fine("Finished in "+(System.currentTimeMillis()-start)/1000+" seconds.");
//		logger.fine(evaluator.evaluationStatsForLog());
	}
}
