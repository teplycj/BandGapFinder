public class FindBandGap{

	private LinkedList<BGVal> values = new LinkedList<>(); //stores all of the kpoint energy information in a specific iteration
	private double maxVB = -100; //arbitrary low value that will be beat
	private double minCB = 100; //arbitrary high value that will be beat
	private double BG = 0; //placeholder for the band gap

	public FindBandGap(){
		//initializes the class
	}
	
	//stores all of the potential band gap energies at each k point
	void inputCurrKPT(int a, double x, double y, double z, double b, double c){
		BGVal currBG = new BGVal(a, x, y, z, b, c);
		values.add(currBG);
	}

	//calculates the band gap
	void calculateBG(int stp){

		int vbkindex=0;
		int cbkindex = 0;

		for(int i=0; i<values.size(); i++){
			if(values.get(i).vbVal>maxVB){
				maxVB=values.get(i).vbVal;
				vbkindex = i;
			}
			if(values.get(i).cbVal<minCB){
				minCB=values.get(i).cbVal;
				cbkindex = i;
			}
		}

		BG = minCB-maxVB;
		if(vbkindex==cbkindex){
			System.out.println("The direct band gap for iteration "+stp+ " is: "+BG);
			System.out.println("vb & cb k-points= ("+values.get(vbkindex).xVal+", "+values.get(vbkindex).yVal+", "+values.get(vbkindex).zVal+")");

		}else{
			System.out.println("The indirect band gap for iteration "+stp+" is: "+BG);
			System.out.println("vb k-point= ("+values.get(vbkindex).xVal+", "+values.get(vbkindex).yVal+", "+values.get(vbkindex).zVal+")");
			System.out.println("cb k-point= ("+values.get(cbkindex).xVal+", "+values.get(cbkindex).yVal+", "+values.get(cbkindex).zVal+")");
		}
		System.out.println();
	
	}

}
