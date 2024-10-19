package com.example.file_database_service.repo;


import com.example.file_database_service.entity.Column;
import com.example.file_database_service.entity.TableData;
import com.example.file_database_service.entity.TableMetadata;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Repository
public class TableRepository {

    private static final String METADATA_FILE = "metadata.txt";
    private static final String DATA_FILE = "table_data.txt";

    public void saveMetadata(TableMetadata metadata) throws IOException {

        clearDataFile();

        String tableName = metadata.getTableName();
        List<Column> columns = metadata.getColumns();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(METADATA_FILE))) {
            writer.write("Table Name: " + tableName);
            writer.newLine();
            for (Column column : columns) {
                writer.write("Column: " + column.getName() + ", Type: " + column.getType());
                writer.newLine();
            }
        }
    }

    public TableMetadata readMetadata() throws IOException {
        File file = new File(METADATA_FILE);
        if (!file.exists()) {
            throw new IOException("Metadata file does not exist.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tableNameLine = reader.readLine();
            String tableName = tableNameLine.split(": ")[1].trim();
            List<Column> columns = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Column:")) {
                    String[] parts = line.split(", ");
                    String columnName = parts[0].split(":")[1].trim();
                    String columnType = parts[1].split(":")[1].trim();
                    columns.add(new Column(columnName, columnType));
                }
            }
            return new TableMetadata(tableName, columns);
        }
    }

    public void saveData(TableData data) throws IOException {
        TableMetadata metadata = readMetadata();
        List<Column> columns = metadata.getColumns();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            for (Map.Entry<String, String> entry : data.getRowData().entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + ", ");
            }
            writer.newLine();
        }
    }

    public List<TableData> readData() throws IOException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            throw new IOException("Data file does not exist.");
        }

        String TableName = readMetadata().getTableName();

        List<TableData> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] entries = line.split(",");
                Map<String, String> rowData = new LinkedHashMap<>();
                for (String entry : entries) {
                    if (!entry.trim().isEmpty()) {
                        String[] keyValue = entry.split(":");
                        String colVal = keyValue[1].trim();
                        if(colVal.startsWith("\""))
                        {
                            colVal = colVal.substring(1,colVal.length()-1);
                        }
                        rowData.put(keyValue[0].trim(), colVal);
                    }
                }
                rows.add(new TableData(rowData));
            }
        }
        return rows;
    }

    private void clearDataFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write("");
        }
    }
}

