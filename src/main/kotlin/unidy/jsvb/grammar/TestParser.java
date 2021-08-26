/*
 * TestParser.java
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
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A token stream parser.
 *
 * @author   UNIDY
 * @version  0.0
 */
public class TestParser extends RecursiveDescentParser {

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_1 = 3001;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_2 = 3002;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_3 = 3003;

    /**
     * Creates a new parser with a default analyzer.
     *
     * @param in             the input stream to read from
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public TestParser(Reader in) throws ParserCreationException {
        super(in);
        createPatterns();
    }

    /**
     * Creates a new parser.
     *
     * @param in             the input stream to read from
     * @param analyzer       the analyzer to use while parsing
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public TestParser(Reader in, TestAnalyzer analyzer)
        throws ParserCreationException {

        super(in, analyzer);
        createPatterns();
    }

    /**
     * Creates a new tokenizer for this parser. Can be overridden by a
     * subclass to provide a custom implementation.
     *
     * @param in             the input stream to read from
     *
     * @return the tokenizer created
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    protected Tokenizer newTokenizer(Reader in)
        throws ParserCreationException {

        return new TestTokenizer(in);
    }

    /**
     * Initializes the parser by creating all the production patterns.
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        ProductionPattern             pattern;
        ProductionPatternAlternative  alt;

        pattern = new ProductionPattern(TestConstants.X,
                                        "X");
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.IF, 1, 1);
        alt.addProduction(TestConstants.M, 1, 1);
        alt.addToken(TestConstants.THEN, 1, 1);
        alt.addProduction(TestConstants.Z, 1, 1);
        alt.addToken(TestConstants.ELSE, 1, 1);
        alt.addProduction(TestConstants.Z, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(TestConstants.Z,
                                        "Z");
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.ID, 1, 1);
        alt.addToken(TestConstants.AS, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(TestConstants.M,
                                        "M");
        alt = new ProductionPatternAlternative();
        alt.addProduction(TestConstants.A, 1, 1);
        alt.addProduction(SUBPRODUCTION_1, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(TestConstants.A,
                                        "A");
        alt = new ProductionPatternAlternative();
        alt.addProduction(TestConstants.B, 1, 1);
        alt.addProduction(SUBPRODUCTION_2, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(TestConstants.B,
                                        "B");
        alt = new ProductionPatternAlternative();
        alt.addProduction(TestConstants.C, 1, 1);
        alt.addProduction(SUBPRODUCTION_3, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(TestConstants.C,
                                        "C");
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.NUM, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.ID, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.LP, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        alt.addToken(TestConstants.RP, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_1,
                                        "Subproduction1");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.GT, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.LT, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.GE, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.LE, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.EQ, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.NE, 1, 1);
        alt.addProduction(TestConstants.A, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_2,
                                        "Subproduction2");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.ADD, 1, 1);
        alt.addProduction(TestConstants.B, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.SUB, 1, 1);
        alt.addProduction(TestConstants.B, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_3,
                                        "Subproduction3");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.MUL, 1, 1);
        alt.addProduction(TestConstants.C, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(TestConstants.DIV, 1, 1);
        alt.addProduction(TestConstants.C, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
