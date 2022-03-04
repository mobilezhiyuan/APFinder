package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.matrix.DenseMatrix;

import entity.BranchTree;
import entity.CAppearNode;
import entity.Compound;
import entity.Path;
import entity.Point;
import entity.Reaction;
import process.FileUtils.CompoundType;
import process.FileUtils.MatrixType;

public class FileUtils {
	public static String MolFilePath = "/mol/";
	public static int AtomInfoStart = 3;

	public enum MatrixType {
		Adjecent, Transfer, EdgeIdentify
	};

	public static enum CompoundType {
		substrate, product
	};

	public String atomPath = "/data/reaction-atom.txt";
//	public String atomGroupPath = "/data/reactions-atomgroupcount.txt";
	public String atomGroupPath = "/data/atomgroup.txt";
	public String startMetabolitesPath = "/data/startMetabolitesList.txt";
	public String basisMetabolitesListPath = "/data/basisMetabolitesList.txt";
	public String genericMetabolitesListPath = "/data/genericMetabolitesList.txt";
	public String excludedMetabolitesListPath = "/data/excludedMetabolitesList.txt";
	public String cofactorsListPath = "/data/cofactorsList.txt";
	public String compoundNamePath = "/data/compoundName.txt";
	public String coliReactionPath = "/data/coli.txt";
	public String reactionPath = "/data/reaction.txt";
	public String reactionALLPath = "/data/reactionsALL.txt";

	public ArrayList<String> getReactionAllList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.reactionALLPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				if (line.trim().contains("-")) {
					map.add(line.trim().substring(0, 7));
				} else {
					map.add(line.trim().substring(0, 6));
				}
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<String, String> getReactionMap() {
		ArrayList<String> reactionStrings = getReactionData(); // s.contains("ENTRY") ||s.contains("EQUATION")

		int count = 0;
		HashMap<String, String> reactionMap = new HashMap<String, String>();
		while (count < reactionStrings.size()) {
			String s1 = reactionStrings.get(count).trim().replaceAll(" ", "").substring(5, 11);
			String s2 = reactionStrings.get(count + 1).trim().replaceAll(" ", "").substring(8);
			reactionMap.put(s1, s2);
			count += 2;
		}
		return reactionMap;
	}

