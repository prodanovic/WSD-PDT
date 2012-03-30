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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.Arguments;
import util.FileUtil;

public class CzechIndexer {

	private String inputDocumentPath;
	private int paragraphSize=1; 
	private int contextSize=-1;
	
	public Hashtable<String, Integer> typesTokens;
	public HashMap<String, ArrayList<String>> meanings ;
	
	public CzechIndexer(String inputDocumentPath,int paragraphSize, int contextSize) {
		super();
		this.inputDocumentPath = inputDocumentPath;
		this.paragraphSize = paragraphSize;
		this.contextSize = contextSize;
		typesTokens = new Hashtable<String, Integer>();
		meanings = new HashMap<String, ArrayList<String>>();
	}

	public void index(String indexLocation) {
		  File docDir = new File(inputDocumentPath);
	    
		  try {
			  File INDEX_DIR = new File(indexLocation);
			  IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR), 
					new WordSpaceAnalyzer(Version.LUCENE_34), 
					  							// non-word entries are filtered  (begin with non-word char)	
					  true, 
					  IndexWriter.MaxFieldLength.UNLIMITED);
//		      System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
		      addSentencesFromFileToIndex(writer,docDir);
		      writer.optimize();
		      writer.close();
	
//		      System.out.println("created index in: "+(end.getTime() - start.getTime())/1000 + " seconds");
		  } catch (IOException e) {
		      e.printStackTrace();
	    }
	  }
	 
	 	//for the specified index @writer puts the @sentenceNumber of sentences (found in a @file) 
	  //per 1 luceneDocument; puts all lucene docs into index
	  private void addSentencesFromFileToIndex(IndexWriter writer,File file) 
	  throws IOException{
//		  System.err.println("paragraphSize="+paragraphSize+" contextSize="+contextSize);
		  InputStreamReader fReader =  new InputStreamReader(new FileInputStream(file.getAbsolutePath()),
				  "Windows-1250");
		  StringBuilder sentences = new StringBuilder();
		  int sentenceCounter = 0;
		  Scanner scanner = new Scanner(file, "Windows-1250");
		  while(scanner.hasNextLine()){
			  	String sentence = scanner.nextLine();
			  	
//			  	++sentenceCounter;
  				if((sentenceCounter++ % paragraphSize)==0){
  					if(contextSize>-1){
    					ArrayList<String> fragments = getFragments(sentence, contextSize);
	    				for(String fragment:fragments){
	    					Document luceneDocument = new Document();
		    				Field field =  new Field("contents",fragment,Field.Store.YES, 
		    						Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
		    				luceneDocument.add( field );
		    				writer.addDocument(luceneDocument);
//		    				System.out.println("added frag to index:"+fragment);
	    				}
	    			}
    				else{
    					String content = sentences.toString();//paragraphSize==1?sentence:
    					if(!content.equals("")){
    						Document luceneDocument = new Document();
        					Field field =  new Field("contents",content,Field.Store.YES, 
    	    						Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
    	    				luceneDocument.add( field );
    	    				writer.addDocument(luceneDocument);
//    	    				System.out.println("added to index:"+content);
    	    				sentences = new StringBuilder();
    					}
    				}
  				}
				sentences.append(sentence);//+"\n"
		  }
		  if(!(contextSize>-1)){
			  Document luceneDocument = new Document();
			  Field field =  new Field("contents",sentences.toString(),Field.Store.YES, 
						Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
			  luceneDocument.add( field );
			  writer.addDocument(luceneDocument);
		  }
	  }
	  
	  public ArrayList<String> getFragments(String sentence,int contextSize){
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
	  
	  public static void loadSentencesFromIndex(String indexPath, String outFilePath) throws CorruptIndexException, IOException{
			IndexReader indexReader= IndexReader.open(FSDirectory.open(new File(indexPath)));
			OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream(outFilePath),"Windows-1250");
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while(i<indexReader.numDocs()){
				Document doc = indexReader.document(i++);
				sb.append(doc.getField("contents").stringValue()+"\n");
			}
			fWriter.write(sb.toString()+"\n");
			fWriter.close();
		}
	  
	  public void writeIndexTermsToFile(String outPath){
		  IndexReader indexReader;
		  StringBuilder sb = new StringBuilder();
		  try {
			  	indexReader = IndexReader.open(FSDirectory.open(new File("index")));
				TermEnum terms = indexReader.terms();
				int i=1;
				while(terms.next()){
			        Term term = terms.term();
			        sb.append(term.text()+"\t");
			        if((i++%10)==0){
			        	sb.append("\n");
			        }
				}
				FileUtil.writeTextToFile(sb.toString(), new File(outPath), "Windows-1250", false);
		  } catch (CorruptIndexException e) {
				e.printStackTrace();
		  } catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	  }
	  
	  //for testing purposes
	  public void writeTermDocumentMatrixToFile(String outPath){
		  	try {
		  		StringBuilder sb = new StringBuilder();
				OutputStreamWriter fWriter = new OutputStreamWriter(new FileOutputStream(outPath),"Windows-1250");
				IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));
				int i = 0;
				while(i<indexReader.numDocs()){
					Document doc = indexReader.document(i++);
					sb.append(doc.getField("contents").stringValue()+"||\n");
				}
				fWriter.write(sb.toString()+"\n");
				
				TermEnum terms = indexReader.terms();
				sb=new StringBuilder();
				int[] vector = new int[indexReader.numDocs()];
				while(terms.next()){
			        Term term = terms.term();
			        TermDocs termDocs = indexReader.termDocs(new Term("contents", term.text()));
			        i=0;
			        sb.append(term.text()+":\t");
			        while(i<indexReader.numDocs()){
			        	vector[i++]=0;	
					}
			        while(termDocs.next()){
			        	vector[termDocs.doc()] = termDocs.freq();
			        }
			        i=0;
			        while(i<indexReader.numDocs()){
			        	sb.append(vector[i++]+"\t");	
					}
			        fWriter.write(sb.toString()+"\n");
			        sb=new StringBuilder();
				}
				fWriter.close();
				indexReader.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  
	  public void loadMeaningsAndTokens() throws CorruptIndexException, IOException{
		  IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));
		  TermEnum terms = indexReader.terms();
		  while(terms.next()){
		        Term term = terms.term();
		        if(term.text().contains("-")){
					storeMeaningInHash(term.text());
				}
		        TermDocs termDocs = indexReader.termDocs(new Term("contents", term.text()));
		        int tf=1;
		        if (termDocs != null) {
		        	while (termDocs.next()) {
			            tf += termDocs.freq();
			        }
		        }
		        if(null!=typesTokens.put(term.text(), tf)){
		        	System.err.println(term.text() +"already was in htablem. Duplicate!");;
		        }
		          
		  }
		  
	  }
	  
	  public void storeMeaningInHash(String meaning){
			String lemma = meaning.substring(0,meaning.indexOf("-"));
			ArrayList<String> lemma_meanings = meanings.get(lemma);
			if(lemma_meanings == null){
				lemma_meanings = new ArrayList<String>();
				lemma_meanings.add(meaning);
				meanings.put(lemma, lemma_meanings);
//				System.out.println("AList dont exist: lemma["+lemma+"] ["+meaning+"]");
			}
			else{
				if(!lemma_meanings.contains(meaning)){
					lemma_meanings.add(meaning);
					meanings.put(lemma, lemma_meanings);
				}
			}
	  }
	  
	  public void printAllMeaningsToFile(){
			Iterator<Entry<String, ArrayList<String>>> it = meanings.entrySet().iterator();
			File trainFile = new File("pdt1_0//allMeanings");
			OutputStreamWriter fWriter;
			try {
				fWriter = new OutputStreamWriter(new FileOutputStream(trainFile.getAbsolutePath()),"Windows-1250");
				while (it.hasNext()) {
			    	Entry<String, ArrayList<String>> pairs = it.next();
			    	ArrayList<String> list =  pairs.getValue();
			    	StringBuilder sb = new StringBuilder();
			    	for(String s:list)sb.append(s+", ");
			    	fWriter.append(pairs.getKey() + " = " +sb.toString()+"\n");
			    }
				fWriter.append("Total: "+meanings.size());
				fWriter.close();
			} catch (Exception e) {e.printStackTrace();
			}
		}
	  
	  
	  public void pruneMeaningThatOccurLessThan(int count){
			//delete All One Meaning Entrys
			Iterator<Entry<String, ArrayList<String>>> it = meanings.entrySet().iterator();
			ArrayList<String> deleteKeys = new ArrayList<String>();
			while (it.hasNext()) {
		    	Entry<String, ArrayList<String>> pairs = it.next();
			    if(pairs.getValue().size()< count) {
//			    	pairs.setValue(new ArrayList<String>());
			    	deleteKeys.add(pairs.getKey());
			    }
		    }
			for(String dk:deleteKeys)meanings.remove(dk);
		    meanings.remove("");
	  }
	  
	  public int getTotalTokenCount(){
		  int cnt = 0;
		  Enumeration<Integer> elems = typesTokens.elements();
		  while(elems.hasMoreElements()){
			  cnt+=elems.nextElement();
		  }
		  return cnt;
	  }
	  
	  public int getNumberOfWordsWithMeaningCount(int meaningCount){
		  int result=0;
		  Iterator<String> keys =  meanings.keySet().iterator();
		  while(keys.hasNext()){
			  ArrayList<String> means = meanings.get(keys.next());
			  if(means.size()==meaningCount)result++;
		  }
		return result;
	  }
	  
	  public int getNumberOfAmbiguousMeanings(){
		  int result=0;
		  Iterator<String> iter = meanings.keySet().iterator();
		  while(iter.hasNext()){
			  result+=meanings.get(iter.next()).size();
		  }
		 return result;
	  }
	  public int getNumberOfAmbiguousWords(){
		return meanings.keySet().size();
	  }
	  
	  
	 public void printMeanings(){
		 Iterator<String> keys = meanings.keySet().iterator();
			while(keys.hasNext()){
				String lemma = keys.next();
				ArrayList<String> means = meanings.get(lemma);
				System.out.print(lemma+":");
				for(String m : means)System.out.print(m+", ");
				System.out.println();
			}
	 }
	
	public static void main(String []varg) throws CorruptIndexException, IOException{
		 
//		ArrayList<String> frags= ci.getFragments("this-1 is some-2 sentence", 1); 
//		for(String frag:frags)System.out.println(frag);
		
		CzechIndexer ci = new CzechIndexer("pdt1_0//trainSmall", 5, -1);
//		ci.index();
//		ci.loadMeaningsAndTokens();
//		System.out.println(ci.getNumberOfAmbiguousWords());
//		System.out.println(ci.getNumberOfAmbiguousMeanings());
//				
//		ci = new CzechIndexer("pdt1_0//trainSmall", 3, -1);
//		ci.index();
//		ci.loadMeaningsAndTokens();
//		System.out.println(ci.getNumberOfAmbiguousWords());
//		System.out.println(ci.getNumberOfAmbiguousMeanings());
		
//		ci = new CzechIndexer("pdt1_0//trainSmall", 1, -1);
//		ci.index("index");
		ci.loadMeaningsAndTokens();
		System.out.println("NumberOfAmbiguousWords:"+ci.getNumberOfAmbiguousWords());
		System.out.println("NumberOfAmbiguousMeanings:"+ci.getNumberOfAmbiguousMeanings());
		ci.printAllMeaningsToFile();
		
//		ci = new CzechIndexer("pdt1_0//trainSmall", 1, 3);
//		ci.index();
//		ci.loadMeaningsAndTokens();
//		System.out.println(ci.getNumberOfAmbiguousWords());
//		System.out.println(ci.getNumberOfAmbiguousMeanings());
//		
//		ci = new CzechIndexer("pdt1_0//trainSmall", 1, 2);
//		ci.index();
//		ci.loadMeaningsAndTokens();
//		System.out.println(ci.getNumberOfAmbiguousWords());
//		System.out.println(ci.getNumberOfAmbiguousMeanings());
//		
//		ci = new CzechIndexer("pdt1_0//trainSmall", 1, 1);
//		ci.index();
//		ci.loadMeaningsAndTokens();
//		System.out.println(ci.getNumberOfAmbiguousWords());
//		System.out.println(ci.getNumberOfAmbiguousMeanings());
		
		
	}
	  
	  
	  
}
