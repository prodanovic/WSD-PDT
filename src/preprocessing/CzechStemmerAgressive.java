package preprocessing;

/**
 * @author Ljiljana Dolamic  University of Neuchatel
 * -removes case endings form nouns and adjectives, possesive adj. endings from names,
 *  diminutive, augmentative, comparative sufixes and derivational sufixes from nouns, 
 *  takes care of palatalisation 
 */
public class CzechStemmerAgressive {
	/**
	 * A buffer of the current word being stemmed
	 */
	private StringBuffer sb=new StringBuffer();

	/**
	 * Default constructor
	 */
public CzechStemmerAgressive(){} // constructor
    
	public String stem(String input){
		//
		input=input.toLowerCase();
		//reset string buffer
		sb.delete(0,sb.length());
		sb.insert(0,input);
		// stemming...
		//removes case endings from nouns and adjectives
		removeCase(sb);
                //removes possesive endings from names -ov- and -in-
		removePossessives(sb);
		//removes comparative endings
		removeComparative(sb);
		//removes diminutive endings
		removeDiminutive(sb);
		//removes augmentatives endings
		removeAugmentative(sb);
		//removes derivational sufixes from nouns
		removeDerivational(sb);
		
		return sb.toString();
	}
	private void removeDerivational(StringBuffer buffer) {
		int len=buffer.length();
		// 
		if( (len > 8 )&&
			buffer.substring( len-6 ,len).equals("obinec")){
						
			buffer.delete( len- 6 , len);
			return;
		}//len >8
		if(len > 7){
			if(buffer.substring( len-5 ,len).equals("ion\u00e1\u0159")){ // -ion�r 
			
			buffer.delete( len- 4 , len);
			palatalise(buffer);
			return;
			}
			if(buffer.substring( len-5 ,len).equals("ovisk")||  
					buffer.substring( len-5 ,len).equals("ovstv")||
					buffer.substring( len-5 ,len).equals("ovi\u0161t")||  //-ovi�t
					buffer.substring( len-5 ,len).equals("ovn\u00edk")){ //-ovn�k
					
					buffer.delete( len- 5 , len);
					return;
				}
		}//len>7
		if(len > 6){
			if(	buffer.substring( len-4 ,len).equals("\u00e1sek")|| // -�sek 
				buffer.substring( len-4 ,len).equals("loun")||
				buffer.substring( len-4 ,len).equals("nost")||
				buffer.substring( len-4 ,len).equals("teln")||
				buffer.substring( len-4 ,len).equals("ovec")||
				buffer.substring( len-5 ,len).equals("ov\u00edk")|| //-ov�k
				buffer.substring( len-4 ,len).equals("ovtv")||
				buffer.substring( len-4 ,len).equals("ovin")||
				buffer.substring( len-4 ,len).equals("\u0161tin")){ //-�tin
				
				buffer.delete( len- 4 , len);
				return;
			}
			if(buffer.substring( len-4 ,len).equals("enic")||
					buffer.substring( len-4 ,len).equals("inec")||
				    buffer.substring( len-4 ,len).equals("itel")){ 
						
				    buffer.delete( len- 3 , len);
				    palatalise(buffer);
				    return;
			}
		}//len>6
		if(len > 5){
			if(buffer.substring( len-3 ,len).equals("\u00e1rn")){ //-�rn
						
				buffer.delete( len- 3 , len);
				return;
			}
			if(buffer.substring( len-3 ,len).equals("\u011bnk")){ //-enk
						
				    buffer.delete( len- 2 , len);
				    palatalise(buffer);
				    return;
			}
			if(buffer.substring( len-3 ,len).equals("i\u00e1n")|| //-i�n
				    buffer.substring( len-3 ,len).equals("ist")||
				    buffer.substring( len-3 ,len).equals("isk")|| 
				    buffer.substring( len-3 ,len).equals("i\u0161t")|| //-i�t
				    buffer.substring( len-3 ,len).equals("itb")|| 
				    buffer.substring( len-3 ,len).equals("\u00edrn")){  //-�rn
						
				    buffer.delete( len- 2 , len);
				    palatalise(buffer);
				    return;
			}
			if(buffer.substring( len-3 ,len).equals("och")|| 
				    buffer.substring( len-3 ,len).equals("ost")||
				    buffer.substring( len-3 ,len).equals("ovn")||
				    buffer.substring( len-3 ,len).equals("oun")|| 
				    buffer.substring( len-3 ,len).equals("out")|| 
				    buffer.substring( len-3 ,len).equals("ou\u0161")){  //-ou�
						
				    buffer.delete( len- 3 , len);
				    return;
			}
			if(buffer.substring( len-3 ,len).equals("u\u0161k")){ //-u�k
						
				    buffer.delete( len- 3 , len);
				    return;
			}
			if(buffer.substring( len-3 ,len).equals("kyn")|| 
					buffer.substring( len-3 ,len).equals("\u010dan")||    //-can
				    buffer.substring( len-3 ,len).equals("k\u00e1\u0159")|| //k�r
				    buffer.substring( len-3 ,len).equals("n\u00e9\u0159")|| //n�r
				    buffer.substring( len-3 ,len).equals("n\u00edk")||      //-n�k
				    buffer.substring( len-3 ,len).equals("ctv")|| 
				    buffer.substring( len-3 ,len).equals("stv")){  
						
				    buffer.delete( len- 3 , len);
				    return;
			}
		}//len>5
		 if(len > 4){
			if(buffer.substring( len-2 ,len).equals("\u00e1\u010d")|| // -�c
				buffer.substring( len-2 ,len).equals("a\u010d")||      //-ac
				buffer.substring( len-2 ,len).equals("\u00e1n")||      //-�n
			        buffer.substring( len-2 ,len).equals("an")|| 
			        buffer.substring( len-2 ,len).equals("\u00e1\u0159")|| //-�r
			        buffer.substring( len-2 ,len).equals("as")){ 
						
				buffer.delete( len- 2 , len);
				return;
			}
			if(buffer.substring( len-2 ,len).equals("ec")|| 
				    buffer.substring( len-2 ,len).equals("en")|| 
				    buffer.substring( len-2 ,len).equals("\u011bn")||   //-en
				    buffer.substring( len-2 ,len).equals("\u00e9\u0159")){  //-�r
						
				    buffer.delete( len-1 , len);
				    palatalise(buffer);
				    return;
			}
			if(buffer.substring( len-2 ,len).equals("\u00ed\u0159")|| //-�r
				    buffer.substring( len-2 ,len).equals("ic")||
				    buffer.substring( len-2 ,len).equals("in")||
				    buffer.substring( len-2 ,len).equals("\u00edn")||  //-�n
				    buffer.substring( len-2 ,len).equals("it")||
				    buffer.substring( len-2 ,len).equals("iv")){  
						
				    buffer.delete( len- 1 , len);
				    palatalise(buffer);
				    return;
			}
			
			if(buffer.substring( len-2 ,len).equals("ob")|| 
				    buffer.substring( len-2 ,len).equals("ot")||
				    buffer.substring( len-2 ,len).equals("ov")|| 
				    buffer.substring( len-2 ,len).equals("o\u0148")){ //-on 
						
				    buffer.delete( len- 2 , len);
				    return;
			}
			if(buffer.substring( len-2 ,len).equals("ul")){ 
				
			        buffer.delete( len- 2 , len);
			        return;
		    }
			if(buffer.substring( len-2 ,len).equals("yn")){ 
				
		        buffer.delete( len- 2 , len);
		        return;
	        }
			if(buffer.substring( len-2 ,len).equals("\u010dk")||              //-ck
				    buffer.substring( len-2 ,len).equals("\u010dn")||  //-cn
				    buffer.substring( len-2 ,len).equals("dl")|| 
				    buffer.substring( len-2 ,len).equals("nk")|| 
				    buffer.substring( len-2 ,len).equals("tv")|| 
				    buffer.substring( len-2 ,len).equals("tk")||
				    buffer.substring( len-2 ,len).equals("vk")){  
						
				    buffer.delete( len-2 , len);
				    return;
			}
		}//len>4
		 if(len > 3){
				if(buffer.charAt(buffer.length()-1)=='c'||
				   buffer.charAt(buffer.length()-1)=='\u010d'|| //-c
				   buffer.charAt(buffer.length()-1)=='k'||
				   buffer.charAt(buffer.length()-1)=='l'||
				   buffer.charAt(buffer.length()-1)=='n'||
				   buffer.charAt(buffer.length()-1)=='t'){
					
					buffer.delete( len-1 , len);	
				}
			}//len>3	
				
	}//removeDerivational

