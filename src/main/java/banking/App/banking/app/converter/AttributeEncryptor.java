package banking.App.banking.app.converter;

import banking.App.banking.app.util.EncryptionUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

@Converter(autoApply = true)
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptionUtil;

    @Autowired
    public AttributeEncryptor(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : decrypt(dbData);
    }

    private String encrypt(String attribute) {
        try {
            return encryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting attribute: " + e.getMessage(), e);
        }
    }

    private String decrypt(String dbData) {
        try {
            return encryptionUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting attribute: " + e.getMessage(), e);
        }
    }
}
