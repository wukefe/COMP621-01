import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/*
 * function:
 *    merge functions into a single file
 * 
 * command lines:
 *    javac processInput.java
 *    java processInput main.m subfun0.m ... -I./ -O./
 *   
 * output:
 *    main_new.m
 */
public class processInput {
	String defaultinputpath = "./";
	String defaultoutputpath = "./";
	ArrayList<String> myargs = new ArrayList<>();
	String newmain = "", oldmain = "";
	
	public processInput(String[] args){
		findPath(args);
	}
	
	public void processArgs(){
		String allfiles = "";
		if(myargs.size() > 0 ){
			createMainName(myargs.get(0));
		}
		for(int i=0;i<myargs.size();i++){
			allfiles += readAFile(defaultinputpath + myargs.get(i), i);
		}
		if(myargs.size() > 0){
			writeAFile(defaultoutputpath + newmain + ".m", allfiles);
		}
	}
	
	public void findPath(String[] args){
		for(String s : args){
			if(s.startsWith("-I")){
				defaultinputpath = s.substring(2);
			}
			else if(s.startsWith("-O")){
				defaultoutputpath = s.substring(2);
			}
			else {
				myargs.add(s);
			}
		}
		return ;
	}
	
	public String readAFile(String filename, int funcid){
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			int cnt = 0;
			while (line != null) {
				if(funcid == 0 && cnt == 0 && !line.trim().isEmpty() && !line.startsWith("%")){
					if(line.contains(oldmain)){
						line = line.replace(oldmain, newmain);
					}
					else {
						System.out.println("WARNING: the main function: " + oldmain + " is not found");
						System.out.println("  First line is: " + line); // first line
//						System.exit(1);
					}
					cnt++;
				}
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public void writeAFile(String filename, String content){
		int success = 1;
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for(String s : content.split(System.lineSeparator())){
				writer.println(s);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = 0;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = 0;
		}
		if(success == 1)
			System.out.println("successfully write file: " + filename);
	}
	
	/*
	 * Simply the case: ignore name.m.m
	 */
	private void createMainName(String name){
		String[] names = name.split("\\."); // . is special character in regx
//		System.out.println("debug: " + name + ", " + names.toString());
		oldmain = names[0];
		newmain = oldmain + "_new";
	}
	
	public static void main(String[] args){
		processInput pi = new processInput(args);
		pi.processArgs();
	}
}
