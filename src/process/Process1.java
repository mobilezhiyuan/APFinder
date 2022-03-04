package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.ujmp.core.Matrix;

import control.YenTopKShortestPathsAlg;
import entity.BranchTree;
import entity.CAppearNode;
import entity.CombTreeNode;
import entity.Compound;
import entity.DrawThread;
import entity.Path;
import entity.Point;
import entity.Reaction;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import model.VariableGraph;
import process.FileUtils.CompoundType;
import process.FileUtils.MatrixType;

public class Process1 {
	public ArrayList<Path> originalPath = null;

	public ArrayList<Path> atomPath = null;

//	public DBUtils dbUtils = null;
	
	public FileUtils fileUtils = null;

	public TreeTools1 treeTools = null;

	public Init1 init = null;//////////////////////////////////////////////////////////

	public HashMap<String, Point> pointMap = null;

	public HashMap<String, CAppearNode> appearMap = null;

	public String start = "C01953";

	public String end = "C05498";

	public ArrayList<Point> waitPoints = null;

	public ArrayList<Point> points = null;

	ArrayList<ArrayList<CombTreeNode>> resultSet = null;

	ArrayList<ArrayList<Point>> resultPoint = null;

	ArrayList<BranchTree> branchTrees = null;

	String rpwName = "rn00010";

	int k = 2000;

	int maxPointsSize = 8;

	int maxCombSize = 12;

	int maxCombination = 10000;

	int minAtomGroupTransfer = 3;

	int minPathLength = 2;

	int maxPathLength = 15;

	boolean ifPreciseFindBranch = true;

	String saveTxtPath = "D:\\" + this.end + "\\";

	String savePicPath = "F:\\";

	double minAtomTransfRate = 0.0D;

	int natrueSize = 0;

	boolean ifBranch = true;

	boolean ifDraw = true;

	String graPhVizPath = "D:\\graphviz\\bin\\dot.exe";

	boolean ifSave = true;

	boolean ifInit = false;

	boolean ifInterSet = false;

	boolean ifAbundant = false;

	boolean ifStart = false;///////////////////////////////////
	
	boolean ifCycle = false;///////////////////////////////////
	
	boolean ifSpecies = false;///////////////////////////////////
	
	boolean keepAtomgroupTransfer = false;///////////////////////////////////

	int timeLimit = 1000;/////////////////////////////////////

	public String YenPath = "/data/c1-c2-01.txt";

	// -----database mysql------
	String host = "localhost";
	String port = "3306";
	String dbName = "fga";
	String user = "root";
	String password = "123456";

	// String yenPath = "";

	String[] abundant = { "C00001", "C00002", "C00003", "C00004", "C00005", "C00006", "C00007", "C00008", "C00009",
			"C00010", "C00011", "C00080" };
	public HashMap<String, String> compMap = new HashMap();

	// -------------------path relative--------
	/**
	 * path length
	 */
	double aLength = 0.5;
	/**
	 * sum free energy of a path
	 */
	double aFreeEnergy = 0.1;
	/**
	 * atomic group transfer
	 */
	double aTransf = 0.2;
	/**
	 * penalty of hub
	 */
	double hub = 0.2;
	// -------------------branch relative------
	double aPoints = 0.8;
	// -------select reaction----
	double aSim = 0.3;

	//// ------------Model Data--------
	// startMetabolitesList,basisMetabolitesList,genericMetabolitesList,excludedMetabolitesList,cofactorsList-(kegg)
	ArrayList<String> startMetabolitesList = null;
	ArrayList<String> basisMetabolitesList = null;
	ArrayList<String> genericMetabolitesList = null;
	ArrayList<String> excludedMetabolitesList = null;
	ArrayList<String> cofactorsList = null;
	// atomGroupTransferNum--(keggc1,keggc2,keggr atomGroupTransferNum)
	HashMap<String, String> atomGroupTransferNum = null;
	HashMap<String, String> kegg2CompName = null;
	// metabolites-(kegg,id) metabolitesNum2Kegg-(id,kegg)
	// reactions-(kegg,id) reactionsNum2Kegg-(id,kegg)
	HashMap<String, String> metabolites = null;
	HashMap<String, String> metabolitesNum2Kegg = null;
	HashMap<String, String> reactions = null;
	HashMap<String, String> reactionsNum2Kegg = null;
	// arcs-(id,id) Dijr-(id,id,id)
	ArrayList<String> arcs = null;
	ArrayList<String> Dijr = null;
	// metaboliteReactions-(id,"r1:1,r2:1,r3:-1....")
	// arcReactions-("id,id","r1,r2,r3....")
	HashMap<String, String> metaboliteReactions = null;
	HashMap<String, String> arcReactions = null;
	ArrayList<ArrayList<String>> uSolutions = null;
	ArrayList<ArrayList<String>> linkArcsSolutions = null;
	ArrayList<ArrayList<String>> linkReactiosSolutions = null;
	ArrayList<ArrayList<String>> linkPathSolutions = null;
	HashMap<ArrayList<String>, ArrayList<String>> solutions= null;
	// reactionMap-(kegg, EQUATION) 
	HashMap<String, String> reactionMap = null;
	//reactionALL-(KEGG)
	ArrayList<String> reactionALL = null;
	
	public void setParameter(String start, String end, int k, int minAtomGroupTransfer, int minPathLength,
			int maxPathLength, String saveTxtPath, String savePicPath, boolean ifDraw, boolean ifStart, String graPhVizPath, 
			int timeLimit, boolean ifCycle, boolean ifSpecies, boolean keepAtomgroupTransfer) {
		this.keepAtomgroupTransfer = keepAtomgroupTransfer;
		this.ifSpecies = ifSpecies;
		this.ifCycle = ifCycle;
		this.timeLimit = timeLimit;
		this.start = start;
		this.end = end;
		this.k = k;
		this.minAtomGroupTransfer = minAtomGroupTransfer;
		this.minPathLength = minPathLength;
		this.maxPathLength = maxPathLength;
		this.ifPreciseFindBranch = ifPreciseFindBranch;
		this.saveTxtPath = saveTxtPath;
		this.savePicPath = savePicPath;
		this.ifAbundant = ifAbundant;
		this.ifDraw = ifDraw;
		this.ifStart = ifStart;
		this.graPhVizPath = graPhVizPath;
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.password = password;
		this.aLength = aLength;
		this.aFreeEnergy = aFreeEnergy;
		this.aTransf = aTransf;
		// this.hub = hub;
		this.aSim = aSim;
		this.aPoints = aPoints;
	}

