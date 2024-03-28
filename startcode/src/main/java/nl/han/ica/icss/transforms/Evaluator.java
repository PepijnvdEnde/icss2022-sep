package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.*;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();

    }

    @Override
    public void apply(AST ast) {
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet stylesheet) {
        variableValues.addFirst(new HashMap<>());
        List<ASTNode> nodesToRemove = new ArrayList<>();
            for (ASTNode node : stylesheet.getChildren()) {
                if (node instanceof Stylerule) {
                    applyStijlRegel(((Stylerule) node).body);
                } else if (node instanceof VariableAssignment) {
                    applyVariabeleToewijzing((VariableAssignment) node);
                    nodesToRemove.add(node);
                }
            }

        for (ASTNode child : nodesToRemove) {
            stylesheet.removeChild(child);
        }

        variableValues.removeFirst();
    }

    private void applyStijlRegel(List<ASTNode> stijlRegel) {
        variableValues.addFirst(new HashMap<>());

        List<ASTNode> nodesToRemove = new ArrayList<>();
        List<ASTNode> nodesToAdd = new ArrayList<>();

        for (ASTNode node : stijlRegel) {
            if (node instanceof Declaration) {
                applyDeclaratie((Declaration) node);
            } else if (node instanceof IfClause) {
                nodesToAdd.addAll(applyIfClause((IfClause) node));
                nodesToRemove.add(node);
            } else if (node instanceof VariableAssignment) {
                applyVariabeleToewijzing((VariableAssignment) node);
                nodesToRemove.add(node);
            }
        }

        for (ASTNode node : nodesToRemove) {
            stijlRegel.remove(node);
        }

        stijlRegel.addAll(nodesToAdd);
        variableValues.removeFirst();
    }

    private List<ASTNode> applyIfClause(IfClause ifClause) {
        boolean ifClauseIsTrue = ((BoolLiteral) Objects.requireNonNull(evaluateExpression(ifClause.conditionalExpression))).value;

        if (!ifClauseIsTrue) {
            if (ifClause.elseClause == null) {
                return new ArrayList<>();
            }
            applyStijlRegel(ifClause.elseClause.body);
            return ifClause.elseClause.body;
        }

        applyStijlRegel(ifClause.body);
        return ifClause.body;
    }

    private void applyVariabeleToewijzing(VariableAssignment variableAssignment) {
        Literal literal = evaluateExpression(variableAssignment.expression);
        assert variableValues.peek() != null;
        variableValues.peek().put(variableAssignment.name.name, literal);
        variableAssignment.expression = literal;
    }

    private void applyDeclaratie(Declaration declaration) {
        declaration.expression = evaluateExpression(declaration.expression);
    }


    private Literal evaluateExpression(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        } else if (expression instanceof MultiplyOperation) {
            return evaluateMultiplyOperation((MultiplyOperation) expression);
        } else if (expression instanceof AddOperation) {
            return evaluateAddOperation((AddOperation) expression);
        } else if (expression instanceof SubtractOperation) {
            return evaluateSubtractOperation((SubtractOperation) expression);
        } else if (expression instanceof VariableReference) {
            return evaluateVariableReference((VariableReference) expression);
        }
        return null;
    }

    private Literal evaluateVariableReference(VariableReference variableReference) {
        for (HashMap<String, Literal> variableValue : variableValues) {
            if (variableValue.containsKey(variableReference.name)) {
                return variableValue.get(variableReference.name);
            }
        }
        return null;
    }

    private Literal evaluateAddOperation(AddOperation operation) {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal evaluateSubtractOperation(SubtractOperation operation) {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal evaluateMultiplyOperation(MultiplyOperation operation) {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);
        if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        }
        return null;
    }


}
