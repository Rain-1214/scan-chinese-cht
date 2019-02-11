package com.wang.chinese.scan;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ReadFile {

    private HashMap<String, ArrayList<String>> fileContentMap;
    private String[] excludeFileType;
    private static ReadFile instance;

    private ReadFile() {}

    static ReadFile getInstance () {
        if (instance == null) {
            instance = new ReadFile();
        }
        return instance;
    }

    public HashMap<String, ArrayList<String>> readFile (String readFilePath, String[] excludeFileType) throws IOException {
        this.fileContentMap = new HashMap<>();
        this.excludeFileType = excludeFileType;
        this.readFile(new File(readFilePath));
        return this.fileContentMap;
    }

    private void readFile(File readFile) throws IOException {

        if (readFile.exists()) {
            if (readFile.isDirectory()) {
                for (File file: Objects.requireNonNull(readFile.listFiles())) {
                    this.readFile(file);
                }
                return;
            }
        }

        String fileName = readFile.getName();
        if (fileName.startsWith(".")) {
            return;
        }
        String[] temp = fileName.split("\\.");
        String currentFileType = temp[temp.length - 1];
        for (String fileType: this.excludeFileType) {
            if (fileType.equals(currentFileType)) {
                return;
            }
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(readFile));

        ArrayList fileContend = new ArrayList<String>();

        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            fileContend.add(line);
        }

        bufferedReader.close();
        this.fileContentMap.put(readFile.getPath(), fileContend);
    }

}
