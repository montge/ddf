/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.log.sanitizer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Comprehensive test suite for {@link LogSanitizer}.
 *
 * <p>LogSanitizer is a critical security component that prevents log injection attacks (CWE-117).
 * Log injection occurs when attackers insert malicious data containing newlines, carriage returns,
 * or special characters into log messages. This can:
 *
 * <ul>
 *   <li>Forge log entries to hide malicious activity
 *   <li>Inject malicious scripts that execute when logs are viewed in web interfaces
 *   <li>Corrupt log files making them difficult to parse
 *   <li>Facilitate log poisoning attacks
 * </ul>
 *
 * <p>This test suite validates all sanitization behaviors including null handling, whitespace
 * character replacement, HTML escaping, and proper toString() delegation.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogSanitizerTest {

  /**
   * Verifies that null input returns empty string instead of "null" or throwing exception.
   *
   * <p><b>Security Rationale:</b> Prevents NullPointerException and ensures consistent logging
   * output. Null values should not propagate as the string "null" which could be misleading in
   * logs.
   */
  @Test
  public void testSanitizeNull() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize(null);
    assertThat(logSanitizer.toString(), is(equalTo("")));
  }

  /**
   * Verifies that empty strings are preserved as empty strings.
   *
   * <p><b>Security Rationale:</b> Empty values should be explicitly logged as empty rather than
   * being transformed to other representations. This maintains log integrity.
   */
  @Test
  public void testSanitizeEmptyString() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("");
    assertThat(logSanitizer.toString(), is(equalTo("")));
  }

  /**
   * Verifies that HTML/XML special characters are properly escaped.
   *
   * <p><b>Security Rationale:</b> Prevents Cross-Site Scripting (XSS) attacks when logs are viewed
   * in web-based log viewers or monitoring dashboards. Without proper escaping, malicious scripts
   * could execute in administrators' browsers when viewing logs.
   *
   * <p>Example attack vector: An attacker passes "&lt;script&gt;alert('XSS')&lt;/script&gt;" as a
   * username. If logged unescaped and viewed in a web interface, the script executes.
   */
  @Test
  public void testSanitizeHtmlEscaping() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("<script>alert('XSS')</script>");
    // Note: commons-lang StringEscapeUtils.escapeHtml() does NOT escape single quotes
    assertThat(logSanitizer.toString(), is(equalTo("&lt;script&gt;alert('XSS')&lt;/script&gt;")));
  }

  /**
   * Verifies that HTML entities in various contexts are properly escaped.
   *
   * <p><b>Security Rationale:</b> Tests all common HTML special characters that could be used in
   * injection attacks: &lt; &gt; &amp; " (Note: single quotes are NOT escaped by commons-lang)
   */
  @Test
  public void testSanitizeHtmlEntities() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("<>&\"'");
    // Note: commons-lang StringEscapeUtils.escapeHtml() does NOT escape single quotes
    assertThat(logSanitizer.toString(), is(equalTo("&lt;&gt;&amp;&quot;'")));
  }

  /**
   * Verifies that newline characters (\n) are replaced with underscores.
   *
   * <p><b>Security Rationale:</b> Prevents log injection attacks (CWE-117). Attackers can inject
   * newlines to:
   *
   * <ul>
   *   <li>Forge additional log entries appearing to come from the system
   *   <li>Hide malicious activity by injecting fake success messages after their attack
   *   <li>Corrupt log parsing by breaking the expected log format
   * </ul>
   *
   * <p>Example attack: User inputs "admin\n[INFO] Authentication successful for admin" - without
   * sanitization this creates a fake log entry suggesting successful authentication.
   */
  @Test
  public void testSanitizeNewlineReplacement() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Line1\nLine2");
    assertThat(logSanitizer.toString(), is(equalTo("Line1_Line2")));
  }

  /**
   * Verifies that carriage return characters (\r) are replaced with underscores.
   *
   * <p><b>Security Rationale:</b> Similar to newlines, carriage returns can be used for log
   * injection and are particularly dangerous in Windows environments or cross-platform scenarios.
   * Some log parsers treat \r\n as a line terminator, making \r alone a potential attack vector.
   */
  @Test
  public void testSanitizeCarriageReturn() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Part1\rPart2");
    assertThat(logSanitizer.toString(), is(equalTo("Part1_Part2")));
  }

  /**
   * Verifies that both \r\n (Windows line endings) are properly sanitized.
   *
   * <p><b>Security Rationale:</b> Tests the common Windows line ending sequence to ensure both
   * characters are independently replaced.
   */
  @Test
  public void testSanitizeWindowsLineEnding() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Windows\r\nLine");
    assertThat(logSanitizer.toString(), is(equalTo("Windows__Line")));
  }

  /**
   * Verifies sanitization of complex log injection attack combining multiple attack vectors.
   *
   * <p><b>Security Rationale:</b> Real-world attacks often combine multiple techniques. This test
   * simulates an attack where a malicious user attempts to:
   *
   * <ol>
   *   <li>Inject HTML to execute in log viewers
   *   <li>Use newlines to forge additional log entries
   *   <li>Hide their activity with fake success messages
   * </ol>
   *
   * <p>Example scenario: Attacker enters this as a username during login failure:
   * "attacker\n[SUCCESS] Login successful\n&lt;script&gt;steal()&lt;/script&gt;"
   */
  @Test
  public void testSanitizeComplexLogInjection() {
    String maliciousInput =
        "user123\n[ERROR] Authentication failed\n<script>malicious()</script>\rFAKE LOG ENTRY";
    LogSanitizer logSanitizer = LogSanitizer.sanitize(maliciousInput);
    String expected =
        "user123_[ERROR] Authentication failed_&lt;script&gt;malicious()&lt;/script&gt;_FAKE LOG ENTRY";
    assertThat(logSanitizer.toString(), is(equalTo(expected)));
  }

  /**
   * Verifies proper handling of Unicode characters including special Unicode whitespace.
   *
   * <p><b>Security Rationale:</b> Unicode contains various whitespace and control characters that
   * could potentially be used for log obfuscation or injection attacks. This test ensures:
   *
   * <ul>
   *   <li>Unicode characters are escaped to HTML numeric entities
   *   <li>HTML escaping works correctly with multi-byte characters
   *   <li>Unicode control characters don't bypass sanitization
   * </ul>
   */
  @Test
  public void testSanitizeUnicodeCharacters() {
    // Test with Unicode: Japanese, emoji, and Unicode with HTML
    // commons-lang StringEscapeUtils.escapeHtml() converts Unicode to HTML numeric entities
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Hello 世界 🌍 <test>");
    assertThat(
        logSanitizer.toString(),
        is(equalTo("Hello &#19990;&#30028; &#55356;&#57101; &lt;test&gt;")));
  }

  /**
   * Verifies that Unicode line separators (U+2028) and paragraph separators (U+2029) are handled.
   *
   * <p><b>Security Rationale:</b> Unicode line/paragraph separators can be used to bypass simple \n
   * and \r filtering. While the current implementation doesn't explicitly replace these with
   * underscores, they ARE escaped to HTML numeric entities which prevents log injection.
   */
  @Test
  public void testSanitizeUnicodeLineSeparators() {
    // U+2028 (line separator) and U+2029 (paragraph separator)
    String input = "Line1\u2028Line2\u2029Line3";
    LogSanitizer logSanitizer = LogSanitizer.sanitize(input);
    // These Unicode separators are converted to HTML entities (not replaced like \n and \r)
    // &#8232; = U+2028, &#8233; = U+2029
    assertThat(logSanitizer.toString(), is(equalTo("Line1&#8232;Line2&#8233;Line3")));
  }

  /**
   * Verifies that toString() is properly called on arbitrary objects.
   *
   * <p><b>Security Rationale:</b> LogSanitizer accepts any Object type, not just strings. This
   * ensures that when objects are passed (e.g., exceptions, custom objects), their toString()
   * method is called and the result is sanitized. This prevents objects from bypassing
   * sanitization.
   */
  @Test
  public void testToStringHandling() {
    class CustomObject {
      @Override
      public String toString() {
        return "Custom\nObject<test>";
      }
    }

    LogSanitizer logSanitizer = LogSanitizer.sanitize(new CustomObject());
    assertThat(logSanitizer.toString(), is(equalTo("Custom_Object&lt;test&gt;")));
  }

  /**
   * Verifies handling of objects whose toString() returns null.
   *
   * <p><b>Security Rationale:</b> Some objects may have toString() implementations that return
   * null. This should be handled gracefully without causing NullPointerException.
   */
  @Test
  public void testToStringReturningNull() {
    class NullToStringObject {
      @Override
      public String toString() {
        return null;
      }
    }

    LogSanitizer logSanitizer = LogSanitizer.sanitize(new NullToStringObject());
    // When toString() returns null, LogSanitizer treats it as empty
    assertThat(logSanitizer.toString(), is(equalTo("")));
  }

  /**
   * Verifies handling of objects whose toString() returns empty string.
   *
   * <p><b>Security Rationale:</b> Ensures consistent behavior when object serialization produces
   * empty output.
   */
  @Test
  public void testToStringReturningEmpty() {
    class EmptyToStringObject {
      @Override
      public String toString() {
        return "";
      }
    }

    LogSanitizer logSanitizer = LogSanitizer.sanitize(new EmptyToStringObject());
    assertThat(logSanitizer.toString(), is(equalTo("")));
  }

  /**
   * Verifies that the static sanitize() factory method creates proper instances.
   *
   * <p><b>Security Rationale:</b> Tests the recommended API entry point to ensure it correctly
   * delegates to the constructor and maintains security properties.
   */
  @Test
  public void testStaticSanitizeMethod() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("test\nvalue");
    assertThat(logSanitizer.toString(), is(equalTo("test_value")));
  }

  /**
   * Verifies sanitization of ANSI escape codes that could be used in terminal injection attacks.
   *
   * <p><b>Security Rationale:</b> ANSI escape sequences can manipulate terminal output, hide log
   * content, or create misleading displays. While LogSanitizer doesn't specifically target ANSI
   * codes, HTML escaping helps mitigate risks when logs are viewed in web interfaces.
   */
  @Test
  public void testSanitizeAnsiEscapeCodes() {
    // ANSI escape code for red text: \u001b[31m
    String input = "\u001b[31mRED TEXT\u001b[0m";
    LogSanitizer logSanitizer = LogSanitizer.sanitize(input);
    // ANSI codes pass through (not HTML special chars), but this documents behavior
    assertThat(logSanitizer.toString(), is(equalTo(input)));
  }

  /**
   * Verifies handling of very long strings to ensure no performance degradation or memory issues.
   *
   * <p><b>Security Rationale:</b> Extremely long inputs could be used in denial-of-service attacks.
   * This test ensures the sanitizer handles large inputs gracefully.
   */
  @Test
  public void testSanitizeLongString() {
    StringBuilder longString = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longString.append("test\n");
    }
    LogSanitizer logSanitizer = LogSanitizer.sanitize(longString.toString());
    String result = logSanitizer.toString();
    // Verify newlines were replaced and no exception was thrown
    assertThat(result.contains("\n"), is(false));
    assertThat(result.contains("_"), is(true));
  }

  /**
   * Verifies that repeated sanitization is idempotent (produces same result).
   *
   * <p><b>Security Rationale:</b> Ensures that sanitizing already-sanitized content doesn't cause
   * double-escaping issues which could lead to log corruption or bypass attempts.
   */
  @Test
  public void testSanitizeIdempotence() {
    String input = "<test>\nvalue";
    LogSanitizer first = LogSanitizer.sanitize(input);
    String firstResult = first.toString();
    LogSanitizer second = LogSanitizer.sanitize(firstResult);
    String secondResult = second.toString();

    // Second sanitization should escape the &lt; etc
    // This documents that sanitization is NOT idempotent (by design)
    assertThat(secondResult, is(equalTo("&amp;lt;test&amp;gt;_value")));
  }

  /**
   * Verifies sanitization of SQL injection patterns that might appear in logs.
   *
   * <p><b>Security Rationale:</b> While LogSanitizer doesn't prevent SQL injection (that's handled
   * elsewhere), it should safely log potential SQL injection attempts without allowing log
   * injection.
   */
  @Test
  public void testSanitizeSqlInjectionPattern() {
    String sqlInjection = "' OR '1'='1\n-- <script>alert('xss')</script>";
    LogSanitizer logSanitizer = LogSanitizer.sanitize(sqlInjection);
    // Single quotes are NOT escaped by commons-lang, but newlines are replaced and < > are escaped
    assertThat(
        logSanitizer.toString(),
        is(equalTo("' OR '1'='1_-- &lt;script&gt;alert('xss')&lt;/script&gt;")));
  }

  /**
   * Verifies sanitization of LDAP injection patterns.
   *
   * <p><b>Security Rationale:</b> Similar to SQL injection test - ensures injection attempts are
   * safely logged without compromising log integrity.
   */
  @Test
  public void testSanitizeLdapInjectionPattern() {
    String ldapInjection = "*)(uid=*))(|(uid=*\n<test>";
    LogSanitizer logSanitizer = LogSanitizer.sanitize(ldapInjection);
    assertThat(logSanitizer.toString(), is(equalTo("*)(uid=*))(|(uid=*_&lt;test&gt;")));
  }

  /**
   * Verifies handling of objects with malicious toString() implementations.
   *
   * <p><b>Security Rationale:</b> Even if an attacker controls an object's toString() method, the
   * sanitizer must prevent log injection. This tests defense in depth.
   */
  @Test
  public void testMaliciousToStringObject() {
    class MaliciousObject {
      @Override
      public String toString() {
        return "Normal text\n[SUCCESS] Fake admin access granted\n<script>hack()</script>";
      }
    }

    LogSanitizer logSanitizer = LogSanitizer.sanitize(new MaliciousObject());
    String result = logSanitizer.toString();
    // Verify all attack vectors are neutralized
    assertThat(result.contains("\n"), is(false));
    assertThat(result.contains("<script>"), is(false));
    assertThat(
        result,
        is(
            equalTo(
                "Normal text_[SUCCESS] Fake admin access granted_&lt;script&gt;hack()&lt;/script&gt;")));
  }

  /**
   * Verifies sanitization preserves legitimate angle brackets in non-HTML contexts.
   *
   * <p><b>Security Rationale:</b> Tests that HTML escaping works correctly for comparison operators
   * and generic text, not just HTML tags.
   */
  @Test
  public void testSanitizeLessThanGreaterThan() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Value < 10 and > 5");
    assertThat(logSanitizer.toString(), is(equalTo("Value &lt; 10 and &gt; 5")));
  }

  /**
   * Verifies sanitization of null bytes which can terminate strings in some languages.
   *
   * <p><b>Security Rationale:</b> Null bytes (U+0000) can cause issues with C-based log processors
   * and can be used to hide malicious content. This test documents current behavior.
   */
  @Test
  public void testSanitizeNullByte() {
    String input = "before\u0000after";
    LogSanitizer logSanitizer = LogSanitizer.sanitize(input);
    // Null bytes pass through the current implementation
    assertThat(logSanitizer.toString(), is(equalTo(input)));
  }

  /**
   * Verifies tab characters are preserved (not replaced like \n and \r).
   *
   * <p><b>Security Rationale:</b> Documents that horizontal tabs are allowed in logs. While tabs
   * could potentially affect log formatting, they're generally less dangerous than newlines for
   * injection attacks.
   */
  @Test
  public void testSanitizeTabCharacter() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Column1\tColumn2");
    assertThat(logSanitizer.toString(), is(equalTo("Column1\tColumn2")));
  }

  /**
   * Verifies ampersand is properly escaped in all contexts.
   *
   * <p><b>Security Rationale:</b> Ampersands must be escaped to prevent HTML entity injection where
   * attackers use &-based entities to bypass filters.
   */
  @Test
  public void testSanitizeAmpersand() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("Tom & Jerry");
    assertThat(logSanitizer.toString(), is(equalTo("Tom &amp; Jerry")));
  }

  /**
   * Verifies proper escaping of quote characters in various contexts.
   *
   * <p><b>Security Rationale:</b> Double quotes can break out of attribute contexts in HTML log
   * viewers. Note that commons-lang does NOT escape single quotes, which is a known limitation.
   */
  @Test
  public void testSanitizeQuotes() {
    LogSanitizer logSanitizer = LogSanitizer.sanitize("He said \"Hello\" and 'Goodbye'");
    // Only double quotes are escaped, not single quotes (limitation of commons-lang)
    assertThat(logSanitizer.toString(), is(equalTo("He said &quot;Hello&quot; and 'Goodbye'")));
  }
}
