package preprocess.codeextractor;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class CodeExtractor {
    public static enum ContentType {
        COMMENT,
        JAVA_CODE,
    }

    // Reads source file content.
    private static String readFile(File file) throws IOException {
        StringBuffer fileData = new StringBuffer();
        try (InputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                fileData.append(line);
            }
        }
        return fileData.toString();
    }

    private static ASTParser getParser()  {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        return parser;
    }

    public static List<String> extract(String sourceCode, ContentType contentType) {
        ASTParser parser = getParser();
        parser.setSource(sourceCode.toCharArray());
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        if (contentType == ContentType.COMMENT) {
            // Java doc, block comment and line comment.
            CommentASTVisitor commentVisitor = new CommentASTVisitor(sourceCode);
            for (Comment comment : (List<Comment>) cu.getCommentList()) {
                comment.accept(commentVisitor);
            }
            return commentVisitor.comments;
        } else {
            // Java code.
            JavaCodeASTVisitor javaCodeVisitor = new JavaCodeASTVisitor();
            cu.accept(javaCodeVisitor);
            return Arrays.asList(cu.toString());
        }
    }

    public static List<String> extract(File sourceFile, ContentType contentType) throws IOException {
        String sourceCode = readFile(sourceFile);
        return extract(sourceCode, contentType);
    }

//    public static void main(String[] args) {
//        String testCode = "" +
//                "package ASTParser;\n" +
//                "/**\n" +
//                " *   test\n" +
//                " *   @param a\n" +
//                " */\n"+
//                "public class a {\n" +
//                "public static void main(String[] args) {\n" +
//                "//VariableDeclarationStatement\n" +
//                "String s = \"abc\";\n" +
//                "s.toCharArray();\n" +
//                "//EmptyStatement\n" +
//                "/*\n" +
//                " *\n" +
//                " * block comment\n" +
//                " */\n" +
//                "}}";
//
//        CodeExtractor extractor = new CodeExtractor();
//        List<String> comment = extractor.extract(testCode, ContentType.COMMENT);
//        List<String> codes = extractor.extract(testCode, ContentType.JAVA_CODE);
//        for (String s : comment) {
//            System.out.println(s);
//        }
//        System.out.println("++++++++++++++++++");
//
//        for (String s : codes) {
//            System.out.println(s);
//        }
//    }
}
