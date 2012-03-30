package vectorModels;

import java.io.IOException;

import pitt.search.semanticvectors.BuildIndex;
import preprocessing.OldIndexer;

public class RandomIndexingMatrix {

	
	public static void makeRandomIndex(){
		
		//make RI
		String vectortype="real";  //[real, complex or binary]"
		String dimension="300"; //[number of vector dimensions]
		String minfrequency="2"; //[minimum term frequency]
		String maxnonalphabetchars="-1"; //[number non-alphabet characters (-1 for any number)]
		String trainingcycles="1"; //training cycles
		String docindexing=""; //incremental|inmemory|none] Switch between building doc vectors
//		incrementally (requires positional index), all in memory (default case), or not at all"
		
		String indexName="index";
		String [] args = {"-vectortype",vectortype,"-dimension",dimension,"-minfrequency",minfrequency
				,"-maxnonalphabetchars",maxnonalphabetchars,"-trainingcycles",trainingcycles,
				indexName};
		BuildIndex.main(args);
		
	}
//	public static double getSimpleSimilarity(String phraseSrc, String phraseTarget) throws IllegalArgumentException, IOException{
//		String[] arg = {phraseSrc,phraseTarget}; // same as {"tires","inch"} = 0.23263216
//		double result= CompareTermsSimple.main(arg);;
////		System.out.println(phraseSrc+","+phraseTarget+" = "+result);
//		return result;
//	}
//	
//	public static double getSimpleSimilarity(String previous, String current,String next) throws IllegalArgumentException, IOException{
//		String[] arg = {previous,current}; 
//		double result= CompareTermsSimple.main(arg);
//		String[] arg2 = {current,next}; 
//		double result2= CompareTermsSimple.main(arg2);
//		System.out.println(previous+","+current+" = "+result+"  "+current+","+next+" = "+result2);
//		return (result + result2)/2;
//	}
}
