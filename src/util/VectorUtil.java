package util;

public class VectorUtil {

	public static double[] createZeroVector(int d){
		double[] zero = new double[d];
		for(int i=0;i<zero.length;i++)zero[i]=0;
        return zero;
	}
	
	
	public static double[] add(double[]v1, double[]v2 ){
		if(v1.length!=v2.length)return null;
		for(int i=0; i<v1.length ; i++)v1[i]+=v2[i];
		return v1;		
	}
	
	public static double[] divide(double[]v1, int divisor){
		if(divisor<0) return null;
		
		for(int i=0; i<v1.length ; i++)v1[i]=v1[i]/divisor;
		
		return v1;
	}
	
	public static double dot(double[]v1, double[]v2 ){
		if(v1.length!=v2.length)return -10;
		double result=0;
		for(int i=0; i<v1.length ; i++)result+=v1[i]*v2[i];
		return result;		
	}
	public static double dot(float[]v1, float[]v2 ){
		if(v1.length!=v2.length)return -10;
		double result=0;
		for(int i=0; i<v1.length ; i++)result+=v1[i]*v2[i];
		return result;		
	}
	
	public static double norm(double[]v1){
		double result=0;
		for(int i=0; i<v1.length ; i++)result+=v1[i]*v1[i];
		return Math.sqrt(result);	
	}
	public static double norm(float[]v1){
		double result=0;
		for(int i=0; i<v1.length ; i++)result+=v1[i]*v1[i];
		return Math.sqrt(result);	
	}
	
	public static double cosineSim(double[]v1, double[]v2){
		return (dot(v1, v2)/(norm(v1)*norm(v2)));
	}
	
	public static double cosineSim(float[]v1, float[]v2){
		return (dot(v1, v2)/(norm(v1)*norm(v2)));
	}
	
	public static void print(double[] v){
		for (double e:v)System.out.print(e+" ");
		System.out.print("\n");
	}
	
	public static void main(String [] var){
		double[] v1 = {1,2,3};
		double[] v2 = {1,2,3};
		
//		add(v1, v2);
		print(v1);
		print(v2);
//		
//		divide(v1, 2);
//		print(v1);
		
		System.out.println("v1xv2="+dot(v1, v2));
		System.out.println("norm v1="+norm(v1));
		System.out.println("cosine(v1, v2)="+cosineSim(v1, v2));
		
		
	}
}
