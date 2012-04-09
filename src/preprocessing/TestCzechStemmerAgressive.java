package preprocessing;

import org.junit.Test;

public class TestCzechStemmerAgressive {

	//grouped into similar ortographic groups, but the stems should be different 
	private String[] set1 = {"abchazskou","abchazská","abchazské","abchazského","abchazském","abchazskému",
			"abchazskı","abchazskıch","abchazskım","abchazskımi", //noun, adjective
			"","abchazy",//similar noun
			"","abcházie","abcházii","abcházií",//similar noun
			"","abcházové",//similar noun
			"","abcházskou","abcházská",// noun. stem should be the same as first group
			"","absolvent","absolventa","absolventek","absolventem","absolventi","absolventky","absolventskı",
			"absolventy",
			"","absolvoval","absolvovala","absolvovali","absolvovalo","absolvovanıch","absolvování",
			"absolvováním","absolvuje","absolvují" //verbs, adverb. stems in this group should be different
			,"","kontinuitypod-1","Slovan-2PrahaNákladùm","cizinìXXX-3`30VıhodyOhlasy","práci" //should not stem these 
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
