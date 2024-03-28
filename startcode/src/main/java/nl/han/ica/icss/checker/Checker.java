package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode node : stylesheet.getChildren()) {
            if (node instanceof Stylerule) {
                checkStijlRegel((Stylerule) node);
            } else if (node instanceof VariableAssignment) {
                checkVariabeleToewijzing((VariableAssignment) node);
            } else {
                node.setError("Onbekend type: geen stijlregel of variabele toewijzing");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkStijlRegel(Stylerule stylerule) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode node : stylerule.body) {
            if (node instanceof Declaration) {
                checkDeclaratie((Declaration) node);
            } else if (node instanceof IfClause) {
                checkIfClause((IfClause) node);
            } else if (node instanceof VariableAssignment) {
                checkVariabeleToewijzing((VariableAssignment) node);
            } else {
                node.setError("Onbekend type: geen declaratie, if-clause of variabele toewijzing");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkDeclaratie(Declaration declaration) {
        ExpressionType expressionType = checkExpressie(declaration.expression);
        if (expressionType != ExpressionType.UNDEFINED) {

            switch (declaration.property.name) {
                case "background-color":
                    if (expressionType != ExpressionType.COLOR) {
                        declaration.setError("Alleen color expressies zijn toegestaan voor background-color");
                    }
                    break;
                case "color":
                    if (expressionType != ExpressionType.COLOR) {
                        declaration.setError("Alleen color expressies zijn toegestaan voor color");
                    }
                    break;
                case "width":
                    if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                        declaration.setError("Alleen pixel en percentage expressies zijn toegestaan voor width");
                    }
                    break;
                case "height":
                    if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                        declaration.setError("Alleen pixel expressies zijn toegestaan voor height");
                    }
                    break;
                default:
                    declaration.setError("Onbekende eigenschap: " + declaration.property.name);
            }
        }
    }

    private ExpressionType checkExpressie(Expression expression) {
        if (expression instanceof VariableReference) {
            return checkVariabeleReferentie((VariableReference) expression);
        } else if (expression instanceof Operation) {
            return checkOperationType((Operation) expression);
        } else if (expression instanceof Literal) {
            return checkLiteral((Literal) expression);
        }
        expression.setError("Onbekende expressie: " + expression.getClass().getSimpleName());
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkLiteral(Literal literal) {
        if (literal instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (literal instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (literal instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (literal instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (literal instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        }

        literal.setError("Onbekende literal: " + literal.getClass().getSimpleName());
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkVariabeleReferentie(VariableReference variableReference) {
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(variableReference.name)) {
                return scope.get(variableReference.name);
            }
        }

        variableReference.setError("Variable" + variableReference.name + " is niet gedefinieerd");
        return ExpressionType.UNDEFINED;

    }

    private ExpressionType checkOperationType(Operation operation) {
        for (ASTNode child : operation.getChildren()) {
            if (child instanceof ColorLiteral) {
                child.setError("Color literals zijn niet toegestaan in operaties");
                return ExpressionType.UNDEFINED;
            } else if (child instanceof BoolLiteral) {
                child.setError("Boolean literals zijn niet toegestaan in operaties");
                return ExpressionType.UNDEFINED;
            }
        }

        if (operation instanceof AddOperation) {
            return checkAddOperation((AddOperation) operation);
        } else if (operation instanceof SubtractOperation) {
            return checkSubtractOperation((SubtractOperation) operation);
        } else if (operation instanceof MultiplyOperation) {
            return checkMultiplyOperation((MultiplyOperation) operation);
        } else {
            operation.setError("Onbekende operatie");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkAddOperation(AddOperation addOperation) {
        ExpressionType leftType = checkExpressie(addOperation.lhs);
        ExpressionType rightType = checkExpressie(addOperation.rhs);

        if (leftType == rightType) {
            return leftType;
        } else {
            addOperation.setError("Plus operatie kan alleen worden gebruikt met expressies van hetzelfde type");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkSubtractOperation(SubtractOperation subtractOperation) {
        ExpressionType leftType = checkExpressie(subtractOperation.lhs);
        ExpressionType rightType = checkExpressie(subtractOperation.rhs);

        if (leftType == rightType) {
            return leftType;
        } else {
            subtractOperation.setError("Min operatie kan alleen worden gebruikt met expressies van hetzelfde type");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkMultiplyOperation(MultiplyOperation multiplyOperation) {
        ExpressionType leftType = checkExpressie(multiplyOperation.lhs);
        ExpressionType rightType = checkExpressie(multiplyOperation.rhs);

        if (leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) {
            multiplyOperation.setError("Keer operatie kan alleen worden gebruikt met een expressie van het type scalar en een expressie van een ander type");
            return ExpressionType.UNDEFINED;
        }

        if (leftType == ExpressionType.SCALAR) {
            return rightType;
        } else {
            return leftType;
        }
    }

    private void checkIfClause(IfClause ifClause) {
        variableTypes.addFirst(new HashMap<>());

        if (ifClause.conditionalExpression instanceof VariableReference) {
            if (checkVariabeleReferentie((VariableReference) ifClause.conditionalExpression) != ExpressionType.BOOL) {
                ifClause.conditionalExpression.setError("If clause kan alleen worden gebruikt met een boolean expressie");
            }
        }

        for (ASTNode child : ifClause.body) {
            if (child instanceof VariableAssignment) {
                checkVariabeleToewijzing((VariableAssignment) child);
            } else if (child instanceof Declaration) {
                checkDeclaratie((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            } else if (child instanceof ElseClause) {
                checkElseClause((ElseClause) child);
            } else {
                child.setError("If clause kan alleen worden gebruikt met declaraties, variabele toewijzingen, if-clauses en else-clauses");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkElseClause(ElseClause elseClause) {
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode child : elseClause.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariabeleToewijzing((VariableAssignment) child);
            } else if (child instanceof Declaration) {
                checkDeclaratie((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            } else {
                child.setError("Else clause kan alleen worden gebruikt met declaraties, variabele toewijzingen en if-clauses");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkVariabeleToewijzing(VariableAssignment variableAssignment) {
        variableTypes.getFirst().put(variableAssignment.name.name, checkExpressie(variableAssignment.expression));

    }

}
