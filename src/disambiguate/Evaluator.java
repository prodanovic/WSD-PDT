package disambiguate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.RealVector;
import pitt.search.semanticvectors.vectors.Vector;
import pitt.search.semanticvectors.vectors.VectorFactory;
import preprocessing.CzechIndexer;
import preprocessing.OldIndexer;
import util.Arguments;
import util.Context;
import util.EvaluationEntry;
import util.FileUtil;
import util.Log;
import util.VectorUtil;
import vectorModels.RandomIndexingMatrix;
import vectorModels.VectorSpaceMatrix;

public class Evaluator {

	public float truePositive=0;
	public float falsePositive=0;
	public float trueNegative=0;
	public float falseNegative=0;
	
	public float discardedTestEntries=0f;
//..for baseline prediction
	public float testInstanceCount=0f, randomAccuracy=0f;
	
	VectorStoreReaderLucene vecReader ;
	LuceneUtils luceneUtils;
	CzechIndexer czechIndexer;
	public Map<String, ArrayList<String>> meanings ;
	public  Hashtable<String,EvaluationEntry> evaluationEntries ;
	private Hashtable<String,Vector> termVectors; 
	Logger logger ;
	int upBoarderForNumberOfMeanings;
	public int similaritiesCalculated=0;
	
	boolean isRI = Arguments.matrixType==Arguments.MATRIX_TYPE.RI.ordinal();
	boolean isPMI = Arguments.matrixType==Arguments.MATRIX_TYPE.PMI.ordinal();
	
	public Evaluator(int numberOfWordsInDocument, int numberOfSentencesInLuceneDoc,
			int upBoarderForNumberOfMeanings, String inputFilePath) throws CorruptIndexException, IOException {
		super();
		logger=Log.getLogger(Arguments.logName);
		czechIndexer = new CzechIndexer(inputFilePath,numberOfSentencesInLuceneDoc,numberOfWordsInDocument);
		czechIndexer.index("index");
		czechIndexer.loadMeaningsAndTokens();
		
		if(isRI){
			File riDIR = new File("termvectors.bin");
			if(riDIR.exists())riDIR.delete();
			RandomIndexingMatrix.makeRandomIndex();
			vecReader = new VectorStoreReaderLucene("termvectors.bin");
			luceneUtils = new LuceneUtils("index");
		}
		
		evaluationEntries = new Hashtable<String, EvaluationEntry>();
	    termVectors = new Hashtable<String, Vector>();
//		tokenTestList = new ArrayList<String>();
//		cachedDistances = new HashMap<String, Double>();
		this.upBoarderForNumberOfMeanings = upBoarderForNumberOfMeanings;
//		czechIndexer.pruneMeaningThatOccurLessThan(upBoarderForNumberOfMeanings);
		meanings = czechIndexer.meanings;
	}
	
	public void extractTestContext(String testFilePath,int contextWindowSize) throws Exception{

		 Scanner scanner = new Scanner(new File(testFilePath), "Windows-1250");
		  while(scanner.hasNextLine()){
			  extractSentenceContext(scanner.nextLine(),contextWindowSize);
		  }
	}
	
	//sliding window depending on the contextSize
	// mozda da se ne uzimaju u obzir meaning-1 kad se stavlja u kontekst 
	public  void extractSentenceContext(String sentence, int contextSize){
		String[] tokens = sentence.split(" ");
		String current;
		for (int i = 0; i < tokens.length; i++) {
			current = tokens[i];
			if(current.contains("-")){ // && current.length()>1 && Character.isLetter(current.charAt(0))
				String lemma = current.substring(0, current.indexOf("-"));
				ArrayList<String> lemma_meanings = meanings.get(lemma);
				if(lemma_meanings != null && lemma_meanings.size()>1){
					ArrayList<String> previousContext = new ArrayList<String>();
					ArrayList<String> nextContext = new ArrayList<String>();
					int startPreviousContextIndex = i-contextSize;
					if(startPreviousContextIndex<0)startPreviousContextIndex=0;
					for(int k=startPreviousContextIndex; k<i ; k++){
						if(czechIndexer.typesTokens.containsKey(tokens[k].toLowerCase()))
							previousContext.add(0,tokens[k].toLowerCase());
					}
					int startNextContextIndex = i+1;
					if(startNextContextIndex>tokens.length)startNextContextIndex=tokens.length;
					int endNextContextIndex = i+1+contextSize;
					if(endNextContextIndex>tokens.length)endNextContextIndex =tokens.length;
//					System.err.println("start:"+startNextContextIndex+", end:"+endNextContextIndex);
					for(int k=startNextContextIndex; k<endNextContextIndex ; k++){
						if(czechIndexer.typesTokens.containsKey(tokens[k].toLowerCase()))
							nextContext.add(tokens[k].toLowerCase());
					}
					if(!(previousContext.size()==0 && nextContext.size()==0)){
						EvaluationEntry evaluationEntry = evaluationEntries.get(current);
						ArrayList<Context> contexts;
						if(evaluationEntry==null){
							contexts = new ArrayList<Context>();
							evaluationEntry = new EvaluationEntry();
						}
						else{
							contexts = evaluationEntry.getContext();
						}
						contexts.add(new Context(previousContext, nextContext));
						evaluationEntry.setContext(contexts);
						evaluationEntries.put(current, evaluationEntry);
					}
				}
				
			}
		}
	}
	

