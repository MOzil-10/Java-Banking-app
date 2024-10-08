package banking.App.banking.app.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * A utility class for encrypting and decrypting strings using AES encryption.
 * <p>
 * This class is a Spring component that initializes a secret key and a cipher
 * for encryption and decryption of data. It requires a 16-byte secret key,
 * which should be provided in the application properties.
 * </p>
 */
@Component
public class EncryptionUtil {

    @Value("${encryption.key}")
    private String secretKey;

    private SecretKeySpec secretKeySpec;
    private Cipher cipher;

    /**
     * Initializes the EncryptionUtil by setting up the secret key and cipher.
     *
     * @throws Exception if the secret key is not 16 bytes or if there is an error
     *                   initializing the cipher
     */
    @PostConstruct
    public void init() throws Exception {
        byte[] key = secretKey.getBytes();
        if (key.length != 16) {
            throw new IllegalArgumentException("Secret key must be 16 bytes");
        }
        this.secretKeySpec = new SecretKeySpec(key, "AES");
        this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    /**
     * Encrypts a given string using AES encryption.
     *
     * @param strToEncrypt the string to encrypt
     * @return the encrypted string encoded in Base64
     * @throws Exception if there is an error during encryption
     */
    public String encrypt(String strToEncrypt) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts a given Base64-encoded string using AES decryption.
     *
     * @param strToDecrypt the Base64-encoded string to decrypt
     * @return the decrypted string
     * @throws Exception if there is an error during decryption
     */
    public String decrypt(String strToDecrypt) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decoded = Base64.getDecoder().decode(strToDecrypt);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }
}
