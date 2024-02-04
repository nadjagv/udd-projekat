package com.udd.uddprojekat.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.experimental.UtilityClass;
import org.elasticsearch.common.unit.Fuzziness;

import java.util.List;
import java.util.Stack;

@UtilityClass
public class QueryBuilder {

    public static Query buildSimpleSearchQuery(List<String> tokens, List<String> fieldNames) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            tokens.forEach(token -> {
                b.should(sb -> sb.match(
                        m -> m.field("title").fuzziness(Fuzziness.ONE.asString()).query(token)));
                fieldNames.forEach(fieldName -> {
                    b.should(sb -> sb.match(m -> m.field(fieldName).fuzziness(Fuzziness.ONE.asString()).query(token)));
                });
            });
            return b;
        })))._toQuery();
    }

    public static Query buildAdvancedSearchQuery(List<String> expression) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            parseWithPrecedence(expression, b);
            return b;
        })))._toQuery();
    }

    public static void parseWithPrecedence(List<String> expression, BoolQuery.Builder b) {
        var operatorStack = new Stack<String>();
        var operandStack = new Stack<String>();
        var stringBuilder = new StringBuilder();
        for (var token : expression) {
            if (isOperator(token)) {
                if (operatorStack.isEmpty()) {
                    operatorStack.push(token);
                    continue;
                }
                while (true) {
                    if (operatorStack.isEmpty()) {
                        operatorStack.push(token);
                        break;
                    }

                    var topOfStack = operatorStack.peek();
                    var stackOperandPrecedenceScore = getPrecedenceScore(topOfStack);
                    var inputOperandPrecedenceScore = getPrecedenceScore(token);

                    if (inputOperandPrecedenceScore > stackOperandPrecedenceScore) {
                        operatorStack.push(token);
                        break;
                    }

                    var operatorToApply = operatorStack.pop();
                    stringBuilder.append(operatorToApply);
                    switch (operatorToApply.strip().toLowerCase()) {
                        case "not" -> addNot(operandStack, b);
                        case "and" -> addAnd(operandStack, b);
                        case "or" -> addOr(operandStack, b);
                        default -> System.out.println(operatorToApply);
                    }
                }
            } else {
                operandStack.push(token);
                stringBuilder.append(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            var operatorToApply = operatorStack.pop();
            stringBuilder.append(operatorToApply);
            switch (operatorToApply.strip().toLowerCase()) {
                case "not" -> addNot(operandStack, b);
                case "and" -> addAnd(operandStack, b);
                case "or" -> addOr(operandStack, b);
                default -> System.out.println(operatorToApply);
            }
        }

        System.out.println("\n\n------------");
        System.out.println(stringBuilder);
        System.out.println("------------\n\n");
    }

    private static boolean isOperator(String input) {
        return switch (input.strip().toLowerCase()) {
            case "not", "and", "or" -> true;
            default -> false;
        };
    }

    private static int getPrecedenceScore(String input) {
        return switch (input.strip().toLowerCase()) {
            case "not" -> 2;
            case "and" -> 1;
            case "or" -> 0;
            default -> 0;
        };
    }

    private void addNot(Stack<String> operandStack, BoolQuery.Builder b) {
        var operand = operandStack.pop().split(":");
        var field = operand[0];
        var value = operand[1];
        b.mustNot(sb -> sb.match
                (m -> m.field(field).fuzziness(Fuzziness.ONE.asString()).query(value)));
    }

    private void addAnd(Stack<String> operandStack, BoolQuery.Builder b) {
        var operand1 = operandStack.pop().split(":");
        var field1 = operand1[0];
        var value1 = operand1[1];
        b.must(sb -> sb.match(
                m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
    }

    private void addOr(Stack<String> operandStack, BoolQuery.Builder b) {
        var operand1 = operandStack.pop().split(":");
        var field1 = operand1[0];
        var value1 = operand1[1];
        b.should(sb -> sb.match(
                m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
    }
}
