/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utility.Tools.createDirectoryIfNotExisting;

/**
 *
 * @author apple
 */
public class PreProcessTool {
    private String inputRootFilePath;
    private String outputFilePath;
    private String copyrightInfoContent;
    private String customizedPackageList;
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;
    private Map<String, Integer> documentWordsCountList;

    public PreProcessTool(String inputRootFilePath, String outputFilePath, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, String customizedPackageList) {
        this.inputRootFilePath = inputRootFilePath;
        this.outputFilePath = outputFilePath;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
        this.copyrightInfoContent = copyrightInfoContent;
        this.customizedPackageList = customizedPackageList;
        documentWordsCountList = new HashMap<>();
    }
    

    public void preProcess() throws IOException {
        
        //Create a new folder
//        createDirectoryIfNotExisting(outputFilePath);
        createDirectoryIfNotExisting(outputFilePath + "/preprocess");
           
        File inputRootFile = new File(inputRootFilePath);
        ArrayList<String> path = new ArrayList<>();
        if(!inputRootFile.isDirectory()) {
            System.out.println("Please input a extisted directory.");
        } else {
            TraversalFiles.fileList(inputRootFile, 0, path, outputFilePath, ifGeneral, libraryTypeCondition, copyrightInfoContent, customizedPackageList, documentWordsCountList);
        }

        File documentWordsCountFile = new File(outputFilePath + "/documentsWordsCount.txt");
        if(documentWordsCountFile.createNewFile()) {
            System.out.println("Create successful: " + documentWordsCountFile.getName());
        }
        //sort the map by key
        documentWordsCountList = new TreeMap<>(documentWordsCountList);
        writeToFile(documentWordsCountFile, documentWordsCountList);
    }

    private void writeToFile(File documentWordsCountFile, Map<String, Integer> documentWordsCountList) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(documentWordsCountFile.getPath()))) {
                for(Map.Entry<String, Integer> entry: documentWordsCountList.entrySet()) {
                    writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(PreProcessTool.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
