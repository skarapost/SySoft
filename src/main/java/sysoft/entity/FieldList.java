package sysoft.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FieldList implements Serializable {
	
	private static final long serialVersionUID = 6941789824920529738L;
	
	private Map<String, String> fields = new LinkedHashMap<String, String>();
	
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
}
