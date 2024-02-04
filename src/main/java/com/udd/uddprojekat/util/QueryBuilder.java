package com.udd.uddprojekat.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.experimental.UtilityClass;
import org.elasticsearch.common.unit.Fuzziness;

import java.util.List;

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

    public static Query buildAdvancedSearchQuery(List<String> operands, String operation) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            var field1 = operands.get(0).split(":")[0];
            var value1 = operands.get(0).split(":")[1];
            var field2 = operands.get(1).split(":")[0];
            var value2 = operands.get(1).split(":")[1];

            switch (operation) {
                case "AND":
                    b.must(sb -> sb.match(
                            m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
                    b.must(sb -> sb.match(m -> m.field(field2).query(value2)));
                    break;
                case "OR":
                    b.should(sb -> sb.match(
                            m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
                    b.should(sb -> sb.match(m -> m.field(field2).query(value2)));
                    break;
                case "NOT":
                    b.must(sb -> sb.match(
                            m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
                    b.mustNot(sb -> sb.match(m -> m.field(field2).query(value2)));
                    break;
            }

            return b;
        })))._toQuery();
    }
}
