package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.List;

public class Generator {

    public String generate(AST ast) {
        return generateStyleSheet(ast.root);
    }

    public String generateStyleSheet(Stylesheet stylesheet) {
        StringBuilder styleSheet = new StringBuilder();
        for (ASTNode node : stylesheet.getChildren()) {
            if (node instanceof Stylerule) {
                styleSheet.append(generateStijlRegel((Stylerule) node)).append("\n");
            }
        }
        return styleSheet.toString();
    }

    private String generateStijlRegel(Stylerule stijlregel) {
        StringBuilder stijlRegel = new StringBuilder();
        stijlRegel.append(generateSelector(stijlregel.selectors));

        for (ASTNode node : stijlregel.body) {
            if (node instanceof Declaration) {
                // Dit zijn twee spaties.
                stijlRegel.append("  ").append(generateDeclaratie((Declaration) node)).append("\n");
            }
        }
        return stijlRegel.append("}").toString();
    }

    private String generateSelector(List<Selector> selector) {
        StringBuilder selectors = new StringBuilder();
        for (Selector s : selector) {
            if (s instanceof TagSelector) {
                selectors.append(((TagSelector) s).tag);
            } else if (s instanceof ClassSelector) {
                selectors.append(((ClassSelector) s).cls);
            } else if (s instanceof IdSelector) {
                selectors.append(((IdSelector) s).id);
            }
        }
        return selectors + " {\n";
    }

    private String generateDeclaratie(Declaration declaratie) {
        return declaratie.property.name + ": " + generateExpression(declaratie.expression) + ";";
    }


    private String generateExpression(Expression expression) {
        if (expression instanceof Literal) {
            return generateLiteral((Literal) expression);
        }
        return null;
    }

    private String generateLiteral(Literal expression) {
        if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
        } else if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
        } else if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
        } else if (expression instanceof ScalarLiteral) {
            return ((ScalarLiteral) expression).value + "";
        } else {
            return null;
        }
    }
}