	public List<String> predict() throws IllegalArgumentException, IOException{
		List<String> predictionList = new ArrayList<String>();
		String lemma, current;
		Set<Entry<String, EvaluationEntry>> set = evaluationEntries.entrySet();
		Iterator<Entry<String, EvaluationEntry>> it =set.iterator();
		while(it.hasNext()){
			long start = System.currentTimeMillis();
			Entry<String, EvaluationEntry> ee = it.next();
			current = ee.getKey();
			lemma = current.substring(0, current.indexOf("-"));
			ArrayList<String> lemma_meanings = meanings.get(lemma);
//..........lemma for the particular meaning wasn't encountered in training. counted as FP			
			if(lemma_meanings==null){// sto je jako cudno?? probaj da ne filtriras
				logger.info("Lemma: "+lemma+" not found in training.\n");
				falseNegative+=numberOfTestContextsForMeaning(current); 
				continue;
			}
//..........meaning was not encountered in training			
			if(!lemma_meanings.contains(current)){
				logger.info("Meaning not found in training:"+current);
				falseNegative+=numberOfTestContextsForMeaning(current); 
				continue;
			}
//..........lemma had an insufficient/incorrect number of meanings to be counted
			if(lemma_meanings.size()!=upBoarderForNumberOfMeanings){
//				logger.info("Lemma: "+lemma+" had meanings less than."+upBoarderForNumberOfMeanings+"\n");
				discardedTestEntries+=numberOfTestContextsForMeaning(current);
				continue;
			}
			
			ArrayList<Context> contexts = ee.getValue().getContext();
			for(Context context:contexts){
				testInstanceCount++; //for baseline precision
				float temp = lemma_meanings.size();
				randomAccuracy+=1/temp;
				
				
				String chosenMeaning = isRI?calculateMeaningRI(context,lemma_meanings)
							:calculateMeaningTFIDF_PMI(context,lemma_meanings);
				if(chosenMeaning.equals("")){
					logger.severe("System couldn't calculate for meaning="+current+", lemma="+lemma);
					falseNegative++;
				}
//..................increment TP or FP  
//				predictionList.add(current+"="+chosenMeaning);
//				logger.fine(current+"="+chosenMeaning);
				if(current.equals(chosenMeaning))truePositive++;
				else falsePositive++;
			}		
					
		}
		return predictionList;
	}
	
