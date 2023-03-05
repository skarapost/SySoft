package sysoft.entity;

public class Entry {
	
	private int id;
	private long timeOfCreation;
	private FieldList listOfFields;
	
	public Entry(int id, long timeOfCreation) {
		this.id = id;
		this.timeOfCreation = timeOfCreation;
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

	public int getId() {
		return id;
	}

}
