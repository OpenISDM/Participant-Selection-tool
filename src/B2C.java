

public class B2C {
	int participant = 0, region = 0, benefit, cost;
	boolean select = false;
	double cp = 0;
	
	
	public B2C(){
		participant = 0;
		region = 0;
		cp = 0;
	}
	
	public void set(int participant, int region, int benefit, int cost){
		this.participant = participant;
		this.region = region;
		this.benefit = benefit;
		this.cost = cost;
		cp = benefit / cost;
	}
	
	public double get(){
		return cp;
	}
	
}