package vectorModels;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import preprocessing.CzechIndexer;
import util.Arguments;
import util.FileUtil;

public class TestVectorSpaceMatrix {

	String inputPath = "test\\preprocessing\\pdt_cleaned_test_sample"; 
	String matrixFilePath = "test\\vectorModels\\matrix";
	String index= "test\\vectorModels\\index";
	
	VectorSpaceMatrix vectorSpaceMatrix = new VectorSpaceMatrix();
	CzechIndexer czechIndexer ;
	
	@Test
	public void writeVectorsToFile() throws Exception{
//		czechIndexer = new CzechIndexer(inputPath,1,-1);
//		czechIndexer.index(index);
//		czechIndexer.writeTermDocumentMatrixToFile(matrixFilePath);
		
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));
		TermEnum terms = indexReader.terms();
		FileUtil.writeTextToFile("", matrixFilePath+"", "", false);
		while(terms.next()){
	        String term = terms.term().text();
	        writeVectorToFile(vectorSpaceMatrix.getTermVectorPMI(term), term);
	        writeVectorToFile(vectorSpaceMatrix.getTFIDFTermVector(term), term);
		}
	}
	
	private void writeVectorToFile(double[]vector, String term) throws IOException{
		StringBuilder sb = new StringBuilder(term+":");
		for(double b:vector)sb.append(b+"\t");
		sb.append("\n");
		FileUtil.writeTextToFile(sb.toString(), matrixFilePath, "", true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
