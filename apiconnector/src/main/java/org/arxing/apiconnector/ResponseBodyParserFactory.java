package org.arxing.apiconnector;



import java.util.HashMap;
import java.util.Map;

public class ResponseBodyParserFactory {
    private static ResponseBodyParserFactory instance = new ResponseBodyParserFactory();
    private Map<ResponseType, ResponseBodyParser> parsers = new HashMap<>();

    private ResponseBodyParserFactory() {
    }

    public static ResponseBodyParserFactory getInstance() {
        return instance;
    }

    public void registerParser(ResponseType type, ResponseBodyParser parser) {
        parsers.put(type, parser);
    }

    public ResponseBodyParser findParser(ResponseType type) {
        return parsers.get(type);
    }
}
