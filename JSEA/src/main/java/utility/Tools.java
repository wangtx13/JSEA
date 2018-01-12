/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import org.apache.commons.lang3.StringUtils;
import preprocess.TraversalFiles;
import servlet.PreProcessServlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author apple
 */
public class Tools {

    public static boolean createDirectoryIfNotExisting(String dirPath) {
        File dir = new File(dirPath);
        if(dir.exists()) {
            System.out.println("The folder has existed: " + dirPath);
            return false;
        }
        if(!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        if(dir.mkdirs()) {
//            System.out.println("create successful: " + dirPath);
            return true;
        } else {
            System.out.println("create fail...");
            return false;
        }
    }

    public static void deleteFolderContent(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolderContent(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void writeWordsToFile(String words, File outputFile) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()))) {
                writer.write(words);
            }

        } catch (IOException ex) {
            Logger.getLogger(TraversalFiles.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void writeMapIntStrAToFile(File documentWordsCountFile, Map<Integer, String[]> topThreeDocuments) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(documentWordsCountFile.getPath()))) {
                for(Map.Entry<Integer, String[]> entry: topThreeDocuments.entrySet()) {
                    String[] docAndPerList = entry.getValue();
                    writer.write(entry.getKey() + "\t" + docAndPerList[0] + "\t" + docAndPerList[1] + "\t" + docAndPerList[2] + "\n");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(PreProcessServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //sort map in terms of value in descending order
    public static Map<String, Double> sortMapByValueWithStringKey(Map<String, Double> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Double>> list =
                new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    //sort map in terms of value in descending order
    public static Map<Integer, Double> sortMapByValueIntDouble(Map<Integer, Double> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Integer, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static Map<Integer, Integer> sortMapByValueIntInt(Map<Integer, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Integer, Integer>> list =
                new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<Integer, Integer> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Integer, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static String highlightKeywords(String source, List<String> keywords) {
        String lowerSource = source.toLowerCase();
        for (String query : keywords) {
            if(query.equals(lowerSource)) {
                source = "<b style='color:red'>" + source + "</b>";
            }
        }
        return source;
//        String regex = "(?i)(" + StringUtils.join(keywords, "|") + ")";
//        return source.replaceAll(regex, "<b style=\"color:red\">$1</b>");
    }

    public static double formatDouble(double d) {
        return (double)Math.round(d*100)/100;
    }

    public static String[] splitCamelWords(String word) {
        word = word.replace("XML", "Xml");
        word = word.replace("DOM", "Dom");
        word = word.replace("JHotDraw", "Jhotdraw");
        word = word.replace("ID", "Id");

        String regEx = "[A-Z]";
        Pattern p1 = Pattern.compile(regEx);
        Matcher m1 = p1.matcher(word);

        boolean startWithUpper = false;
        startWithUpper = Pattern.matches("[A-Z].*", word);

        String[] words = p1.split(word);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            list.add(words[i]);
        }

        int count = 0;
        while (m1.find()) {
            if (count + 1 < words.length) {
                list.set(count + 1, m1.group() + list.get(count + 1));
                ++count;
            } else {
                list.add(m1.group());
            }
        }

        if (startWithUpper && words.length != 0) {
            list.remove(0);
        }

        for (int i = 0; i < list.size(); ++i) {
            list.set(i, list.get(i).toLowerCase());
        }

        String[] result = list.toArray(new String[1]);
        return result;
    }

    public static String[] splitDocName(String word) {
        word = word.replace("XML", "Xml");
        word = word.replace("DOM", "Dom");
        word = word.replace("JHotDraw", "Jhotdraw");
        word = word.replace("ID", "Id");
        String regEx = "[A-Z]";
        Pattern p1 = Pattern.compile(regEx);
        Matcher m1 = p1.matcher(word);

        boolean startWithUpper = false;
        startWithUpper = Pattern.matches("[A-Z].*", word);

        String[] words = p1.split(word);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            list.add(words[i]);
        }

        int count = 0;
        while (m1.find()) {
            if (count + 1 < words.length) {
                list.set(count + 1, m1.group() + list.get(count + 1));
                ++count;
            } else {
                list.add(m1.group());
            }
        }

        if (startWithUpper && words.length != 0) {
            list.remove(0);
        }

//        for (int i = 0; i < list.size(); ++i) {
//            list.set(i, list.get(i).toLowerCase());
//        }

        String[] result = list.toArray(new String[1]);
        return result;
    }
}