	public void showParameter() {
		String parameter = "start:" + start;
		parameter = parameter + "\r\n";
		parameter = parameter + "target:" + end;
		parameter = parameter + "\r\n";
		parameter = parameter + "k:" + k;
		parameter = parameter + "\r\n";
		parameter = parameter + "minAtomGroupTransfer:" + minAtomGroupTransfer;
		parameter = parameter + "\r\n";
		parameter = parameter + "minPathLength:" + minPathLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "maxPathLength:" + maxPathLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "ifPreciseFindBranch:" + ifPreciseFindBranch;
		parameter = parameter + "\r\n";
		parameter = parameter + "aLength:" + aLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "aFreeEnergy:" + aFreeEnergy;
		parameter = parameter + "\r\n";
		parameter = parameter + "aTransf:" + aTransf;
		parameter = parameter + "\r\n";
		parameter = parameter + "aSim:" + aSim;
		parameter = parameter + "\r\n";
		parameter = parameter + "aPoints:" + aPoints;
		parameter = parameter + "\r\n";
//		 System.out.println(parameter);
	}

	public void Init() {
		this.ifInit = false;
		this.originalPath = new ArrayList();
		this.atomPath = new ArrayList();
//		this.dbUtils = new DBUtils();
		this.fileUtils = new FileUtils();
		this.treeTools = new TreeTools1();
		this.pointMap = new HashMap();
		this.appearMap = new HashMap();
		this.waitPoints = new ArrayList();
		this.points = new ArrayList();
		this.resultPoint = new ArrayList();
		this.resultSet = new ArrayList();
		this.branchTrees = new ArrayList();
		if (aSim == 0.1) {
			YenPath = "/data/c1-c2-01.txt";
		}
		if (aSim == 0.2) {
			YenPath = "/data/c1-c2-02.txt";
		}
		if (aSim == 0.3) {
			YenPath = "/data/c1-c2-03.txt";
		}
		if (aSim == 0.4) {
			YenPath = "/data/c1-c2-04.txt";
		}
		if (aSim == 0.5) {
			YenPath = "/data/c1-c2-05.txt";
		}
		if (aSim == 0.6) {
			YenPath = "/data/c1-c2-06.txt";
		}
		if (aSim == 0.7) {
			YenPath = "/data/c1-c2-07.txt";
		}
		if (aSim == 0.8) {
			YenPath = "/data/c1-c2-08.txt";
		}
		if (aSim == 0.9) {
			YenPath = "/data/c1-c2-09.txt";
		}
	}

	public void InitModelData() {
		this.startMetabolitesList = new ArrayList();
		this.basisMetabolitesList = new ArrayList();
		this.genericMetabolitesList = new ArrayList();
		this.excludedMetabolitesList = new ArrayList();
		this.cofactorsList = new ArrayList();
		// atomGroupTransferNum--(keggc1,keggc2,keggr atomGroupTransferNum)
		this.atomGroupTransferNum = new HashMap();
		// metabolites-(kegg,id) metabolitesNum2Kegg-(id,kegg)
		// reactions-(kegg,id) reactionsNum2Kegg-(id,kegg)
		this.metabolites = new HashMap();
		this.metabolitesNum2Kegg = new HashMap();
		this.reactions = new HashMap();
		this.reactionsNum2Kegg = new HashMap();
		// arcs-(id,id) Dijr-(id,id,id)
		this.arcs = new ArrayList();
		this.Dijr = new ArrayList();
		// metaboliteReactions-(id,"r1:1,r2:1,r3:-1....")     1:output  -1:input
		this.metaboliteReactions = new HashMap<String, String>();
		// arcReactions-("id,id","r1,r2,r3....")
		this.arcReactions = new HashMap<String, String>();
		this.uSolutions = new ArrayList();
		this.linkArcsSolutions = new ArrayList<ArrayList<String>>();
		this.linkReactiosSolutions = new ArrayList<ArrayList<String>>();
		this.linkPathSolutions = new ArrayList<ArrayList<String>>();
		this.solutions = new HashMap<ArrayList<String>, ArrayList<String>>();
		this.reactionMap = new HashMap<String, String>();
		this.reactionALL = new ArrayList<String>();
	}

	public void finish() {
		this.ifInit = true;
		this.originalPath = null;
		this.atomPath = null;
//		this.dbUtils = null;
		this.fileUtils = null;
		this.treeTools = null;
		this.pointMap = null;
		this.appearMap = null;
		this.waitPoints = null;
		this.points = null;
		this.resultPoint = null;
		this.resultSet = null;
		this.branchTrees = null;
	}

	public void startBySingle() {
		if (this.ifInterSet) {
			processInterSet();
		} else {
			process();
		}
	}

	public void start() {
//		this.compMap = getKEGGID2CompoundNameMap();
//		this.compMap = this.fileUtils.getCompoundName();
		process();
//	    if (this.branchTrees.size() == 0) {
//	      finish();
//	      processInterSet();
//	    } 
//	    if (this.branchTrees.size() == 0)
//	      return; 
	}

