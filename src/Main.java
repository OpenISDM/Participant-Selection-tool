import java.io.IOException;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Main {
	
	// args[0] pass in input.xls
	public static void main(String[] args) throws IOException, InterruptedException, BiffException, RowsExceededException, WriteException {
		
		// read in an excel file of PSP input
		PSP_Data pspData = new PSP_Data(args[0]);
		String inputSubstring = args[0].substring(0,args[0].length()-4);
		Heuristics heuristics = new Heuristics(pspData);
		// run PSP heuristic
		heuristics.PSP_G();
		
		
	
		//output psp results to a excel file
		
		
		
	}
	
	
}
