package experiments;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import preprocessing.CzechIndexer;
import preprocessing.LinguisticPreprocessing;
import util.Arguments;
import util.FileUtil;

public class ObtainFileStats {

	public static int getDocNum() throws Exception, Exception{
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));
		return indexReader.numDocs();
	}
	
	public static void getTypesTokens(String indexLocation) throws Exception, Exception{
		StringBuilder sb = new StringBuilder("no preprocessing &");
		FileUtil.copyFile("pdt1_0/train", "pdt1_0/train_");
		FileUtil.copyFile("pdt1_0/testDev", "pdt1_0/testDev_");
		FileUtil.copyFile("pdt1_0/testFinal", "pdt1_0/testFinal_");
		CzechIndexer ci = new  CzechIndexer("pdt1_0//train_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" & ");
		ci = new  CzechIndexer("pdt1_0//testDev_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" & ");
		ci = new  CzechIndexer("pdt1_0//testFinal_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" \\\\ \n");
		

		sb.append(" stemming &");
		LinguisticPreprocessing.stemCzechTerms();
		ci = new  CzechIndexer("pdt1_0//train_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" & ");
		ci = new  CzechIndexer("pdt1_0//testDev_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" & ");
		ci = new  CzechIndexer("pdt1_0//testFinal_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" \\\\ \n");
		
		FileUtil.copyFile("pdt1_0/train", "pdt1_0/train_");
		FileUtil.copyFile("pdt1_0/testDev", "pdt1_0/testDev_");
		FileUtil.copyFile("pdt1_0/testFinal", "pdt1_0/testFinal_");
		sb.append(" merge variants &");
		LinguisticPreprocessing.mergeCzechTermVariants();
		ci = new  CzechIndexer("pdt1_0//train_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" & ");
		ci = new  CzechIndexer("pdt1_0//testDev_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" & ");
		ci = new  CzechIndexer("pdt1_0//testFinal_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index(indexLocation);
		ci.loadMeaningsAndTokens();
		sb.append(ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+" \\\\ \n");

		System.out.println(sb.toString());
	}

	public static void getLatexTableForMeaningsTypes() throws Exception, Exception{
		CzechIndexer ci = new  CzechIndexer("pdt1_0//train_", Arguments.numberOfSentencesInLuceneDoc, Arguments.numberOfWordsInDocument);
		ci.index("index");
		ci.loadMeaningsAndTokens();
		int total=0;
		System.out.println("&   \\# of meanings & \\# of types \\\\");
		System.out.println("\\hline  ");
		for(int i=2; i<22 ; i++){
			int cnt = ci.getNumberOfWordsWithMeaningCount(i);
			if(i>0)
				total+= cnt;
			System.out.println(" & "+i+" & "+cnt+"  \\\\");	
		}
		System.out.println("\\hline  ");
		System.out.println("& &Total ="+ total+"\\\\  ");
	}
	
	public static void getSizeOfDocOnVocab(String inputFile,int paragraphSize,int contextSize) throws Exception{
		CzechIndexer ci = new  CzechIndexer(inputFile, paragraphSize, contextSize);
		ci.index("index");
		ci.loadMeaningsAndTokens();
		System.err.println(getDocNum()+" & "+ci.getNumberOfAmbiguousWords()+" & "+ci.getNumberOfAmbiguousMeanings()
				+" & "+ci.typesTokens.keySet().size()+" & "+ci.getTotalTokenCount()+"\\\\");
	}
	public static void getSizeOfDocOnVocabAll(String inputFile) throws Exception{
		System.err.println("\\# of docs & \\# of amb.types & \\# of amb.tokens & \\# of types & \\# of tokens \\\\");
		System.err.println("\\hline  ");
		getSizeOfDocOnVocab(inputFile,5,-1);
		getSizeOfDocOnVocab(inputFile,3,-1);
		getSizeOfDocOnVocab(inputFile,1,-1);
		getSizeOfDocOnVocab(inputFile,1,3);
		getSizeOfDocOnVocab(inputFile,1,2);
		getSizeOfDocOnVocab(inputFile,1,1);
	}
	
	
	public static void main(String[] args) throws Exception, Exception {
		String inputFile = "pdt1_0//testDev";//"pdt1_0//train"
//		getSizeOfDocOnVocabAll(inputFile);
		
		getTypesTokens("index");
		
//		getLatexTableForMeaningsTypes();
		
//		CzechIndexer ci = new  CzechIndexer("", 5, -1);
//		ci.loadMeaningsAndTokens();
//		ci.printAllMeaningsToFile();
	}

}