	private String calculateMeaningTFIDF_PMI(Context context, ArrayList<String> lemma_meanings) throws IOException{
		String chosenMeaning="";
		Double currentSim=0d,  biggest;
		VectorSpaceMatrix matrix = new VectorSpaceMatrix();
		double[] contextVector = VectorUtil.createZeroVector(matrix.termVectorDimension());
		double[] meaningVector = VectorUtil.createZeroVector(matrix.termVectorDimension());
		StringBuilder leftC=new StringBuilder(), rightC=new StringBuilder();
		ArrayList<String> left = context.previousContext;
		
		for(String term:left){
			VectorUtil.add(contextVector,isPMI?matrix.getTermVectorPMI(term):matrix.getTFIDFTermVector(term));//
			leftC.append(term+" ");
		}	
		ArrayList<String> right = context.nextContext;
		for(String term:right){
//			phraseQuery.add(new Term("contents",term));
			VectorUtil.add(contextVector,isPMI?matrix.getTermVectorPMI(term):matrix.getTFIDFTermVector(term));
			rightC.append(term+" ");
		}
		biggest = -1000d;
		for(String meaning: lemma_meanings){
			meaningVector = isPMI?matrix.getTermVectorPMI(meaning):matrix.getTFIDFTermVector(meaning);
			currentSim = VectorUtil.cosineSim(contextVector, meaningVector);
			if(currentSim>biggest){
				biggest = currentSim;
				chosenMeaning = meaning;
			}	
		}
		if(chosenMeaning.equals("")){
			logger.severe(leftC.toString()+" |X| "+rightC.toString());
		}
		
		return chosenMeaning;
	}
	private String calculateMeaningRI(Context context, ArrayList<String> lemma_meanings){
		String chosenMeaning="";
		Double currentSim=0d,  biggest;
		StringBuilder leftC=new StringBuilder(), rightC=new StringBuilder();
		
//......build left context (preceding)				
		RealVector leftContextVec = (RealVector) VectorFactory.createZeroVector(
		        vecReader.getVectorType(), vecReader.getDimension());
		ArrayList<String> left = context.previousContext;
		for(String term:left){
			RealVector tempVec = (RealVector) getVector(term);
			//TODO add weights to addition
			leftContextVec.superpose(tempVec, 1, null);
			leftC.append(term+" ");
		}				
//......build right context (succeeding)				
		Vector rightContextVec = VectorFactory.createZeroVector(
		        vecReader.getVectorType(), vecReader.getDimension());
		ArrayList<String> right = context.nextContext;
		for(String term:right){
			RealVector tempVec = (RealVector)getVector(term);
			//TODO add weights to addition
			rightContextVec.superpose(tempVec, 1, null);
			rightC.append(term+" ");
		}				
//..............build context vector
		leftContextVec.superpose(rightContextVec, 1, null);
		
		float[] contextVector = leftContextVec.getCoordinates();
		
//......go through all similar meanings and calculate appropriateness for the contextVector
		biggest = -1000d;
		for(String meaning: lemma_meanings){
			RealVector termVector = (RealVector) getVector(meaning);
			currentSim = VectorUtil.cosineSim(contextVector, termVector.getCoordinates());
//			currentSim = leftContextVec.measureOverlap(termVector);
			if(currentSim>biggest){
				biggest = currentSim;
				chosenMeaning = meaning;
			}	
		}
		if(chosenMeaning.equals("")){
			logger.severe(leftC.toString()+" |X| "+rightC.toString());
		}
		return chosenMeaning;
	}
	
	public void evaluateAndLog(){
		logger.severe("Calculated precision so far: "+this.getPrecision()+"\t tp="+this.truePositive+"\t fp="+this.falsePositive);
		logger.fine("Random precision: "+(testInstanceCount/randomAccuracy));
		logger.severe("");
	}
	
	//goes through the sentences, and if finds concept with multiple meaning(denoted in "concept-uniqueNumber" annotation)
	//then it goes through the list of all meanings for that concept, and calculates distance from the neighboring words 
	//if the distance of the current meaning of the concept is closer than all other meanings of that concept,
	@Deprecated
	public void evaluate(List<String> predictionList)throws IllegalArgumentException, IOException{
		String expected="", predicted="";
		String[] pred= new String[2];  
		for(String prediction:predictionList){
			pred=prediction.split(":");
			expected = pred[0];
			predicted = pred[1];
			
			if(expected.equals(predicted))truePositive++;
			else falsePositive++;
		}
	}
	
	public float numberOfTestMeanings(){
		return evaluationEntries.keySet().size();
	}
	
	public float numberOfTestContexts(){
		float contextNo = 0;
		Iterator<String>  keys = evaluationEntries.keySet().iterator();
		while(keys.hasNext()){
			String testMeaning = keys.next();
//			System.out.println(testMeaning);
			EvaluationEntry ee=evaluationEntries.get(testMeaning);
			contextNo+=ee.getContext().size();
		}
		return contextNo;
	}
	
	public int numberOfTestContextsForMeaning(String testMeaning){
		return evaluationEntries.get(testMeaning).getContext().size();
	}
	
	public String testSetStatsForLog(){
		String msg = "Test Set Statistics\n";
		msg+="\ttotal number Of Test Meanings[ts cases]:"+numberOfTestMeanings()+"\n";
		msg+="\ttotal number Of Test Contexts[ts instances]:"+numberOfTestContexts()+"\n";
		return msg;
	}
	
