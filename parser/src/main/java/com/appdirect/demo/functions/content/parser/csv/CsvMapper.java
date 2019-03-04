package com.appdirect.demo.functions.content.parser.csv;

import com.appdirect.demo.functions.domain.exception.ParserException;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Stream;
import org.simpleflatmapper.csv.CsvParser;

public final class CsvMapper {

  public Stream<String[]> parse(Reader r) {
    try {
      return _parser().stream(r);

    } catch (IOException e) {
      throw new ParserException("Unable to parse", e);
    }
  }

  public Stream<String[]> parse(String payload) {
    try {
      return _parser().stream(payload);

    } catch (IOException e) {
      throw new ParserException("Unable to parse", e);
    }
  }

  // ----------------------------------------------------------------------- //

  private CsvParser.DSL _parser() {
    return CsvParser.separator(',').trimSpaces();
  }
}