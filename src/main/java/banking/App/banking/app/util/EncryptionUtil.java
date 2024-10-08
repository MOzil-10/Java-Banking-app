package banking.App.banking.app.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    @Value("${encryption.key}")
    private String secretKey;

    private SecretKeySpec secretKeySpec;
    private Cipher cipher;

    @PostConstruct
    public void init() throws Exception {
        byte[] key = secretKey.getBytes();
        if (key.length != 16) {
            throw new IllegalArgumentException("Secret key must be 16 bytes");
        }
        this.secretKeySpec = new SecretKeySpec(key, "AES");
        this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    public String encrypt(String strToEncrypt) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String strToDecrypt) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decoded = Base64.getDecoder().decode(strToDecrypt);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }
}