	public void process() {
		showParameter();
		Init();
		HashMap<String, String> atommap = this.fileUtils.getAtomInfo();
		Compound c = this.fileUtils.getCompondInfo(this.end);
		if (c == null) {
			System.out.println("the end compound can't be found in database!-" + this.end);
			return;
		}
		int statNum = c.getEdgeNum();
		if (this.minAtomTransfRate != 0.0D) {
			this.minAtomGroupTransfer = (int) Math.round(statNum * this.minAtomTransfRate);
			if (this.minAtomGroupTransfer < 3) {
				this.minAtomGroupTransfer = 3;
			}
		}
		if (this.minAtomGroupTransfer > statNum) {
			System.out
					.println("you can't choose the minAtomGroupTransfer greater than the EndCompound's edge lenghth!");
			return;
		}

//		// startMetabolitesList,basisMetabolitesList,genericMetabolitesList,excludedMetabolitesList,cofactorsList-(kegg)
//		ArrayList<String> startMetabolitesList = new ArrayList<String>();
//		ArrayList<String> basisMetabolitesList = new ArrayList<String>();
//		ArrayList<String> genericMetabolitesList = new ArrayList<String>();
//		ArrayList<String> excludedMetabolitesList = new ArrayList<String>();
//		ArrayList<String> cofactorsList = new ArrayList<String>();
//		// atomGroupTransferNum--(keggc1,keggc2,keggr, atomGroupTransferNum)
//		HashMap<String, String> atomGroupTransferNum = new HashMap<String, String>();
//		HashMap<String, String> kegg2CompName = new HashMap<String, String>();
//		// metabolites-(kegg,id) metabolitesNum2Kegg-(id,kegg)
//		// reactions-(kegg,id) reactionsNum2Kegg-(id,kegg)
//		HashMap<String, String> metabolites = new HashMap<String, String>();
//		HashMap<String, String> metabolitesNum2Kegg = new HashMap<String, String>();
//		HashMap<String, String> reactions = new HashMap<String, String>();
//		HashMap<String, String> reactionsNum2Kegg = new HashMap<String, String>();
//		// arcs-(id,id) Dijr-(id,id,id)
//		ArrayList<String> arcs = new ArrayList<String>();
//		ArrayList<String> Dijr = new ArrayList<String>();
		InitModelData();

		getBasisData();

		getModelData();
//		HashMap<String, String> metaboliteReactions = new HashMap<String, String>();
//		HashMap<String, String> arcReactions = new HashMap<String, String>();
		this.arcReactions = getArcReactionHashMap();
		this.metaboliteReactions = getStoichiometricCoefficientsHashMap();
//		ArrayList<ArrayList<String>> uSolutions = new ArrayList<ArrayList<String>>();
		buildModel(this.metabolites, this.metabolitesNum2Kegg, this.reactions, this.reactionsNum2Kegg, this.arcs);

	}

	public void processInterSet() {
		showParameter();
		Init();
		Init1 init = new Init1(aSim);
		HashMap<String, String> atommap = init.getAtomInfo();
		Compound c = this.fileUtils.getCompondInfo(this.start);
		if (c == null) {
			System.out.println("the start compound can't be found in database!-" + this.start);
			return;
		}
		int statNum = c.getEdgeNum();
		if (this.minAtomTransfRate != 0.0D) {
			this.minAtomGroupTransfer = (int) Math.round(statNum * this.minAtomTransfRate);
			if (this.minAtomGroupTransfer < 3)
				this.minAtomGroupTransfer = 3;
		}
		if (this.minAtomGroupTransfer > statNum) {
			System.out.println(
					"you can't choose the minAtomGroupTransfer greater than the StartCompound's edge lenghth!");
			return;
		}

	}

	public HashMap<String, String> getArcReactionHashMap() {
		HashMap<String, String> temp = new HashMap<String, String>();
		for (String dijr : this.Dijr) {
			String c1 = dijr.split(",")[0];
			String c2 = dijr.split(",")[1];
			String arc = c1 + "," + c2;
			String r = dijr.split(",")[2];
			if (!temp.containsKey(arc)) {
				temp.put(arc, r);
			} else {
				String tempReactions = temp.get(arc);
				if (!tempReactions.contains(r)) {
					tempReactions = tempReactions + "," + r;
					temp.put(arc, tempReactions);
				}
			}
		}
		return temp;
	}

	public HashMap<String, String> getStoichiometricCoefficientsHashMap() {
		HashMap<String, String> temp = new HashMap<String, String>();
		for (String dijr : this.Dijr) {
			String c1 = dijr.split(",")[0];
			String c2 = dijr.split(",")[1];
			String arc = c1 + " " + c2;
			String r = dijr.split(",")[2];
			if (!temp.containsKey(c1)) {
				String tempR = r + ":-1";
				temp.put(c1, tempR);
			} else if (temp.containsKey(c1)) {
				String oldR = temp.get(c1);
				String newR = r + ":-1";
				if (!oldR.contains(newR)) {
					oldR = oldR + "," + newR;
					temp.put(c1, oldR);
				}
			}
			if (!temp.containsKey(c2)) {
				String tempR = r + ":1";
				temp.put(c2, tempR);
			} else if (temp.containsKey(c2)) {
				String oldR = temp.get(c2);
				String newR = r + ":1";
				if (!oldR.contains(newR)) {
					oldR = oldR + "," + newR;
					temp.put(c2, oldR);
				}
			}
		}
		return temp;
	}

	/**
	 * startMetabolitesList-keggid basisMetabolitesList-keggid
	 * genericMetabolitesList-keggid excludedMetabolitesList-keggid
	 * cofactorsList-keggid compMap-[keggname,keggid]
	 * atomGroupTransferNum-[(c1-keggid,c1-keggid,r-kiggid),atomgrouptransfernum]
	 */
	public void getBasisData() {
		this.startMetabolitesList = this.fileUtils.getStartMetabolitesList();
		this.basisMetabolitesList = this.fileUtils.getBasisMetabolitesList();
		this.genericMetabolitesList = this.fileUtils.getGenericMetabolitesList();
		this.excludedMetabolitesList = this.fileUtils.getExcludedMetabolitesList();
		this.cofactorsList = this.fileUtils.getcofactorsList();
		this.compMap = this.fileUtils.getCompoundName();
		this.reactionMap = this.fileUtils.getReactionMap();
		this.reactionALL = this.fileUtils.getReactionAllList();
		
		HashMap<String, String> temp = this.fileUtils.getAtomGroupInfo();
		for (String string : temp.keySet()) {
			if (Integer.parseInt(temp.get(string).split(" ")[4]) >= this.minAtomGroupTransfer) {
				this.atomGroupTransferNum.put(string, temp.get(string).split(" ")[4]);
//				System.out.println(string + "::" + temp.get(string).split(" ")[4]);
//				System.out.println(string +"::" + temp.get(string));
//				System.out.println("maxsizeOfatomgroup::" + temp.get(string).split(" ")[0] + " minsizeOfatomgroup::"
//						+ temp.get(string).split(" ")[1] + " maxnumOfatomgroup::" + temp.get(string).split(" ")[2] + " minnumOfatomgroup::"
//						+ temp.get(string).split(" ")[3] + " maxnumOfminimalatomgroup::" + temp.get(string).split(" ")[4]
//						+ " minnumOfminimalatomgroup::" + temp.get(string).split(" ")[5]);
			}

		}
	}

