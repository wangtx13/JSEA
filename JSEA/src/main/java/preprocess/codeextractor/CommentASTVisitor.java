package preprocess.codeextractor;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

class CommentASTVisitor extends ASTVisitor {
    // String of source code.
    private String source;

    public CommentASTVisitor(String source) {
        this.source = source;
    }

    public List<String> comments = new ArrayList();

    public void postVisit(ASTNode node) {
        if (node instanceof Comment) {
            extractComment((Comment) node);
        }
    }

    private void extractComment(Comment node) {
        int start = node.getStartPosition();
        int length = node.getLength();
        String comment = source.substring(start, start + length);
        comments.add(comment);
    }
}
