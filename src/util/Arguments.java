package util;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class Arguments {

	public static String logName="log";
	
//..preprocessing
	public  static String lowercase = "n";
	public  static String stopWordsRemoval = "n";
	public  static String stemming = "n";
	public  static String mergeLexicalVariants = "n";
	
//..document size in the matrix	
	public static String  inputFilePath="pdt1_0//pdt1_cleaned";
	public static Integer numberOfSentencesInLuceneDoc=1;
	public static Integer numberOfWordsInDocument=-1;
	
//..normalization and dimensionality reduction
	public static Integer matrixType = 0;
	public static enum MATRIX_TYPE {TFIDF,PMI,RI}; 
	
//..evaluation
	public static Integer upBoarderForNumberOfMeanings=6;
	public static Integer evaluationContextWindowSize=3;
	

	
	
	
	public static void parseArguments(String[]args) throws  SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		String[]arguments = args[0].split(" ");
		Field field=null;
		int i=0;
		for(String argument:arguments){
			if(i++==0)
				if(!argument.trim().startsWith("-")){
					printFieldsAndTheirTypes();
					throw new IllegalArgumentException();
				}
			if(argument.trim().startsWith("-")){
				String argName = argument.substring(1);
					try {
						field = Arguments.class.getField(argName);
					} catch (NoSuchFieldException e) {
						nonExistingField(argName);
						throw new NoSuchFieldException();
					}
			}
			else{
				try{
					Class type = field.getType();
					if(type.isInstance(new Integer(0))){
						field.set(field, Integer.parseInt(argument));
					}
					else 
						if(type.isInstance(new String(""))){
						field.set(field, argument);
					}
					field=null;
				}
				catch (NumberFormatException  e){
					System.out.println("Field "+argument+" expects a numeral.");
					throw new NumberFormatException ();
				}
				catch (NullPointerException e){
					System.out.println("There is no field:"+argument+".");
					throw new IllegalArgumentException();
				}
			}
		}
	}
	
	public static void nonExistingField(String argName){
		System.out.println("There is no field:"+argName+".");
		System.out.println("This is the list of valid arguments:");
		Field[] fields = Arguments.class.getFields();
		for(Field f:fields){
			System.out.print("-"+f.getName()+" ");
		}
		System.out.println();
		System.out.println("Arguments should be passed in a single string in the form:(-parameterName parameterValue)*");
		System.out.println();
	}
	
	public static void printFieldsAndTheirTypes(){
		System.out.println("List of fields and their expected input types:");
		Field[] fields = Arguments.class.getFields();
		for(Field f:fields){
			System.out.println("-"+f.getName()+": "+f.getType());
		}
		
//		System.out.println("logName:"+logName);
//		System.out.println("numberOfSentencesInLuceneDoc:"+numberOfSentencesInLuceneDoc);
//		System.out.println("upBoarderForNumberOfMeanings:"+upBoarderForNumberOfMeanings);
//		System.out.println("evaluationContextWindowSize:"+evaluationContextWindowSize);
//		System.out.println("MATRIX_TYPE.TFIDF:"+MATRIX_TYPE.TFIDF.ordinal());
//		System.out.println("MATRIX_TYPE.PMI:"+MATRIX_TYPE.PMI.ordinal());
	}
	
	public static String preprocessingName(){
		StringBuilder name=new StringBuilder();
		if(stemming.equalsIgnoreCase("y"))name.append("STEM"+"+");
		if(stopWordsRemoval.equalsIgnoreCase("y"))name.append("STOP"+"+");
		if(mergeLexicalVariants.equalsIgnoreCase("y"))name.append("MERGE"+"+");
		if(lowercase.equalsIgnoreCase("y"))name.append("LOWCASE"+"+");
		if(name.length()==0)name.append("NO+");
		String stringName = name.toString();
		return stringName.substring(0,stringName.length()-1);
	}
	
	public static String documentName(){
		String docName = Arguments.numberOfWordsInDocument==-1?
				Arguments.numberOfSentencesInLuceneDoc+"s":
					Arguments.numberOfWordsInDocument+"w";
		return docName;
	}
	
	public static String matrixName(){
		String matrix="";
		switch(matrixType){
			case 0: matrix = MATRIX_TYPE.TFIDF.name(); break;
			case 1: matrix = MATRIX_TYPE.PMI.name(); break;
			case 2: matrix = MATRIX_TYPE.RI.name(); break;
		}
		return matrix;
	}
	
	public static String initLogName(){
		logName = matrixName()+"_"+preprocessingName()+"_"+documentName()+"_"+
		evaluationContextWindowSize+"w_"+upBoarderForNumberOfMeanings+"m";
		return logName;
	}
	
	public static String modelNameForLog(){
		StringBuilder logText=new StringBuilder();
		logText.append("Model: "+matrixName());
		return logText.toString();
	}
	
	public static String preprocessingParamsForLog(){
		StringBuilder logText=new StringBuilder();
		logText.append("Preprocessing:"+preprocessingName()+"\n");
		logText.append("Size of document in the matrix:\n");
		logText.append("\t number Of Sentences In Document:"+numberOfSentencesInLuceneDoc+"\n");
		logText.append("\t number Of Words In Document:"+numberOfWordsInDocument);
		return logText.toString();
	}
	
	public static String evaluationParamsForLog(){
		StringBuilder logText=new StringBuilder();
		logText.append("Evaluation:\n");
		logText.append("\t Up Threshold For Number Of Meanings:"+upBoarderForNumberOfMeanings+"\n");
		logText.append("\t Evaluation Context Window Size:"+evaluationContextWindowSize+"\n");
		return logText.toString();
	}
	
	public static void main(String []varg){
//		String[] args ={"-logName log -matrixType 1 -evaluationContextWindowSize 4"}; 
//		Arguments.parseArguments(args);
//		printFieldsAndTheirValues();
		matrixType = 0;
		lowercase = "y";
//		System.out.println(	initLogName());
		System.out.println(	modelNameForLog());
	}
}
