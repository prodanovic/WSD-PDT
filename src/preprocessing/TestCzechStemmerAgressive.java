package preprocessing;

import org.junit.Test;

public class TestCzechStemmerAgressive {

	//grouped into similar ortographic groups, but the stems should be different 
	private String[] set1 = {"abchazskou","abchazsk�","abchazsk�","abchazsk�ho","abchazsk�m","abchazsk�mu",
			"abchazsk�","abchazsk�ch","abchazsk�m","abchazsk�mi", //noun, adjective
			"","abchazy",//similar noun
			"","abch�zie","abch�zii","abch�zi�",//similar noun
			"","abch�zov�",//similar noun
			"","abch�zskou","abch�zsk�",// noun. stem should be the same as first group
			"","absolvent","absolventa","absolventek","absolventem","absolventi","absolventky","absolventsk�",
			"absolventy",
			"","absolvoval","absolvovala","absolvovali","absolvovalo","absolvovan�ch","absolvov�n�",
			"absolvov�n�m","absolvuje","absolvuj�" //verbs, adverb. stems in this group should be different
			,"","kontinuitypod-1","Slovan-2PrahaN�klad�m","cizin�XXX-3`30V�hodyOhlasy","pr�ci" //should not stem these 
			};
	
	
	private CzechStemmerAgressive aggresive = new CzechStemmerAgressive();
	private CzechStemmerLight light = new CzechStemmerLight();
	
	@Test
	public void testSet1(){
		for(String input: set1){
//			System.out.println("A:"+input+"->"+aggresive.stem(input));
			System.out.println("L:"+input+"->"+light.stem(input));
		}
	}
	
	

}
