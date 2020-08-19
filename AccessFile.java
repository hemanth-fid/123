/**
 * 
 */
package gov.dcra.filenet.ingest.test;

import java.io.File;
import java.util.Arrays;

/**
 * @author Administrator
 *
 */
public class AccessFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			
			readFile("");
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
	}
	
	public static void readFile(String srcFileDirPath) throws Exception{

		File srcFolder = new File(srcFileDirPath.toString());
		
		if(srcFolder!=null){
			if(srcFolder.isDirectory()) { 
				File[] subFiles=srcFolder.listFiles();
				if(subFiles!=null && subFiles.length>0){
				for(File file : Arrays.asList(subFiles)){
					
					System.out.println("file : "+file.getAbsolutePath());
					
				} 
			}else{
			}
			throw new Exception("Error Fetching file");
		}

	}


	}
}
