package process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ujmp.core.Matrix;

public class MyMain5 {

	public static void readPara(String[] args) {
		int zz = 0;
		String fileName = args[0];
		String content = FileUtils.readFileByLines(fileName);
		String[] lines = content.split("\n");
//		String host = "localhost";
//		String port = "3306";
//		String dbName = "fga";
//		String user = "root";
//		String password = "123456";
		String start = "";
		String end = "";
		int k = 2000;
		int minPathLength = 2;
		int maxPathLength = 15;
		boolean ifPreciseFindBranch = true;
		String saveTxtPath = "";
		String savePicPath = "";
//		boolean ifStartBySingle = false;
//		boolean ifInterset = true;
//		boolean ifAbundant = false;
		boolean ifDraw = true;
		String graPhVizPath = "";
//		double aLength = 0.5;
//		double aFreeEnergy = 0.1;
//		double aTransf = 0.2;
//		double aSim = 0.1;
//		double aPoints = 0.8;
		int TimeLimit = 50;/////////////////

		// 添加条件
		// 主要有四个条件
		// 1、有无起始化合物，通过控制ifStart
		// 2、是否使用原子团转移，通过使用minAtomGroupTransfer控制，为0表示不控制原子团转移
		// 3、是否追踪原子团转移，通过使用keepAtomgroupTransfer控制
		// 4、是否控制物种，通过使用ifSpecies控制，通过物种所包含的反应来控制
		boolean ifStart = false;//////////////////////////////////
		int minAtomGroupTransfer = 2;
		boolean keepAtomgroupTransfer = true;///////////////////////////////////

		boolean ifSpecies = true;///////////////////////////////////
		boolean ifCycle = false;///////////////////////////////////

		for (String line : lines) {
			String name = line.split(" +")[0].trim();
			String value = line.split(" +")[1].trim();
//			System.out.println(line);
//			if (name.equals("aSf")) {
//				aFreeEnergy = Double.parseDouble(value);
//			} else 

			if (name.equals("sourceCompound")) {
				start = value;
				ifStart = true;///////////////////////////////////
			}
//			else if (name.equals("numberOfTheMinimalAtomGroups")) {
//				minAtomGroupTransfer = Integer.parseInt(value);
//			} 
//			else if (name.equals("keepAtomgroupTransfer")) {
//				keepAtomgroupTransfer =  true;
//			}
//			else if (name.equals("ifSpecies")) {
//				ifSpecies = true;
//			}
//			else if (name.equals("timeLimit")) {
//				TimeLimit = Integer.parseInt(value);
//			}
//			else if (name.equals("Bio")) {
//				ifBio = true;
//			}
//			else if (name.equals("Cycle")) {
//				ifCycle = true;
//			}
//			else if (name.equals("aT")) {
//				aTransf = Double.parseDouble(value);
//			} else if (name.equals("aS")) {
//				aSim = Double.parseDouble(value);
//			} else if (name.equals("aP")) {
//				aPoints = Double.parseDouble(value);
//			} else if (name.equals("k")) {
//				k = Integer.parseInt(value);
//			} 
			else if (name.equals("targetCompound")) {
				end = value;
			} else if (name.equals("resultDirectory")) {
				saveTxtPath = value;
				savePicPath = value;
			} 
//				else if (name.equals("mergingStrategy")) {
//				if (value.equals("overlapping")) {
//					ifInterset = true;
//					ifStartBySingle = true;
//				} else if (value.equals("non-overlapping")) {
//					ifInterset = false;
//					ifStartBySingle = true;
//				} else
//					ifStartBySingle = false;
//			} 
			else if (name.equals("graphVizDirectory")) {
				graPhVizPath = value;
			}
		}

//		System.out.println("---" + "start:" + start + " end:" + end + " minAtomGroupTransfer:" + minAtomGroupTransfer
//				+ " keepAtomgroupTransfer:" + keepAtomgroupTransfer + " ifStart:" + ifStart + " ifSpecies:" + ifSpecies
//				+ " TimeLimit:" + TimeLimit + "----");

		long startTime = System.currentTimeMillis(); // 获取开始时间
		Process1 Process1 = new Process1();
		Process1.setParameter(start, end, k, minAtomGroupTransfer, minPathLength, maxPathLength, saveTxtPath,
				savePicPath, ifDraw, ifStart, graPhVizPath, TimeLimit, ifCycle, ifSpecies, keepAtomgroupTransfer);
		Process1.start();
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("Runing time：" + (endTime - startTime) + "ms");

	}

	public static void main(String[] args) {
		readPara(args);
	}
}
