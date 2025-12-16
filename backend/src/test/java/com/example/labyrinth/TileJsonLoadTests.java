package com.example.labyrinth;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class TileJsonLoadTests {

    @Test
    public void loadBoard50Json() throws Exception {
        String json = new String(new ClassPathResource("tiles/board-50.json").getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Extract object substrings by matching braces to handle spacing and newlines robustly
        int idx = 0;
        int len = json.length();
        int count = 0;
        int spareCount = 0;
        Set<String> coords = new HashSet<>();

        Pattern sparePattern = Pattern.compile("\"spare\"\\s*:\\s*true");
        Pattern rowPattern = Pattern.compile("\"row\"\\s*:\\s*(\\d+)");
        Pattern colPattern = Pattern.compile("\"col\"\\s*:\\s*(\\d+)");
        Pattern treasurePattern = Pattern.compile("\"treasure\"\\s*:\\s*\"([^\"]*)\"");

        while (idx < len) {
            int start = json.indexOf('{', idx);
            if (start < 0) break;
            int depth = 0;
            int end = start;
            while (end < len) {
                char ch = json.charAt(end);
                if (ch == '{') depth++;
                else if (ch == '}') {
                    depth--;
                    if (depth == 0) break;
                }
                end++;
            }
            if (end >= len) break; // malformed

            String obj = json.substring(start, end + 1);
            idx = end + 1;
            count++;

            if (sparePattern.matcher(obj).find()) {
                spareCount++;
                Matcher t = treasurePattern.matcher(obj);
                assertTrue(t.find(), "spare must have treasure");
                continue;
            }

            Matcher r = rowPattern.matcher(obj);
            Matcher c2 = colPattern.matcher(obj);
            assertTrue(r.find(), "tile must have row");
            assertTrue(c2.find(), "tile must have col");

            int row = Integer.parseInt(r.group(1));
            int col = Integer.parseInt(c2.group(1));
            assertTrue(row >= 0 && row < 7, "row out of bounds");
            assertTrue(col >= 0 && col < 7, "col out of bounds");

            String key = row + ":" + col;
            assertFalse(coords.contains(key), "duplicate tile at " + key);
            coords.add(key);

            Matcher t = treasurePattern.matcher(obj);
            assertTrue(t.find(), "tile must have treasure field (empty string allowed)");
        }

        assertEquals(50, count, "Expected 50 JSON objects (49 tiles + 1 spare)");
        assertEquals(1, spareCount, "Expected exactly one spare tile");
        assertEquals(49, coords.size(), "Expected 49 unique board coordinates");
    }
}
