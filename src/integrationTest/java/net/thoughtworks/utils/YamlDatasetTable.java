package net.thoughtworks.utils;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.dbunit.dataset.datatype.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class YamlDatasetTable implements ITable {
    private List<Map> data;
    private ITableMetaData meta;

    YamlDatasetTable(String name, List<String> columnNames) {
        this.data = new ArrayList<>();
        meta = createMeta(name, columnNames);
    }

    public int getRowCount() {
        return data.size();
    }

    public ITableMetaData getTableMetaData() {
        return meta;
    }

    public Object getValue(int row, String column) throws DataSetException {
        if (data.size() <= row) {
            throw new RowOutOfBoundsException("" + row);
        }
        return data.get(row).get(column.toUpperCase());
    }

    void addRow(Map values) {
        data.add(convertMap(values));
    }

    private ITableMetaData createMeta(String name, List<String> columnNames) {
        Column[] columns = null;
        if (columnNames != null) {
            columns = new Column[columnNames.size()];
            for (int i = 0; i < columnNames.size(); i++) {
                columns[i] = new Column(columnNames.get(i), DataType.UNKNOWN);
            }
        }
        return new DefaultTableMetaData(name, columns);
    }

    private Map convertMap(Map<String, Object> values) {
        Map<String, Object> ret = new HashMap<>();
        for (Map.Entry<String, Object> ent : values.entrySet()) {
            ret.put(ent.getKey().toUpperCase(), ent.getValue());
        }
        return ret;
    }
}
