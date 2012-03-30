package util;

import java.util.ArrayList;

public class EvaluationEntry {

	ArrayList<Context> contexts;
	String meaning;
	
	public EvaluationEntry() {
		super();
		contexts = new ArrayList<Context>();
		meaning="";
	}
	public EvaluationEntry(ArrayList<Context> context, String meaning) {
		super();
		this.contexts = context;
		this.meaning = meaning;
	}
	public ArrayList<Context> getContext() {
		return contexts;
	}
	public void setContext(ArrayList<Context> context) {
		this.contexts = context;
	}
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	
}
