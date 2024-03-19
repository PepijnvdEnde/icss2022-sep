package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    //This is a linked list of hashmaps, each hashmap contains the variable name and its type
    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
         checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet) {
        checkStyleRule((Stylerule) stylesheet.getChildren().get(0));

    }

    private void checkStyleRule(Stylerule astNode) {
        for (ASTNode child : astNode.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration declaration) {
        if (declaration.property.name.equals("width")){
            if (declaration.expression instanceof PixelLiteral){
                // Width is a pixel literal, so it's valid
            } else if (declaration.expression instanceof Operation) {
                Operation operation = (Operation) declaration.expression;
                if ((operation.lhs instanceof PixelLiteral && operation.rhs instanceof ScalarLiteral) ||
                        (operation.lhs instanceof ScalarLiteral && operation.rhs instanceof PixelLiteral) ||
                        (operation.lhs instanceof PixelLiteral && operation.rhs instanceof PixelLiteral)) {
                    // Width is an operation of pixel literals or an operation of a pixel literal and a scalar, so it's valid
                } else {
                    declaration.setError("Width must be a pixel literal or an operation of pixel literals or an operation of a pixel literal and a scalar");
                }
            } else {
                declaration.setError("Width must be a pixel literal or an operation of pixel literals or an operation of a pixel literal and a scalar");
            }
        }
    }
}
