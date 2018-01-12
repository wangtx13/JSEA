/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import config.Config;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static utility.Tools.splitCamelWords;

/**
 * @author apple
 */
public class ParseWords {
    private StringBuffer originalWords;
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;
    private String copyrightStopwordList;
    private String customizedPackageList;
    private Map<String, Integer> documentWordsCountList;
    private File extractedFile;

    public ParseWords(StringBuffer originalWords, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, String customizedPackageList, Map<String, Integer> documentWordsCountList, File extractedFile) {
        this.originalWords = originalWords;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
        this.copyrightStopwordList = copyrightInfoContent;
        this.customizedPackageList =customizedPackageList;
        this.documentWordsCountList = documentWordsCountList;
        this.extractedFile = extractedFile;
    }

    public StringBuffer parseAllWords() {
        StringBuffer outputWords = new StringBuffer();

        String[] allWords = originalWords.toString().split(" |\"|\\(|\\)|\\[|\\]|\\.|&|:|;|\r\n|\\\\r\\\\n|\n|\\\\n|\t|\\\\t|,|-|_|//|/|\\*|$|@|\\{|\\}|'|~|>|<|=|!");

        //Prepare for stem words
        String DICT_PATH = Config.DICT_PATH;
        Dictionary dict = new Dictionary(new File(DICT_PATH));
        try {
            dict.open();
        } catch (IOException e) {
            System.out.println("Cannot find the dictionary of the WordNet. Please make sure the DICT_PATH is correct:" + DICT_PATH);
//            Logger.getLogger(ParseWords.class.getName()).log(Level.SEVERE, null, e);
        }

        int wordCount = 0;
        for (String word : allWords) {
            if (!word.equals("")) {
                String[] splitWords = splitCamelWords(word);

                //加入拆分前的原变量名
//                outputWords.append(word);
//                outputWords.append(" ");
//                wordCount++;

                /*若word被拆分，将拆分后的词加入*/
                for (String aSplitWord : splitWords) {
                    String parsedWords = stemWords(aSplitWord, dict);
                    if (parsedWords != null)
                        parsedWords = removeStopWords(parsedWords);
                    if (parsedWords != null)
                        parsedWords = removeClassLibrary(parsedWords.toLowerCase());
                    if (parsedWords != null)
                        parsedWords = removeCustomizedPackageInfo(parsedWords.toLowerCase());
                    if (parsedWords != null)
                        parsedWords = removeCopyrightInfo(parsedWords.toLowerCase());
                    if (parsedWords != null) {
                        outputWords.append(parsedWords);
                        outputWords.append(" ");
                        wordCount++;
                    }

                }
            }
        }

        documentWordsCountList.put(extractedFile.getName(), wordCount);
        dict.close();
        return outputWords;
    }

    private String removeStopWords(String word) {
        //my stopword list
        String stopList = "abstract array arg args assert boolean br break byte catch case char class code continue " +
                "default dd ddouble dl do don double dt else enum error exception exist exists extends false file final " +
                "finally float for gt id if implementation implemented implements import instanceof int integer interface " +
                "interfaces invoke invokes java lead li long main method methodname methods native nbsp new null object " +
                "objects overrides package packages param parameters precison println private protected public quot return " +
                "returned returns short static string strictfp super switch synchronized system this throw throws transient " +
                "true try ul version void volatile while";

        //oracle stopword list
//        String stopList = "abstract continue for new switch assert default goto package synchronized boolean do if private " +
//        "this break double implements protected throw byte else import public throws case enum instanceof return transient " +
//        "catch extends int short try char final interface static void class finally long strictfp volatile const float native" +
//                "super while";

        String[] stopwords = stopList.split(" ");
        for (String s : stopwords) {
            if (s.equals(word)) {
                word = null;
                break;
            }
        }

        return word;
    }

    private String removeClassLibrary(String word) {

        String stopList_common = "util lang";
        boolean general = ifGeneral;
        String stopList_draw = "javax swing awt nio";
        boolean draw = libraryTypeCondition.get("Drawing");
        String stopList_modeling = "";
        boolean modeling = libraryTypeCondition.get("Modeling");
//        String stopList_editing = "";
//        boolean editing = libraryTypeCondition.get("Editing");

        if (general) {
            word = processAWord(stopList_common, word);
        }

        if (draw && (word != null)) {
            word = processAWord(stopList_draw, word);
        }

        if (modeling && (word != null)) {
            word = processAWord(stopList_modeling, word);
        }

//        if (editing && (word != null)) {
//            word = processAWord(stopList_editing, word);
//        }

        return word;
    }

    private String removeCustomizedPackageInfo(String word) {
        word = processAWord(customizedPackageList, word);

        return word;
    }

    private String removeCopyrightInfo(String word) {
        word = processAWord(copyrightStopwordList, word);

        return word;
    }

    private String processAWord(String stopwordsString, String word) {
        String[] stopwords = stopwordsString.split(" |\"|\\(|\\)|\\[|\\]|\\.|&|:|;|\r\n|\\\\r\\\\n|\n|\\\\n|\t|\\\\t|,|-|_|//|/|\\*|$|@|\\{|\\}|'|~|>|<|=|!");
        for (String s : stopwords) {
            if (s.equals(word)) {
                word = null;
                break;
            }
        }
        return word;
    }

    private String stemWords(String word, Dictionary dict) {
        if(word == null || word.equals("")) return "";

        WordnetStemmer stemmer = new WordnetStemmer(dict);

        List<String> stemmedWords = stemmer.findStems(word, null);
        if(stemmedWords.size() > 0) return stemmedWords.get(0);
        else return word;
    }
}
