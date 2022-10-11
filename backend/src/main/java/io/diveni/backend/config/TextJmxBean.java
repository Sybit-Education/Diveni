package io.diveni.backend.config;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ManagedResource
public class TextJmxBean {
  private final Map<String, String> map = new HashMap<>();

  @ManagedAttribute
  public int getSize() {
    return map.size();
  }

  @ManagedOperation
  public String get( String key ) {
    return map.get( key );
  }


  @ManagedOperation
  public String put( String key, String value ) {
    return map.put( key, value );
  }

  public void remove( String key ) {
    map.remove( key );
  }
}
