package com.ynthm.common.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author ethan
 */
public class Constant {
  private Constant() {}

  public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;

  public static final String UTF_8 = CHARSET_UTF_8.name();

  /** zh-CN */
  public static final String SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE.toLanguageTag();
}
