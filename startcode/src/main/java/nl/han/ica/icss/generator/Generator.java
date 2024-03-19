package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;

public class Generator {

	public String generate(AST ast) {
        for (int i = 0; i < ast.root.body.size(); i++) {
            return ast.root.body.get(i).toString();

        }
    return null;
	}
}
