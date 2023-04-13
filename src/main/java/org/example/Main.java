package org.example;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;

public class Main {


    public static String fileContent(String filePath){
        try {
            // Read the contents of the input file
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder fileContents = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line);
                fileContents.append(System.lineSeparator());
            }
            reader.close();


            String cCode = fileContents.toString().replaceAll("//.*", "");

            // Remove multi-line comments
            int commentStart = cCode.indexOf("/*");
            while (commentStart != -1) {
                int commentEnd = cCode.indexOf("*/", commentStart + 2);
                if (commentEnd == -1) {
                    // The multi-line comment continues to the end of the code
                    cCode = cCode.substring(0, commentStart);
                } else {
                    // Remove the multi-line comment
                    cCode = cCode.substring(0, commentStart) + cCode.substring(commentEnd + 2);
                }
                commentStart = cCode.indexOf("/*");
            }

            cCode=cCode.replaceAll("(?m)^[ \t]*\r?\n", "");





            return cCode;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static boolean isFunctions(String filePath) {


        int bracket = 0, function_namber = 0;
        String[] functions = fileContent(filePath).split("\n");


        for (String s : functions) {

            if (s.contains("{"))
                    bracket++;

            if (s.contains("}")) {
                bracket--;
                if (bracket == 0) {

                    function_namber++;
                }
            }


        }
        return function_namber>1;
    }


public static void separateFunctions (String filePath) {


        String[] functions = fileContent(filePath).split("\n");
        filePath = filePath.trim().substring(0,filePath.trim().length()-2);
            File folder = new File(filePath);

            if (!folder.exists()) {
                boolean success = folder.mkdir();
                if (!success) {
                    System.err.println("Failed to create "+filePath );
                }
            }
        String directive = "";
        for (String Code_line : functions) {
            if (Code_line.contains("#include")) {
                directive = directive + Code_line + "\n";
            }
        }
        int bracket = 0, start = 0;
        String function ;
        // Write each function to its own .c file
        boolean function_start = false;
        for (int i = 0; i < functions.length; i++) {


            if (functions[i].contains("(") && (!functions[i].contains("#"))) {
                function_start = true;
            }

            if (function_start)
                // Extract the function name
                if (functions[i].contains("{")) {
                    if (bracket == 0) {
                        int index = i;

                        while (!functions[index].contains("(")) {
                            index--;
                        }
                        start = index;
                    }
                    bracket++;

                }
            if (functions[i].contains("}")) {
                bracket--;
                if (bracket == 0) {
                    function = directive + "\n";
                    for (int j = start; j <= i; j++) {

                        function = function + " " + functions[j];

                    }

                    try {
                    String functionName = functions[start].substring(functions[start].indexOf(" "), functions[start].indexOf("("));
                    functionName = functionName.replace("*", "").trim();


                    String functionPath = filePath + "\\" + functionName + ".c";


                        PrintWriter writer = new PrintWriter(new FileWriter(functionPath));
                        writer.println(function);
                        writer.close();
                    }

                        catch (IndexOutOfBoundsException | IOException e) {
                            System.err.println(e.getMessage());
                        }

                }

            }
        }
    }

    public static void separate (String directory) throws IOException {

        Path dirPath = Paths.get(directory);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    separate(entry.toString()); // recursively browse subdirectories
                } else if (Files.isRegularFile(entry) && entry.toString().endsWith(".c")) {
                    if(isFunctions(entry.toString())){
                        separateFunctions(entry.toString()); // call separateFunctions on each .c file
                    }
                }
            }
        }
    }

public static void main (String[]args) throws IOException {

    System.out.println("Checking Code ...");
    separate("..\\Datafiles\\Code");


    }
}
