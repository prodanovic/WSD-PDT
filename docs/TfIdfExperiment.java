package experiments;

import java.util.logging.Logger;

import disambiguate.Evaluator;

import preprocessing.CzechIndexer;
import preprocessing.ExperimentPreprocessing;
import preprocessing.LinguisticPreprocessing;

import util.Arguments;
import util.FileUtil;
import util.Log;
import util.Arguments.MATRIX_TYPE;

public class TfIdfExperiment {



	public static void main(String[] args) throws Exception {
//......preprocessing		
		Arguments.lowercase="y";
		Arguments.stopWordsRemoval="n";
		Arguments.stemming="n";
		Arguments.mergeLexicalVariants="n";
//......matrix generation
		Arguments.numberOfWordsInDocument=-1;//3,2,1
		Arguments.numberOfSentencesInLuceneDoc = 1;//5,3,1
		if(Arguments.numberOfWordsInDocument>-1)Arguments.numberOfSentencesInLuceneDoc = 1;
//......matrix type - normalizing frequencies
		Arguments.matrixType = MATRIX_TYPE.TFIDF.ordinal();
//......evaluation
		Arguments.upBoarderForNumberOfMeanings = 5;
		Arguments.evaluationContextWindowSize = 3;
		
		Logger logger=Log.getLogger(Arguments.initLogName());
		
		logger.fine(Arguments.modelNameForLog());
		
		
//		PDT1Reader.cleanPDTFile();
//		ExperimentPreprocessing.divideIntoTrainAndTestSets("pdt1_0/pdt1_cleaned");
		
		FileUtil.copyFile("pdt1_0/train", "pdt1_0/train_");
		FileUtil.copyFile("pdt1_0/testDev", "pdt1_0/testDev_");
		FileUtil.copyFile("pdt1_0/testFinal", "pdt1_0/testFinal_");

		LinguisticPreprocessing.removeNonWords();
		if(Arguments.stopWordsRemoval.equalsIgnoreCase("y"))LinguisticPreprocessing.stopWordRemoval();
		if(Arguments.lowercase.equalsIgnoreCase("y"))LinguisticPreprocessing.lowercase();
		if(Arguments.mergeLexicalVariants.equalsIgnoreCase("y"))LinguisticPreprocessing.mergeCzechTermVariants();
		if(Arguments.stemming.equalsIgnoreCase("y"))LinguisticPreprocessing.stemCzechTerms();
		
//......train and test
//		testDocumentSizeSentenceLevel(logger);
		testDocumentSizeWordLevel(logger);
		
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
	
	public static void testDocumentSizeWordLevel(Logger logger) throws Exception{
		long start = System.currentTimeMillis();
		for(int i=1; i<4; ){
			start = System.currentTimeMillis();
			Arguments.numberOfWordsInDocument= i;
			logger.fine(Arguments.preprocessingParamsForLog());
			Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
					Arguments.numberOfSentencesInLuceneDoc,
					Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
			evaluator.extractTestContext("pdt1_0/testDev_",Arguments.evaluationContextWindowSize);
			logger.fine(evaluator.testSetStatsForLog());
			
			evaluator.predict();
			
			logger.fine(Arguments.evaluationParamsForLog());
			ResultFormatter.precicisions.add(evaluator.getPrecision());
			ResultFormatter.recalls.add(evaluator.getRecall());
			ResultFormatter.Fmeasures.add(evaluator.getFMeasure(0.5f));
			ResultFormatter.coverages.add(evaluator.getCoverage());
			ResultFormatter.randomAcc=evaluator.getRandomPrecision();
			
			long end = (System.currentTimeMillis()-start)/1000;
			logger.fine("Finished in "+end+" seconds.\n");
			System.err.println(Arguments.initLogName()+" ["+end+"s]");
			logger.fine(evaluator.evaluationStatsForLog());
			i+=1;
		}
//		logger.fine(ResultFormatter.getTableForPreprocessingFC(Arguments.preprocessingName()));
		logger.fine(ResultFormatter.getTableForPreprocessingPR(Arguments.preprocessingName()));
	}
	
	public static void testDocumentSizeSentenceLevel(Logger logger) throws Exception{
		for(int i=1; i<6; ){
			long start = System.currentTimeMillis();
			Arguments.numberOfSentencesInLuceneDoc= i;
			logger.fine(Arguments.preprocessingParamsForLog());
			Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
					Arguments.numberOfSentencesInLuceneDoc,
					Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
			evaluator.extractTestContext("pdt1_0/testDev_",Arguments.evaluationContextWindowSize);
			logger.fine(evaluator.testSetStatsForLog());
			
			evaluator.predict();
			
			logger.fine(Arguments.evaluationParamsForLog());
			ResultFormatter.precicisions.add(evaluator.getPrecision());
			ResultFormatter.recalls.add(evaluator.getRecall());
			ResultFormatter.Fmeasures.add(evaluator.getFMeasure(0.5f));
			ResultFormatter.coverages.add(evaluator.getCoverage());
			ResultFormatter.randomAcc=evaluator.getRandomPrecision();
			
			long end = (System.currentTimeMillis()-start)/1000;
			logger.fine("Finished in "+end+" seconds.\n");
			System.err.println(Arguments.initLogName()+" ["+end+"s]");
			logger.fine(evaluator.evaluationStatsForLog());
			i+=2;
		}
//		logger.fine(ResultFormatter.getTableForPreprocessingFC(Arguments.preprocessingName()));
		logger.fine(ResultFormatter.getTableForPreprocessingPR(Arguments.preprocessingName()));

	}

}