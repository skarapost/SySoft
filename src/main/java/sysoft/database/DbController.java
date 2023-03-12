package sysoft.database;

import sysoft.core.Alerts;
import sysoft.entity.Entry;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;


public class DbController {
	
	public static enum Tables {
		Customer,
		Fields;
	}
	
    private static Connection conn;

    private DbController() {
    }

    public static DbController initialiseDatabase() {
        DbController controller = DbControllerHelper.dbController;
        if (conn == null)
            Alerts.fireMissingDatabaseAlert();
        return controller;
    }

    public static boolean insert(Entry entry) throws SQLException {
        PreparedStatement stm = null;
        try {
        	StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT INTO Customer (TimeOfCreation,FieldList) VALUES (?,?)");
            stm = conn.prepareStatement(stringBuilder.toString());
            stm.setObject(1, System.currentTimeMillis());
            stm.setObject(2, entry.getListOfFields());
            stm.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                return false;
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return true;
    }

    public static ResultSet search(String firstCriteria, String secondCriteria, String name, String surname) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        try {
            if ((((name != null) && (surname != null) && (!(name.isEmpty())) && (!(surname.isEmpty()))))) {
            	preparedStatement = conn.prepareStatement("SELECT * FROM Customer WHERE( " + firstCriteria + " = ? AND " + secondCriteria + " = ?)");
            	preparedStatement.setObject(1, name);
            	preparedStatement.setObject(2, surname);
            	resultSet = preparedStatement.executeQuery();
            } else if ((name == null) || (name.isEmpty())) {
            	preparedStatement = conn.prepareStatement("SELECT * FROM Customer WHERE " + secondCriteria + " = ?");
            	preparedStatement.setObject(1, surname);
            	resultSet = preparedStatement.executeQuery();
            } else {
            	preparedStatement = conn.prepareStatement("SELECT * FROM Customer WHERE " + firstCriteria + " = ?");
            	preparedStatement.setObject(1, name);
            	resultSet = preparedStatement.executeQuery();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
        return resultSet;
    }

    public static boolean update(Entry entry) throws SQLException {
        PreparedStatement stm = null;
        try {
        	StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE Customer SET FieldList = ? WHERE Id = ?");
            stm = conn.prepareStatement(stringBuilder.toString());
            stm.setObject(1, entry.getListOfFields());
            stm.setObject(2, entry.getId());
            stm.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                return false;
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return true;
    }

    public static void delete(Entry entry) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement("DELETE from Customer WHERE Id = ?")) {
            s.setObject(1, entry.getId());
            s.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
    }

    public static ResultSet showAll(Tables table) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        try {
        	preparedStatement = conn.prepareStatement("SELECT * FROM " + table.name());
            resultSet = preparedStatement.executeQuery();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
        return resultSet;
    }

    public static void newField(String field) throws SQLException {
        PreparedStatement stm = null;
        try {
        	StringBuilder stringBuilder = new StringBuilder();
        	stringBuilder.append("INSERT INTO Fields (Name) VALUES (?)");
            stm = conn.prepareStatement(stringBuilder.toString());
            stm.setObject(1, field);
            stm.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        
    }

    public static void deleteField(String field) throws SQLException {
    	PreparedStatement stm = null;
        try {
        	StringBuilder stringBuilder = new StringBuilder();
        	stringBuilder.append("DELETE FROM Fields WHERE Name = ?");
            stm = conn.prepareStatement(stringBuilder.toString());
            stm.setObject(1, field);
            stm.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static void renameField(String field, String newField) throws SQLException {
    	PreparedStatement stm = null;
    	try {	
        	StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE Fields SET Name = ? WHERE Name = ?");
            stm = conn.prepareStatement(stringBuilder.toString());
            stm.setObject(1, field);
            stm.setObject(2, newField);
            stm.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static String[] getFields(boolean includeId, boolean includeTimeOfCreation) throws SQLException {
    	String[] columnNames = null;
    	ResultSet resultSet;
    	try (PreparedStatement s = conn.prepareStatement("SELECT * FROM Fields")) {
    		resultSet = s.executeQuery();
    		conn.commit();
    		ResultSetMetaData rsmd = resultSet.getMetaData();
    		columnNames = new String[rsmd.getColumnCount()];
    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
    			if ((i == 0 && !includeId) || (i == 1 && !includeTimeOfCreation)) { continue; }
    			columnNames[i] = rsmd.getColumnName(i + 1);
    		}
    	} catch (SQLException e) {
    		if (conn != null)
    			conn.rollback();
    	}
    	return columnNames;
    }

    private static class DbControllerHelper {
        private static final DbController dbController = new DbController();

        static {
            URI s = null;
            try {
                s = DbController.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String dir = s.getPath().replace("sysoft-1.0.jar", "");
            File file = new File(dir + "Records.sqlite");
            if (file.exists() && !file.isDirectory()) {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    conn = DriverManager.getConnection("JDBC:sqlite:" + file.getAbsolutePath());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (conn != null)
                        conn.setAutoCommit(false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}