package banking.App.banking.app.converter;

import banking.App.banking.app.util.EncryptionUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A JPA AttributeConverter that encrypts and decrypts String attributes
 * when persisting to and reading from the database.
 * <p>
 * This converter automatically applies to all String attributes in entities
 * where this converter is specified.
 * </p>
 */
@Converter(autoApply = true)
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptionUtil;

    @Autowired
    public AttributeEncryptor(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    /**
     * Converts the given attribute value into a format suitable for database storage.
     *
     * @param attribute the attribute value to be converted; if null, returns null
     * @return the encrypted value for database storage or null if the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : encrypt(attribute);
    }

    /**
     * Converts the database value back into the entity attribute format.
     *
     * @param dbData the database value to be converted; if null, returns null
     * @return the decrypted value or null if the database value is null
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : decrypt(dbData);
    }

    /**
     * Encrypts the given attribute.
     *
     * @param attribute the attribute to encrypt
     * @return the encrypted attribute
     * @throws RuntimeException if an error occurs during encryption
     */
    private String encrypt(String attribute) {
        try {
            return encryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting attribute: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts the given database value.
     *
     * @param dbData the database value to decrypt
     * @return the decrypted value
     * @throws RuntimeException if an error occurs during decryption
     */
    private String decrypt(String dbData) {
        try {
            return encryptionUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting attribute: " + e.getMessage(), e);
        }
    }
}
