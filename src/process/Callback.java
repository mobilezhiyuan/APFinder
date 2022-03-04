package process;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

public class Callback extends GRBCallback {
	private GRBVar[][] vars;
	private FileWriter logfile;
	private String end;

	public Callback(GRBVar[][] xvars, FileWriter xlogfile, String meteEnd) {
		vars = xvars;
		logfile = xlogfile;
		end = meteEnd;
	}

	// arcs--[1,2 2,3 4,5]
	public ArrayList<String> findCycle(ArrayList<String> arcs) {
		ArrayList<String> unvisited = new ArrayList<String>();
		HashMap<String, String> temp = new HashMap<String, String>();
		for (String arc : arcs) {
			if (!unvisited.contains(arc.split(",")[0])) {
				unvisited.add(arc.split(",")[0]);
			}
			if (!unvisited.contains(arc.split(",")[1])) {
				unvisited.add(arc.split(",")[1]);
			}
			temp.put(arc.split(",")[0], arc.split(",")[1]);
		}
		ArrayList<String> cycle = new ArrayList<String>();
		while (unvisited.size() != 0) {
			String current = unvisited.get(0);
			unvisited.remove(current);
			String thiscycle = current.substring(0) + ",";
			int numArc = 0;
			String end = "";
			while (temp.get(current) != null) {
				String value = current.substring(0);
				current = temp.get(current);
				if (unvisited.contains(current)) {
					thiscycle += current + ",";
					numArc++;
				}
				temp.remove(value);
				unvisited.remove(current);
			}
			if (thiscycle.split(",")[0].equals(current)) {
				cycle.add(thiscycle);
			}
		}
		return cycle;
	}
	
	public ArrayList<String> buildList(String end, HashMap<String, String> arcEnd2Start) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(end);
		while (arcEnd2Start.get(end) != null) {
			String temp = arcEnd2Start.get(end);
			if (!list.contains(temp)) {
				list.add(0, temp);
			}
		}
		return list;
	}

	@Override
	protected void callback() {
		try {
//			System.out.println("getIntInfo(GRB.CB_MIPSOL_SOLCNT)---" + getIntInfo(GRB.CB_MIPSOL_SOLCNT));
//			System.out.println("getDoubleInfo(GRB.CB_MIPSOL_OBJ)---" + getDoubleInfo(GRB.CB_MIPSOL_OBJ));
//			System.out.println("getDoubleInfo(GRB.CB_MIPSOL_OBJBND)---" + getDoubleInfo(GRB.CB_MIPSOL_OBJBND));
//			System.out.println("getDoubleInfo(GRB.CB_MIPSOL_OBJBST)---" + getDoubleInfo(GRB.CB_MIPSOL_OBJBST));

			if (where == GRB.CB_MIPSOL) {
				// MIP solution callback
				ArrayList<String> selsected = new ArrayList<String>();
				HashMap<String, String> temp = new HashMap<String, String>();
				HashMap<String, String> arcEnd2Start = new HashMap<String, String>();
				for (GRBVar[] grbVars : vars) {
					for (GRBVar uVars : grbVars) {
						if (uVars != null && getSolution(uVars) > 0.9) {
							String uName = uVars.get(GRB.StringAttr.VarName);
							if (!selsected.contains(uName)) {
								selsected.add(uName);
							}
							if (!temp.containsKey(uName.split(",")[0])) {
								temp.put(uName.split(",")[0], uName.split(",")[1]);
								arcEnd2Start.put(uName.split(",")[1], uName.split(",")[0]);
							}
						}
					}
				}
//				if (selsected.contains(end)) {
//					selsected.remove(end);
//				}
//				System.out.println(selsected);
				ArrayList<String> tour = new ArrayList<String>();
				tour = findCycle(selsected);
//				System.out.println(tour);
				for (String node : tour) {
					GRBLinExpr expr = new GRBLinExpr();
					for (String string : node.split(",")) {
						String edges = temp.get(string);
						if (edges != null) {
							expr.addTerm(1.0,
									vars[Integer.valueOf(string).intValue()][Integer.valueOf(edges).intValue()]);
						}
					}
					addLazy(expr, GRB.EQUAL, 0.0);
				}
				
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error during callback");
			e.printStackTrace();
		}
	}
}
