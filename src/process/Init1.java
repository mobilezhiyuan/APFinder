package process;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Init1 {
	public String atomPath = "/data/reaction-atom.txt";
	double a = 0.1D;
	
	public Init1(double a) {
		this.a = a;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
