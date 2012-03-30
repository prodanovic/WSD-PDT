package preprocessing;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCzechIndexer {

	String inputPath = "test\\preprocessing\\pdt_cleaned_test_sample";
	String matrixFilePath = "test\\preprocessing\\matrix";
	String docFilePath = "test\\preprocessing\\documents";
	
	CzechIndexer czechIndexer = new CzechIndexer(inputPath,1,1);
	
	
	@Test
	public void testGetContext(){
		String testSent = "and-4 this is a-1 test sentence-3 oh my god-1";
		ArrayList<String> fragments = czechIndexer.getFragments(testSent,3);
//		Assert.assertEquals(fragments.size(), 4);
//		Assert.assertEquals(fragments.get(0), "and-4 this is a-1");
//		Assert.assertEquals(fragments.get(1), "and-4 this is a-1 test sentence-3");
//		Assert.assertEquals(fragments.get(2), "is a-1 test sentence-3 oh my god-1");
//		Assert.assertEquals(fragments.get(3), "sentence-3 oh my god-1");
		
		for(String f:fragments)System.out.println(f);
	}
	
	@Test
	public void testIndex(){
		czechIndexer = new CzechIndexer(inputPath,1,-1);
		czechIndexer.index("index");
		czechIndexer.writeTermDocumentMatrixToFile(matrixFilePath);
		
	}
}