	private void removeAugmentative(StringBuffer buffer) {
		int len=buffer.length();
		//
		if( (len> 6 )&&
			 buffer.substring( len- 4 ,len).equals("ajzn")){
					
			 buffer.delete( len- 4 , len);
			 return;
			 }
		 if( (len> 5 )&&
			 (buffer.substring( len- 3 ,len).equals("izn")||	
			  buffer.substring( len- 3 ,len).equals("isk"))){
				 	
			  buffer.delete( len- 2 , len);
			  palatalise(buffer);
			  return;
		  }
		if( (len> 4 )&&
			 buffer.substring( len- 2 ,len).equals("\00e1k")){ //-�k
						
			 buffer.delete( len- 2 , len);
			 return;
		 }
		
	}

	private void removeDiminutive(StringBuffer buffer) {
		int len=buffer.length();
		// 
		if( (len> 7 )&&
			 buffer.substring( len- 5 ,len).equals("ou\u0161ek")){  //-ou�ek
				
			 buffer.delete( len- 5 , len);
			 return;
			 }
		if( len> 6){
			 if(buffer.substring( len-4,len).equals("e\u010dek")||      //-ecek
			    buffer.substring( len-4,len).equals("\u00e9\u010dek")||    //-�cek
			    buffer.substring( len-4,len).equals("i\u010dek")||         //-icek
			    buffer.substring( len-4,len).equals("\u00ed\u010dek")||    //�cek
			    buffer.substring( len-4,len).equals("enek")||
			    buffer.substring( len-4,len).equals("\u00e9nek")||      //-�nek
			    buffer.substring( len-4,len).equals("inek")||
			    buffer.substring( len-4,len).equals("\u00ednek")){      //-�nek
			
		                buffer.delete( len- 3 , len);
			    palatalise(buffer);
			    return;
		             }
		            if( buffer.substring( len-4,len).equals("\u00e1\u010dek")|| //�cek
		                 buffer.substring( len-4,len).equals("a\u010dek")||   //acek
		        	     buffer.substring( len-4,len).equals("o\u010dek")||   //ocek
		        	     buffer.substring( len-4,len).equals("u\u010dek")||   //ucek
		        	     buffer.substring( len-4,len).equals("anek")||
		        	     buffer.substring( len-4,len).equals("onek")||
		        	     buffer.substring( len-4,len).equals("unek")||
			     buffer.substring( len-4,len).equals("\u00e1nek")){   //-�nek
			
			     buffer.delete( len- 4 , len);
			     return;
			 }
		}//len>6
		if( len> 5){
			    if(buffer.substring( len-3,len).equals("e\u010dk")||   //-eck
			       buffer.substring( len-3,len).equals("\u00e9\u010dk")||  //-�ck 
			       buffer.substring( len-3,len).equals("i\u010dk")||   //-ick
			       buffer.substring( len-3,len).equals("\u00ed\u010dk")||    //-�ck
			       buffer.substring( len-3,len).equals("enk")||   //-enk
			       buffer.substring( len-3,len).equals("\u00e9nk")||  //-�nk 
			       buffer.substring( len-3,len).equals("ink")||   //-ink
			       buffer.substring( len-3,len).equals("\u00ednk")){   //-�nk
			
			       buffer.delete( len- 3 , len);
			       palatalise(buffer);
			       return;
			     }
			    if(buffer.substring( len-3,len).equals("\u00e1\u010dk")||  //-�ck
			        buffer.substring( len-3,len).equals("au010dk")|| //-ack
			        buffer.substring( len-3,len).equals("o\u010dk")||  //-ock
			        buffer.substring( len-3,len).equals("u\u010dk")||   //-uck 
			        buffer.substring( len-3,len).equals("ank")||
			        buffer.substring( len-3,len).equals("onk")||
			        buffer.substring( len-3,len).equals("unk")){   
					
			        buffer.delete( len- 3 , len);
			        return;
					       
		                }
		                if(buffer.substring( len-3,len).equals("\u00e1tk")|| //-�tk
		        	       buffer.substring( len-3,len).equals("\u00e1nk")||  //-�nk
			       buffer.substring( len-3,len).equals("u\u0161k")){   //-u�k
			
			       buffer.delete( len- 3 , len);
			        return;
			    }
		}//len>5
		if( len> 4){
			  if(buffer.substring( len-2,len).equals("ek")||
			     buffer.substring( len-2,len).equals("\u00e9k")||  //-�k
			     buffer.substring( len-2,len).equals("\u00edk")||  //-�k
			     buffer.substring( len-2,len).equals("ik")){   
			
			      buffer.delete( len- 1 , len);
			      palatalise(buffer);
			      return;
			      }
			    if(buffer.substring( len-2,len).equals("\u00e1k")||  //-�k
			        buffer.substring( len-2,len).equals("ak")||
			        buffer.substring( len-2,len).equals("ok")||
			        buffer.substring( len-2,len).equals("uk")){   
					
			        buffer.delete( len- 1 , len);
			        return;
			 }
		}
		if( (len> 3 )&&
			 buffer.substring( len- 1 ,len).equals("k")){  
			
			 buffer.delete( len- 1, len);
			 return;
			 }
	}//removeDiminutives

