package vectorModels;

import java.io.IOException;

import pitt.search.semanticvectors.CompareTerms;
import pitt.search.semanticvectors.LSA;
import preprocessing.OldIndexer;

public class LSAMatrix {


	public static void makeLuceneIndex(String corpusPath){
		OldIndexer.index(corpusPath, 100);
	}
	
	public static void makeLSAmatrices(){
		String dimension="100";
		String minfrequency="2"; //[minimum term frequency]
		String maxnonalphabetchars="-1"; //[number non-alphabet characters (-1 for any number)]
		String termweight = "logentropy";
		String indexName="index";
		
		String[] args={"-dimension",dimension,"-minfrequency",minfrequency,"-maxnonalphabetchars",
				maxnonalphabetchars,"-termweight",termweight,indexName};
		try {
			LSA.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) throws  Exception {
		makeLuceneIndex("pdt1_0\\training");
//		makeLSAmatrices();
		String term1 = "na-1";
		String term2 = "projektu l-3";
		String term3 = "l-4";
//		System.out.println(CompareTerms.measureSimilarity(term1, term2));
//		System.out.println(CompareTerms.measureSimilarity(term1, term3));

		
//		CompareTerms.getTermForGivenContext("na-1", "l-3");
		
		
	}

}
