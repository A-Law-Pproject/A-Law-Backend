package com.service.alaw.platform.contract.domain.document;

import com.service.alaw.platform.contract.application.dto.ocr.OcrBlock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@Document(collection = "ocr_results")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OcrResultDocument {

  @Id
  private String id;

  @Indexed
  @Field("s3_key")
  private String s3Key;

  @Field("image_url")
  private String imageUrl;

  @Field("image_width")
  private int imageWidth;

  @Field("image_height")
  private int imageHeight;

  @Field("full_text")
  private String fullText;

  @Field("markdown")
  private String markdown;

  @Field("contract_data")
  private Map<String, Object> contractData;

  @Field("validation")
  private Map<String, Object> validation;

  @Field("words")
  private List<OcrBlock> words;

  @Field("warnings")
  private List<String> warnings;

  @CreatedDate
  @Field("created_at")
  private LocalDateTime createdAt;

  public static OcrResultDocument of(
      String s3Key,
      String imageUrl,
      int imageWidth,
      int imageHeight,
      String fullText,
      String markdown,
      Map<String, Object> contractData,
      Map<String, Object> validation,
      List<OcrBlock> words,
      List<String> warnings) {
    return OcrResultDocument.builder()
        .s3Key(s3Key)
        .imageUrl(imageUrl)
        .imageWidth(imageWidth)
        .imageHeight(imageHeight)
        .fullText(fullText)
        .markdown(markdown)
        .contractData(contractData)
        .validation(validation)
        .words(words)
        .warnings(warnings)
        .build();
  }
}
