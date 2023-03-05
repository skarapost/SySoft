package sysoft.entity;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FieldList implements Serializable, Iterable<Map.Entry<String, String>> {
	
	private static final long serialVersionUID = 6941789824920529738L;
	
	private LinkedHashMap<String, String> fields;
	
	public FieldList() {
		fields = new LinkedHashMap<String, String>();
	}
	
	public String newField(String column, String value) {
		if (fields.get(column) != null) {
			throw new IllegalArgumentException("Column already exists.");
		}
		return fields.put(column, value);
	}
	
	public String deleteField(String column) {
		if (fields.get(column) == null) {
			throw new IllegalArgumentException("Column does not exist.");
		}
		return fields.remove(column);
	}
	
	public boolean containsField(String column) {
		return fields.containsKey(column);
	}
	
	public String modifyField(String column, String value) {
		if (fields.get(column) == null) {
			throw new IllegalArgumentException("Column does not exist.");
		}
		return fields.put(column, value);
	}
	
	public int size() {
		return fields.size();
	}

	@Override
	public Iterator<Map.Entry<String, String>> iterator() {
		return fields.entrySet().iterator();
	}
}
