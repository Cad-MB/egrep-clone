package src.step3_DFA.graphics;

import src.step2_NDFA.NDFA_Struct;
import src.step2_NDFA.graphics.NDFA_JSON_Generator;
import src.step3_DFA.DFA_Determinisation;
import src.step3_DFA.DFA_Struct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code DFAMinimized_JSON_Generator} class reads regular expressions from a CSV-like file,
 * converts each expression into an NDFA, determinizes the NDFA into a DFA, and exports the DFA as a JSON file.
 * This class extends the functionality of {@code NDFA_JSON_Generator}.
 */
public class DFA_JSON_Generator extends NDFA_JSON_Generator {

    private static final List<String> regexList = new ArrayList<>();  // Store the list of regular expressions

    /**
     * Override the method to build a DFA from a regular expression by first creating an NDFA and then determinizing it.
     *
     * @param regEx The regular expression to parse and convert into a DFA.
     * @return The DFA_Struct representing the DFA created from the NDFA.
     */
    public static DFA_Struct buildDFAFromRegex(String regEx) {
        // Reset state IDs before building a new NDFA
        resetStateCounter();

        // Call the superclass method to build the NDFA from the regular expression
        System.out.println("Processing regex (NDFA): " + regEx);
        buildNDFAFromRegex(regEx);  // This builds the NDFA and stores it in the superclass

        // Determinize the NDFA into a DFA

        return DFA_Determinisation.determinise(getNDFA());  // Return the determinized DFA
    }

    /**
     * Helper method to get the NDFA built by the superclass.
     *
     * @return The NDFA_Struct created by the superclass.
     */
    public static NDFA_Struct getNDFA() {
        // Access the NDFA that was created in the superclass (since it's static in the superclass)
        return ndfa;
    }

    /**
     * Main method to execute the conversion from regular expressions to DFA.
     * It will read the CSV file and generate DFA JSONs.
     */
    public static void main(String[] args) {
        // Define the input CSV file and output folder path
        String inputFilePath = "Backend/src/step3_DFA/graphics/Samples/regex_list.csv";
        String outputFolderPath = "Backend/src/step3_DFA/graphics/Samples";

        // Process the regular expressions from the input file and export DFA JSONs
        DFA_JSON_Generator dfaGenerator = new DFA_JSON_Generator();
        processRegexFile(inputFilePath, outputFolderPath);
    }

    public static void processRegexFile(String inputFilePath, String outputFolderPath) {
        // Ensure output folder exists
        File folder = new File(outputFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            int regexIndex = 1;

            // Read the file line by line and store each regex
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                // Add regex to the list for later DFA processing
                regexList.add(line.trim());

                // Build NDFA from the current regex line
                buildNDFAFromRegex(line.trim());

                regexIndex++;
            }

            // Now process the DFA for each regex
            for (int i = 0; i < regexList.size(); i++) {
                String regEx = regexList.get(i);
                System.out.println("Converting NDFA to DFA for regex: " + regEx);

                // Build DFA from NDFA
                DFA_Struct dfa = buildDFAFromRegex(regEx);

                // Define the DFA JSON file name
                String dfaFilename = outputFolderPath + "/DFA_Sample_" + (i + 1) + ".json";

                // Export the DFA to a JSON file
                DFA_JSON_Exporter dfaExporter = new DFA_JSON_Exporter();
                dfaExporter.toJsonFile(dfa, dfaFilename);
            }

            System.out.println("DFA JSON generation complete.");

        } catch (IOException e) {
            System.err.println("Error reading the regex file: " + e.getMessage());
        }
    }
}