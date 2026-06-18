package com.service.alaw.common.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PersonalInfoMasker {

    private static final Pattern RRN = Pattern.compile("\\d{6}-[1-4]\\d{6}");
    private static final Pattern PHONE = Pattern.compile("(01[016789])-?(\\d{3,4})-(\\d{4})");
    private static final Pattern ACCOUNT = Pattern.compile("\\d{3,6}-\\d{2,6}-\\d{4,8}");

    public String mask(String text) {
        if (text == null) return null;
        String result = RRN.matcher(text).replaceAll(m -> m.group().substring(0, 7) + "*******");
        result = PHONE.matcher(result).replaceAll(m -> m.group(1) + "-****-" + m.group(3));
        result = ACCOUNT.matcher(result).replaceAll(m -> {
            String raw = m.group();
            int half = raw.length() / 2;
            return raw.substring(0, half) + "*".repeat(raw.length() - half);
        });
        return result;
    }
}
