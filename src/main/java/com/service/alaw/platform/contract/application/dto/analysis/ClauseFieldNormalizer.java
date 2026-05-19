package com.service.alaw.platform.contract.application.dto.analysis;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public final class ClauseFieldNormalizer {

  private static final Pattern GROUNDED_REFERENCE_PATTERN =
      Pattern.compile("확인된\\s*법령\\s*근거\\s*:\\s*([^\\n.]+)");

  private static final Pattern GENERIC_REFERENCE_PATTERN =
      Pattern.compile(
          "([가-힣A-Za-z0-9·\\s]+?(?:법|법률|시행령|시행규칙|규칙))\\s*"
              + "(제\\d+조(?:의\\d+)?(?:\\s*제\\d+항)?)");

  private ClauseFieldNormalizer() {}

  public static String normalizeLegalReference(String legalReference, String reasoningSummary) {
    if (StringUtils.hasText(legalReference)) {
      return normalizeText(legalReference);
    }

    String normalizedSummary = normalizeText(reasoningSummary);
    if (!StringUtils.hasText(normalizedSummary)) {
      return "";
    }

    Matcher groundedMatcher = GROUNDED_REFERENCE_PATTERN.matcher(normalizedSummary);
    if (groundedMatcher.find()) {
      return normalizeReferenceList(groundedMatcher.group(1));
    }

    Matcher genericMatcher = GENERIC_REFERENCE_PATTERN.matcher(normalizedSummary);
    if (genericMatcher.find()) {
      String lawName = normalizeText(genericMatcher.group(1));
      String article = normalizeArticle(genericMatcher.group(2));
      return StringUtils.hasText(lawName) && StringUtils.hasText(article)
          ? lawName + " " + article
          : "";
    }

    return "";
  }

  public static String normalizeRelatedWork(String relatedWork, String reasoningSummary) {
    if (StringUtils.hasText(relatedWork)) {
      return normalizeText(relatedWork);
    }
    return normalizeText(reasoningSummary);
  }

  public static String normalizeText(String value) {
    return value == null ? "" : value.trim().replaceAll("\\s+", " ");
  }

  private static String normalizeReferenceList(String value) {
    return Arrays.stream(value.split(";"))
        .map(ClauseFieldNormalizer::normalizeText)
        .filter(StringUtils::hasText)
        .collect(Collectors.joining("; "));
  }

  private static String normalizeArticle(String value) {
    return value == null ? "" : value.replaceAll("\\s+", "");
  }
}