	/**
	 * metabolites-[keggid, id] metabolitesNum2Kegg-[id, keggid] reactions-[keggid,
	 * id] reactionsNum2Kegg-[id, keggid] arcs-(id,id) Dijr-(id,id,id)
	 */
	public void getModelData() {

		int mID = 0;
		int rID = 0;
		
		for (String metaRe : this.atomGroupTransferNum.keySet()) {
			String c_1 = metaRe.split(",")[0];
			String c_2 = metaRe.split(",")[1];
			String r = reactionUpdate(c_1, c_2, metaRe.split(",")[2]);
			if (r == null) continue;
			if (!this.metabolites.containsKey(c_1)) {
				this.metabolites.put(c_1, String.valueOf(mID));
				this.metabolitesNum2Kegg.put(String.valueOf(mID), c_1);
				mID++;
			}
			if (!this.metabolites.containsKey(c_2)) {
				this.metabolites.put(c_2, String.valueOf(mID));
				this.metabolitesNum2Kegg.put(String.valueOf(mID), c_2);
				mID++;
			}
			if (!this.reactions.containsKey(r)) {
				this.reactions.put(r, String.valueOf(rID));
				this.reactionsNum2Kegg.put(String.valueOf(rID), r);
				rID++;
			}

//			String arcString = this.metabolites.get(c_1) + "," + this.metabolites.get(c_2);
//			if (!this.arcs.contains(arcString) && !arcString.contains("null") && !c_1.equals(c_2)) {
//				this.arcs.add(arcString);
//			}
			String arcString = c_1 + "," + c_2;
			if (!this.arcs.contains(arcString) && !arcString.contains("null") && !c_1.equals(c_2)) {
				this.arcs.add(arcString);
			}

			String dijrNum = this.metabolites.get(c_1) + "," + this.metabolites.get(c_2) + "," + this.reactions.get(r);
//			String dijr_1Num = this.metabolites.get(c_2) + "," + this.metabolites.get(c_1) + ","
//					+ this.reactions.get(r);
			if (!this.Dijr.contains(dijrNum)) this.Dijr.add(dijrNum);
//			if (!this.Dijr.contains(dijrNum)) {
//				if (this.Dijr.contains(dijr_1Num)) {
//					String reverse = "-" + r;
//					if (!this.reactions.containsKey(reverse)) {
//						this.reactions.put(reverse, String.valueOf(rID));
//						this.reactionsNum2Kegg.put(String.valueOf(rID), reverse);
//						rID++;
//					}
//					String dijr2Num = this.metabolites.get(c_1) + "," + this.metabolites.get(c_2) + ","
//							+ this.reactions.get(reverse);
//					this.Dijr.add(dijr2Num);
//				} else {
//					this.Dijr.add(dijrNum);
//				}
//			}
			
			if (r.contains("-")) {
				String temp_r = r.substring(1);
				if (!this.reactions.containsKey(temp_r)) {
					this.reactions.put(temp_r, String.valueOf(rID));
					this.reactionsNum2Kegg.put(String.valueOf(rID), temp_r);
					rID++;
				}
				String tempArc = c_2 + "," + c_1;
				if (!this.arcs.contains(tempArc) && !tempArc.contains("null") && !c_1.equals(c_2)) {
					this.arcs.add(tempArc);
				}

				String t_dijrNum = this.metabolites.get(c_2) + "," + this.metabolites.get(c_1) + "," + this.reactions.get(temp_r);
				if (!this.Dijr.contains(t_dijrNum)) this.Dijr.add(t_dijrNum);
			}
			
			
			
		}
		int reactionsCount = 0;
		int reactionsReverseCount = 0;
		for (String string : this.reactions.keySet()) {
			if (string.contains("-")) {
				reactionsReverseCount++;
			} else {
				reactionsCount++;
			}
		}
		
		System.out.println("mSize:" + this.metabolites.size() + " rSize:" + this.reactions.size() + " reactionsCount:" + reactionsCount + " reactionsReverseCount:" + reactionsReverseCount +
				" arcSize:" + this.arcs.size() + " dijrSize:" + this.Dijr.size() + " atomgroupSize:" + this.atomGroupTransferNum.size());

	}
	
	public String reactionUpdate(String m1, String m2, String r) {
		String reaction = "";
		if (this.reactionMap.get(r) == null) return null;
		String equationLeft = this.reactionMap.get(r).split("<=>")[0];
		String equationRight = this.reactionMap.get(r).split("<=>")[1];
		if (equationLeft.contains(m1) && equationRight.contains(m2)) {
			reaction = r;
		}else {
			reaction = "-" + r;
		}
		return reaction;
	}

