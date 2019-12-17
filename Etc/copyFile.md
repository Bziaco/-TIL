```js
package copy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FolderCopy {
	public static void main(String[] args) throws IOException {
		
		String originPath = "복사할 파일 경로"
		String copyPath = "붙여넣기할 파일  " + todayDate() + "\\" + todayTime();
		
		File sourceFolder = new File(originPath);
		File destinationFolder = new File(copyPath);
		
		if(!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}
		
		copyFolder(sourceFolder, destinationFolder);
	}
	
	/**
     * This function recursively copy all the sub folder and files from sourceFolder to destinationFolder
     * */
    private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
                System.out.println("Directory created :: " + destinationFolder);
            }
             
            //Get all files from source directory
            String files[] = sourceFolder.list();
             
            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
            	
            	if(findParentsPath(sourceFolder, "\\WEB-INF\\classes\\main") == -1 && !file.equals("main")) {
            		File srcFile = new File(sourceFolder, file);
                    File destFile = new File(destinationFolder, file);
                     
                    //Recursive function call
                    copyFolder(srcFile, destFile);
            	}
            }
        } else {
        	if(!destinationFolder.getName().endsWith(".properties")) {
        		
        		String getParents = destinationFolder.getParent();
        		
        		if(getParents.indexOf("\\weblb\\hsweb\\hsck\\web") == -1) {
        			//Copy the file content from one place to another 
                    Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        		} else {
        			if(!findFileExtension(destinationFolder.getName()).equals(".js")) {
        				Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        			}
        		}
        	}
        }
    }
    
    // 파일 확장자 찾기
    private static String findFileExtension(String str) {
    	return str.substring(str.indexOf("."),str.length());
    }
    
    // 특정 파일 부모 경로 존재 여부
    private static int findParentsPath(File sourceFolder, String path) {
    	return sourceFolder.getParent().indexOf(path);
    } 
    
    private static String todayDate() {
    	LocalDate localDate = LocalDate.now();
		return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    
    private static String todayTime() {
    	LocalTime localTime = LocalTime.now();
		return localTime.format(DateTimeFormatter.ofPattern("HHmmss"));
    }
}

```