	private void removeComparative(StringBuffer buffer) {
		int len=buffer.length();
		// 
		if( (len> 5)&&
			(buffer.substring( len-3,len).equals("ej\u0161")||  //-ej�
			 buffer.substring( len-3,len).equals("\u011bj\u0161"))){   //-ej�
				
			 buffer.delete( len- 2 , len);
			 palatalise(buffer);
			 return;
			 }
		
	}

	private void palatalise(StringBuffer buffer){
		int len=buffer.length();
		
		if( buffer.substring( len- 2 ,len).equals("ci")||
		     buffer.substring( len- 2 ,len).equals("ce")||		
		     buffer.substring( len- 2 ,len).equals("\u010di")||      //-ci
		     buffer.substring( len- 2 ,len).equals("\u010de")){   //-ce
				
		     buffer.replace(len- 2 ,len, "k");
		     return;
		}
		if( buffer.substring( len- 2 ,len).equals("zi")||
		     buffer.substring( len- 2 ,len).equals("ze")||		
		     buffer.substring( len- 2 ,len).equals("\u017ei")||    //-�i
		     buffer.substring( len- 2 ,len).equals("\u017ee")){  //-�e
					
		     buffer.replace(len- 2 ,len, "h");
		     return;
		}
		if( buffer.substring( len- 3 ,len).equals("\u010dt\u011b")||     //-cte
		     buffer.substring( len- 3 ,len).equals("\u010dti")||   //-cti
		     buffer.substring( len- 3 ,len).equals("\u010dt\u00ed")){   //-ct�
						
		     buffer.replace(len- 3 ,len, "ck");
		     return;
		}
		if( buffer.substring( len- 2 ,len).equals("\u0161t\u011b")||   //-�te
		    buffer.substring( len- 2 ,len).equals("\u0161ti")||   //-�ti
		     buffer.substring( len- 2 ,len).equals("\u0161t\u00ed")){  //-�t�
						
		     buffer.replace(len- 2 ,len, "sk");
		     return;
		}
		buffer.delete( len- 1 , len);
		return;
	}//palatalise
	
