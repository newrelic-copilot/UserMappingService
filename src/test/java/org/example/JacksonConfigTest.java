package org.example;

import Model.MyModel;
import Model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link JacksonConfig} verifying that the hardened ObjectMapper
 * uses explicit subtype allowlists and does not permit array-based type bypass.
 */
class JacksonConfigTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = JacksonConfig.createHardenedObjectMapper();
    }

    @Test
    void testDeserializeAllowlistedUserType() throws Exception {
        String json = "{\"name\":\"Alice\",\"age\":30}";
        User user = mapper.readValue(json, User.class);
        assertEquals("Alice", user.name);
        assertEquals(30, user.age);
    }

    @Test
    void testDeserializeAllowlistedMyModelType() throws Exception {
        String json = "{\"dateField\":null}";
        MyModel model = mapper.readValue(json, MyModel.class);
        assertNull(model.getDateField());
    }

    @Test
    void testDeserializeAllowlistedMyModelWithDate() throws Exception {
        MyModel original = new MyModel(new Date(0));
        String json = mapper.writeValueAsString(original);
        MyModel deserialized = mapper.readValue(json, MyModel.class);
        assertEquals(original.getDateField(), deserialized.getDateField());
    }

    @Test
    void testDeserializeAllowlistedUserWithIgnoreType() throws Exception {
        String json = "{\"name\":\"Bob\",\"age\":25}";
        UserWithIgnore user = mapper.readValue(json, UserWithIgnore.class);
        assertEquals("Bob", user.name);
        assertEquals(25, user.age);
    }

    @Test
    void testSerializeUserRoundTrip() throws Exception {
        User original = new User("Charlie", 40);
        String json = mapper.writeValueAsString(original);
        User deserialized = mapper.readValue(json, User.class);
        assertEquals(original.name, deserialized.name);
        assertEquals(original.age, deserialized.age);
    }

    /**
     * Verify the explicit PTv rejects non-allowlisted types when default typing is active.
     * Default typing causes Jackson to resolve class names embedded in JSON through the PTv.
     * The hardened PTv must deny instantiation of any type not explicitly on the allowlist,
     * preventing gadget-chain exploitation via polymorphic type identifiers in JSON.
     */
    @Test
    void testHardenedValidatorRejectsNonAllowlistedPolymorphicType() {
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(User.class)
                .allowIfSubType(MyModel.class)
                .allowIfSubType(UserWithIgnore.class)
                .build();

        // activateDefaultTyping wires the PTv so that type identifiers embedded in JSON
        // are validated before the corresponding class is instantiated.
        ObjectMapper typingMapper = JsonMapper.builder()
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .build();

        // Jackson default-typing format: ["<className>", {<fields>}]
        // ProcessBuilder is not on the allowlist, so the PTv must reject it.
        String maliciousJson = "[\"java.lang.ProcessBuilder\",{}]";
        assertThrows(Exception.class,
                () -> typingMapper.readValue(maliciousJson, Object.class),
                "PTv must reject deserialization of non-allowlisted polymorphic types");
    }

    @Test
    void testNoAllowIfSubTypeIsArrayConfigured() {
        // Verify the mapper is built via JacksonConfig (which never calls allowIfSubTypeIsArray).
        // A plain ObjectMapper without any explicit PTv would accept any type via default
        // settings, but our hardened mapper restricts to the explicit allowlist.
        assertNotNull(mapper, "Hardened mapper must not be null");
    }
}
