package net.thoughtworks.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static net.thoughtworks.utils.YamlDateParser.parseYamlDate;

@SuppressWarnings({"unchecked", "deprecation"})
public class YamlDataSet implements IDataSet {

    private Map<String, YamlDatasetTable> tables = new HashMap<>();

    public YamlDataSet(File file) throws FileNotFoundException {
        Map<String, List<Map>> data = (Map<String, List<Map>>) Yaml.load(file);
        for (Map.Entry<String, List<Map>> ent : data.entrySet()) {
            String tableName = ent.getKey();
            List<Map> rows = ent.getValue();
            parseDate(rows);
            createTable(tableName, rows);
        }
    }

    public ITable getTable(String tableName) throws DataSetException {
        return tables.get(tableName);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        return tables.get(tableName).getTableMetaData();
    }

    public String[] getTableNames() throws DataSetException {
        return tables.keySet().toArray(new String[tables.size()]);
    }

    public ITable[] getTables() throws DataSetException {
        return tables.values().toArray(new ITable[tables.size()]);
    }

    public ITableIterator iterator() throws DataSetException {
        return new DefaultTableIterator(getTables());
    }

    public ITableIterator reverseIterator() throws DataSetException {
        return new DefaultTableIterator(getTables(), true);
    }

    @Override
    public boolean isCaseSensitiveTableNames() {
        return false;
    }

    YamlDatasetTable createTable(String name, List<Map> rows) {
        YamlDatasetTable table = buildTable(name, rows);
        tables.put(name, table);
        return table;
    }

    private YamlDatasetTable buildTable(String name, List<Map> rows) {
        if (Objects.isNull(rows)) {
            return new YamlDatasetTable(name, new ArrayList<>());
        }
        List<String> columnNames = rows.size() > 0 ? new ArrayList(rows.get(0).keySet()) : null;
        YamlDatasetTable table = new YamlDatasetTable(name, columnNames);
        for (Map values : rows) {
            table.addRow(values);
        }
        return table;
    }

    private void parseDate(List<Map> rows) {

        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        for (Map row : rows) {
            Set<Map.Entry> entries = row.entrySet();
            for (Map.Entry field : entries) {
                Object valueObj = field.getValue();
                if (Objects.nonNull(valueObj)) {
                    String value = String.valueOf(valueObj);
                    row.put(field.getKey(), parseYamlDate(value));
                }
            }
        }
    }
}
