package disambiguate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;



import pitt.search.semanticvectors.BuildIndex;
import preprocessing.OldIndexer;


public class SVBuilder {

	
	public static void indexAndMakeVectorStore(String pathToFile, int numberOfSentencesInDoc){
			OldIndexer.index(pathToFile , numberOfSentencesInDoc);
			
			//make RI
			String vectortype="real";  //[real, complex or binary]"
			String dimension="300"; //[number of vector dimensions]
			String minfrequency="2"; //[minimum term frequency]
			String maxnonalphabetchars="-1"; //[number non-alphabet characters (-1 for any number)]
			String trainingcycles="1"; //training cycles
			String docindexing=""; //incremental|inmemory|none] Switch between building doc vectors
//			incrementally (requires positional index), all in memory (default case), or not at all"
			
			String indexName="index";
			String [] args = {"-vectortype",vectortype,"-dimension",dimension,"-minfrequency",minfrequency
					,"-maxnonalphabetchars",maxnonalphabetchars,"-trainingcycles",trainingcycles,
					indexName};
			BuildIndex.main(args);
			
			//old index builder
//			BuildIndexSimple semanticIndex = new BuildIndexSimple(200, 10, 0, 1);
//			semanticIndex.buildSemanticIndex();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
//		ts.parseFileForTokens("pdt1_0//pdt1_cleaned");
//		System.out.println("Manually Counted tokens:"+ts.uniqueTokens.size()); //134023
		indexAndMakeVectorStore("pdt1_0\\training",1);
		
		OldIndexer.getTermsFromLuceneIndex(); //15546
		
		System.out.println("--Simple term vector compare --");
		ArrayList<String[]> terms = new ArrayList<String[]>();
		String[] arg1 = {"tak� v-1","mezist�tn�m"};//"-queryvectorfile","termvectors.bin",
		String[] arg2 = {"tak� v-5","mezist�tn�m"};
//		String[] arg2 = {"vnitrost�tn�ch a-2","o-1"};
		String[] arg3 = {"tak� v-8","mezist�tn�m"};
		String[] arg4 = {"tak� v-9","mezist�tn�m"};
//		String[] arg4 = {"procent u-3","mezist�tn�ch"};
		terms.add(arg1);
		terms.add(arg2);
		terms.add(arg3);
		terms.add(arg4);
		
		
		
		//only frequency-based; doesnt take into account collocates
//		getSimpleSimilarity("others","j�no�ka");//0.49827293 - others exists somewhere
//		getSimpleSimilarity("j�no�ka","others");
//		getSimpleSimilarity("kazim�r","j�no�ka");
//		getSimpleSimilarity("kazim�r-2","j�no�ka");
		
		
//		while(++i<1000)System.out.println(getSimpleSimilarity("profesion�ln�m","boxu"));
//		String[] arg3 = {"big","deal"};// 0.37412325; {"tires","way"}  = 0.11802788
//		String[] arg4 = {"dealer+2","profit+2"}; // same as {"tires","inch"} = 0.23263216
//		CompareTerms.main(arg3);
//		CompareTerms.main(arg4);
		
//		String[] arg3 = {"tires","high way"};// 0.37412325; {"tires","way"}  = 0.11802788
//		String[] arg4 = {"inch","tires"}; // same as {"tires","inch"} = 0.23263216
//		CompareTerms.usage();
//		CompareTerms.main(arg3); // when checked for content words like  {"that","tires"} returns 0; 
									//must be using a stop word list when making the index
//		CompareTerms.main(arg4);
		
//		String[] terms = {"dealer","dealeraba","sl","profit", "saturn", "saturn@2.com","saturn-2"};
//		System.out.println("---Terms in Lucene index---");
//		for(String t:terms){
//			System.out.println(t+":"+termFreqInLuceneIndex(t));
//		}
				
		
	}

}