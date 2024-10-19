package com.example.file_database_service.service;


import com.example.file_database_service.entity.TableData;
import com.example.file_database_service.entity.TableMetadata;
import com.example.file_database_service.repo.TableRepository;
import com.example.file_database_service.entity.Column;
import com.example.file_database_service.util.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TableUtils tableUtils;

    private String createdTableName;

    public void createTable(String sql) throws IOException {
        if (sql.toUpperCase().startsWith("CREATE TABLE")) {
            String tableName = tableUtils.parseTableName(sql);
            List<Column> columns = tableUtils.parseColumns(sql);
            TableMetadata metadata = new TableMetadata(tableName, columns);
            tableRepository.saveMetadata(metadata);
        } else {
            throw new IllegalArgumentException("Invalid SQL command for table creation.");
        }
    }

    public void insertData(String sql) throws IOException {
        if (sql.toUpperCase().startsWith("INSERT INTO")) {
            String tableName = tableUtils.parseTableName(sql);

            List<String> insertColumns = tableUtils.parseInsertColumns(sql);
            List<String> values = tableUtils.parseValues(sql);

            if (insertColumns.size() != values.size()) {
                throw new IllegalArgumentException("Number of columns and inserted values do not match");
            }

            TableMetadata metadata = tableRepository.readMetadata();

            if(!tableName.equals(metadata.getTableName()))
            {
                throw new IllegalArgumentException("Table name does not match the actual table name in the database");
            }

            List<Column> tableColumns = metadata.getColumns();

            tableUtils.validateInsertColumns(tableColumns, insertColumns);
            List<String> orderedValues = tableUtils.reorderValuesWithDefaults(insertColumns, values, tableColumns);

            tableUtils.validateDataTypes(orderedValues, tableColumns);

            Map<String, String> rowData = new LinkedHashMap<>();
            for (int i = 0; i < tableColumns.size(); i++) {
                String columnName = tableColumns.get(i).getName();
                String value = orderedValues.get(i);
                rowData.put(columnName, value);
            }

            TableData tableData = new TableData(rowData);
            tableRepository.saveData(tableData);

        } else {
            throw new IllegalArgumentException("Invalid SQL command for data insertion.");
        }
    }

    public List<TableData> getData() throws IOException {

        List<TableData> data = tableRepository.readData();

        return data;
    }

}

