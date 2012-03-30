package experiments;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.index.CorruptIndexException;

import util.Arguments;
import util.EvaluationEntry;
import disambiguate.Evaluator;

public class ResultFormatter {


	public static String preprocessingMethod;
	public static ArrayList<Float> Fmeasures=new ArrayList<Float>() ;  
	public static ArrayList<Float> coverages=new ArrayList<Float>() ;
	public static ArrayList<Float> precicisions=new ArrayList<Float>() ;  
	public static ArrayList<Float> recalls=new ArrayList<Float>() ;
	public static Float randomAcc = 0f;
	
	
	public static String getTableTestSetMeaningCountInstanceCount(){
		StringBuilder sb = new StringBuilder("\\hline");
		Hashtable<String, Integer> table=new Hashtable<String, Integer>();
		Evaluator evaluator;
		try {
			evaluator = new Evaluator(Arguments.upBoarderForNumberOfMeanings);
			evaluator.extractTestContext("pdt1_0/testDev_",Arguments.evaluationContextWindowSize);
			Set<Entry<String, EvaluationEntry>> set = evaluator.evaluationEntries.entrySet();
			Iterator<Entry<String, EvaluationEntry>> it =set.iterator();
			while(it.hasNext()){
				Entry<String, EvaluationEntry> ee = it.next();
				String current = ee.getKey();
				String lemma = current.substring(0, current.indexOf("-"));
				Integer lemma_meanings_count = evaluator.meanings.get(lemma).size();
				if(table.get(lemma_meanings_count)==null){
					table.put(lemma_meanings_count+"", 1);
				}else{
					Integer testSetInstanceCnt = table.get(lemma_meanings_count);
					table.put(lemma_meanings_count+"", testSetInstanceCnt+1);
				}
			}
			Set<Entry<String, Integer>> elements = table.entrySet();
			Iterator<Entry<String, Integer>> iter =elements.iterator();
			while(iter.hasNext()){
				Entry<String, Integer> elem = iter.next();
				System.out.println(" & "+elem.getKey()+" & "+elem.getValue() );
			}
		} catch (Exception e) {
			
		}
		return sb.toString();
	}
	
	
	public static String getTableForPreprocessingFC(String preprocessingApproach){
		DecimalFormat df= new DecimalFormat("#.##");
		StringBuilder sb = new StringBuilder(preprocessingApproach+" "); 
		int i=0;
		for(Float Fmeasure:Fmeasures)sb.append(" & "+df.format(Fmeasure)+" & "+df.format(coverages.get(i++)));
		sb.append(" & "+randomAcc);
		sb.append(" \\\\");
		return sb.toString();
	}
	
	public static String getTableForPreprocessingPR(String preprocessingApproach){
		DecimalFormat df= new DecimalFormat("#.##");
		StringBuilder sb = new StringBuilder(preprocessingApproach+" "); 
		int i=0;
		for(Float precicision:precicisions)sb.append(" & "+df.format(precicision)+" & "+df.format(recalls.get(i++)));
		sb.append(" & "+randomAcc);
		sb.append(" \\\\");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		
//		float i=0;
//		while(i++<4){
//			Fmeasures.add(i+0.4599999f);
//			coverages.add(i);
//		}
//		System.out.println(getTableForPreprocessingFC("NO"));
	
		getTableTestSetMeaningCountInstanceCount();
	}

}
