package org.com.sysoft.database;

import javafx.scene.control.TextField;
import org.com.sysoft.core.Alerts;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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

    public static boolean insert(List<TextField> t) throws SQLException {
        PreparedStatement stm = null;
        try {
            stm = conn.prepareStatement(builInsertStatement(t));
            for (int i = 0; i < t.size(); i++) {
                stm.setObject(i + 1, t.get(i).getText());
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

    public static ResultSet search(String radio1, String radio2, String name, String surname) throws SQLException {
        PreparedStatement s;
        ResultSet r = null;
        try {
            if ((((name != null) && (surname != null) && (!(name.isEmpty())) && (!(surname.isEmpty()))))) {
                s = conn.prepareStatement("SELECT * FROM Customer WHERE( " + radio1 + " = ? AND " + radio2 + " = ?)");
                s.setObject(1, name);
                s.setObject(2, surname);
                r = s.executeQuery();
                conn.commit();
            } else if ((name == null) || (name.isEmpty())) {
                s = conn.prepareStatement("SELECT * FROM Customer WHERE " + radio2 + " = ?");
                s.setObject(1, surname);
                r = s.executeQuery();
                conn.commit();
            } else {
                s = conn.prepareStatement("SELECT * FROM Customer WHERE " + radio1 + " = ?");
                s.setObject(1, name);
                r = s.executeQuery();
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
        return r;
    }

    public static int update(ArrayList<TextField> g, String id) throws SQLException {
        PreparedStatement stm = null;
        int result = 1;
        try {
            stm = conn.prepareStatement(buildUpdateStatement(g));
            stm.setObject(1, id);
            stm.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                result = 0;
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return result;
    }

    public static void delete(int f) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement("DELETE from Customer WHERE Id = ?")) {
            s.setObject(1, f);
            s.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
    }

    public static ResultSet showAll() throws SQLException {
        PreparedStatement s;
        ResultSet r = null;
        try {
            s = conn.prepareStatement("SELECT * FROM Customer");
            r = s.executeQuery();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
        return r;
    }

    public static void newAttribute(String at) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement("ALTER TABLE Customer ADD COLUMN \"" + at + "\" TEXT DEFAULT 00000")) {
            s.execute();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
    }

    public static void deleteField(int point) throws SQLException {
        Statement st1 = null;
        try {
            String s1 = buildSqlStatement4(point), s3 = buildSqlStatement5(point);
            st1 = conn.createStatement();
            String[] queries = {"CREATE TABLE CustomerTemp (" + s1 + ")",
                    "INSERT INTO CustomerTemp SELECT " + s3 + " FROM Customer",
                    "DROP TABLE Customer",
                    "ALTER TABLE CustomerTemp RENAME TO Customer"};
            for (String query: queries) {
                st1.addBatch(query);
            }
            st1.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        } finally {
            if (st1 != null)
                st1.close();
        }
    }

    public static String[] getColumnsNames() throws SQLException {
        String[] f = null;
        ResultSet r;
        try (PreparedStatement s = conn.prepareStatement("SELECT * FROM Customer")) {
            r = s.executeQuery();
            conn.commit();
            ResultSetMetaData rsmd = r.getMetaData();
            f = new String[rsmd.getColumnCount()];
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                f[i] = rsmd.getColumnName(i + 1);
            }
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        }
        return f;
    }

    public static void renameField(int point, String name) throws SQLException {
        Statement st1 = null;
        try {
            String s1 = buildSqlStatement1(), s2 = buildSqlStatement2(point + 1, name), s3 = buildSqlStatement3(point + 1, name);
            st1 = conn.createStatement();
            String[] queries = {"ALTER TABLE Customer RENAME TO CustomerTemp",
            "CREATE TABLE Customer(" + s3 + ")",
            "INSERT INTO Customer(" + s2 + ") SELECT " + s1 + " FROM CustomerTemp",
            "DROP TABLE CustomerTemp"};
            for (String query : queries) {
                st1.addBatch(query);
            }
            st1.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
        } finally {
            if (st1 != null)
                st1.close();
        }
    }

    private static String buildSqlStatement1() throws SQLException {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = getColumnsNames();
        for (int i = 0; i < columnsNames.length; i++) {
            if (i != columnsNames.length - 1)
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private static String buildSqlStatement2(int point, String name) throws SQLException {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = getColumnsNames();
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i != point) && (i != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i == point) && (i != columnsNames.length - 1))
                sql.append("\"").append(name).append("\"").append(",");
            else if (i == point)
                sql.append("\"").append(name).append("\"");
            else
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private static String buildSqlStatement3(int point, String name) throws SQLException {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = getColumnsNames();
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i == 0) && (i != columnsNames.length - 1))
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL" + ",");
            else if (i == 0)
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL");
            else if ((i != point) && (i != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i == point) && (i != columnsNames.length - 1))
                sql.append("\"").append(name).append("\"").append(" TEXT").append(",");
            else if (i == point)
                sql.append("\"").append(name).append("\"").append(" TEXT");
            else
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
        }
        return sql.toString();
    }

    private static String buildSqlStatement4(int point) throws SQLException {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = getColumnsNames();
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i == 0) && (columnsNames.length > 2))
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL" + ",");
            else if (i == 0)
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL");
            else if ((i != point) && (i != columnsNames.length - 1) && (i != columnsNames.length - 2))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i != point) && (i == columnsNames.length - 2) && (point == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
            else if ((i != point) && (i == columnsNames.length - 2) && (point != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i != point) && (i == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
        }
        return sql.toString();
    }

    private static String buildSqlStatement5(int point) throws SQLException {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = getColumnsNames();
        for (int i = 0; i < columnsNames.length; i++) {
            if ((i == 0) && (columnsNames.length > 2))
                sql.append("\"" + "Id" + "\"" + ",");
            else if (i == 0)
                sql.append("\"" + "Id" + "\"");
            else if ((i != point) && (i != columnsNames.length - 1) && (i != columnsNames.length - 2))
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i != point) && (i == columnsNames.length - 2) && (point == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"");
            else if ((i != point) && (i == columnsNames.length - 2) && (point != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i != point) && (i == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private static String builInsertStatement(List<TextField> t) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Customer (");
        String[] columnsNames = getColumnsNames();
        for (int i = 1; i < columnsNames.length; i++) {
            if(i > 1)
                sql.append(",");
            sql.append("\"").append(columnsNames[i]).append("\"");
        }
        sql.append(") VALUES (");
        for (int i = 1; i < columnsNames.length; i++) {
            if (i > 1)
                sql.append(",");
            sql.append("?");
        }
        sql.append(")");
        return sql.toString();
    }

    private static String buildUpdateStatement(List<TextField> g) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Customer SET ");
        String[] columnsNames = getColumnsNames();
        for (int i = 1; i < columnsNames.length; i++) {
            if (i > 1)
                sql.append(",");
            if (!g.get(i - 1).getText().equals(""))
                sql.append("\"").append(columnsNames[i]).append("\"=").append("'").append(g.get(i - 1).getText()).append("'");
        }
        sql.append(" WHERE Id = ?");
        return sql.toString();
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