	public void buildModel(HashMap<String, String> metabolites, HashMap<String, String> metabolitesNum2Kegg,
			HashMap<String, String> reactions, HashMap<String, String> reactionsNum2Kegg, ArrayList<String> arcs) {
		if (metabolites.get(this.end) == null) {
			System.out.println("the target " + this.end + " not found!!!");
			return;
		}
		String endIDString = metabolites.get(this.end);
		int endID = Integer.parseInt(endIDString);
		int metaIDMax = metabolites.size();
		int reacIDMax = reactions.size();
		FileWriter logfile = null;
		try {
			// Create empty environment, set options, and start
			GRBEnv env = new GRBEnv(true);
			env.set("logFile", "MPFinder.log");
			env.start();

			// Create empty model
			GRBModel model = new GRBModel(env);

			// Create variables
			GRBVar[][] u = new GRBVar[metaIDMax][metaIDMax];
			for (String arc : arcs) {
				String t1 = arc.split(",")[0];
				String t2 = arc.split(",")[1];
				if (this.cofactorsList.contains(t1) || this.cofactorsList.contains(t2)
						|| this.excludedMetabolitesList.contains(t1) || this.excludedMetabolitesList.contains(t2)
						|| this.genericMetabolitesList.contains(t1) || this.genericMetabolitesList.contains(t2)) {
					continue;
				}
				int startEdge = Integer.parseInt(metabolites.get(t1));
				int endEdge = Integer.parseInt(metabolites.get(t2));
				String uName = metabolites.get(t1) + "," + metabolites.get(t2);
				if (startEdge != endEdge) {
					u[startEdge][endEdge] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, uName);
				}
			}
			model.update();
			// Set objective
			GRBLinExpr obj_u = new GRBLinExpr();
			for (GRBVar[] grbVar : u) {
				for (GRBVar grbVar2 : grbVar) {
					if (grbVar2 != null) {
						obj_u.addTerm(1.0, grbVar2);
					}
				}
			}
			
//			model.setObjective(obj_u, GRB.MINIMIZE);

//			System.out.println("metaIDMax" + metaIDMax + "arcs.size()" + arcs.size() + "u[1].length" + u[1].length
//					+ "u.length" + u.length + "reacIDMax" + reacIDMax);

			model.addConstr(obj_u, GRB.GREATER_EQUAL, this.minPathLength, "minPathLength");
			model.addConstr(obj_u, GRB.LESS_EQUAL, this.maxPathLength, "maxPathLength");
			
			// Path finding constraints
			// Equations 1 to 6 define a simple path
			// that preserves carbon exchange in each of its intermediate steps.

			if (this.ifStart) {
				GRBLinExpr exprStartEnter = new GRBLinExpr();
				GRBLinExpr exprStartLeave = new GRBLinExpr();
				int startID = Integer.parseInt(metabolites.get(this.start));
				for (int i = 0; i < metaIDMax; i++) {
					if (u[startID][i] != null) {
						exprStartLeave.addTerm(1.0, u[startID][i]);
					}
					if (u[i][startID] != null) {
						exprStartEnter.addTerm(1.0, u[i][startID]);
					}
				}
				model.addConstr(exprStartLeave, GRB.EQUAL, 1.0, "exprStartLeave");
				model.addConstr(exprStartEnter, GRB.EQUAL, 0.0, "exprStartEnter");
			}

			GRBLinExpr expr1 = new GRBLinExpr();
			GRBLinExpr expr2 = new GRBLinExpr();
			for (int i = 0; i < metaIDMax; i++) {
				if (u[i][endID] != null) {
					expr1.addTerm(1.0, u[i][endID]);
				}
				if (u[endID][i] != null) {
					expr2.addTerm(1.0, u[endID][i]);
				}
			}
			model.addConstr(expr1, GRB.EQUAL, 1.0, "exprEndEnter");
			model.addConstr(expr2, GRB.EQUAL, 0.0, "exprEndLeave");

			// Add constraint3
			for (String startMeta : this.startMetabolitesList) {
				if (metabolites.get(startMeta) == null || startMeta.equals(end)) {
					continue;
				}
				GRBLinExpr expr3Left = new GRBLinExpr();
				GRBLinExpr expr3Right = new GRBLinExpr();
				int l = Integer.parseInt(metabolites.get(startMeta));
				for (int i = 0; i < metaIDMax; i++) {
					if (u[i][l] != null) {
						expr3Left.addTerm(1.0, u[i][l]);
					}
					if (u[l][i] != null) {
						expr3Right.addTerm(1.0, u[l][i]);
					}
				}
				if (expr3Left.size() == 0 && expr3Right.size() == 0) {
					continue;
				}
				String constrName = "Constraint3_" + String.valueOf(l);
				model.addConstr(expr3Left, GRB.LESS_EQUAL, expr3Right, constrName);
			}

			// Add constraint4
			for (String basisMeta : this.basisMetabolitesList) {
				if (metabolites.get(basisMeta) == null || basisMeta.equals(end)) {
					continue;
				}
				GRBLinExpr expr4 = new GRBLinExpr();
				int l = Integer.parseInt(metabolites.get(basisMeta));
				for (int i = 0; i < metaIDMax; i++) {
					if (u[i][l] != null) {
						expr4.addTerm(1.0, u[i][l]);
					}
				}
				if (expr4.size() == 0) {
					continue;
				}
				String constrName = "Constraint4_" + String.valueOf(l);
				model.addConstr(expr4, GRB.EQUAL, 0.0, constrName);
			}

			// Add constraint5
			for (String meta : metabolites.keySet()) {
				if (startMetabolitesList.contains(meta) || meta.equals(end)) {
					continue;
				}
				GRBLinExpr expr5Left = new GRBLinExpr();
				GRBLinExpr expr5Right = new GRBLinExpr();
				int k = Integer.parseInt(metabolites.get(meta));
				for (int i = 0; i < metaIDMax; i++) {
					if (u[i][k] != null) {
						expr5Left.addTerm(1.0, u[i][k]);
					}
					if (u[k][i] != null) {
						expr5Right.addTerm(1.0, u[k][i]);
					}
				}
				if (expr5Left.size() == 0 && expr5Right.size() == 0) {
					continue;
				}
				String constrName = "Constraint5_" + String.valueOf(k);
				model.addConstr(expr5Left, GRB.EQUAL, expr5Right, constrName);
			}

			// Add constraint6
			for (String meta : metabolites.keySet()) {
				GRBLinExpr expr6 = new GRBLinExpr();
				int k = Integer.parseInt(metabolites.get(meta));
				for (int i = 0; i < metaIDMax; i++) {
					if (u[i][k] != null) {
						expr6.addTerm(1.0, u[i][k]);
					}
				}
				if (expr6.size() == 0) {
					continue;
				}
				String constrName = "Constraint6_" + String.valueOf(k);
				model.addConstr(expr6, GRB.LESS_EQUAL, 1.0, constrName);
			}

			// Stoichiometric constraints
			// Equations 7 to 11 define the steady-state flux space for a particular
			// metabolic network.
			
			GRBVar[] v = new GRBVar[reacIDMax];
			GRBVar[] z = new GRBVar[reacIDMax];
			
			
			if (this.ifSpecies) {
				ArrayList<String> speciesReaction = new ArrayList<String>();
				speciesReaction = this.fileUtils.getSpeciesReactionList();
				for (String rid : reactions.keySet()) {
//						System.out.println(rid + "------------");
					if (rid.contains("-")) {
						String tempRid = rid.substring(1);
						if (!speciesReaction.contains(tempRid)) {
							continue;
						}
					} else {
						if (!speciesReaction.contains(rid)) {
							continue;
						}
					}
//						System.out.println(rid + "++++++++++++");
					int id = Integer.parseInt(reactions.get(rid));
					v[id] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, rid);
					z[id] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, rid);
				}
			} else {
				for (String rid : reactions.values()) {
					int id = Integer.parseInt(rid);
					v[id] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, rid);
					z[id] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, rid);
				}
			}
			
			GRBLinExpr obj_z = new GRBLinExpr();
			for (GRBVar grbVar : z) {
				if (grbVar != null) {
					obj_z.addTerm(1.0, grbVar);
				}
			}
			model.addConstr(obj_u, GRB.GREATER_EQUAL, obj_z, "z==u");
			
			double zCoeff = 1.0 / (reacIDMax + 1);
			for (GRBVar grbVar : z) {
				if (grbVar != null) {
					obj_u.addTerm(zCoeff, grbVar);
				}
			}
			
			for (String meta : metabolites.keySet()) {
				if (this.startMetabolitesList.contains(meta) || this.cofactorsList.contains(meta)
						|| this.excludedMetabolitesList.contains(meta)) {
					continue;
				}
				GRBLinExpr expr7 = new GRBLinExpr();
				String m = metabolites.get(meta);
				if (this.metaboliteReactions.get(m) == null) {
					continue;
				}
				String tempR = this.metaboliteReactions.get(m);
				if (!tempR.contains(",")) {
					int rid = Integer.parseInt(tempR.split(":")[0]);
					double coff = Double.parseDouble(tempR.split(":")[1]);
					if (v[rid] != null) {
						expr7.addTerm(coff, v[rid]);
					}
				} else {
					for (String nowR : tempR.split(",")) {
						int rid = Integer.parseInt(nowR.split(":")[0]);
						double coff = Double.parseDouble(nowR.split(":")[1]);
						if (v[rid] != null) {
							expr7.addTerm(coff, v[rid]);
						}
					}
				}
				String constrName = "Constraint7_" + String.valueOf(m);
				model.addConstr(expr7, GRB.GREATER_EQUAL, 0.0, constrName);
			}

			// Add constraint8
			GRBLinExpr expr8 = new GRBLinExpr();
			String tempExpr8R = this.metaboliteReactions.get(String.valueOf(endID));
			if (!tempExpr8R.contains(",")) {
				int rid = Integer.parseInt(tempExpr8R.split(":")[0]);
				double coff = Double.parseDouble(tempExpr8R.split(":")[1]);
				if (v[rid] != null) {
					expr8.addTerm(coff, v[rid]);
				}
			} else {
				for (String nowR : tempExpr8R.split(",")) {
					int rid = Integer.parseInt(nowR.split(":")[0]);
					double coff = Double.parseDouble(nowR.split(":")[1]);
					if (v[rid] != null) {
						expr8.addTerm(coff, v[rid]);
					}
				}
			}
			model.addConstr(expr8, GRB.GREATER_EQUAL, 1.0, "Constraint8");

			// Add constraint9
			for (int i = 0; i < reacIDMax; i++) {
				if (v[i] == null || z[i] == null) {
					continue;
				}
				String constrName = "Constraint9_" + String.valueOf(i);
				model.addConstr(z[i], GRB.LESS_EQUAL, v[i], constrName);
			}

			// Add constraint10
			double Max = 5.0;
			for (int i = 0; i < reacIDMax; i++) {
				if (v[i] == null || z[i] == null) {
					continue;
				}
				GRBLinExpr expr10 = new GRBLinExpr();
				expr10.addTerm(Max, z[i]);
				String constrName = "Constraint10_" + String.valueOf(i);
				model.addConstr(v[i], GRB.LESS_EQUAL, expr10, constrName);
			}

			// Add constraint11
			for (String reacsString : reactions.keySet()) {
				if (reacsString.contains("-")) {
					int a = Integer.parseInt(reactions.get(reacsString));
					int b = Integer.parseInt(reactions.get(reacsString.substring(1)));
					if (z[a] == null || z[b] == null) {
						continue;
					}
					GRBLinExpr expr11 = new GRBLinExpr();
					expr11.addTerm(1.0, z[a]);
					expr11.addTerm(1.0, z[b]);
					String name = "z[" + reactions.get(reacsString) + "]" + "z["
							+ reactions.get(reacsString.substring(1)) + "]<=1.0";
					String constrName = "Constraint11--" + name;
					model.addConstr(expr11, GRB.LESS_EQUAL, 1.0, constrName);
				}
			}

			// Linking path finding and stoichiometric constraints
			// Add constraint12
			for (int i = 0; i < metaIDMax; i++) {
				for (int j = 0; j < metaIDMax; j++) {
					if (u[i][j] != null) {
						GRBLinExpr expr12 = new GRBLinExpr();
						String arcTemp = String.valueOf(i) + "," + String.valueOf(j);
						String reactionsLsit = this.arcReactions.get(arcTemp);
						if (reactionsLsit != null) {
							if (reactionsLsit.contains(",")) {
								for (String r : reactionsLsit.split(",")) {
									if (z[Integer.parseInt(r)] != null) {
										expr12.addTerm(1.0, z[Integer.parseInt(r)]);
									}
								}

							} else {
								int r = Integer.parseInt(reactionsLsit);
								if (z[r] != null) {
									expr12.addTerm(1.0, z[r]);
								}
							}
							String constrName = "Constraint12-" + "Dijr * Zr>= U[" + String.valueOf(i) + "]["
									+ String.valueOf(j) + "]";
							model.addConstr(expr12, GRB.GREATER_EQUAL, u[i][j], constrName);
						}
					}
				}
			}
			
			
			
			model.setObjective(obj_u, GRB.MINIMIZE);
			

			// Limit how many solutions to collect
			model.set(GRB.IntParam.PoolSolutions, 5000);

			// Turn off display and heuristics
