/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package preprocess;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

import static utility.Tools.writeWordsToFile;

/**
 *
 * @author apple
 */
public class TraversalFiles {
    
    //traversal all files
    public static void fileList(File inputFile, int node, ArrayList<String> path, String outputFilePath, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, String customizedPackageList, Map<String, Integer> documentWordsCountList) {
        node ++;
        File[] files = inputFile.listFiles();
        if (!inputFile.exists()){
            System.out.println("File doesn't exist!");
        } else if(inputFile.isDirectory()) {
            path.add(inputFile.getName());
            
            for (File f : files) {
                String ext = FilenameUtils.getExtension(f.getName());
                if(ext.equals("java")) {
                    try {
                        
                        //Get extracted file location and add it to output file name,
                        //in order to avoid files in different folder 
                        //have the same name.
                        String extractedCodeFilePath = outputFilePath + "/preprocess/" + f.getName() + "-src.txt";
                        
                        //create output file for extracted comments
                        File extractedCodeFile = new File(extractedCodeFilePath);
                        if(extractedCodeFile.createNewFile()) {
                            System.out.println("Create successful: " + extractedCodeFile.getName());
                        }
                        //extract all codes
                        StringBuffer allCodes = new StringBuffer();
                        try (InputStream in = new FileInputStream(f.getPath());
                             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                            String line = "";
                            while((line = reader.readLine())!=null) {
                                allCodes.append(line);
                            }
                        }
                        ParseWords parseWords = new ParseWords(allCodes, ifGeneral, libraryTypeCondition, copyrightInfoContent, customizedPackageList, documentWordsCountList, extractedCodeFile);
                        allCodes = parseWords.parseAllWords();
                        writeWordsToFile(allCodes.toString(), extractedCodeFile);

                    } catch (IOException ex) {
                        Logger.getLogger(TraversalFiles.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
//                    System.out.println(f.getPath());
//                    System.out.println(" isn't a java file.");
                } 
                fileList(f, node, path, outputFilePath, ifGeneral, libraryTypeCondition, copyrightInfoContent, customizedPackageList, documentWordsCountList);
            }
            path.remove(node - 1);
        }
    }
    
    
}
