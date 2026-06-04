package io.github.core.queryx.parser;

import io.github.core.queryx.metadata.QueryFieldMetadata;

import java.util.List;

public interface QueryParser {

    List<QueryFieldMetadata> parse(Object query);

}
