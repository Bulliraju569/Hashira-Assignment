import java.math.BigInteger;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;

public class HashiraSolution {
    
    public static void main(String[] args) {
        try {
            HashiraSolution solver = new HashiraSolution();
            String result1 = solver.solveFromFile("test1.json");
            String result2 = solver.solveFromFile("test2.json");
            String result3 = solver.solveFromFile("test3.json");
            
            System.out.println("Test Case 1 Result: " + result1);
            System.out.println("Test Case 2 Result: " + result1);
            System.out.println("Test Case 3 Result: " + result2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String solveFromFile(String filename) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filename));
        
        JsonNode keys = root.get("keys");
        int n = keys.get("n").asInt();
        int k = keys.get("k").asInt();
        
        Map<Integer, Point> points = new HashMap<>();
        
        for (int i = 1; i <= n; i++) {
            if (root.has(String.valueOf(i))) {
                JsonNode point = root.get(String.valueOf(i));
                int base = point.get("base").asInt();
                String value = point.get("value").asText();
                
                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = convertToDecimal(value, base);
                points.put(i, new Point(x, y));
            }
        }
        
        List<Point> selectedPoints = selectPoints(points, k);
        BigInteger constantTerm = lagrangeInterpolation(selectedPoints);
        
        return constantTerm.toString();
    }
    
    private BigInteger convertToDecimal(String value, int base) {
        return new BigInteger(value, base);
    }
    
    private List<Point> selectPoints(Map<Integer, Point> points, int k) {
        List<Point> selected = new ArrayList<>();
        List<Integer> keys = new ArrayList<>(points.keySet());
        Collections.sort(keys);
        
        for (int i = 0; i < k && i < keys.size(); i++) {
            selected.add(points.get(keys.get(i)));
        }
        
        return selected;
    }
    
    private BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        int n = points.size();
        
        for (int i = 0; i < n; i++) {
            BigInteger term = points.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    numerator = numerator.multiply(BigInteger.ZERO.subtract(points.get(j).x));
                    denominator = denominator.multiply(points.get(i).x.subtract(points.get(j).x));
                }
            }
            
            term = term.multiply(numerator);
            term = term.divide(denominator);
            result = result.add(term);
        }
        
        return result;
    }
    
    static class Point {
        BigInteger x, y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}

class AlternativeHashiraSolution {
    
