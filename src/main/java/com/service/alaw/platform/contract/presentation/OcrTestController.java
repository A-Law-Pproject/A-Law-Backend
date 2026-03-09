package com.service.alaw.platform.contract.presentation;

import com.service.alaw.platform.contract.application.service.ContractService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Profile("local")
@Controller
@RequestMapping("/test/ocr")
@RequiredArgsConstructor
public class OcrTestController {

    private final ContractService contractService;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf");

    @GetMapping
    public String form() {
        return "ocr-test";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String process(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "파일이 비어있습니다.");
            return "ocr-test";
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            model.addAttribute("error", "지원하지 않는 형식입니다. (JPG, PNG, GIF, WEBP, PDF)");
            return "ocr-test";
        }

        try {
            model.addAttribute("ocr", contractService.uploadAndOCR(file, null));
        } catch (Exception e) {
            log.error("OCR 처리 실패: {}", e.getMessage());
            model.addAttribute("error", "OCR 실패: " + e.getMessage());
        }
        return "ocr-test";
    }
}
