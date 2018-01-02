/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;

import static utility.Tools.writeToFile;

/**
 *
 * @author apple
 */
public class ParseJavaComments {

    private File inputFile;
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;
    private String copyrightInfoContent;
    private String customizedPackageList;
    private Map<String, Integer> documentWordsCountList;
    private File extractedFile;

    public ParseJavaComments(File inputFile, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, String customizedPackageList, Map<String, Integer> documentWordsCountList, File extractedFile) {
        this.inputFile = inputFile;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
        this.copyrightInfoContent = copyrightInfoContent;
        this.customizedPackageList = customizedPackageList;
        this.documentWordsCountList = documentWordsCountList;
        this.extractedFile = extractedFile;
    }

    public void extractComments() {
        try {
            String converted = readFileToString(inputFile.getPath());
            parse(converted);

        } catch (IOException ex) {
            Logger.getLogger(ParseJavaComments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // use ASTParse to parse string
    private void parse(final String str) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(str.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        StringBuffer allComments = new StringBuffer();

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        for (Comment comment : (List<Comment>) cu.getCommentList()) {
            CommentVisitor commentVisitor = new CommentVisitor(cu, str);
            comment.accept(commentVisitor);
//            System.out.println(commentVisitor.getAllComments().toString());
            allComments.append(commentVisitor.getAllComments().toString());
        }

        ParseWords parseWords = new ParseWords(allComments, ifGeneral, libraryTypeCondition, copyrightInfoContent, customizedPackageList, documentWordsCountList, extractedFile);
        allComments = parseWords.parseAllWords();
        writeToFile(allComments.toString(), extractedFile);
    }

    // read file content into a string
//    private String readFileToString(String filePath) throws IOException {
//
//        StringBuilder fileData = new StringBuilder(1000);
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            char[] buf = new char[10];
//            int numRead = 0;
//            while ((numRead = reader.read(buf)) != -1) {
//                String readData = String.valueOf(buf, 0, numRead);
//                fileData.append(readData);
//                buf = new char[1024];
//            }
//
//            reader.close();
//            return fileData.toString();
//        }
//
//    }

    private String readFileToString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        try (InputStream in = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                fileData.append(line);
            }
        }
        return fileData.toString();
    }
}

//comment visitor
class   CommentVisitor extends ASTVisitor {

    CompilationUnit cu;
    String source;
    StringBuffer allComments = new StringBuffer();

    public CommentVisitor(CompilationUnit cu, String source) {
        super();
        this.cu = cu;
        this.source = source;

    }

    public boolean visit(LineComment node) {
        int start = node.getStartPosition();
        int end = start + node.getLength();
        String comment = source.substring(start, end);
        allComments.append(comment);
        allComments.append("\r\n");
        return true;
    }

    public boolean visit(BlockComment node) {
        int start = node.getStartPosition();
        int end = start + node.getLength();
        String comment = source.substring(start, end);

        if (!comment.contains("Copyright")) {
            allComments.append(comment);
            allComments.append("\r\n");
            return true;
        } else {
            return false;
        }
        //return true;
    }

    public StringBuffer getAllComments() {
        return allComments;
    }

}
