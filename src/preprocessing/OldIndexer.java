package preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import util.FileUtil;


public class OldIndexer {

	  static final File INDEX_DIR = new File("index");
//	  static ArrayList<String> indexTerms = new ArrayList<String>();
	  static HashSet<String> indexTerms = new HashSet<String>();
	  
	  
	  public static void index(String path, int numberOfSentencesInDoc) {
		  File docDir = new File(path);
	    
		  Date start = new Date();
		  try {
			  IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR), 
//					  new StandardAnalyzer(Version.LUCENE_30), //124834 with numbers
//					  new CzechAnalyzer(Version.LUCENE_30),//
					  new WordSpaceAnalyzer(Version.LUCENE_30),//115313 - whitespace tokenizer, all words are indexed; 
					  							// non-word entries are filtered  (begin with non-word char)
//					  new WhitespaceAnalyzer(),//141956 , with : &amp;	&ast; and numbers		
//					  new StopAnalyzer(Version.LUCENE_30), //119584 without numbers;
//					  new SimpleAnalyzer(), //119611 without numbers
					  true, 
					  IndexWriter.MaxFieldLength.UNLIMITED);
		      System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
//		      indexDocs(writer, docDir ,numberOfSentencesInDoc);
		      addSentencesFromFileToIndex(writer,docDir, numberOfSentencesInDoc);
		      System.out.println("Optimizing index...");
		      writer.optimize();
		      writer.close();
	
