package com.bigcompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface EmployeeFileReader {
    List<String[]> readFile(BufferedReader reader) throws IOException;
}
