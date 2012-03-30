

package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.lang.Math;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


/**
 * Class to support reading extra information from Lucene indexes,
 * including term frequency, doc frequency.
 */
public class IndexUtils{
   
  private IndexReader indexReader;
  private Hashtable<Term, Float> termEntropy = new Hashtable<Term, Float>();


  
  /**
   * @param path - path to lucene index
   */
  public IndexUtils (String path) throws IOException {
    this.indexReader = IndexReader.open(FSDirectory.open(new File(path)));
  }


  

  /**
   * Gets the global term frequency of a term,
   * i.e. how may times it occurs in the whole corpus
   * @param term whose frequency you want
   * @return Global term frequency of term, or 1 if unavailable.
   */
  public int getGlobalTermFreq(Term term){
    int tf = 0;
    try{
      TermDocs tDocs = this.indexReader.termDocs(term);
      if (tDocs == null) {
        return 1;
      }
      while (tDocs.next()) {
        tf += tDocs.freq();
      }
    }
    catch (IOException e) {
      return 1;
    }
    return tf;
  }

  /**
   * Gets a term weight for a string, adding frequency over occurences
   * in all contents fields.
   * Currently returns some power of inverse document frequency.
   */
  public float getGlobalTermWeightFromString(String termString) {
    try {
      int freq = 0;
      freq += indexReader.docFreq(new Term("contents", termString));
      return (float) Math.pow(freq, -0.05);
    } catch (IOException e) {
      System.out.println("Couldn't get term weight for term '" + termString + "'");
      return 1;
    }
  }

  /**
   * Gets the global term weight for a term, used in query weighting.
   * Currently returns some power of inverse document frequency .
   * @param term whose frequency you want
   * @return Global term weight, or 1 if unavailable.
   */
  public float getGlobalTermWeight(Term term) {
    try {
    	return (float) Math.pow(indexReader.docFreq(term), -0.05);
    } catch (IOException e) {
    	System.out.println("Couldn't get term weight for term '" + term.text() + "'");
    	return 1;
    }
  }

  /**
   * Gets the number of documents
   */
  public int getNumDocs()
  {return indexReader.numDocs();}

  /**
   * Gets the 1 - entropy (i.e. 1+ plogp) of a term,
   * a function that favors terms that are focally distributed
   * We use the definition of log-entropy weighting provided in
   * Martin and Berry (2007):
   * Entropy = 1 + sum ((Pij log2(Pij)) /  log2(n))
   * where Pij = frequency of term i in doc j / global frequency of term i
   * 		 n	 = number of documents in collection
   * @param term whose entropy you want
   */
  public float getEntropy(Term term){
    if(termEntropy.containsKey(term))
      return termEntropy.get(term);
    int gf = getGlobalTermFreq(term);
    double entropy = 0;
    try {
      TermDocs tDocs = indexReader.termDocs(term);
      while (tDocs.next())
      {
        double p = tDocs.freq(); //frequency in this document
        p=p/gf;		//frequency across all documents
        entropy += (p*(Math.log(p)/Math.log(2))); //sum of Plog(P)
      }
      int n= this.getNumDocs();
      double log2n = Math.log(n)/Math.log(2);
      entropy = entropy/log2n;
    }
    catch (IOException e) {
    	System.err.println("Couldn't get term entropy for term " + term.text());
    }
    termEntropy.put(term, 1+(float)entropy);
    return (float) (1 + entropy);
  }

  /**
   * Filters out low frequency terms.
   * 
   * @param term Term to be filtered.
   * @param desiredFields Terms in only these fields are filtered in
   * @param minFreq minimum term frequency accepted
   * @param maxFreq maximum term frequency accepted
   * @param maxNonAlphabet reject terms with more than this number of non-alphabetic characters
   */
  protected boolean termFilter(
      Term term, String[] desiredFields, int minFreq, int maxFreq, int maxNonAlphabet) {

	int termfreq = getGlobalTermFreq(term);
    if (termfreq < minFreq | termfreq > maxFreq)  {
      return false;
    }

    return true;
  }
  
  

  
 
}
