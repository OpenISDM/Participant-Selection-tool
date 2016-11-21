import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class PSP_Data {

	public String ProjectID;
	public int PARTICIPANTS;
	public int REGIONS;
	public float BUDGET;
	
	
	// first index is the index of participant
	// second index [0] is the id of the participant
	// 				[1] is the name of the participant
	// this serve as a look-up table to match participant index, name and id.
	public String[][] participants = null;
	// the region value array
	public int[] regionValues = null;
	// the region name array
	public String[] regionNames = null;
	// the benefit array, bi,k
	public int[][] benefits = null;
	// the cost array
	public int[][] costs = null;
	// the objective value
	public int objective;
	// number of selected participants
	public int selectedParticipant;
	// the total cost
	public int totalCost;
	// assignment indicating which participant is assigned to which region
	public int[] assignment = null; 
	// num in each region
	public int[] NumOfParticipantsInRegions = null;
	// keep track of the assignment
	// first index region
	// second index are the participants index
	// ex: assignmentDouble[1][2] means the id of
	//     the second participant assigned to region 1
	public int[][] assignmentDouble = null;
	
	//CP queue
	public B2C b2c [] = null;
	public Queue <B2C> queue = new LinkedList<B2C>();
	
	// constructor / parser
	public PSP_Data(String input)
	{
		try {
			Workbook w = Workbook.getWorkbook(new File(input));
			System.out.println("input file is "+input);
			// parse data from project sheet
			Sheet project = w.getSheet("project");
			// get project ID
			ProjectID = String.valueOf(project.getCell(1, 0).getContents());
			// get number of participants
			PARTICIPANTS = Integer.valueOf(project.getCell(1,1).getContents());
			REGIONS = Integer.valueOf(project.getCell(1,2).getContents());
			BUDGET = Float.valueOf(project.getCell(1,3).getContents());
			//initialize arrays
			participants = new String[PARTICIPANTS+1][2];
			regionNames = new String[REGIONS+1];
			regionValues = new int[REGIONS+1];
			benefits = new int[PARTICIPANTS+1][REGIONS+1];
			costs = new int[PARTICIPANTS+1][REGIONS+1];
			b2c = new B2C[PARTICIPANTS*REGIONS];
			assignment = new int[PARTICIPANTS+1];
			NumOfParticipantsInRegions = new int[REGIONS+1];
			assignmentDouble = new int[REGIONS+1][PARTICIPANTS+1];
			
			for(int i=4 ; i<PARTICIPANTS ; i++)
			{
				// participant id
				participants[i-3][0] = String.valueOf(project.getCell(1, i).getContents());;
				// participant name
				participants[i-3][1] = String.valueOf(project.getCell(2, i).getContents());;
				System.out.println(" "+participants[i-3][1]);
			}
			
			// parse region values and name from sheet values
			Sheet values = w.getSheet("values");
			for(int i=0 ; i<REGIONS ; i++)
			{
				// set region name
				regionNames[i+1] = String.valueOf(values.getCell(1, i).getContents());
				// set region value
				regionValues[i+1] = Integer.valueOf(values.getCell(2,i).getContents());
			}
			
			// parse the benefit for each participant i to visit region k from sheet benefits 
			// read from the sheet benefits in excel file 
			Sheet benefitsSheet = w.getSheet("benefits");
			
			// read the benefit value if participant i visit region j 
			for(int i=1 ; i<=PARTICIPANTS ; i++)
			{
				for(int j=1 ; j<=REGIONS; j++)
				{
					benefits[i][j] = Integer.valueOf(benefitsSheet.getCell(j,i).getContents());
				}
				
			}
			
			// read from the sheet costs in excel file 
			Sheet costsSheet = w.getSheet("costs");
			// read the costs value if participant i visit region j
			for(int i=1 ; i<=PARTICIPANTS ; i++)
			{
				for(int j=1 ; j<=REGIONS; j++)
				{
					costs[i][j] = Integer.valueOf(costsSheet.getCell(j,i).getContents());
				}
			}
			
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// output TPP input with PSP information
	// this may be needed in the future
	/*
	public void GenerateTPPInput(String input) throws IOException, RowsExceededException, WriteException
	{
		WritableWorkbook TPPInput;
		TPPInput = Workbook.createWorkbook(new File(input));
		WritableSheet project = TPPInput.createSheet("project", 0);
		WritableSheet[] rSheets = new WritableSheet[REGIONS+1];
		for(int i=1 ; i<= REGIONS ; i++)
		{
			rSheets[i] = TPPInput.createSheet("R"+i, i);
		}
		// write to project sheet
		project.addCell(new Label(0,1,"Project ID"));
		project.addCell(new Label(0,2,"Number of Regions"));
		project.addCell(new Label(0,3,"Number of Nodes"));
		project.addCell(new Label(1,0,"Please Fill in this column according to time"));
		project.addCell(new Label(1,1,"ID (String)"));
		for(int i=0 ; i<2*REGIONS+3 ; i++)
		{
			project.addCell(new Label(1,2+i,"integer"));
		}
		
		// num of node section
		for(int i=1 ; i<=REGIONS ; i++)
		{
			project.addCell(new Label(0,3+i,"Number of Nodes in region "+i));
			project.addCell(new Label(0,2+REGIONS+1+i,"Number of participants in region "+i));
		}
		project.addCell(new Label(0,2+REGIONS+1,"Number of participants"));
		project.addCell(new Label(0,2+2*REGIONS+2,"How many participants visits a node"));
		
		// write to region sheets
		for(int i=1 ; i<=REGIONS ; i++)
		{
			rSheets[i] = TPPInput.createSheet("R"+i, i);
			for(int j=1 ; j<=NumOfParticipantsInRegions[i] ; j++)
			{
				int whichParticipant = assignmentDouble[i][j];
				rSheets[i].addCell(new Label(0,j-1,"Participant "+j));
				rSheets[i].addCell(new Label(1,j-1,participants[whichParticipant][1]));
				rSheets[i].addCell(new Label(2,j-1,"x"));
				rSheets[i].addCell(new Label(3,j-1,"y"));
			}
			int magicNum = 6;
			for(int j=1 ; j<=magicNum ; j++)
			{
				if(j<3)
					rSheets[i].addCell(new Label(0,j-1+NumOfParticipantsInRegions[i],"Node "+j));
				else if(j!=magicNum)
					rSheets[i].addCell(new Label(0,j-1+NumOfParticipantsInRegions[i],"."));
				else
					rSheets[i].addCell(new Label(0,j-1+NumOfParticipantsInRegions[i],"Node n"));
				rSheets[i].addCell(new Label(1,j-1+NumOfParticipantsInRegions[i],"x"));
				rSheets[i].addCell(new Label(2,j-1+NumOfParticipantsInRegions[i],"y"));
			}
		}
		TPPInput.write();
		TPPInput.close();
	}
	*/
	// outputing files for neos
	// @param : formerSheetName : name for former style sheet of gougou
	// @param : gdxName : name for the gdx file 
	// @param : gamsName : name for gams
	public void prepareNeosInputs (String formerSheetName, String gdxName, String gamsName) throws RowsExceededException, WriteException, IOException
	{
		// output gou gou sheet
		OutputFormer(formerSheetName);
		// output 
		OutputGdx(formerSheetName , gdxName);
		//OutputPracticalGams(gamsName);
	}
	// print out former style sheet gougou made
	public void OutputFormer(String name) throws IOException, RowsExceededException, WriteException
	{
		  WritableWorkbook wworkbook;
	      wworkbook = Workbook.createWorkbook(new File(name));
	      WritableSheet sheet1 = wworkbook.createSheet("sheet1", 0);
	      WritableSheet values = wworkbook.createSheet("values", 1);
	      WritableSheet benefitsOutputSheet = wworkbook.createSheet("benefits", 2);
	      WritableSheet costsOutputSheet = wworkbook.createSheet("costs", 3);

	      // values for each region
	      Number[] regions = new Number[REGIONS+1];
	      for(int i=1 ; i<=REGIONS ; i++)
	      {
	    	  regions[i] = new Number(i-1,1,regionValues[i]);
	    	  values.addCell(regions[i]);
	      }
	      
	      // b(i,k) values
	      Number[][] bfit = new Number[PARTICIPANTS+1][REGIONS+1];
	      for(int i=1 ; i<=PARTICIPANTS ; i++)
	      {
	    	  for(int j=1 ; j<=REGIONS; j++)
	    	  {
	    		  bfit[i][j] = new Number(j,i,benefits[i][j]);
	    		  benefitsOutputSheet.addCell(bfit[i][j]);
	    	  }
	      }
	      // c(i,k) values
	      Number[][] cst = new Number[PARTICIPANTS+1][REGIONS+1];
	      for(int i=1 ; i<=PARTICIPANTS ; i++)
	      {
	    	  for(int j=1 ; j<=REGIONS; j++)
	    	  {
	    		  cst[i][j] = new Number(j,i,costs[i][j]);
	    		  costsOutputSheet.addCell(cst[i][j]);
	    	  }
	      }
	      wworkbook.write();
	      wworkbook.close();
	      
	      
	}
	
	
	// generate gdx
	public void OutputGdx(String formerSheetName, String output)
	{
		//Generate.main(formerSheetName, output);
	}
	// generate gams
	public void OutputPracticalGams(String gamsName) throws IOException
	{
		
		FileWriter oF = new FileWriter(gamsName,false);
		BufferedWriter o = new BufferedWriter(oF);
		o.write("$GDXIN in.gdx\n");
		o.write("Sets\n");
		o.write("         agent           The number of agents            ");
		o.write("/Participant1*Participant"+PARTICIPANTS+"/\n");
		o.write("         location        The number of locations         ");
		o.write("/Region1*Region"+REGIONS+"/\n");
		o.write("         value           The label of region value       /Value/ ;\n\n");
		o.write("Parameter b(agent,location);\n");
		o.write("Parameter c(agent,location);\n");
		o.write("Parameter v(value, location);\n\n");
		o.write("Scalar   Budget          \"The value of budget\"           ");
		o.write("/"+BUDGET+"/;\n\n");
		o.write("$load b, c, v\n\n");
		o.write("Variables\n");
		o.write("         ans  the final answer of maximize object\n");
		o.write("         people the total amount of assigned participants\n");
		o.write("         usebudget the used of budget\n");
		o.write("         lans    the total value of locations;\n\n");
		o.write("binary variables\n");
		o.write("         x(agent,location) shipment quantities in cases\n");
		o.write("         y(location)       appear        ;\n\n");
		o.write("Equations\n");
		o.write("         totalValue       total velue of total region\n");
		o.write("         sub2(agent)      ensure that each agent is only assigned to one region\n");
		o.write("         sub3             ensure that the total cost won't exceed the budget\n");
		o.write("         sub4             set var lans to the total acheived value by participants alltogether\n");
		o.write("         sub5(value, location)             all participants assigned to a single region must exceed the acheivable value of that region\n");
		o.write("         totalpeople      set people var to the total amount of assigned participants\n");
		o.write("         totalbudget      total value of budget;\n\n");
		o.write(" totalValue      ..              ans =e= sum(location, y(location));\n");
		o.write(" sub2(agent)     ..              sum(location, x(agent,location)) =l= 1;\n");
		o.write(" sub3            ..              sum(location, sum(agent, c(agent,location)*x(agent,location))) =l= Budget;\n");
		o.write(" sub4(value)     ..              lans =e= sum(location, y(location)*v(value, location));\n");
		o.write(" sub5(value, location)  ..       sum(agent, b(agent,location)*x(agent,location)) =g= y(location)*v(value, location);\n");
		o.write(" totalpeople     ..              people =e= sum(agent, sum(location, x(agent, location)));\n");
		o.write(" totalbudget     ..              usebudget =e= sum(location, sum(agent, c(agent,location)*x(agent,location)));\n\n\n");
		o.write("Model objectpeople /all/;\n");
		o.write("Solve objectpeople using mip max lans;\n");
		o.flush();
		o.close();
		
	}
}


