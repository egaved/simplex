package ru.ac.uniyar.simplex.secondary;

import com.fasterxml.jackson.databind.SerializationFeature;
import ru.ac.uniyar.simplex.domain.Condition;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JSONReader {

    public static void saveTaskToJSONFile(Condition condition) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File("task.json"), condition);
    }

    public static Condition readTaskFromJSON() throws IOException {
        File file = new File("task.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, Condition.class);
    }
}
