package disambiguate;

import pitt.search.semanticvectors.Search;
import preprocessing.OldIndexer;

public class SearchVectors {

	//
	
	String queryvectorfile="termvectors.bin";
	String searchvectorfile;
	String luceneindexpath="index";
	String searchtype; // SUM, SPARSESUM, SUBSPACE, MAXSIM,TENSOR, CONVOLUTION, BALANCED_PERMUTATION, 
						//PERMUTATION, PRINTQUERY"
	String queryTerms=""; // terms separated by \\s
	
	//Default option - build a query by adding together (weighted) vectors for each of the query terms,
	//and search using cosine similarity.
	//ex: -searchtype sum abraham isaac
	public static void sumSimilarityCosineDistance(String queryTerms){
		String searchtype = new String("sum");
		String[] args = {"-searchtype",searchtype,queryTerms};
		Search.main(args);
		
	}

	//"Quantum disjunction" - get vectors for each query term, create a representation for the subspace 
	//spanned by these vectors, and score by measuring cosine similarity with this subspace.
	//ex: -searchtype subspace abraham isaac
	public static void subspaceSimilarityCosineDistance(String queryTerms){
		String searchtype = new String("subspace");
		String[] args = {"-searchtype",searchtype,queryTerms};
		Search.main(args);
	}
	
	
	
	public static void main(String[] varg){
//		SVBuilder.indexAndMakeVectorStore("index", 1);
		
		
		sumSimilarityCosineDistance("na-1");
		subspaceSimilarityCosineDistance("podíl projektu"); //podíl na-1 projektu
		
	}
	
}
