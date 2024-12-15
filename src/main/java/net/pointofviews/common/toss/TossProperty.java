package net.pointofviews.common.toss;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@ConfigurationProperties(prefix = "toss")
public record TossProperty(
        String secretKey
) {
    public String base64SecretKey() {
        String secretKeyForAuth = secretKey + ":";
        String base64SecretKey = Base64.getEncoder().encodeToString(secretKeyForAuth.getBytes(UTF_8));

        return "Basic " + base64SecretKey;
    }
}