	private void removePossessives(StringBuffer buffer) {
		int len=buffer.length();
		
		if( len> 5 ){
			if( buffer.substring( len- 2 ,len).equals("ov")){
				
			    buffer.delete( len- 2 , len);
			    return;
			 }
			if(buffer.substring( len-2,len).equals("\u016fv")){ //-uv
			 	
		                buffer.delete( len- 2 , len);
		                return;
			}
	                       if( buffer.substring( len- 2 ,len).equals("in")){
			 	
			    buffer.delete( len- 1 , len);
			    palatalise(buffer);
			    return;
		            }
		}
	}//removePossessives
	
	private void removeCase(StringBuffer buffer) {
		int len=buffer.length();
		// 
		if( (len> 7 )&&
			 buffer.substring( len- 5 ,len).equals("atech")){
			
			 buffer.delete( len- 5 , len);
			 return;
		}//len>7
		if( len> 6 ){
		      if(buffer.substring( len- 4 ,len).equals("\u011btem")){   //-etem
		
		         buffer.delete( len- 3 , len);
		         palatalise(buffer);
		         return;
		      }
		       if(buffer.substring( len- 4 ,len).equals("at\u016fm")){  //-atum
		    	      buffer.delete( len- 4 , len);
		    	      return;
		      }
		      
	   }
		if( len> 5 ){
			      if(buffer.substring( len-3,len).equals("ech")|| 
			            buffer.substring( len-3,len).equals("ich")|| 
				buffer.substring( len-3,len).equals("\u00edch")){ //-�ch
				
				  buffer.delete( len-2 , len);
				  palatalise(buffer);
				  return;
				}
		                        if(buffer.substring( len-3,len).equals("\u00e9ho")|| //-�ho
				    buffer.substring( len-3,len).equals("\u011bmi")||  //-emu
				    buffer.substring( len-3,len).equals("emi")||
				    buffer.substring( len-3,len).equals("\u00e9mu")||  // -�mu				                                                                buffer.substring( len-3,len).equals("ete")||
				    buffer.substring( len-3,len).equals("eti")||
				    buffer.substring( len-3,len).equals("iho")||
				    buffer.substring( len-3,len).equals("\u00edho")||  //-�ho
				    buffer.substring( len-3,len).equals("\u00edmi")||  //-�mi
				    buffer.substring( len-3,len).equals("imu")){
				
				    buffer.delete( len- 2 , len);
				    palatalise(buffer);
				    return;
			            }
		                        if( buffer.substring( len-3,len).equals("\u00e1ch")|| //-�ch
				     buffer.substring( len-3,len).equals("ata")||
				     buffer.substring( len-3,len).equals("aty")||
				     buffer.substring( len-3,len).equals("\u00fdch")||   //-�ch
				     buffer.substring( len-3,len).equals("ama")||
				     buffer.substring( len-3,len).equals("ami")||
				     buffer.substring( len-3,len).equals("ov\u00e9")||   //-ov�
				     buffer.substring( len-3,len).equals("ovi")||
				     buffer.substring( len-3,len).equals("\u00fdmi")){  //-�mi
				
                                                     buffer.delete( len- 3 , len);
		                             return;
			               }
		}  
		if( len> 4){
			 if(buffer.substring( len-2,len).equals("em")){
			
			         buffer.delete( len- 1 , len);
			         palatalise(buffer);
			         return;
			         
			      }
		                 if( buffer.substring( len-2,len).equals("es")|| 
			          buffer.substring( len-2,len).equals("\u00e9m")||    //-�m
			          buffer.substring( len-2,len).equals("\u00edm")){   //-�m
			
			              buffer.delete( len- 2 , len);
			              palatalise(buffer);
			              return;
			      }
		                if( buffer.substring( len-2,len).equals("\u016fm")){
			
			          buffer.delete( len- 2 , len);
			          return;
			      }
		               if( buffer.substring( len-2,len).equals("at")|| 
			        buffer.substring( len-2,len).equals("\u00e1m")||    //-�m
			        buffer.substring( len-2,len).equals("os")||
			        buffer.substring( len-2,len).equals("us")||   
			        buffer.substring( len-2,len).equals("\u00fdm")||     //-�m
			        buffer.substring( len-2,len).equals("mi")||   
			        buffer.substring( len-2,len).equals("ou")){
				
			        buffer.delete( len- 2 , len);
			        return;
			 }
		}//len>4
		if( len> 3){
			 if(buffer.substring( len-1,len).equals("e")||
			    buffer.substring( len-1,len).equals("i")){
			
			     palatalise(buffer);
			     return;
			}
		            if(buffer.substring( len-1,len).equals("\u00ed")||    //-�
			    buffer.substring( len-1,len).equals("\u011b")){   //-e
				
			     palatalise(buffer);
			     return;
			 }
		            if( buffer.substring( len-1,len).equals("u")||
			     buffer.substring( len-1,len).equals("y")||
			     buffer.substring( len-1,len).equals("\u016f")){   //-u
					
			     buffer.delete( len- 1 , len);
			     return;
			 }
		          if( buffer.substring( len-1,len).equals("a")||
		        	  buffer.substring( len-1,len).equals("o")||
			  buffer.substring( len-1,len).equals("\u00e1")||  // -�
			  buffer.substring( len-1,len).equals("\u00e9")||  //-�
			  buffer.substring( len-1,len).equals("\u00fd")){   //-�
				
			buffer.delete( len- 1 , len);
			 return;
		          }
		}//len>3
	}
	



}
