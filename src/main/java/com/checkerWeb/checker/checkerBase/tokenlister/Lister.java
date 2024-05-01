package com.checkerWeb.checker.checkerBase.tokenlister;
import java.util.ArrayList;
import java.io.*;
import com.checkerWeb.checker.checkerBase.sablecc.node.*;
import com.checkerWeb.checker.checkerBase.sablecc.node.Token;

public class Lister {
	private static com.checkerWeb.checker.checkerBase.sablecc.lexer.Lexer lex;
	private static com.checkerWeb.checker.checkerBase.sablecc.java_lexer.Lexer jlex;
	private static ArrayList<Token> tokens;

	public static ArrayList<Token> ConvertToList(String a, String lang) {
	
		tokens = new ArrayList<Token>();
		Token T;
		if(lang.contentEquals(".cpp")) {
				lex = new com.checkerWeb.checker.checkerBase.sablecc.lexer.Lexer
						(new PushbackReader
						(new StringReader(a), 1024));
				try {
					T = lex.next();
					while (!(T instanceof EOF)) {
						tokens.add(T);
						T = lex.next();
					}
				}
				catch (com.checkerWeb.checker.checkerBase.sablecc.lexer.LexerException le)
				{ System.out.println ("Lexer Exception " + le); }
				catch (IOException ioe)
				{ System.out.println ("IO Exception " + ioe); }
			} // end of c++ check
			else if(lang.equals(".java")) {
				jlex = new com.checkerWeb.checker.checkerBase.sablecc.java_lexer.Lexer
						(new PushbackReader
						(new StringReader(a), 1024));
				try {
					T = jlex.next();
					while (!(T instanceof EOF)) {
						tokens.add(T);
						T = jlex.next();
					}
				}
				catch (com.checkerWeb.checker.checkerBase.sablecc.java_lexer.LexerException le)
				{ System.out.println ("Lexer Exception " + le); }
				catch (IOException ioe)
				{ System.out.println ("IO Exception " + ioe); }
			} // end of java check
		return tokens;
	} 
}