	public String evaluationStatsForLog(){
		StringBuilder msg = new StringBuilder("Evaluation Statistics\n");
		msg.append("\tChecksum(#test set=tp+fp+discarded):"+
				numberOfTestContexts()+"=="+(truePositive+falsePositive+discardedTestEntries)+"\n");
		msg.append("\ttp="+truePositive+" fp="+falsePositive+" discarded="+discardedTestEntries+"\n");
		msg.append("\tPrecision:"+getPrecision()+" tp="+truePositive+" fp="+falsePositive+"\n");
		msg.append("\tRecall:"+getRecall()+" tp="+truePositive+" fn="+falseNegative+"\n");
		msg.append("\tF-measure:"+getFMeasure(0.5f)+"\n");
		msg.append("\tRandom precision= "+getRandomPrecision()+" (testInstances="+testInstanceCount+"" +
				" ,invertSenseCount="+randomAccuracy+")\n");
		return msg.toString();
	}
	
	
	private Vector getVector(String term){
		Vector vector = termVectors.get(term);
		if(vector==null){
			vector=CompoundVectorBuilder.getQueryVectorFromString(vecReader, luceneUtils, term);
			termVectors.put(term, vector);
		}
//		else System.out.println(term+" found in termVectors hash.");
		return vector;
	}
	
	public float getPrecision(){
//		System.out.println("truePositive="+truePositive);
//		System.out.println("falsePositive="+falsePositive);
		return (truePositive/(truePositive + falsePositive))*100;
	}
	public float getAdjustedPrecision(){
		return (truePositive/(truePositive + falsePositive - falseNegative))*100;
	}
	public float getRecall(){
		return (truePositive/(truePositive + falseNegative))*100;
	}
	public float getFMeasure(float alpha){
		float under = alpha/this.getPrecision() + (1-alpha)/this.getRecall();
		return 1/under;
	}
	public float getCoverage(){
		return((truePositive+ falsePositive)/(truePositive + falsePositive + falseNegative))*100;
	}
	public float getRandomPrecision(){
		return (randomAccuracy/testInstanceCount)*100;
	}
	//simple test to see if the Evaluator works
	public static void main(String[] args) throws Exception {
//		ArrayList<String> tokenTestList = new ArrayList<String>();
//		String []tokens= {"word","token1-1","word","word","token4-1","word","token2-3","word","token3-2",
//				"word","word","word","word"};
//		for(String token:tokens)tokenTestList.add(token);
//		HashMap<String, ArrayList<String>> meanings  = new HashMap<String, ArrayList<String>>();
//		String[] meaning1 = {"token1-1","token1-2", "token1-3"};
//		String[] meaning2 = {"token2-1","token2-2","token2-3"};
//		String[] meaning3 = {"token3-1"};
//		ArrayList<String> meanings1 = new ArrayList<String>();
//		ArrayList<String> meanings2 = new ArrayList<String>();
//		ArrayList<String> meanings3 = new ArrayList<String>();
//		for(String m:meaning1)meanings1.add(m);
//		for(String m:meaning2)meanings2.add(m);
//		for(String m:meaning3)meanings3.add(m);
//		meanings.put("token1", meanings1);
//		meanings.put("token2", meanings2);
//		meanings.put("token3", meanings3);
//	
//		Evaluator evaluator = new Evaluator(tokenTestList,meanings);
//		ArrayList<String> predictedList = (ArrayList<String>) evaluator.predictRandomly();
////		for(String s:predictedList)System.out.print(s+" ");
//		evaluator.evaluate(predictedList);
//		System.out.println(evaluator.getAdjustedPrecision());
//		
//		System.out.println(evaluator.isToken("token1-1"));
//		System.out.println(evaluator.isToken("1token"));
//		System.out.println(evaluator.isToken("70"));
//		System.out.println(evaluator.isToken(")"));
//		System.out.println(evaluator.isToken("^a3344"));
		
		
		
		
		
//		String[] previous="previous context".split(" ");
//		String[] next="next context".split(" ");
		
//		Evaluator evaluator = new Evaluator(6);
//		evaluator.extractTestContext("pdt1_0//testDev", 3);
//		System.err.println("numberOfTestMeanings:"+evaluator.numberOfTestMeanings());
//		System.err.println("numberOfTestContexts:"+evaluator.numberOfTestContexts());
		
	}

}
