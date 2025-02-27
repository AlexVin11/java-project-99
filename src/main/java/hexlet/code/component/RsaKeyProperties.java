package hexlet.code.component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "rsa")
@Setter
@Getter
public final class RsaKeyProperties {
    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;

    @PostConstruct
    public void init() throws Exception {
        String privateKeyPem = loadResourceAsString("certs/private.pem");
        String publicKeyPem = loadResourceAsString("certs/public.pem");

        this.rsaPublicKey = getPublicKeyFromPem(publicKeyPem);
        this.rsaPrivateKey = getPrivateKeyFromPem(privateKeyPem);
    }

    private String loadResourceAsString(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private RSAPublicKey getPublicKeyFromPem(String pem) throws Exception {
        String publicKeyPEM = cleanPemString(pem);

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey getPrivateKeyFromPem(String pem) throws Exception {
        String privateKeyPEM = cleanPemString(pem);

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private String cleanPemString(String pem) {
        return pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace(" ", "")
                .replace("\t", "")
                .replace("\n", "")
                .replace("\r", "");
    }
}