	public ArrayList<String> getReactionData() {
		ArrayList<String> map = new ArrayList<String>();
		String result = "";
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.reactionPath)));
//			BufferedReader bf = new BufferedReader(new FileReader(new File(this.reactionPath)));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = bf.readLine()) != null) {// 使用readLine方法，一次读一行
				if (s.contains("ENTRY") || s.contains("EQUATION")) {
					map.add(s);
				}
			}
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	public ArrayList<String> getSpeciesReactionList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.coliReactionPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				map.add(line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<String, String> getCompoundName() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.compoundNamePath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				String[] temp = line.split(",");
				map.put(temp[0], temp[1]);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<String> getcofactorsList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.cofactorsListPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				map.add(line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<String> getExcludedMetabolitesList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.excludedMetabolitesListPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				map.add(line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<String> getGenericMetabolitesList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.genericMetabolitesListPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				map.add(line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<String> getBasisMetabolitesList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.basisMetabolitesListPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				map.add(line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<String> getStartMetabolitesList() {
		ArrayList<String> map = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.startMetabolitesPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				map.add(line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<String, String> getAtomGroupInfo() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.atomGroupPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {
				String[] temp = line.split(" +");
				map.put(temp[0],
						temp[1] + " " + temp[2] + " " + temp[3] + " " + temp[4] + " " + temp[5] + " " + temp[6]);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<String, String> getAtomInfo() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader bf = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(this.atomPath)));
			String line = "";
			while ((line = bf.readLine()) != null) {

				String[] temp = line.split(" +");
				line = line.replaceFirst(temp[0], "").trim();
				map.put(temp[0], line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public void saveReactionAtomGroupCount(String filePath) {
		HashMap<String, String> atomMap = getAtomInfo();

		File file = new File(filePath);
		filePath = String.valueOf(filePath) + "reactions-atomgroupcount" + ".txt";
		try {
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			FileWriter fwriter = new FileWriter(filePath, true);
			for (String metaRe : atomMap.keySet()) {
				String c_1 = metaRe.split(",")[0];
				String c_2 = metaRe.split(",")[1];
				String r = metaRe.split(",")[2];
				String tranfer = atomMap.get(metaRe);
				int tranferNum = handleAtom(c_1, c_2, r, tranfer);
//				if (tranferNum >= 2) {
//					String s = metaRe + " " + String.valueOf(tranferNum);
//					s = String.valueOf(s) + "\r\n";
//					fwriter.write(s);
//				}
				String s = metaRe + " " + String.valueOf(tranferNum);
				s = String.valueOf(s) + "\r\n";
				fwriter.write(s);
			}
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readFileByLines(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		BufferedReader reader = null;
		String s = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				s = s + tempString + "\n";
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// TODO: handle finally clause
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e2) {
					// TODO: handle exception
				}
			}
		}
		return s;
	}

	public String readFileByLinesOnClass(String fileName) {
		InputStream in = this.getClass().getResourceAsStream(fileName);
		if (in == null) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String s = "";
		try {
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				s = s + tempString + "\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return s;
	}

	public Compound getCompondInfo(String ID) {
		Compound c = new Compound();
		String s = readFileByLinesOnClass(MolFilePath + ID + ".mol");
		if (s == null)
			return null;
		String[] lines = s.split("\n");
		int atomNum = Integer.parseInt(lines[AtomInfoStart].trim().split(" +")[0]);
		int edgeNum = Integer.parseInt(lines[AtomInfoStart].trim().split(" +")[1]);
		int InfoStart = AtomInfoStart + atomNum + 1;
		int InfoEnd = AtomInfoStart + atomNum + edgeNum;
		String Info = "";
		for (int i = InfoStart; i <= InfoEnd; i++) {
			String temp = lines[i].trim().split(" +")[0] + " " + lines[i].trim().split(" +")[1] + ",";
			Info = Info + temp;
		}
		if (Info.length() == 0) {
			System.out.println("无法获取化合物分子信息名称：" + ID);
			return null;
		}
		Info = Info.substring(0, Info.length() - 1);
		int atomStart = AtomInfoStart + 1;
		int atomEnd = atomStart + atomNum;
		HashMap<String, String> atomMap = new HashMap<String, String>();
		int idx = 1;
		for (int j = atomStart; j < atomEnd; j++) {
			atomMap.put(idx + "", lines[j].trim().split(" +")[3]);
			idx++;
		}
		c.setAtomNum(atomNum);
		c.setCompoundID(ID);
		c.setEdgeInfo(Info);
		c.setEdgeNum(edgeNum);
		c.setAtomMap(atomMap);
//    	System.out.println("atomNum" + atomNum + "edgeNum" + edgeNum +
//    			"InfoStart" + InfoStart +"InfoEnd" + InfoEnd +"atomStart" + atomStart +
//    			"atomEnd" + atomEnd + "atomMap" + atomMap + "Info" + Info);
		return c;
	}

	public int handleAtom(String subtrate, String product, String reaction, String tranfer) {
		Matrix MA = null;
		Matrix MB = null;
		Matrix M = null;
		Matrix MC = null;
		ArrayList<Matrix> tempMA = new ArrayList<Matrix>();
		Compound c_1 = null, c_2 = null;

		c_1 = getCompondInfo(subtrate);
		c_2 = getCompondInfo(product);
		if (c_1 == null || c_2 == null)
			return 0;
		if (!checkIfMapCorrect(tranfer, c_1, c_2)) {
			System.out.println("transfer error:" + c_1.getCompoundID() + "-" + c_2.getCompoundID() + "-" + reaction);
			return 0;
		}
		if (MA == null) {
			MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(), MatrixType.Adjecent, c_2);
		} else {
			MB = MA;
			MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(), MatrixType.Adjecent, c_2);
		}
		if (MB == null) {
			MB = getAdjMatrix(c_1.getEdgeInfo(), c_1.getAtomNum(), c_1.getAtomNum(), MatrixType.EdgeIdentify, c_1);
		}
		M = getAdjMatrix(tranfer, c_2.getAtomNum(), c_1.getAtomNum(), MatrixType.Transfer, c_1);
		MC = M.mtimes((M.mtimes(MB)).transpose());
		MA = MC.times(MA);
		tempMA.add(MA);
		return getTransferEdgeCount(MA);
	}

	/**
	 * 返回求原子团块数和原子团内部个数 {{1，12，3, 9}，{4,5}，{7,8}} 原子团块数为3，最大原子团原子个数为4，最小原子团原子个数为2
	 */
	public Matrix handleAtom1(String subtrate, String product, String reaction, String tranfer) {
		Matrix MA = null;
		Matrix MB = null;
		Matrix M = null;
		Matrix MC = null;
		ArrayList<Matrix> tempMA = new ArrayList<Matrix>();
		Compound c_1 = null, c_2 = null;

		c_1 = getCompondInfo(subtrate);
		c_2 = getCompondInfo(product);
		if (c_1 == null || c_2 == null)
			return null;
		if (!checkIfMapCorrect(tranfer, c_1, c_2)) {
			System.out.println("transfer error:" + c_1.getCompoundID() + "-" + c_2.getCompoundID() + "-" + reaction);
			return null;
		}
		if (MA == null) {
			MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(), MatrixType.Adjecent, c_2);
		} else {
			MB = MA;
			MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(), MatrixType.Adjecent, c_2);
		}
		if (MB == null) {
			MB = getAdjMatrix(c_1.getEdgeInfo(), c_1.getAtomNum(), c_1.getAtomNum(), MatrixType.EdgeIdentify, c_1);
		}
		M = getAdjMatrix(tranfer, c_2.getAtomNum(), c_1.getAtomNum(), MatrixType.Transfer, c_1);
		MC = M.mtimes((M.mtimes(MB)).transpose());
		MA = MC.times(MA);
		tempMA.add(MA);
		return MA;
	}

	public ArrayList<String> sizeOfatomgroup(Matrix MA) {
		ArrayList<String> atomgroupInfo = new ArrayList<String>();
		int sum = 0;
		for (int i = 0; i < MA.getRowCount(); i++) {
			for (int j = 0; j <= i; j++) {
				int value = MA.getAsInt(i, j);
				if (value > 0) {
					String temp_i = String.valueOf(i);
					String temp_j = String.valueOf(j);
					numOfatomgroup(atomgroupInfo, temp_i, temp_j);
				}
			}
		}
		return atomgroupInfo;
	}

	/**
	 * 求原子团块数和原子团内部个数 {{1，12，3, 9}，{4,5}，{7,8}} 原子团块数为3，最大原子团转移为4
	 */
	public void numOfatomgroup(ArrayList<String> arr, String num1, String num2) {
		String teString = "," + num1 + "," + num2 + ",";
		if (arr == null) {
			arr.add(teString);
			return;
		}
		num1 = "," + num1 + ",";
		num2 = "," + num2 + ",";

		for (String string : arr) {
			if (string.contains(num1) && string.contains(num2)) {
				return;
			} else if (string.contains(num1) && !string.contains(num2)) {
				String temp = string + num2.substring(1);
				arr.add(temp);
				arr.remove(string);
				return;
			} else if (!string.contains(num1) && string.contains(num2)) {
				String temp = string + num1.substring(1);
				arr.add(temp);
				arr.remove(string);
				return;
			}
		}
		arr.add(teString);
	}

//	[C00022,C06010, C06010,C04272, C04272,C00141, C00141,C00183]
//	[R00226, -R04441, -R04439, -R01214]
	public int keepAtomGroup(ArrayList<String> linkArcs, ArrayList<String> linkReactions, int minGroupTransfer) {
		HashMap<String, String> atomMap = getAtomInfo();
		Matrix MA = null;
		Matrix MB = null;
		Matrix M = null;
		Matrix MC = null;
		ArrayList<Matrix> tempMA = new ArrayList<Matrix>();
		Compound c_1 = null, c_2 = null;
		String subtrate = "", product = "", r = "";
		for (int i = 0; i < linkArcs.size() - 1; i++) {
			subtrate = linkArcs.get(i);
			product = linkArcs.get(i + 1);
			r = linkReactions.get(i);
			c_1 = getCompondInfo(subtrate);
			c_2 = getCompondInfo(product);
			if (c_1 == null || c_2 == null)
				return 0;
			String transferKey = "";
			if (r.contains("-")) {
				transferKey = subtrate + "," + product + "," + r.substring(1);
			} else {
				transferKey = subtrate + "," + product + "," + r;
			}
			String transfer = atomMap.get(transferKey);
			if (!checkIfMapCorrect(transfer, c_1, c_2)) {
				System.out.println("transfer error:" + c_1.getCompoundID() + "-" + c_2.getCompoundID() + "-" + r);
				return 0;
			}
			if (MA == null) {
				MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(), MatrixType.Adjecent, c_2);
			} else {
				MB = MA;
				MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(), MatrixType.Adjecent, c_2);
			}

			if (MB == null) {
				MB = getAdjMatrix(c_1.getEdgeInfo(), c_1.getAtomNum(), c_1.getAtomNum(), MatrixType.EdgeIdentify, c_1);
			}
			M = getAdjMatrix(transfer, c_2.getAtomNum(), c_1.getAtomNum(), MatrixType.Transfer, c_1);
			MC = M.mtimes((M.mtimes(MB)).transpose());
//        	System.out.println("subtrate:" + c_1.getCompoundID());
//        	System.out.println(c_1.getAtomMap());
//        	System.out.println(MB);
//        	System.out.println("product:" + c_2.getCompoundID());
//        	System.out.println(c_2.getAtomMap());
//        	System.out.println(MA);
//        	System.out.println("MMMMMMM:");
//        	System.out.println(M);
//        	System.out.println("MCCCCCCCCCC:");
//        	System.out.println(MC);
			MA = MC.times(MA);
//        	System.out.println("MAAAAAAAAAAAAAA:");
//        	System.out.println(MA);
//        	System.out.println("---------------------------------");
			tempMA.add(MA);

		}
		if (MA.getAbsoluteValueSum() < 1)
			return 0;

		if (getTransferEdgeCount(MA) < minGroupTransfer)
			return 0;
		else {
//    		System.out.println(MA);
//    		System.out.println(c_2.getAtomMap());
			return getTransferEdgeCount(MA);
		}
	}

	public int getTransferEdgeCount(Matrix MA) {
		int sum = 0;
		for (int i = 0; i < MA.getRowCount(); i++) {
			for (int j = 0; j <= i; j++) {
				int value = MA.getAsInt(i, j);
				if (value > 0) {
					sum++;
				}
			}
		}
		return sum;
	}

	public static Matrix getAdjMatrix(String edgeInfo, int pointsLA, int pointsLB, MatrixType type, Compound c) {
		if (edgeInfo == null)
			System.err.println(c.CompoundID);
		String[] edge = edgeInfo.split(",");
		Matrix matrix = DenseMatrix.factory.zeros(pointsLA, pointsLB);
		for (int i = 0; i < edge.length; i++) {
			String[] point = edge[i].split(" ");
			int row = Integer.parseInt(point[0]) - 1;
			int col = Integer.parseInt(point[1]) - 1;
			if (row > 200 || col > 200)
				System.err.println("wrong" + c.getCompoundID());
			switch (type) {
			case Adjecent:
				matrix.setAsInt(1, row, col);
				matrix.setAsInt(1, col, row);
				break;
			case Transfer:
				matrix.setAsInt(1, col + 1, row + 1);
				break;
			case EdgeIdentify:
				matrix.setAsInt(i + 1, row, col);
				matrix.setAsInt(i + 1, col, row);
				break;
			}
		}
		return matrix;
	}

	public boolean checkIfMapCorrect(String transfer, Compound C1, Compound C2) {
		boolean ifCorrect = true;
		String[] trans = transfer.split(",");
		for (String e : trans) {
			String[] atom = e.split(" +");
			String C1_idx = (Integer.parseInt(atom[0]) + 1) + "";
			String C2_idx = (Integer.parseInt(atom[1]) + 1) + "";
			if (!C1.getAtomMap().get(C1_idx).equals(C2.getAtomMap().get(C2_idx)))
				return false;
		}
		return ifCorrect;
	}

}
