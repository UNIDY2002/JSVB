/*
 * TestTokenizer.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 *
 * Nothing important.
 *
 * Copyright (c) 2017-2018 UNIDY. All rights reserved.
 */

package main.kotlin.unidy.jsvb.grammar;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A character stream tokenizer.
 *
 * @author   UNIDY
 * @version  0.0
 */
public class TestTokenizer extends Tokenizer {

    /**
     * Creates a new tokenizer for the specified input stream.
     *
     * @param input          the input stream to read
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    public TestTokenizer(Reader input) throws ParserCreationException {
        super(input, false);
        createPatterns();
    }

    /**
     * Initializes the tokenizer by creating all the token patterns.
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        TokenPattern  pattern;

        pattern = new TokenPattern(TestConstants.ADD,
                                   "ADD",
                                   TokenPattern.STRING_TYPE,
                                   "+");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.SUB,
                                   "SUB",
                                   TokenPattern.STRING_TYPE,
                                   "-");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.MUL,
                                   "MUL",
                                   TokenPattern.STRING_TYPE,
                                   "*");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.DIV,
                                   "DIV",
                                   TokenPattern.STRING_TYPE,
                                   "/");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.LP,
                                   "LP",
                                   TokenPattern.STRING_TYPE,
                                   "(");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.RP,
                                   "RP",
                                   TokenPattern.STRING_TYPE,
                                   ")");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.IF,
                                   "IF",
                                   TokenPattern.STRING_TYPE,
                                   "if");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.THEN,
                                   "THEN",
                                   TokenPattern.STRING_TYPE,
                                   "then");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.ELSE,
                                   "ELSE",
                                   TokenPattern.STRING_TYPE,
                                   "else");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.NUM,
                                   "NUM",
                                   TokenPattern.REGEXP_TYPE,
                                   "[1-9][0-9]*");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.ID,
                                   "ID",
                                   TokenPattern.REGEXP_TYPE,
                                   "[a-zA-Z][a-zA-Z0-9_]*");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.AS,
                                   "AS",
                                   TokenPattern.STRING_TYPE,
                                   "<-");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.GT,
                                   "GT",
                                   TokenPattern.STRING_TYPE,
                                   ">");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.LT,
                                   "LT",
                                   TokenPattern.STRING_TYPE,
                                   "<");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.GE,
                                   "GE",
                                   TokenPattern.STRING_TYPE,
                                   ">=");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.LE,
                                   "LE",
                                   TokenPattern.STRING_TYPE,
                                   "<=");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.EQ,
                                   "EQ",
                                   TokenPattern.STRING_TYPE,
                                   "=");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.NE,
                                   "NE",
                                   TokenPattern.STRING_TYPE,
                                   "<>");
        addPattern(pattern);

        pattern = new TokenPattern(TestConstants.WHITESPACE,
                                   "WHITESPACE",
                                   TokenPattern.REGEXP_TYPE,
                                   "[ \\t\\n\\r]+");
        pattern.setIgnore();
        addPattern(pattern);
    }
}
