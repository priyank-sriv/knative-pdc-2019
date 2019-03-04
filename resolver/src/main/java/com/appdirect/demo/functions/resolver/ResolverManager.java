package com.appdirect.demo.functions.resolver;

import static java.util.stream.Collectors.toMap;

import com.appdirect.demo.functions.domain.bo.FieldResolverConfig;
import com.appdirect.demo.functions.domain.bo.RawEvent;
import com.appdirect.demo.functions.domain.exception.resolver.ResolverNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.springframework.stereotype.Component;

@Component
public class ResolverManager {

  private final Map<String, Resolver> resolvers;

  public ResolverManager() {
    this.resolvers = new HashMap<>();
    load();
  }

  public Resolver get(String id) {
    Resolver resolver = resolvers.get(id);
    if (resolver == null) {
      throw new ResolverNotFoundException("Resolver Not Found for Id: ".concat(id));
    }

    return resolver;
  }

  public <T> T apply(RawEvent rawEvent, FieldResolverConfig.Field field) {
    Resolver<T> resolver = get(field.getResolverId());
    Map<String, String> rawFields = field.getRef() != null ?
        field.getRef().stream()
            .collect(toMap(
                FieldResolverConfig.FieldReference::getName,
                ref -> rawEvent.getFields().get(ref.getArg()))) :
        new HashMap<>();

    return resolver.resolve(rawFields);
  }

  private void load() {
    ServiceLoader.load(Resolver.class)
        .forEach(resolver -> resolvers.put(resolver.getId(), resolver));
  }
}
