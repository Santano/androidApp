package resource;

import android.util.SparseArray;

public class Constants { 
	public static int rows = 9;
	public static int cols = 8;
	public static int lightCream = 0xfffaf8ef;
	public static int lightBrown = 0xff8f7a66;
	
	public static SparseArray<String> symbolArray = new SparseArray<String>();	
	static {
		symbolArray.append(0, "+");
		symbolArray.append(1, "1");
		symbolArray.append(2, "2");
		symbolArray.append(3, "3");
		symbolArray.append(4, "4");
		symbolArray.append(5, "5");
		symbolArray.append(6, "6");
		symbolArray.append(7, "7");
		symbolArray.append(8, "8");
		symbolArray.append(9, "9");
		symbolArray.append(10, "+");
		symbolArray.append(11, "-");
		symbolArray.append(12, "X");
		symbolArray.append(13, "/");
	}
}
