package preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import util.Arguments;
import util.FileUtil;

public class LinguisticPreprocessing {

	public static String inputFilePath = Arguments.inputFilePath;
	
	public static void mergeCzechTermVariants() throws IOException{
		mergeCzechTermVariants("pdt1_0/train_","pdt1_0/train_");
		mergeCzechTermVariants("pdt1_0/testDev_","pdt1_0/testDev_");
		mergeCzechTermVariants("pdt1_0/testFinal_","pdt1_0/testFinal_");
	}
	public static void mergeCzechTermVariants(String inPath,String outPath) throws IOException{
		String wholePDT1 = FileUtil.extractTextFromFile(new File(inPath), "Windows-1250");
		wholePDT1=wholePDT1.replaceAll("á", "a");		
		wholePDT1=wholePDT1.replaceAll("é", "e");
		wholePDT1=wholePDT1.replaceAll("í", "i");
		wholePDT1=wholePDT1.replaceAll("ý", "y");
		wholePDT1=wholePDT1.replaceAll("ú", "u");
		wholePDT1=wholePDT1.replaceAll("ù", "u");
		wholePDT1=wholePDT1.replaceAll("ò", "o");
		wholePDT1=wholePDT1.replaceAll("ó", "o");
		FileUtil.writeTextToFile(wholePDT1, new File(outPath), "Windows-1250", false);
	}
	
	public static void stemCzechTerms() throws IOException{
		stemCzechTerms("pdt1_0/train_","pdt1_0/train_");
		stemCzechTerms("pdt1_0/testDev_","pdt1_0/testDev_");
		stemCzechTerms("pdt1_0/testFinal_","pdt1_0/testFinal_");
	}
	public static void stemCzechTerms(String inPath,String outPath) throws IOException{
		CzechStemmerLight czechStemmerLight = new CzechStemmerLight();
		String wholePDT1 = FileUtil.extractTextFromFile(new File(inPath), "Windows-1250");
		String []sentences = wholePDT1.split("\n");
		String[]tokens;
		StringBuilder ssb = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		for(String sentence:sentences){
			ssb = new StringBuilder();
			tokens = sentence.split(" ");
			for(String token:tokens){
				if(!token.contains("-")){
					token = czechStemmerLight.stem(token);
				}
				sb.append(token+" ");
			}
			sb.append(ssb.toString().trim()+"\n");
		}
		FileUtil.writeTextToFile(sb.toString(), outPath, "Windows-1250", false);
	}
	
	//should be taken care of with WordFilter in WordSpaceAnalyzer
//	public static void deleteNonWords() throws IOException{
//		String wholePDT1 = FileUtil.extractTextFromFile(new File(inputFilePath), "Windows-1250");
//		String regex = "\\s[^\\w]+\\s";
//		String regex2 = "\\u0020\\u002E?[\\u0030-\\u0039]+\\u0020"; //\s.?\d+\s in Windows-1250 encoding 
//		String regex3 = "\\u0020?[\\u0030-\\u0039]*\\u002E[\\u0030-\\u0039]*\\u0020"; //\s?\d*\.\d*\s
//		wholePDT1 = wholePDT1.replaceAll(regex, " ");
//		wholePDT1 = wholePDT1.replaceAll(regex, " ");
//		wholePDT1 = wholePDT1.replaceAll(regex2, " ");
//		wholePDT1 = wholePDT1.replaceAll(regex3, " ");
//		wholePDT1 = wholePDT1.replaceFirst(" ", "");
//		FileUtil.writeTextToFile(wholePDT1, inputFilePath+"_", "Windows-1250", false);
//	}
	public static void removeNonWords() throws IOException{
		removeNonWords("pdt1_0/train_","pdt1_0/train_");
		removeNonWords("pdt1_0/testDev_","pdt1_0/testDev_");
		removeNonWords("pdt1_0/testFinal_","pdt1_0/testFinal_");
	}
	public static void removeNonWords(String inPath,String outPath) throws IOException{
		String wholePDT1 = FileUtil.extractTextFromFile(new File(inPath), "Windows-1250");
		wholePDT1=wholePDT1.replaceAll("\\s+\\d+\\.?\\d*", " "); // matches 20.30 ,  20
		wholePDT1=wholePDT1.replaceAll("\\s[^\\w^\\n]+\\s", " "); // greedy punctuation match
		FileUtil.writeTextToFile(wholePDT1, new File(outPath), "Windows-1250", false);
	}
	
	public static void lowercase() throws IOException{
		lowercase("pdt1_0/train_","pdt1_0/train_");
		lowercase("pdt1_0/testDev_","pdt1_0/testDev_");
		lowercase("pdt1_0/testFinal_","pdt1_0/testFinal_");
	}
	public static void lowercase(String inPath,String outPath) throws IOException{
		String wholePDT1 = FileUtil.extractTextFromFile(new File(inPath), "Windows-1250");
		String out =wholePDT1.toLowerCase(); // matches 20.30 ,  20
		FileUtil.writeTextToFile(out, new File(outPath), "Windows-1250", false);
	}
	
	public static void stopWordRemoval() throws IOException{
		stopWordRemoval("pdt1_0/train_","pdt1_0/train_");
		stopWordRemoval("pdt1_0/testDev_","pdt1_0/testDev_");
		stopWordRemoval("pdt1_0/testFinal_","pdt1_0/testFinal_");
	}
	public static void stopWordRemoval(String inPath,String outPath) throws IOException{
		String wholePDT1 = FileUtil.extractTextFromFile(new File(inPath), "Windows-1250");
		String[] stopWords =WordSpaceAnalyzer.CZECH_STOP_WORDS;
		for(String stopWord:stopWords){
			wholePDT1 =wholePDT1.replaceAll("\\s"+stopWord+"\\s", " ");
		}
		FileUtil.writeTextToFile(wholePDT1, new File(outPath), "Windows-1250", false);
	}
	
	
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		FileUtil.copyFile("pdt1_0/train", "pdt1_0/train_");
		System.out.println("copy Time[s]: "+(System.currentTimeMillis()-start)/1000);
		stopWordRemoval("pdt1_0/train_", "pdt1_0/_train_");
//		lowercase("pdt1_0/pdt1_preprocessed", "pdt1_0/pdt1_preprocessed");
//		stopWordRemoval("pdt1_0/pdt1_preprocessed", "pdt1_0/pdt1_preprocessed");
//		mergeCzechTermVariants("pdt1_0/pdt1_preprocessed", "pdt1_0/pdt1_preprocessed");
//		stemCzechTerms("pdt1_0/pdt1_preprocessed", "pdt1_0/pdt1_preprocessed");
		System.out.println("Time[s]: "+(System.currentTimeMillis()-start)/1000);
		
	}

}
