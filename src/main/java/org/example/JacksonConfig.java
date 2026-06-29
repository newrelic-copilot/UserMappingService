package org.example;

import Model.MyModel;
import Model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * Centralized Jackson ObjectMapper configuration with hardened polymorphic type validation.
 * <p>
 * Uses an explicit subtype allowlist instead of allowIfSubTypeIsArray, preventing
 * array-based polymorphic type validator bypass (CVE-2026-54513).
 */
public class JacksonConfig {

    private JacksonConfig() {
    }

    /**
     * Creates an ObjectMapper configured with an explicit subtype allowlist.
     * Only types explicitly enumerated here are permitted during polymorphic
     * deserialization. No array-based allowances are granted.
     *
     * @return a hardened ObjectMapper instance
     */
    public static ObjectMapper createHardenedObjectMapper() {
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(User.class)
                .allowIfSubType(MyModel.class)
                .allowIfSubType(UserWithIgnore.class)
                .build();

        return JsonMapper.builder()
                .polymorphicTypeValidator(ptv)
                .build();
    }
}
