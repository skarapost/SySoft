package sysoft.entity;

import java.sql.SQLException;

import sysoft.database.DbController;

public class Entry {
	
	private int id;
	private long timeOfCreation;
	private FieldList listOfFields;
	
	public Entry(){}
	
	public Entry(int id, long timeOfCreation) {
		this.id = id;
		this.timeOfCreation = timeOfCreation;
	}

	public int getId() {
		return id;
	}
	
	public long getTimeOfCreation() {
		return timeOfCreation;
	}

	public FieldList getListOfFields() {
		return listOfFields;
	}

	public void setListOfFields(FieldList listOfFields) {
		this.listOfFields = listOfFields;
	}

	public static Entry getNewEntry(String... fields) throws SQLException {
		FieldList list = new FieldList();
		String[] columnsNames = DbController.getFields(false, false);
		for (int i = 0; i < columnsNames.length; i++) {
			list.newField(columnsNames[i], fields[i]);
		}
		Entry entry = new Entry();
		entry.setListOfFields(list);
		return entry;
	}
	
}
