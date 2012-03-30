package experiments;

import java.io.IOException;
import java.util.logging.Logger;

import preprocessing.CzechIndexer;
import preprocessing.ExperimentPreprocessing;
import util.Arguments;
import util.Log;
import util.Arguments.MATRIX_TYPE;
import disambiguate.Evaluator;

public class Preprocessing {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, Exception {
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
		
//		PDT1Reader.cleanPDTFile();
		
		ExperimentPreprocessing ep = new ExperimentPreprocessing();
		ep.divideIntoTrainAndTestSets(Arguments.inputFilePath);
		
//		if(Arguments.mergeLexicalVariants.equalsIgnoreCase("y")){
//			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//train","pdt1_0//train_");
//			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testDev","pdt1_0//testDev_");
//			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testFinal","pdt1_0//testFinal_");
//		}
//		if(Arguments.stemming.equalsIgnoreCase("y")){
//			LinguisticPreprocessing.stemCzechTerms("pdt1_0//train_","pdt1_0//train_");
//			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testDev_","pdt1_0//testDev_");
//			LinguisticPreprocessing.mergeCzechTermVariants("pdt1_0//testFinal_","pdt1_0//testFinal_");
//		}
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		Arguments.numberOfSentencesInLuceneDoc = 1;
		for(int i=1 ; i<4 ; i++){
			Arguments.numberOfWordsInDocument=i;
			Logger logger=Log.getLogger(Arguments.initLogName());
//			logger.fine(Arguments.getLogHeader());
			long start = System.currentTimeMillis();
	//......train and test
			logger.fine("\t====train and test====");
			CzechIndexer ci = new  CzechIndexer("pdt1_0//train", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
			ci.index("index");
			logger.fine(Arguments.modelNameForLog());
			Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
					Arguments.numberOfSentencesInLuceneDoc,
					Arguments.upBoarderForNumberOfMeanings);
			evaluator.extractTestContext("pdt1_0//testDev",Arguments.evaluationContextWindowSize);
			logger.fine(evaluator.testSetStatsForLog());
			evaluator.predict();
			logger.fine("Finished in "+(System.currentTimeMillis()-start)/1000+" seconds.");
			logger.fine(evaluator.evaluationStatsForLog());
			sb.append(evaluator.getPrecision()+" &");
			sb2.append(evaluator.getRecall()+" &");
			sb3.append(evaluator.getRandomPrecision()+" &");
		}
		sb.append(" || ");
		sb2.append(" || ");
		sb3.append(" || ");
		Arguments.numberOfWordsInDocument=-1;
		int i=1;
		while(i<6){
			Arguments.numberOfSentencesInLuceneDoc=i;
			i+=2;
			Logger logger=Log.getLogger(Arguments.initLogName());
//			logger.fine(Arguments.getLogHeader());
			long start = System.currentTimeMillis();
	//......train and test
			logger.fine("\t====train and test====");
			CzechIndexer ci = new  CzechIndexer("pdt1_0//train", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
			ci.index("index");
			logger.fine(Arguments.modelNameForLog());
			Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
					Arguments.numberOfSentencesInLuceneDoc,
					Arguments.upBoarderForNumberOfMeanings);
			evaluator.extractTestContext("pdt1_0//testDev",Arguments.evaluationContextWindowSize);
			logger.fine(evaluator.testSetStatsForLog());
			evaluator.predict();
			logger.fine("Finished in "+(System.currentTimeMillis()-start)/1000+" seconds.");
			logger.fine(evaluator.evaluationStatsForLog());
			sb.append(evaluator.getPrecision()+" &");
			sb2.append(evaluator.getRecall()+" &");
			sb3.append(evaluator.getRandomPrecision()+" &");
		}
		
		
		System.out.println(sb.toString());
		System.out.println(sb2.toString());
		System.out.println(sb3.toString());
		

		
	}

	

}
