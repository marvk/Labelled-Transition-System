package net.marvk.lts.compiler.parser;

import net.marvk.lts.compiler.lexer.*;
import net.marvk.lts.compiler.parser.syntaxtree.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class Parser {

    private final Queue<Token> queue;

    public Parser(final String input) {
        this.queue = Lexer.tokenize(input).collect(Collectors.toCollection(LinkedList::new));
    }

    private Token peek() {
        return queue.peek();
    }

    private boolean peek(final TokenType type) {
        return peek(Stream.of(type));
    }

    private boolean peek(final TokenType... types) {
        return peek(Arrays.stream(types));
    }

    private boolean peek(final List<TokenType> types) {
        return peek(types.stream());
    }

    private boolean peek(final Stream<TokenType> tags) {
        return tags.anyMatch(tag -> peek().getType().equals(tag));
    }

    private void consume(final TokenType type) throws ParseException {
        getAndConsume(type);
    }

    private Token getAndConsume(final TokenType type) throws ParseException {
        if (type.equals(peek().getType())) {
            return queue.poll();
        } else {
            throw new ParseException(peek(), type);
        }
    }

    public LtsNode parse() throws ParseException {
        final NameNode name = name();
        consume(SymbolTokenType.LEFT_PARENTHESIS);
        final Set<StateNode> initialStates = initials();
        consume(SymbolTokenType.RIGHT_PARENTHESIS);
        consume(SymbolTokenType.LEFT_BRACE);
        final Set<AssignNode> assigns = assignments();
        consume(SymbolTokenType.RIGHT_BRACE);
        consume(OtherTokenType.EOF);

        return new LtsNode(name, initialStates, assigns);
    }

    private Set<AssignNode> assignments() throws ParseException {
        final Set<AssignNode> result = new HashSet<>();

        while (peek(LexemeTokenType.IDENTIFIER)) {
            result.add(assign());
        }

        return result;
    }

    private AssignNode assign() throws ParseException {
        final Token startStateToken = getAndConsume(LexemeTokenType.IDENTIFIER);
        consume(SymbolTokenType.EQUALS);

        final AssignNode assignNode = new AssignNode(new StateNode(startStateToken.getLexeme()), transition());
        consume(SymbolTokenType.SEMICOLON);

        return assignNode;

    }

    private TransitionNode transition() throws ParseException {
        consume(SymbolTokenType.LEFT_PARENTHESIS);
        final Token symbolToken = getAndConsume(LexemeTokenType.IDENTIFIER);
        consume(SymbolTokenType.ARROW);
        final TransitionNode result;

        if (peek(SymbolTokenType.LEFT_PARENTHESIS)) {
            result = new NestedTransitionNode(new SymbolNode(symbolToken.getLexeme()), transition());
        } else {
            final Token goalState = getAndConsume(LexemeTokenType.IDENTIFIER);
            result = new TerminalTransitionNode(new SymbolNode(symbolToken.getLexeme()), new StateNode(goalState.getLexeme()));
        }

        consume(SymbolTokenType.RIGHT_PARENTHESIS);

        return result;
    }


    private Set<StateNode> initials() throws ParseException {
        final Set<StateNode> result = new HashSet<>();

        final Token initialStateToken = getAndConsume(LexemeTokenType.IDENTIFIER);
        result.add(new StateNode(initialStateToken.getLexeme()));

        while (peek(SymbolTokenType.COMMA)) {
            consume(SymbolTokenType.COMMA);
            final Token anotherInitialState = getAndConsume(LexemeTokenType.IDENTIFIER);
            result.add(new StateNode(anotherInitialState.getLexeme()));
        }

        return result;
    }

    private NameNode name() throws ParseException {
        final Token token = getAndConsume(LexemeTokenType.IDENTIFIER);

        return new NameNode(token.getLexeme());
    }
}
