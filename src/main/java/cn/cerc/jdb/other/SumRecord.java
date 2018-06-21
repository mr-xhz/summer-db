package cn.cerc.jdb.other;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.Record;

public class SumRecord extends Record {
    private static final long serialVersionUID = -8836802853579764175L;
    private DataSet dataSet;
    private Map<String, Double> fields = new HashMap<>();

    public SumRecord(DataSet dataSet) {
        this.setDataSet(dataSet);
    }

    public SumRecord addField(String field) {
        if (!fields.containsKey(field))
            fields.put(field, 0.0);
        return this;
    }

    public SumRecord addField(String... args) {
        for (String field : args) {
            if (!fields.containsKey(field))
                fields.put(field, 0.0);
        }
        return this;
    }

    public SumRecord run() {
        for (Record rs : this.dataSet) {
            for (String field : this.fields.keySet()) {
                Double value = fields.get(field);
                value += rs.getDouble(field);
                fields.put(field, value);
            }
        }
        for (String field : this.fields.keySet()) {
            Double value = fields.get(field);
            this.setField(field, value);
        }
        return this;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Map<String, Double> getFields() {
        return fields;
    }

    /**
     * 仅在调试时使用
     */
    public void print() {
        for (String field : fields.keySet()) {
            System.out.println(String.format("%s: %s", field, "" + fields.get(field)));
        }
    }
}
