package preprocessing;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class WordFilter extends TokenFilter {

	TokenStream ts;
	TermAttribute termAtt; 
	PositionIncrementAttribute posIncrAtt ;
	
	public WordFilter(TokenStream input) {
		super(input);
		ts=input;
		termAtt = addAttribute(TermAttribute.class);
		posIncrAtt = addAttribute(PositionIncrementAttribute.class); 
	}

	@Override
	public boolean incrementToken() throws IOException {
		 int extraIncrement = 0; 
		 while (true) { 
			 boolean hasNext = ts.incrementToken(); 
             if(hasNext) { 
               if( !Character.isLetter(termAtt.term().charAt(0)) ) {  
            	   extraIncrement++; // filter this word 
                   continue; 
               }
               if(extraIncrement>0) { 
            	   posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement()+extraIncrement); 
               }     
             } 
        	 return hasNext; 
        }
	}		 
}
