package com.ynthm.autoconfigure.cache.support;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ethan Wang
 */
@Data
@NoArgsConstructor
public class CacheMessage implements Serializable {

  private static final long serialVersionUID = -1L;

  private String cacheName;

  private Object key;

  public CacheMessage(String cacheName, Object key) {
    super();
    this.cacheName = cacheName;
    this.key = key;
  }
}