    public static void main(String[] args) {
        AlternativeHashiraSolution solver = new AlternativeHashiraSolution();
        
        String testCase1 = "{\"keys\":{\"n\":4,\"k\":3},\"1\":{\"base\":\"10\",\"value\":\"4\"},\"2\":{\"base\":\"2\",\"value\":\"111\"},\"3\":{\"base\":\"10\",\"value\":\"12\"},\"6\":{\"base\":\"4\",\"value\":\"213\"}}";
        
        String testCase2 = "{\"keys\":{\"n\":10,\"k\":7},\"1\":{\"base\":\"6\",\"value\":\"13444211440455345511\"},\"2\":{\"base\":\"15\",\"value\":\"aed7015a346d635\"},\"3\":{\"base\":\"15\",\"value\":\"6aeeb69631c227c\"},\"4\":{\"base\":\"16\",\"value\":\"e1b5e05623d881f\"},\"5\":{\"base\":\"8\",\"value\":\"316034514573652620673\"},\"6\":{\"base\":\"3\",\"value\":\"2122212201122002221120200210011020220200\"},\"7\":{\"base\":\"3\",\"value\":\"20120221122211000100210021102001201112121\"},\"8\":{\"base\":\"6\",\"value\":\"20220554335330240002224253\"},\"9\":{\"base\":\"12\",\"value\":\"45153788322a1255483\"},\"10\":{\"base\":\"7\",\"value\":\"1101613130313526312514143\"}}";
        
        try {
            String result1 = solver.solveFromJson(testCase1);
            String result2 = solver.solveFromJson(testCase2);
            
            System.out.println("Test Case 1 Result: " + result1);
            System.out.println("Test Case 2 Result: " + result2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String solveFromJson(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonString);
        
        JsonNode keys = root.get("keys");
        int n = keys.get("n").asInt();
        int k = keys.get("k").asInt();
        
        List<Point> points = new ArrayList<>();
        
        for (int i = 1; i <= n; i++) {
            String key = String.valueOf(i);
            if (root.has(key)) {
                JsonNode point = root.get(key);
                int base = Integer.parseInt(point.get("base").asText());
                String value = point.get("value").asText();
                
                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = new BigInteger(value, base);
                points.add(new Point(x, y));
                
                if (points.size() == k) break;
            }
        }
        
        return shamirSecretSharing(points).toString();
    }
    
    private BigInteger shamirSecretSharing(List<Point> points) {
        BigInteger secret = BigInteger.ZERO;
        int k = points.size();
        
        for (int i = 0; i < k; i++) {
            BigInteger li = lagrangeBasisPolynomial(points, i, BigInteger.ZERO);
            secret = secret.add(points.get(i).y.multiply(li));
        }
        
        return secret;
    }
    
    private BigInteger lagrangeBasisPolynomial(List<Point> points, int i, BigInteger x) {
        BigInteger result = BigInteger.ONE;
        
        for (int j = 0; j < points.size(); j++) {
            if (i != j) {
                BigInteger numerator = x.subtract(points.get(j).x);
                BigInteger denominator = points.get(i).x.subtract(points.get(j).x);
                result = result.multiply(numerator).divide(denominator);
            }
        }
        
        return result;
    }
    
    static class Point {
        BigInteger x, y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}

class OptimizedSolution {
    
    public static void main(String[] args) {
        String testCase1 = "{\"keys\":{\"n\":4,\"k\":3},\"1\":{\"base\":\"10\",\"value\":\"4\"},\"2\":{\"base\":\"2\",\"value\":\"111\"},\"3\":{\"base\":\"10\",\"value\":\"12\"},\"6\":{\"base\":\"4\",\"value\":\"213\"}}";
        String testCase2 = "{\"keys\":{\"n\":10,\"k\":7},\"1\":{\"base\":\"6\",\"value\":\"13444211440455345511\"},\"2\":{\"base\":\"15\",\"value\":\"aed7015a346d635\"},\"3\":{\"base\":\"15\",\"value\":\"6aeeb69631c227c\"},\"4\":{\"base\":\"16\",\"value\":\"e1b5e05623d881f\"},\"5\":{\"base\":\"8\",\"value\":\"316034514573652620673\"},\"6\":{\"base\":\"3\",\"value\":\"2122212201122002221120200210011020220200\"},\"7\":{\"base\":\"3\",\"value\":\"20120221122211000100210021102001201112121\"},\"8\":{\"base\":\"6\",\"value\":\"20220554335330240002224253\"},\"9\":{\"base\":\"12\",\"value\":\"45153788322a1255483\"},\"10\":{\"base\":\"7\",\"value\":\"1101613130313526312514143\"}}";
        
        System.out.println("Result 1: " + solve(testCase1));
        System.out.println("Result 2: " + solve(testCase2));
    }
    
    public static String solve(String json) {
        try {
            Map<String, Object> data = parseJson(json);
            Map<String, Object> keys = (Map<String, Object>) data.get("keys");
            
            int n = ((Number) keys.get("n")).intValue();
            int k = ((Number) keys.get("k")).intValue();
            
            BigInteger[][] points = new BigInteger[k][2];
            int pointCount = 0;
            
            for (int i = 1; i <= n && pointCount < k; i++) {
                String key = String.valueOf(i);
                if (data.containsKey(key)) {
                    Map<String, Object> point = (Map<String, Object>) data.get(key);
                    int base = Integer.parseInt(point.get("base").toString());
                    String value = point.get("value").toString();
                    
                    points[pointCount][0] = BigInteger.valueOf(i);
                    points[pointCount][1] = new BigInteger(value, base);
                    pointCount++;
                }
            }
            
            return computeConstantTerm(points, pointCount).toString();
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private static BigInteger computeConstantTerm(BigInteger[][] points, int k) {
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 0; i < k; i++) {
            BigInteger term = points[i][1];
            
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term = term.multiply(points[j][0].negate());
                    term = term.divide(points[i][0].subtract(points[j][0]));
                }
            }
            
            result = result.add(term);
        }
        
        return result;
    }
    
    private static Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim().substring(1, json.length() - 1);
        
        Stack<Character> stack = new Stack<>();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean inKey = true;
        boolean inQuotes = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i-1) != '\\')) {
                inQuotes = !inQuotes;
                continue;
            }
            
            if (!inQuotes) {
                if (c == '{') {
                    stack.push(c);
                } else if (c == '}') {
                    if (!stack.isEmpty()) stack.pop();
                } else if (c == ':' && stack.size() == 0 && inKey) {
                    inKey = false;
                    continue;
                } else if (c == ',' && stack.size() == 0) {
                    processKeyValue(result, key.toString().trim(), value.toString().trim());
                    key.setLength(0);
                    value.setLength(0);
                    inKey = true;
                    continue;
                }
            }
            
            if (inKey) {
                key.append(c);
            } else {
                value.append(c);
            }
        }
        
        if (key.length() > 0) {
            processKeyValue(result, key.toString().trim(), value.toString().trim());
        }
        
        return result;
    }
    
    private static void processKeyValue(Map<String, Object> result, String key, String value) {
        key = key.replaceAll("\"", "");
        
        if (value.startsWith("{") && value.endsWith("}")) {
            result.put(key, parseJson(value));
        } else {
            value = value.replaceAll("\"", "");
            result.put(key, value);
        }
    }
}