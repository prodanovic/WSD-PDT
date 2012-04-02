package evaluate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.index.CorruptIndexException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import preprocessing.CzechIndexer;

import util.Arguments;
import util.Context;
import util.EvaluationEntry;

import disambiguate.Evaluator;

public class TestEvaluator {

	String inputPath = "pdt_cleaned_test_sample";
	HashMap<String, ArrayList<String>> meanings ;
	Evaluator evaluator;


	
//	@Test
	public void testExtractTestContext() throws IllegalArgumentException, IOException{
		evaluator = new Evaluator(-1,1,2, "pdt1_0/train_");
		String sentence = "pri-1 tedy nebude pri-1 vyrobe l-3 l-3 a-1 l-3 vazano na-1 jedineho dodavatele spickove avioniky-9";
		System.err.println(sentence);
		evaluator.extractSentenceContext(sentence, 1);
//		evaluator.predict();
		
		Set<Entry<String, EvaluationEntry>> set = evaluator.evaluationEntries.entrySet();
		Iterator<Entry<String, EvaluationEntry>> it =set.iterator();
		while(it.hasNext()){
			Entry<String, EvaluationEntry> ee = it.next();
			String  meaning = ee.getKey();
			ArrayList<Context> contexts = ee.getValue().getContext();
			System.out.println("meaning:"+meaning);
			for(Context context:contexts){
				ArrayList<String> left = context.previousContext;
				ArrayList<String> right = context.nextContext;
				for(String s:left)System.out.print("l:"+s+" ");
				System.out.println();	
				for(String s:right)System.out.print("d:"+s+" ");
				System.out.println();
				System.out.println();
			}
		}
	}
	
//	@Test
	public void testMeaningCountHasNotChangedBasedOnInputDocSizeParagraph() throws Exception, Exception{
		Arguments.numberOfSentencesInLuceneDoc=1;
		Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
		int numberOfTrainPolysemes1 = 0;
		Iterator<String> ks = evaluator.meanings.keySet().iterator();
		while(ks.hasNext()){
			numberOfTrainPolysemes1+=evaluator.meanings.get(ks.next()).size();
		}
		Arguments.numberOfSentencesInLuceneDoc=5;
		int numberOfTrainPolysemes5 = 0;
		evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, null);
		Iterator<String> ks5 = evaluator.meanings.keySet().iterator();
		while(ks5.hasNext()){
			numberOfTrainPolysemes5+=evaluator.meanings.get(ks5.next()).size();
		}
		Assert.assertEquals(numberOfTrainPolysemes1, numberOfTrainPolysemes5);
	}
	
//	@Test
	public void testMeaningCountHasNotChangedBasedOnInputDocSizeWord() throws Exception, Exception{
		long start = System.currentTimeMillis();
		Arguments.numberOfWordsInDocument=1;
		Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
		System.out.println("indexing train set:"+(System.currentTimeMillis()-start)/1000);
		int numberOfTrainPolysemes1 = 0;
		Iterator<String> ks = evaluator.meanings.keySet().iterator();
		while(ks.hasNext()){
			numberOfTrainPolysemes1+=evaluator.meanings.get(ks.next()).size();
		}
		Arguments.numberOfWordsInDocument=4;
		evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
		int numberOfTrainPolysemes4 = 0;
		Iterator<String> ks5 = evaluator.meanings.keySet().iterator();
		while(ks5.hasNext()){
			numberOfTrainPolysemes4+=evaluator.meanings.get(ks5.next()).size();
		}
		Assert.assertEquals(numberOfTrainPolysemes1, numberOfTrainPolysemes4);
	}
	
	
//	@Test
	public void testTestSetSizeHasNotChangedBasedOnInputDocSizeWord() throws Exception, Exception{
		Arguments.numberOfWordsInDocument=1;
		Evaluator evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
		evaluator.extractTestContext("pdt1_0/testDev_",Arguments.evaluationContextWindowSize);
		Hashtable<String,EvaluationEntry> evaluationEntries = evaluator.evaluationEntries;
		int numberOfTestPolysemes1 = evaluationEntries.size();
		int numberOfTestContexts1 = 0;
		Enumeration<EvaluationEntry> entries = evaluationEntries.elements();
		while(entries.hasMoreElements()){
			numberOfTestContexts1+=entries.nextElement().getContext().size();
		}
		Arguments.numberOfWordsInDocument=3;
		evaluator = new Evaluator(Arguments.numberOfWordsInDocument,
				Arguments.numberOfSentencesInLuceneDoc,
				Arguments.upBoarderForNumberOfMeanings, "pdt1_0/train_");
		evaluator.extractTestContext("pdt1_0/testDev_",Arguments.evaluationContextWindowSize);
		evaluationEntries = evaluator.evaluationEntries;
		int numberOfTestPolysemes5 = evaluationEntries.size();
		int numberOfTestContexts5 = 0;
		entries = evaluationEntries.elements();
		while(entries.hasMoreElements()){
			numberOfTestContexts5+=entries.nextElement().getContext().size();
		}
		Assert.assertEquals(numberOfTestPolysemes1, numberOfTestPolysemes5);
		Assert.assertEquals(numberOfTestContexts1, numberOfTestContexts5);
	}
	
	@Test
	public void testPredict() throws Exception, Exception{
		Evaluator evaluator = new Evaluator(-1,1, 2, null);
		String sentence = "aero-1 tedy nebude pri-1 vyrobe l-3 l-3 a-1 l-3 vazano na-1 jedineho dodavatele spickove avioniky";
		evaluator.extractSentenceContext(sentence, 2);
		evaluator.predict();
		
		Set<Entry<String, EvaluationEntry>> set = evaluator.evaluationEntries.entrySet();
		Iterator<Entry<String, EvaluationEntry>> it =set.iterator();
		while(it.hasNext()){
			Entry<String, EvaluationEntry> ee = it.next();
			String  meaning = ee.getKey();
			ArrayList<Context> contexts = ee.getValue().getContext();
			System.out.println(meaning);
			for(Context context:contexts){
				ArrayList<String> left = context.previousContext;
				ArrayList<String> right = context.nextContext;
				for(String s:left)System.out.print("l:"+s+" ");
				System.out.println();	
				for(String s:right)System.out.print("d:"+s+" ");
				System.out.println();
			}
			System.out.println();
		}
	}
}
