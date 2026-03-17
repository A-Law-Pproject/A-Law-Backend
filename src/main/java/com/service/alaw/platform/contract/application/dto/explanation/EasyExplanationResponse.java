package com.service.alaw.platform.contract.application.dto.explanation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "쉬운 말 요약 응답")
public record EasyExplanationResponse(
        @Schema(description = "원문 문장", example = "임차인은 임대인의 서면 동의 없이 본 부동산을 전대하거나 임차권을 양도할 수 없다.")
        @JsonProperty("sentence")
        String sentence,
        @Schema(description = "쉬운 말 요약", example = "세입자는 집주인의 서면 허락 없이 다른 사람에게 집을 빌려주거나 계약을 넘길 수 없습니다.")
        @JsonProperty("easy_explanation")
        String easyExplanation,
        @Schema(description = "예시")
        @JsonProperty("examples")
        List<String> examples) {
}
