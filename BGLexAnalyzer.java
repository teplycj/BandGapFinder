import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class BGLexAnalyzer{

   private Scanner input; //.txt file that is being read
   private Scanner currentLine; //current line of .txt file
   private String currentToken; //current word
   private String line; //current line
   private int linenum = 0; //line number for reference
   private double VBelect = 0; //the placeholder of the valence band electron
   private double CBelect = 0; //the placeholder of the conduction band electron
   private int numKPTS = 0; //number of kpoints specified in KPOINTS
   private int step = 1; //keeps track of which iteration you are on
   private int currKPT = 0; //keeps track of the current kpoint you are reading in a specific iteration
   private boolean startSearch=false;
   private FindBandGap currBG; //find band gap object
   
   void scanInput(Scanner inputFile) throws NoSuchElementException{
   
      input = inputFile; //make local copy of this instance in this class
      currentToken = null;
      line = input.nextLine();
      linenum = 1;
      currentToken = input.next();
      currBG = new FindBandGap();//initialize the object
      scanNext();//starts scanning
   
   }

   private void scanNext(){
   
      while(input.hasNextLine()){

	if(fileDone()){ //this is used to ensure that the file finishes reading
		break;
	}else if(line.contains("NELECT")){ //finds the number of electrons
		handleElects();
		line = input.nextLine();
		linenum++;
	}else if(line.contains("NKPTS")){ //finds the number of kpoints
		handleKpuntos();
		line = input.nextLine();
		linenum++;
	}else if(findingKPTS()){ //this searches through all of the kpoints and finds the corresponding energies of the valence band and the conduction band
		if(currKPT<numKPTS){
			findCurrentBandGap(); //keeps searching if you haven't gone through all of the kpoints yet
		}else{
			currBG = new FindBandGap(); //restarts it's search for the next iteration of the calculation
			step++;
			currKPT=0;
			findCurrentBandGap();
		}
	}else{
		try{
			//check to see if it's a negligible k-point parameter flag
			if(startSearch == false && line.contains("Iteration")){
				startSearch = true;
			}
			line = input.nextLine(); //if it's nothing important, just keep searching
			linenum++;
		}catch (StackOverflowError e){
			System.err.println(e);
			System.out.println(linenum);
			System.out.println(line);
		}
	}
      }

	//calcuate the bandgap for the final iteration
	currBG.calculateBG(step);

   
   }
   
	//finds the number of electrons
   private void handleElects(){
   
      currentLine = new Scanner(line);
	for(int i=0; i<3; i++){
		currentToken = currentLine.next();
	}
      double tempToken = Double.parseDouble(currentToken);
      VBelect = tempToken/2;
      CBelect = VBelect+1;
      //System.out.println("vb elect #: "+VBelect+", cb elect #: "+CBelect);
      System.out.println();
   
   }
   
	//finds the number of kpoints
   private void handleKpuntos(){
   
      currentLine = new Scanner(line);
	for(int i=0; i<4; i++){
		currentToken = currentLine.next();
	}
      int tempToken = Integer.parseInt(currentToken);
      numKPTS = tempToken;
      //System.out.println("number of k puntos: "+numKPTS);
   
   }

	//finds the band gap
   private void findCurrentBandGap(){

	//first it takes note of which kpoint it is calculating
	currentLine = new Scanner(line);
	updateX(2);
	currKPT = Integer.parseInt(currentToken);
	updateX(2);
	double xVal = Double.parseDouble(currentToken);
	updateNext();
	double yVal = Double.parseDouble(currentToken);
	updateNext();
	double zVal = Double.parseDouble(currentToken);

	for(int i=0; i<CBelect; i++){
		line = input.nextLine();
		linenum++;
	}

	//then it gets to the line where the end of the valence band is located and stores the energy value
	currentLine = new Scanner(line);
	updateX(2);
	double vb = Double.parseDouble(currentToken);

	//next it gets to the line of the conduction band and stores that energy value
	line=input.nextLine();
	linenum++;
	currentLine = new Scanner(line);
	updateX(2);
	double cb = Double.parseDouble(currentToken);

	//System.out.println("currkpt: "+currKPT+", x: "+xVal+", y: "+yVal+", z: "+zVal+", vb: "+vb+", cb: "+cb);
	currBG.inputCurrKPT(currKPT, xVal, yVal, zVal, vb, cb);

	
	//if it has gone through all of the kpoints for that iteration, it calculates the band gap
	//if(currKPT==numKPTS){
	//	currBG.calculateBG(step);
	//}
   }

	//starts searching for energies if this test passes
   private boolean findingKPTS(){

	if(line.contains("k-point") && startSearch==true){
		//System.out.println(linenum+": "+line);
		return true;
	}else{
		return false;
	}

   }

   private void updateNext(){
	currentToken = currentLine.next();
   }

   private void updateX(int x){

	for(int i=0; i<x; i++){
		updateNext();
	}

   }   
   
   //checks to see if the file is done
   private boolean fileDone(){
      if(line.contains("accounting") || !(input.hasNextLine())){
         return true;
      }else{
         return false;
      }
   } 

}
