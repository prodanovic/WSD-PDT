package vectorModels;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.FSDirectory;

public class VectorSpaceMatrix {

	IndexReader indexReader;
	Hashtable<Term, Float> termEntropy = new Hashtable<Term, Float>();
	
	
	public double[] getTFIDFTermVector(String term) throws CorruptIndexException, IOException{
		Term t = new Term("contents", term);
		indexReader = IndexReader.open(FSDirectory.open(new File("index")));
		double N = indexReader.numDocs();
		int df=0;
		double[]vector = new double[indexReader.numDocs()];
		int i=0;
		while(i<indexReader.numDocs()){
        	vector[i++]=0;	
		}
		TermDocs termDocs = indexReader.termDocs(new Term("contents", t.text()));
        while(termDocs.next()){
        	vector[termDocs.doc()] = termDocs.freq();
        	df++;
        }
        
        termDocs = indexReader.termDocs(new Term("contents", t.text()));
        while(termDocs.next()){
        	vector[termDocs.doc()] = weight(termDocs.freq(),df,N);
        }
        indexReader.close();
        
		return vector;
	}

	public int termVectorDimension() throws IOException{
		indexReader = IndexReader.open(FSDirectory.open(new File("index")));
		int d = indexReader.numDocs();
		indexReader.close();
		return d;
	}
	
	public double weight(int tf, double df, double N){
		return tf*idf(df,N);
	}
	public double idf(double df, double N){
		return Math.log10(N/df);
	}
	
	public double[] getTermVectorPMI(String term) throws CorruptIndexException, IOException{
		Term t = new Term("contents", term);
		indexReader = IndexReader.open(FSDirectory.open(new File("index")));
		int gf=0;
		double[]vector = new double[indexReader.numDocs()];
		int i=0;
		while(i<indexReader.numDocs()){
        	vector[i++]=0;	
		}
		TermDocs termDocs = indexReader.termDocs(new Term("contents", t.text()));
        while(termDocs.next()){
        	vector[termDocs.doc()] = termDocs.freq();
        	gf+=termDocs.freq();
        }
        
        termDocs = indexReader.termDocs(new Term("contents", t.text()));
        while(termDocs.next()){
        	vector[termDocs.doc()] = vector[termDocs.doc()]*getEntropy(t, gf) ;
        }
//        System.out.println("E["+t.text()+"]="+getEntropy(t, gf));
        indexReader.close();
        
		
		return vector;
	}
	
	/**
	   * Gets the 1 - entropy (i.e. 1+ plogp) of a term,
	   * a function that favors terms that are focally distributed
	   * The definition of log-entropy weighting as provided in
	   * Martin and Berry (2007):
	   * Entropy = 1 + sum ((Pij log2(Pij)) /  log2(n))
	   * where Pij = frequency of term i in doc j / global frequency of term i
	   * 		 n	 = number of documents in collection
	   * @param term whose entropy you want
	   */
	  public float getEntropy(Term term, int gf){
	    if(termEntropy.containsKey(term))
	      return termEntropy.get(term);
	    double entropy = 0;
	    try {
	      TermDocs tDocs = indexReader.termDocs(term);
	      while (tDocs.next())
	      {
	        double p = tDocs.freq(); //frequency in this document
	        p=p/gf;		//frequency across all documents
	        entropy += (p*(Math.log(p)/Math.log(2))); //sum of Plog(P)
	      }
	      int n= indexReader.numDocs();
	      double log2n = Math.log(n)/Math.log(2);
	      entropy = entropy/log2n;
	    }
	    catch (IOException e) {
	    	System.err.println("Couldn't get term entropy for term " + term.text());
	    }
	    
	    termEntropy.put(term, 1+(float)entropy);
	    return (float) (1 + entropy);
	  }
	
	public void printTFIDFVector(String term) throws Exception{
		StringBuilder sb = new StringBuilder(term+":");
		double[] vector = getTFIDFTermVector(term);
		for (double coord:vector)sb.append(coord+" ");
		System.out.println(sb.toString());
	}
	
	public void printPMIVector(String term) throws Exception{
		StringBuilder sb = new StringBuilder(term+":");
		double[] vector = getTermVectorPMI(term);
		for (double coord:vector)sb.append(coord+" ");
		System.out.println(sb.toString());
	}
	  
	
	public static void main(String[] args) {
		VectorSpaceMatrix m = new VectorSpaceMatrix();
		
//		System.out.println(m.idf(1, 2));
//		System.out.println(m.weight(4,1, 2));
		

		
	}

}
