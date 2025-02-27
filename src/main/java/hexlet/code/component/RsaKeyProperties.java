package hexlet.code.component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Setter
@Getter
@Slf4j
public class RsaKeyProperties {
    @Value("${rsa.public-key}")
    private String publicKeyPem;

    @Value("${rsa.private-key}")
    private String privateKeyPem;

    private RSAPrivateKey rsaPrivateKey;

    private RSAPublicKey rsaPublicKey;

    @PostConstruct
    public void init() throws Exception {
        log.info("value of public key pem=" + publicKeyPem);
        this.rsaPublicKey = getPublicKeyFromPem(publicKeyPem);
        log.info("value of private key pem=" + privateKeyPem);
        this.rsaPrivateKey = getPrivateKeyFromPem(privateKeyPem);
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
