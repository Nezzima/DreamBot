package nezz.dreambot.filemethods;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileMethods {
	private BufferedReader in;
	private BufferedWriter out;
	private String filepath;
	private String scriptName;

	public FileMethods(String scriptName){
		this(scriptName, false);
	}
	public FileMethods(String filePath, boolean force){
		if(force){
			this.filepath = filePath+System.getProperty("file.separator");
		}
		else{
			this.filepath = System.getProperty("scripts.path");
			this.scriptName = filePath;
			this.filepath+=scriptName + System.getProperty("file.separator");
			System.out.println("FILE PATH: " + filepath);
		}
	}

	public void writeFile(String[] toWrite, String filename){
		filename+=".txt";
		try {
			File theDir = new File(filepath);
			// if the directory does not exist, create it
			if (!theDir.exists()) {
				boolean result = theDir.mkdirs();  
				if(result) {    
					System.out.println("DIR created");  
				}
			}
			out = new BufferedWriter(new FileWriter(filepath+filename));
			//Write out the specified string to the file
			for(int i = 0; i < toWrite.length; i++){
				if(i != toWrite.length - 1){
					out.write(toWrite[i]);
					out.newLine();
				}
				else{
					out.write(toWrite[i]);
				}
			}
			//flushes and closes the stream
			out.close();
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}

	}
	
	public void writeFile(List<String> toWrite, String filename){
		filename+=".txt";
		try {
			File theDir = new File(filepath);
			// if the directory does not exist, create it
			if (!theDir.exists()) {
				boolean result = theDir.mkdirs();  
				if(result) {    
					System.out.println("DIR created");  
				}
			}
			out = new BufferedWriter(new FileWriter(filepath+filename));
			//Write out the specified string to the file
			for(int i = 0; i < toWrite.size(); i++){
				if(i != toWrite.size() - 1){
					out.write(toWrite.get(i));
					out.newLine();
				}
				else{
					out.write(toWrite.get(i));
				}
			}
			//flushes and closes the stream
			out.close();
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}

	}

	public String readFile(String filename){
		filename+=".txt";
		String tempFinder = "";
		try {
			in = new BufferedReader(new FileReader(filepath+filename));
			String temp = in.readLine();
			while(temp != null){
				tempFinder+=temp;
				temp = in.readLine();
				if(temp != null)
					tempFinder+="\n";
			}			
			in.close();
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}
		return tempFinder;
	}
	
	public String[] readFileArray(String filename){
		filename+=".txt";
		List<String> fileContents = new ArrayList<String>();
		try {
			in = new BufferedReader(new FileReader(filepath+filename));
			String temp = in.readLine();
			while(temp != null){
				if(temp.length() > 0){
					fileContents.add(temp.trim());
				}
				temp = in.readLine();
			}			
			in.close();
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}
		return fileContents.toArray(new String[fileContents.size()]);
	}
}
