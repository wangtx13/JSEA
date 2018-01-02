package preprocess.codeextractor;

import org.eclipse.jdt.core.dom.*;

public class JavaCodeASTVisitor extends ASTVisitor {
    public void preVisit(ASTNode node) {
        if (node instanceof Comment) {
            // Deletes the comment AST node.
            node.delete();
        }
    }
}
