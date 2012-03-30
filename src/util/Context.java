package util;

import java.util.ArrayList;

public class Context {

	public ArrayList<String> previousContext = new ArrayList<String>();
	public ArrayList<String> nextContext = new ArrayList<String>();
	
	public Context(ArrayList<String> previousContext,
			ArrayList<String> nextContext) {
		super();
		this.previousContext = previousContext;
		this.nextContext = nextContext;
	}

	
	
}
