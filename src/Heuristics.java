import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

// a heuristic class for cross 
// includes greedy/greedy-enhanced for psp, clustering and LKH for TPP
public class Heuristics {
	
	private PSP_Data pspData;
	
	public int PARTICIPANTS;
	public int REGIONS;

	private int index = 0;
	
	public Heuristics(PSP_Data pspData)
	{
		this.pspData = pspData;
		
		PARTICIPANTS = pspData.PARTICIPANTS;
		REGIONS = pspData.REGIONS;

	}
	
	// psp greedy 
	public void PSP_G ()
	{
		// calculate b2c factors
		for (int i = 1; i <= PARTICIPANTS; i++){
			for(int j = 1; j <= REGIONS; j++){
				pspData.b2c[index] = new B2C();
				pspData.b2c[index++].set(i, j, pspData.benefits[i][j], pspData.costs[i][j]);
			}
		}
		// sort b2c in decreasing order
		Arrays.sort(pspData.b2c, new Comparator<B2C>(){
		      @Override
		      public int compare(B2C entry1, B2C entry2) {
		    	  double a = entry1.cp;
		    	  double b = entry2.cp;
		    	  return (int) (b - a);
		      }
		});
		// add b2c array to queue
		for(int i = 0; i < PARTICIPANTS*REGIONS; i++){
			pspData.queue.add(pspData.b2c[i]);
		}
		// assigning participants in order
		// while the queue is not empty
		while(!pspData.queue.isEmpty()){
			
			B2C temp = pspData.queue.remove();
			int participant = temp.participant;
			int region = temp.region;
			
			if((temp.select == false) && (pspData.regionValues[region] - temp.benefit >= 0) && (pspData.BUDGET-temp.cost >= 0)){
				pspData.regionValues[region] -= temp.benefit;
				pspData.BUDGET -= temp.cost;
				pspData.objective += temp.benefit;
				setSelected(pspData.b2c, participant, true);
				pspData.selectedParticipant++;
				pspData.totalCost += temp.cost;
				pspData.assignment[participant] = region;
				pspData.NumOfParticipantsInRegions[region]++;
				System.out.println(" assigning region "+region);
				
			}
		}
		
		int[] counter = new int[REGIONS+1];
		// print out the assignement
		for(int i=1; i<=PARTICIPANTS ; i++)
		{
			int r = pspData.assignment[i];
			System.out.println(pspData.participants[i][1]+" to "+pspData.assignment[i]);
			pspData.assignmentDouble[r][counter[r]]=i;
			counter[r]++;
		}
		for(int i=1 ; i<=REGIONS ; i++)
		{
			System.out.print(" Region "+i);
			for(int j=0 ; j<pspData.NumOfParticipantsInRegions[i] ; j++)
			{
				System.out.print(" "+pspData.assignmentDouble[i][j]);
			}
		}
	}
	
	// testing use
	public void setSelected(B2C b2c[], int participant, boolean flag){
		for(int i = 0; i < PARTICIPANTS*REGIONS; i++)
			if(b2c[i].participant == participant)
				b2c[i].select = flag;
	}
	
	// psp greedy enhanced or psp practical
	public void PSP_GE ()
	{
		
	}
	
	// output data for tpp input
	
	
	
	
	
}