//			model.set(GRB.IntParam.OutputFlag, 0);
//			model.set(GRB.DoubleParam.Heuristics, 0.0);

			model.set(GRB.IntParam.LazyConstraints, 1);
//			model.set(GRB.IntParam.PreCrush, 1);

			// Set a 2 second time limit
			model.set(GRB.DoubleParam.TimeLimit, this.timeLimit);

			// Limit the search space by setting a gap for the worst possible solution that
			// will be accepted
//			model.set(GRB.DoubleParam.PoolGap, 0.10);

			// do a systematic search for the k-best solutions
			model.set(GRB.IntParam.PoolSearchMode, 2);

			// save problem
			model.write("MPFinder3.lp");

			// Open log file
			logfile = new FileWriter("MPFinder.log");

			// Create a callback object and associate it with the model
			if (this.ifCycle) {
				Callback cb = new Callback(u, logfile, String.valueOf(endID));

				model.setCallback(cb);
			}

			// Optimize model
			model.optimize();

			int status = model.get(GRB.IntAttr.Status);
			if (status == GRB.INF_OR_UNBD || status == GRB.INFEASIBLE || status == GRB.UNBOUNDED) {
				model.computeIIS();
				if (this.ifStart) {
					model.write(this.start + "_" + this.end + ".ilp");
				}else {
					model.write(this.end + ".ilp");
				}
				System.out.println("The model cannot be solved because it is infeasible or unbounded");
				System.exit(1);
			}
			
			// Print number of solutions stored
			int nSolutions = model.get(GRB.IntAttr.SolCount);
			System.out.println("Number of solutions found: " + nSolutions);

			// print solution
			for (int i = 0; i < nSolutions; i++) {
				long startTime = System.currentTimeMillis();
				model.set(GRB.IntParam.SolutionNumber, i);
				//得到{（compoundName，compoundName），（compoundName，compoundName），（compoundName，compoundName）}
				ArrayList<String> arcSol = new ArrayList<String>();
				for (GRBVar[] grbVars : u) {
					for (GRBVar uVars : grbVars) {
						if (uVars != null && uVars.get(GRB.DoubleAttr.Xn) > 0.9) {
							String uName = uVars.get(GRB.StringAttr.VarName);
							String arcName = metabolitesNum2Kegg.get(uName.split(",")[0]) + "," + metabolitesNum2Kegg.get(uName.split(",")[1]);
							if (!arcSol.contains(arcName)) {
								arcSol.add(arcName);
							}
						}
					}
				}
				//得到{（reactionName），（reactionName），（reactionName）}
				ArrayList<String> zSol = new ArrayList<String>();
				for (GRBVar zVar : z) {
					if (zVar != null && zVar.get(GRB.DoubleAttr.Xn) > 0.9) {
						String zName = zVar.get(GRB.StringAttr.VarName);
						if (!zSol.contains(zName)) {
							zSol.add(zName);
						}
					}
				}

//				System.out.println("++++++++++未作任何处理++++++++++++++");
//				for (String string : arcSol) {
//					System.out.print(string + ">");
//				}
//				System.out.println();
//				for (String string : zSol) {
//					System.out.print(string + ">");
//				}
//				System.out.println();
//				System.out.println();
			
				
				ArrayList<String> linkArc = buildKEGGList(arcSol);
				ArrayList<String> linkReactions = buildKEGGReactionList(linkArc, zSol);
				for (int k = 0; k < linkReactions.size(); k++) {
					if (linkReactions.get(k).contains("-")) {
						String temp = linkReactions.get(k).substring(1);
						linkReactions.remove(k);
						linkReactions.add(k, temp);
					}
				}
//				System.out.println("-------各自成链--------");
//				for (String string : linkArc) {
//					System.out.print(string + "->");
//				}
//				System.out.println();
//				for (String string : linkReactions) {
//					System.out.print(string + "->");
//				}
//				System.out.println();
//				System.out.println();
				
				ArrayList<String> linkPath = new ArrayList<String>();
				for (int k = 0; k < linkArc.size(); k++) {
					if (k != linkArc.size() - 1) {
						linkPath.add(linkArc.get(k));
						linkPath.add(linkReactions.get(k));
					} else {
						linkPath.add(linkArc.get(k));
					}
				}
				
				if (linkArc.size() - 1 >= this.minPathLength && !this.linkPathSolutions.contains(linkPath)) {
					this.linkPathSolutions.add(linkPath);
					this.linkArcsSolutions.add(linkArc);
					this.linkReactiosSolutions.add(linkReactions);
//					System.out.println("******路径成链*********");
//					for (String string : linkPath) {
//						System.out.print(string + "<<");
//					}
//					System.out.println();
//					System.out.println();
				}
				
				long endTime = System.currentTimeMillis(); // 获取结束时间
				
			}
			
			savePathwayMetaboliteReactionTxt(this.saveTxtPath, this.linkArcsSolutions, this.linkReactiosSolutions, "NO");
			
			for (int j = 0; j < this.linkArcsSolutions.size(); j++) {
				ArrayList<String> tempMe = this.linkArcsSolutions.get(j);
				ArrayList<String> tempRe = this.linkReactiosSolutions.get(j);
				int nonConservedAtomGroup = 2;
				if (this.minAtomGroupTransfer == 0) this.minAtomGroupTransfer = nonConservedAtomGroup;
				for (int i = 0; i < tempMe.size()-1; i++) {
					String s = "";
					if (tempRe.get(i).contains("-")) {
						s = tempMe.get(i+1) + "," + tempMe.get(i) + "," + tempRe.get(i).substring(1);
					}else {
						s = tempMe.get(i) + "," + tempMe.get(i+1) + "," + tempRe.get(i);
					}
					if ( !this.atomGroupTransferNum.containsKey(s) || this.atomGroupTransferNum.get(s) == null  || 
							Integer.valueOf(this.atomGroupTransferNum.get(s)) < this.minAtomGroupTransfer) {
						this.linkArcsSolutions.remove(j);
						this.linkReactiosSolutions.remove(j);
						break;
					}
				}
			}
			saveKeepPathwayTxt(this.saveTxtPath, this.linkArcsSolutions, "track-inR");
			savePathwayMetaboliteReactionTxt(this.saveTxtPath, this.linkArcsSolutions, this.linkReactiosSolutions, "track-inR-MR");
	
			if (this.keepAtomgroupTransfer) {
				int temp_minAtomGroupTransfer = 2;
				if (this.minAtomGroupTransfer != 0) temp_minAtomGroupTransfer = this.minAtomGroupTransfer;
				for (int i = 0; i < this.linkArcsSolutions.size(); i++) {
//					System.out.println(this.linkArcsSolutions.get(i));
//					System.out.println(this.linkReactiosSolutions.get(i));
					int keepAtomTran = this.fileUtils.keepAtomGroup(this.linkArcsSolutions.get(i), this.linkReactiosSolutions.get(i), temp_minAtomGroupTransfer);
//					System.out.println("keepAtomTran:::::::::"+keepAtomTran);
//					System.out.println();
					if (keepAtomTran == 0 || keepAtomTran == 1) {
						this.linkArcsSolutions.remove(i);
						this.linkReactiosSolutions.remove(i);
//						System.out.println("arcs::" + this.linkArcsSolutions.get(i));
//						System.out.println("reactions::" + this.linkReactiosSolutions.get(i));
//						System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//						count2++;
					}
				}
				saveKeepPathwayTxt(this.saveTxtPath, this.linkArcsSolutions, "track-inLP");
				savePathwayMetaboliteReactionTxt(this.saveTxtPath, this.linkArcsSolutions, this.linkReactiosSolutions, "track-inLP-MR");
			}
			
			
			
			// Dispose of model and environment
			model.dispose();
			env.dispose();

		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error during optimization");
			e.printStackTrace();
		} finally {
			// Close log file
			if (logfile != null) {
				try {
					logfile.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public String calAcc(ArrayList<String> referencePath, ArrayList<String> computedPath) {

		computedPath.remove(computedPath.size() - 1);
		computedPath.remove(0);
		double TP = 0;
		double FP = 0;
		double FN = 0;
		for (String m : computedPath) {
			if (referencePath.contains(m)) {
				TP = TP + 1.0;
			} else {
				FP = FP + 1.0;
			}
		}
		FN = referencePath.size() - TP;

		double Sn = (double) ((int) (TP / (TP + FP) * 1000)) / 1000;
		double PPV = (double) ((int) (TP / (TP + FN) * 1000)) / 1000;
		double Acc = (double) ((int) (Math.sqrt(Sn * PPV) * 1000)) / 1000;
		String calData = "TP:" + TP + "  FP:" + FP + "  FN:" + FN + "  Sn:" + Sn + "  PPV:" + PPV + "  Acc:" + Acc;
		return calData;
	}

	public ArrayList<String> buildKEGGReactionList(ArrayList<String> arcs, ArrayList<String> reactions) {
//		System.out.println(arcs + "--" + reactions + "buildKEGGReactionList");
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < arcs.size() - 1; i++) {
			String arc = this.metabolites.get(arcs.get(i)) + "," + this.metabolites.get(arcs.get(i+1));
			String possibleReactions = this.arcReactions.get(arc);
			list.add(i, sameReactions(possibleReactions, reactions));
		}
		return list;
	}
	
	public String sameReactions(String computeReactions, ArrayList<String> compareReactions) {
//		System.out.println(computeReactions + "--" + compareReactions + "sameReactions");
		String reactionSet = "";
		String[] pReactions = computeReactions.split(",");
		for (String r : pReactions) {
			String keggR = this.reactionsNum2Kegg.get(r);
//			System.out.println("keggR-----------" + keggR);
			if (compareReactions.contains(keggR)) {
				reactionSet = reactionSet + "-" + keggR;
			}
		}
		return reactionSet.substring(1);
	}
	
	public ArrayList<String> buildKEGGList(ArrayList<String> arcs) {	
		
		HashMap<String, String> arcEnd2Start = new HashMap<String, String>();
		for (String arc : arcs) {
			arcEnd2Start.put(arc.split(",")[1], arc.split(",")[0]);
		}
		ArrayList<String> list = new ArrayList<String>();
		String temp = this.end;
		
		list.add(this.end);
		while (arcEnd2Start.get(temp) != null) {
				list.add(0, arcEnd2Start.get(temp));
			temp = arcEnd2Start.get(temp);
			arcEnd2Start.remove(list.get(1));
		}
		return list;
	}

	public ArrayList<String> buildCompNameList(ArrayList<String> list) {
		ArrayList<String> temp = new ArrayList<String>();
		for (String string : list) {
			temp.add(this.compMap.get(string));
		}
		return temp;
	}
	
	public void savePathwayTxt(String filePath, ArrayList<ArrayList<String>> list) {
		File file = new File(filePath);
		if (this.ifStart) {
			filePath = String.valueOf(filePath) + String.valueOf(this.start) + "_" + String.valueOf(this.end) + ".txt";
		} else {
			filePath = String.valueOf(filePath) + String.valueOf(this.end) + ".txt";
		}

		try {
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			FileWriter fwriter = new FileWriter(filePath, true);
			for (ArrayList<String> metaRe : list) {
				String s = "";
				for (String comp : metaRe) {
					s += comp + "-->";
				}
				s = s.substring(0, s.length() - 3) + "\r\n";
				fwriter.write(s);
			}
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveKeepPathwayTxt(String filePath, ArrayList<ArrayList<String>> list, String fileString) {
		File file = new File(filePath);
		if (this.ifStart) {
			filePath = String.valueOf(filePath) + String.valueOf(this.start) + "_" + String.valueOf(this.end) + "_" + fileString + ".txt";
		} else {
			filePath = String.valueOf(filePath) + String.valueOf(this.end) + "_" + fileString + ".txt";
		}

		try {
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			FileWriter fwriter = new FileWriter(filePath, true);
			for (ArrayList<String> metaRe : list) {
				String s = "";
				for (String comp : metaRe) {
					s += comp + "-->";
				}
				s = s.substring(0, s.length() - 3) + "\r\n";
				fwriter.write(s);
			}
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void savePathwayMetaboliteReactionTxt(String filePath, ArrayList<ArrayList<String>> listMetabolite, ArrayList<ArrayList<String>> listReaction, String fileString) {
		File file = new File(filePath);
		if (this.ifStart) {
			filePath = String.valueOf(filePath) + String.valueOf(this.start) + "_" + String.valueOf(this.end) + "_" + fileString + ".txt";
		} else {
			filePath = String.valueOf(filePath) + String.valueOf(this.end) + "_" + fileString + ".txt";
		}

		try {
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			FileWriter fwriter = new FileWriter(filePath, true);
			for (int j = 0; j < listMetabolite.size(); j++) {
				String s = "";
				ArrayList<String> tempMe = listMetabolite.get(j);
				ArrayList<String> tempRe = listReaction.get(j);
				for (int i = 0; i < tempMe.size(); i++) {
					if (i != tempMe.size() - 1) {
						s += tempMe.get(i) + "-->" + tempRe.get(i) + "-->";
					} else {
						s += tempMe.get(i) + "-->";
					}
				}
				s = s.substring(0, s.length() - 3) + "\r\n";
				fwriter.write(s);
			}
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
