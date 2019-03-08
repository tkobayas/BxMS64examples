package com.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SQLGen {

    public static void main(String[] args) throws Exception {
        BufferedReader bf = new BufferedReader(new FileReader(new File("/home/tkobayas/tmp5.txt")));
        while (bf.ready()) {
            String line = bf.readLine();
            System.out.println("stmt.executeUpdate(\"DROP TABLE " + line.trim() + "\");");
        }
    }
}
