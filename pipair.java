/**
 *  SE465 Group Project
 *
 *  Group Number: 92
 *
 *  Members:
 *      Shoubo Wang ID: 20476417
 *      Yuxin Tang  ID: 20463363
 *
 *
 *  Due Date:
 *      April 2, 2015
 */

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class pipair {

    // Global variable confidence and support
    private static int T_CONFIDENCE;
    private static int T_SUPPORT;

    // Global variable for tables
    private static HashMap<String, HashMap<String, Integer>> OCCURRENCE_MAP;
    private static HashMap<String, HashSet<String>> FUNCTION_MAP;
    private static HashMap<String, Integer> FUNCTION_CALL_COUNT;

    public static void main(String[] args){
	// Initialize Global variables
        OCCURRENCE_MAP = new HashMap<String, HashMap<String, Integer>>();
        FUNCTION_MAP = new HashMap<String, HashSet<String>>();
        FUNCTION_CALL_COUNT = new HashMap<String, Integer>();

        if(args[0] == null) T_SUPPORT = 3;
        else T_SUPPORT = Integer.parseInt(args[0]);

        if(args[1] == null) T_CONFIDENCE = 65;
        else T_CONFIDENCE = Integer.parseInt(args[1]);

        ParserAndReader("out.txt");

        // Print the missing combinations
        PrintMissingPairsWithConfidence();
    }

    private static void print(){
        for (String key : OCCURRENCE_MAP.keySet()){
            HashMap<String, Integer> map = OCCURRENCE_MAP.get(key);

            for(String key2 : map.keySet()){
                System.out.println(key + "," + key2 + " : " + map.get(key2));
            }
        }
    }

    // Reads the file using BufferedReader and parses the file
    private static void ParserAndReader(String fileLocation) {
        try {
            FileReader fileReader = new FileReader(fileLocation);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            StringTokenizer token;

            // Initialize variables that will be used to compare HashVaues
            final int CALL = "Call".hashCode();
            final int NULL_FNC = "<<null".hashCode();

            // Read out each nodes and process individually
            // Read to the end of file
            while((line = reader.readLine()) != null){
                token = new StringTokenizer(line);

                // Continue if the current line is an empty line
                if(token.countTokens() == 0) continue;

                // Continue if the line does not start with "Call"
                if(token.nextToken().hashCode() != CALL) continue;

                // The following two lines skip the next two tokens "graph" and "node"
                token.nextToken();
                token.nextToken();

                // Continue if the line is a null function
                if(token.nextToken().hashCode() == NULL_FNC) continue;

                // Grab the function name
                token.nextToken();
                String functionName = token.nextToken();
                HashSet<String> functionCalls = ReadFunctionBody(reader);

                // Only add to FUNCTION_MAP if the valid function calls > 1
                if(functionCalls.size() > 0)
                    FUNCTION_MAP.put(functionName, functionCalls);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static HashSet<String> ReadFunctionBody(BufferedReader reader){
        HashSet<String> functionCalls = new HashSet<String>();

        String line;
        StringTokenizer token;

        // HashCode for constants
        final int FUNCTION = "function".hashCode();

        try {
            while ((line = reader.readLine()) != null) {
                token = new StringTokenizer(line);

                // Empty line indicates the end of a function node,
                // thus, break out of the loop
                if(token.countTokens() == 0) break;

                // Skip the first two tokens
                // Check if the third token is a function call
                // If it is a function call, add the call to the hashSet
                token.nextToken();
                token.nextToken();

                // continue if the line is not a function call
                if(token.nextToken().hashCode() != FUNCTION) continue;
                String functionCallName = token.nextToken();

                functionCalls.add(functionCallName);
            }
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }

        //Update the function call count
        for(String call: functionCalls){
            // Update the function call count hashMap
            // If the key exist, update the count
            if(FUNCTION_CALL_COUNT.containsKey(call))
                FUNCTION_CALL_COUNT.put(call, FUNCTION_CALL_COUNT.get(call)+1);
            else
                FUNCTION_CALL_COUNT.put(call, 1);
        }

        // Create the combination of function calls and update the occurrence hashMap
        String[] functionCallsArr = functionCalls.toArray(new String[functionCalls.size()]);
        for(int i = 0; i < functionCallsArr.length - 1; i ++){

            String first = functionCallsArr[i];
            for(int j = i + 1; j < functionCallsArr.length; j++){

                String second = functionCallsArr[j];
                // Add the two combination to the OCCURRENCE_MAP
                AddToMap(first, second);
                AddToMap(second, first);
            }
        }

        return functionCalls;
    }

    private static void AddToMap(String first, String second){

        if(OCCURRENCE_MAP.containsKey(first)){

            // If the first function call exist, check if the second function
            // call exist
            HashMap<String, Integer> map = OCCURRENCE_MAP.get(first);

            // If the second function call exist, then add 1 to the current value
            // Else, add the key to the map and set the value to 1
            if(map.containsKey(second))
                map.put(second, map.get(second) + 1);
            else
                map.put(second, 1);
        } else {

            // Create the second HashMap for the second function call
            // Add the map to the OCCURRENCE_MAP
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put(second, 1);
            OCCURRENCE_MAP.put(first, map);
        }
    }

    private static void PrintMissingPairsWithConfidence(){

        for(String firstKey: FUNCTION_MAP.keySet()){

            // Get the first function calls from the first method
            HashSet<String> functionCalls = FUNCTION_MAP.get(firstKey);
            for(String firstFunctionCall: functionCalls){

                int firstFunctionCallCount = FUNCTION_CALL_COUNT.get(firstFunctionCall);
                HashMap<String, Integer> occurrenceCalls = OCCURRENCE_MAP.get(firstFunctionCall);
                if(occurrenceCalls == null) continue;
                // Check if the second function call exist in the functionCalls
                for(String secondCall: occurrenceCalls.keySet()){

                    // Continue if the second call exists in the functionCalls
                    if(functionCalls.contains(secondCall)) continue;
                    // If the second call does not exist, check the call count
                    // and check with T_CONFIDENCE
                    int combinationCount = occurrenceCalls.get(secondCall);
                    // Continue if the support is less than T_SUPPORT
                    if(combinationCount < T_SUPPORT) continue;
                    double rate = ((double)combinationCount / firstFunctionCallCount) * 100;
                    if(rate >= T_CONFIDENCE){
                        String functionName = firstKey.substring(1);
                        functionName = functionName.substring(0, functionName.indexOf("'"));
                        String firstFunction = firstFunctionCall.substring(1, firstFunctionCall.length() - 1);
                        String secondFunction = secondCall.substring(1, secondCall.length() - 1);
                        String bottom = firstFunction;

                        String[] arr = new String[2];
                        arr[0] = firstFunction;
                        arr[1] = secondFunction;
                        Arrays.sort(arr);
                        firstFunction = arr[0];
                        secondFunction = arr[1];

                        // Format the text and print
                        System.out.printf("bug: %s in %s, pair: (%s, %s), support: %d, confidence: %.2f%%\n", bottom, functionName, firstFunction, secondFunction, combinationCount, rate);
                    }
                }
            }

        }
    }

}