		      Date end = new Date();
		      System.out.println("created index in: "+(end.getTime() - start.getTime())/1000 + " seconds");
		  } catch (IOException e) {
		      e.printStackTrace();
	    }
	  }

	  
	  
	  //for the specified index @writer puts the @sentenceNumber of sentences (found in a @file) 
	  //per 1 luceneDocument; puts all lucene docs into index
	  private static void addSentencesFromFileToIndex(IndexWriter writer,File file, int numberOfSentInDoc) 
	  throws IOException{
		  InputStreamReader fReader =  new InputStreamReader(new FileInputStream(file.getAbsolutePath()),
				  "Windows-1250");
		  StringBuilder sentences = new StringBuilder();
		  int sentenceCounter = 0;
		  char c;
		  int i;
		  while((i= fReader.read())!= -1){
	    		c = (char)i;
	    		if(c=='\n'){
	    			++sentenceCounter;
	    			if((sentenceCounter % numberOfSentInDoc)==0){
	    				Document luceneDocument = new Document();
//	    				ArrayList<String> fragments = getFragments(sentences.toString(), 3);
//	    				for(String fragment:fragments){
		    				Field field =  new Field("contents",sentences.toString(),Field.Store.YES, 
		    						Field.Index.ANALYZED, Field.TermVector.NO);
		    				luceneDocument.add( field );
//	    				}
	    				writer.addDocument(luceneDocument);
//	    				System.out.println("added to index:"+sentences.toString());
	    				sentences = new StringBuilder();
	    			}
	    		}
	    		else{
	    			sentences.append(c);
	    		}
	    	}
	  }
	  
	  public static ArrayList<String> getFragments(String sentence,int contextSize){
		  ArrayList<String> fragments=new ArrayList<String>();
		  String[] tokens = sentence.split(" ");
		  int i=0;
		  while(i<tokens.length){
			  if(tokens[i].contains("-")){
				  String fragment= new String();
				  int j=i-contextSize>0?i-contextSize:0;
				  int k=(i+1+contextSize)<tokens.length?(i+1+contextSize):tokens.length;
				  while(j<k){
					  fragment+=tokens[j++]+" ";
				  }
				  fragments.add(fragment);
			  }
			  i++;
		  }
		  return fragments;
	  }
	  
	  public static String getDocumentsFromLuceneIndex(){
		  IndexReader indexReader;
		  StringBuilder sb = new StringBuilder();
		  OutputStreamWriter fWriter ;
		  try {
			  fWriter = new OutputStreamWriter(new FileOutputStream("pdt1_0/indexSentences"),"Windows-1250");
			  indexReader = IndexReader.open(FSDirectory.open(new File("index")));
			  int numDocs = indexReader.numDocs();
			  int i=0;
			  while(i<numDocs){
			        Document doc = indexReader.document(i++);
			        sb.append(doc.get("contents")+"\n");
			  }
			  fWriter.write(sb.toString());
			  fWriter.close();
			  return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	  }
	  
	  public static int getNumberOfTokensInFile(String path){
		  int tokenCount=0;
		  try {
			String text = FileUtil.extractTextFromFile(path, "Windows-1250");
			String[] tokenCandidates = text.split("\\s");
			for(String tc:tokenCandidates){
				if(!tc.matches("^$")){
					Character first = tc.charAt(0);
					if(Character.isLetter(first) && tc.length()>1){
						tokenCount++;
						System.out.println(tc);
					}
				}  
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tokenCount;
		  
		  
	  }
	  
	  public static boolean isTermInIndex(String term){
		  return indexTerms.contains(term);
	  }
	  public static HashSet<String> getIndexTerms(){
		  return indexTerms;
	  }
	  public static String getTermsFromLuceneIndex(){
			IndexReader indexReader;
			StringBuilder sb = new StringBuilder();
			OutputStreamWriter fWriter ;
			try {
				fWriter = new OutputStreamWriter(new FileOutputStream("pdt1_0/indexTerms"),"Windows-1250");
				indexReader = IndexReader.open(FSDirectory.open(new File("index")));
				TermEnum terms = indexReader.terms();
				int i=1;
				while(terms.next()){
			        Term term = terms.term();
			        sb.append(term.text()+"\t");
			        indexTerms.add(term.text());
			        if((i++%10)==0){
			        	sb.append("\n");
			        }
				}
				fWriter.write(sb.toString());
				fWriter.write("\n\nterms.docFreq():"+ terms.docFreq());
				fWriter.write("\nNumber of terms in index:"+i);
				fWriter.close();
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return sb.toString();
			}
		}
	  
	  public static int termFreqInLuceneIndex(String term) throws Exception{
			Directory fsDir = new SimpleFSDirectory(new File("index"));
			IndexSearcher indexSearcher = new IndexSearcher(fsDir);
			IndexReader indexReader = indexSearcher.getIndexReader();
			TermDocs termDocs = indexReader.termDocs(new Term("contents", term));
			int result = 0;
			while (termDocs.next()) {
				result+=termDocs.freq();
			}
			return result;
		}
	  
	  
	  static void indexDocs(IndexWriter writer, File file, int numberOfSentencesInDoc)throws IOException {
		    if (file.canRead()) {
		      if (file.isDirectory()) {
		        String[] files = file.list();
		        if (files != null) {
		          for (int i = 0; i < files.length; i++) {
		            indexDocs(writer, new File(file, files[i]), numberOfSentencesInDoc);
		          }
		        }
		      } else {
		        System.out.println("adding " + file);
		        try {
		        	addSentencesFromFileToIndex(writer,file, numberOfSentencesInDoc);
		        }
		        catch (FileNotFoundException fnfe) {
		        }
		      }
		    }
		  }
	  
	  public static void main(String[] args) {
//		Indexer ind = new Indexer();
//		String testSent = "and-4 this is a-1 test sentence-3 oh my god-1";
//		ArrayList<String> fragments = ind.getFragments(testSent,3);
//		for(String f:fragments)System.out.println(f);
		
//		int tokenNum = Indexer.getNumberOfTokensInFile("pdt1_0/pdt1_cleaned");
		
		int typeNum = OldIndexer.getIndexTerms().size();
		
//		System.out.println("Number of tokens:"+tokenNum);
		System.out.println("Number of tokens:"+typeNum);
	}
}
