package com.fitpal.fitpalspringbootapp.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ExerciseMetaUtil {

    private static final Map<String, Double> MET_MAP = loadMetMap();

    private ExerciseMetaUtil() {}

    private static Map<String, Double> loadMetMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = ExerciseMetaUtil.class.getClassLoader()
                    .getResourceAsStream("met/exercise-met.json");

            if (is == null) {
                System.err.println("exercise-met.json not found in resources/met/");
                return Collections.emptyMap();
            }

            Map<String, Double> raw = mapper.readValue(is, new TypeReference<Map<String, Double>>() {});
            Map<String, Double> normalized = new HashMap<>();

            // normalize keys for safer lookup
            for (var entry : raw.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    normalized.put(entry.getKey().trim().toLowerCase(), entry.getValue());
                }
            }

            System.out.println("Loaded MET entries: " + normalized.size());
            return normalized;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public static double getMetOrDefault(String name) {
        if (name == null) return 0.0;
        return MET_MAP.getOrDefault(name.trim().toLowerCase(), 0.0);
    }
}
