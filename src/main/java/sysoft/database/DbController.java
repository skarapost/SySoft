package sysoft.database;

import javafx.scene.control.TextField;
import sysoft.core.Alerts;
import sysoft.entity.Entry;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DbController {
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
            stringBuilder.append("INSERT INTO Customer (");
            String[] columnsNames = getFields(false, true);
            for (int i = 0; i < columnsNames.length; i++) {
                if(i > 0) {
                	stringBuilder.append(",");
                }
                stringBuilder.append("\"").append(columnsNames[i]).append("\"");
            }
            stringBuilder.append(") VALUES (");
            for (int i = 0; i < columnsNames.length; i++) {
                if (i > 0) {
                	stringBuilder.append(",");
                }
                stringBuilder.append("?");
            }
            stringBuilder.append(")");
        	
            stm = conn.prepareStatement(stringBuilder.toString());
            int counter = 1;
            stm.setObject(counter, System.currentTimeMillis());
            for (Map.Entry<String, String> field: entry.getListOfFields()) {
            	stm.setObject(++counter, field.getValue());
            }
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

    public static boolean update(Entry entry, String id) throws SQLException {
        PreparedStatement stm = null;
        try {
        	
        	StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE Customer SET ");
            String[] columnsNames = getFields(false, false);
            Iterator<java.util.Map.Entry<String, String>> listOfFieldsIterator = entry.getListOfFields().iterator();
            for (int i = 0; i < columnsNames.length; i++) {
                if (i > 0) {
                	stringBuilder.append(",");
                }
                String value = listOfFieldsIterator.next().getValue();
                if (!value.equals("")) {
                	stringBuilder.append("\"").append(columnsNames[i]).append("\"=").append("'").append(value).append("'");
                }
            }
            stringBuilder.append(" WHERE Id = ?");
        	
            stm = conn.prepareStatement(stringBuilder.toString());
            stm.setObject(1, id);
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

    public static void delete(int id) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement("DELETE from Customer WHERE Id = ?")) {
            s.setObject(1, id);
            s.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
    }

    public static ResultSet showAll() throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        try {
        	preparedStatement = conn.prepareStatement("SELECT * FROM Customer");
            resultSet = preparedStatement.executeQuery();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
        return resultSet;
    }

    public static void newField(String at) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement("ALTER TABLE Customer ADD COLUMN \"" + at + "\" TEXT DEFAULT 00000")) {
            s.execute();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
    }

    public static void deleteField(int point) throws SQLException {
        Statement statement = null;
        try {
            String s1 = buildDeleteCreate(point), s3 = buildDeleteSelect(point);
            statement = conn.createStatement();
            String[] queries = {"CREATE TABLE CustomerTemp (" + s1 + ")",
                    "INSERT INTO CustomerTemp SELECT " + s3 + " FROM Customer",
                    "DROP TABLE Customer",
                    "ALTER TABLE CustomerTemp RENAME TO Customer"};
            for (String query: queries) {
            	statement.addBatch(query);
            }
            statement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        } finally {
            if (statement != null)
            	statement.close();
        }
    }

    public static String[] getFields(boolean includeId, boolean includeTimeOfCreation) throws SQLException {
        String[] columnNames = null;
        ResultSet resultSet;
        try (PreparedStatement s = conn.prepareStatement("SELECT * FROM Customer")) {
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

    public static void renameField(int point, String name) throws SQLException {
        Statement statement = null;
        try {
            String select = Arrays.stream(getFields(true, true)).map(column -> "\"" + column + "\",").toString().replaceAll(".$", "");
            String renamedColumns = buildRenamedFields(point + 1, name);
            String renamedCreate = buildRenamedCreate(point + 1, name);
            statement = conn.createStatement();
            String[] queries = {"ALTER TABLE Customer RENAME TO CustomerTemp",
            "CREATE TABLE Customer(" + renamedCreate + ")",
            "INSERT INTO Customer(" + renamedColumns + ") SELECT " + select + " FROM CustomerTemp",
            "DROP TABLE CustomerTemp"};
            for (String query : queries) {
            	statement.addBatch(query);
            }
            statement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        } finally {
            if (statement != null)
            	statement.close();
        }
    }

    private static String buildRenamedFields(int point, String name) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        String[] columnsNames = getFields(true, true);
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i != point) && (i != columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i == point) && (i != columnsNames.length - 1))
            	stringBuilder.append("\"").append(name).append("\"").append(",");
            else if (i == point)
            	stringBuilder.append("\"").append(name).append("\"");
            else
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"");
        }
        return stringBuilder.toString();
    }

    private static String buildRenamedCreate(int point, String name) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        String[] columnsNames = getFields(true, true);
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i == 0) && (i != columnsNames.length - 1))
            	stringBuilder.append("\"Id\" INTEGER PRIMARY KEY NOT NULL" + ",");
            else if (i == 0)
            	stringBuilder.append("\"Id\" INTEGER PRIMARY KEY NOT NULL");
            else if ((i != point) && (i != columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i == point) && (i != columnsNames.length - 1))
            	stringBuilder.append("\"").append(name).append("\"").append(" TEXT").append(",");
            else if (i == point)
            	stringBuilder.append("\"").append(name).append("\"").append(" TEXT");
            else
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
        }
        return stringBuilder.toString();
    }

    private static String buildDeleteCreate(int point) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        String[] columnsNames = getFields(true, true);
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i == 0) && (columnsNames.length > 2))
            	stringBuilder.append("\"Id\" INTEGER PRIMARY KEY NOT NULL" + ",");
            else if (i == 0)
            	stringBuilder.append("\"Id\" INTEGER PRIMARY KEY NOT NULL");
            else if ((i != point) && (i != columnsNames.length - 1) && (i != columnsNames.length - 2))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i != point) && (i == columnsNames.length - 2) && (point == columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
            else if ((i != point) && (i == columnsNames.length - 2) && (point != columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i != point) && (i == columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
        }
        return stringBuilder.toString();
    }

    private static String buildDeleteSelect(int point) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        String[] columnsNames = getFields(true, true);
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i == 0) && (columnsNames.length > 2))
            	stringBuilder.append("\"" + "Id" + "\"" + ",");
            else if (i == 0)
            	stringBuilder.append("\"" + "Id" + "\"");
            else if ((i != point) && (i != columnsNames.length - 1) && (i != columnsNames.length - 2))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i != point) && (i == columnsNames.length - 2) && (point == columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"");
            else if ((i != point) && (i == columnsNames.length - 2) && (point != columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i != point) && (i == columnsNames.length - 1))
            	stringBuilder.append("\"").append(columnsNames[i]).append("\"");
        }
        return stringBuilder.toString();
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