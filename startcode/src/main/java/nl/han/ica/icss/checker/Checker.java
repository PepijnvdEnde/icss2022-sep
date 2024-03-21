package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.parser.ICSSParser;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {
    
    //This is a linked list of hashmaps, each hashmap contains the variable name and its type
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        System.out.println("Checking stylesheet");
         checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode node : stylesheet.getChildren()) {
            if (node instanceof Stylerule) {
                checkStijlRegel((Stylerule) node);
            } else if (node instanceof VariableAssignment) {
                checkVariabeleToewijzing((VariableAssignment) node);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkStijlRegel(Stylerule stylerule) {
        System.out.println("styleRule: " + stylerule.body.toString());
        for (Selector selector : stylerule.selectors) {
            checkSelector(selector);
        }
        for (ASTNode node : stylerule.body) {
            checkRegelInhoud(node);
        }

    }

    private void checkSelector(Selector selector) {
        if (selector instanceof TagSelector) {
            System.out.println("TagSelector: " + selector.toString());
        } else if (selector instanceof ClassSelector) {
            System.out.println("ClassSelector: " + selector.toString());
        } else if (selector instanceof IdSelector) {
            System.out.println("IdSelector: " + selector.toString());
        }
    }

    private void checkRegelInhoud(ASTNode regelInhoud) {
        System.out.println("RegelInhoud: " + regelInhoud.toString());
        if (regelInhoud instanceof Declaration) {
            checkDeclaratie((Declaration) regelInhoud);
        } else if (regelInhoud instanceof IfClause) {
            checkIfClause((IfClause) regelInhoud);
        } else if (regelInhoud instanceof VariableAssignment) {
            checkVariabeleToewijzing((VariableAssignment) regelInhoud);
        }
    }

    private void checkDeclaratie(Declaration declaration) {
        System.out.println("Declaration: " + declaration.property.name.toString() + ":" + declaration.expression.toString());
        checkExpressie(declaration.expression);
    }

    private void checkExpressie(Expression expression) {
        System.out.println("Expression: " + expression.toString());
        if (expression instanceof Literal) {
            checkLiteral((Literal) expression);
        } else if (expression instanceof AddOperation) {
            checkAddOperation((AddOperation) expression);
        } else if (expression instanceof MultiplyOperation) {
            checkMultiplyOperation((MultiplyOperation) expression);
        }
    }

    private void checkLiteral(Literal literal) {
        System.out.println("Literal: " + literal.toString());
        if (literal instanceof BoolLiteral) {
            System.out.println("BoolLiteral: " + literal.toString());
        } else if (literal instanceof ColorLiteral) {
            System.out.println("ColorLiteral: " + literal.toString());
        } else if (literal instanceof PixelLiteral) {
            System.out.println("PixelLiteral: " + literal.toString());
        } else if (literal instanceof PercentageLiteral) {
            System.out.println("PercentageLiteral: " + literal.toString());
        }
    }

    private void checkAddOperation(AddOperation addOperation) {
        System.out.println("AddOperation: " + addOperation.lhs.toString() + " + " + addOperation.rhs.toString());
        checkExpressie(addOperation.lhs);
        checkExpressie(addOperation.rhs);
    }

    private void checkMultiplyOperation(MultiplyOperation multiplyOperation) {
        System.out.println("MultiplyOperation: " + multiplyOperation.lhs.toString() + " * " + multiplyOperation.rhs.toString());
        checkExpressie(multiplyOperation.lhs);
        checkExpressie(multiplyOperation.rhs);
    }

    private void checkIfClause(IfClause ifClause) {
        System.out.println("IfClause: [" + ifClause.conditionalExpression.toString() + "] {" + ifClause.body.toString() + "}");
        checkConditionalExpression(ifClause.conditionalExpression);
        for (ASTNode node : ifClause.body) {
            checkRegelInhoud(node);
        }
        if (ifClause.elseClause != null) {
            checkElseClause(ifClause.elseClause);
        }
    }

    private void checkConditionalExpression(Expression conditionalExpression) {
        System.out.println("ConditionalExpression: " + conditionalExpression.toString());
    }

    private void checkElseClause(ElseClause elseClause) {
        System.out.println("ElseClause: " + elseClause.toString());
        checkRegelInhoud(elseClause.body.get(0));
    }

    private void checkVariabeleToewijzing(VariableAssignment variableAssignment) {
        System.out.println("VariableAssignment: " + variableAssignment.name.toString());
        checkExpressie(variableAssignment.expression);

    }

}
