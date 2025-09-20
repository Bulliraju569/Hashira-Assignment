import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HashiraSolution {
    
    public static void main(String[] args) {
        try {
            HashiraSolution solver = new HashiraSolution();
            String result1 = solver.solveFromFile("test1.json");
            String result2 = solver.solveFromFile("test2.json");
            String result3 = solver.solveFromFile("test3.json");
            
            System.out.println("Test Case 1 Result: " + result1);
            System.out.println("Test Case 2 Result: " + result2);
            System.out.println("Test Case 3 Result: " + result3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String solveFromFile(String filename) throws Exception {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filename)));
        return solve(jsonContent);
    }
    
    public String solve(String json) {
        try {
            Map<String, Object> data = parseJson(json);
            Map<String, Object> keys = (Map<String, Object>) data.get("keys");
            
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());
            
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i <= n && points.size() < k; i++) {
                String key = String.valueOf(i);
                if (data.containsKey(key)) {
                    Map<String, Object> point = (Map<String, Object>) data.get(key);
                    int base = Integer.parseInt(point.get("base").toString());
                    String value = point.get("value").toString();
                    
                    BigInteger x = BigInteger.valueOf(i);
                    BigInteger y = new BigInteger(value, base);
                    points.add(new Point(x, y));
                }
            }
            
            return lagrangeInterpolation(points).toString();
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        int k = points.size();
        
        for (int i = 0; i < k; i++) {
            BigInteger term = points.get(i).y;
            
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger numerator = points.get(j).x.negate();
                    BigInteger denominator = points.get(i).x.subtract(points.get(j).x);
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            
            result = result.add(term);
        }
        
        return result;
    }
    
    private Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new RuntimeException("Invalid JSON format");
        }
        
        json = json.substring(1, json.length() - 1).trim();
        
        int i = 0;
        while (i < json.length()) {
            while (i < json.length() && (json.charAt(i) == ' ' || json.charAt(i) == ',')) {
                i++;
            }
            
            if (i >= json.length()) break;
            
            if (json.charAt(i) != '"') {
                throw new RuntimeException("Expected key to start with quote");
            }
            
            i++;
            StringBuilder key = new StringBuilder();
            while (i < json.length() && json.charAt(i) != '"') {
                key.append(json.charAt(i));
                i++;
            }
            i++;
            
            while (i < json.length() && (json.charAt(i) == ' ' || json.charAt(i) == ':')) {
                i++;
            }
            
            Object value = parseValue(json, i);
            result.put(key.toString(), value);
            
            i = findNextComma(json, i);
            if (i != -1) i++;
        }
        
        return result;
    }
    
    private Object parseValue(String json, int start) {
        while (start < json.length() && json.charAt(start) == ' ') {
            start++;
        }
        
        if (start >= json.length()) return "";
        
        char first = json.charAt(start);
        
        if (first == '"') {
            start++;
            StringBuilder value = new StringBuilder();
            while (start < json.length() && json.charAt(start) != '"') {
                value.append(json.charAt(start));
                start++;
            }
            return value.toString();
        } else if (first == '{') {
            int braceCount = 0;
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '{') braceCount++;
                else if (json.charAt(end) == '}') braceCount--;
                end++;
                if (braceCount == 0) break;
            }
            return parseJson(json.substring(start, end));
        } else {
            StringBuilder value = new StringBuilder();
            while (start < json.length() && json.charAt(start) != ',' && json.charAt(start) != '}') {
                value.append(json.charAt(start));
                start++;
            }
            String val = value.toString().trim();
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                return val;
            }
        }
    }
    
    private int findNextComma(String json, int start) {
        int braceCount = 0;
        boolean inQuotes = false;
        
        while (start < json.length()) {
            char c = json.charAt(start);
            
            if (c == '"' && (start == 0 || json.charAt(start-1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{') braceCount++;
                else if (c == '}') braceCount--;
                else if (c == ',' && braceCount == 0) return start;
            }
            start++;
        }
        return -1;
    }
    
    static class Point {
        BigInteger x, y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
