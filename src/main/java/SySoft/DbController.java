package SySoft;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

class DbController
{
    private Connection conn;

    DbController() throws ClassNotFoundException, SQLException {
        File file = new File(System.getenv("HOME") + "/dbs/Records.sqlite");
        if (file.exists() && !file.isDirectory())
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("JDBC:sqlite:" + file.getAbsolutePath());
            if (conn == null)
                System.exit(1);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Caution");
            alert.setHeaderText(null);
            alert.setContentText("Please connect a database");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK)
                System.exit(1);
        }
    }

    int insertExecutor(ArrayList<TextField> t) throws SQLException
    {
        try{
            PreparedStatement stm;
            conn.setAutoCommit(false); 
            stm = conn.prepareStatement("INSERT INTO Customer (" + buildSqlStatement6() + ") VALUES (" + buildSqlStatement7(t) + ")");
            stm.executeUpdate();    
            conn.commit();
            stm.close();
            }catch (SQLException e){
                if (conn != null)
                {
                    conn.rollback();
                    return 0;
                }
            }
        return 1;
    }

    ResultSet searchExecutor(String radio1, String radio2, String name, String surname) throws SQLException
    {
        PreparedStatement s;
        ResultSet r = null;
        try{
            conn.setAutoCommit(false);
            if ((((name != null)&&(surname != null)&&(!(name.isEmpty()))&&(!(surname.isEmpty())))))
            {
                s = conn.prepareStatement("SELECT * FROM Customer WHERE( " + radio1 + " = ? AND " + radio2 + " = ?)");
                s.setObject(1, name);
                s.setObject(2, surname);
                r = s.executeQuery();
                conn.commit();
            }
            else if ((name == null)||(name.isEmpty()))
            {
                s = conn.prepareStatement("SELECT * FROM Customer WHERE " + radio2 + " = ?");
                s.setObject(1, surname);
                r = s.executeQuery();
                conn.commit();
            }
            else
            {
                s = conn.prepareStatement("SELECT * FROM Customer WHERE " + radio1 +" = ?");
                s.setObject(1, name);
                r = s.executeQuery();
                conn.commit();
            }
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
        return r;
    }

    int updateExecutor(ArrayList<TextField> g, String id) throws SQLException
    {
        try{    
            PreparedStatement stm;
            conn.setAutoCommit(false);
            stm = conn.prepareStatement("UPDATE Customer SET " + buildSqlStatement8(g) + " WHERE Id = ?");
            stm.setObject(1, id);
            stm.executeUpdate();    
            conn.commit();
            stm.close();
        }catch (SQLException e){
            if (conn != null)
            {
                conn.rollback();
                return 0;
            }
        }
        return 1;
    }

    void deleteExecutor(int f) throws SQLException
    {
        try{
            PreparedStatement s;
            conn.setAutoCommit(false);
            s = conn.prepareStatement("DELETE from Customer WHERE Id = ?");
            s.setObject(1, f);
            s.executeUpdate();
            conn.commit();
            s.close();
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
    }

    ResultSet showAll() throws SQLException
    {
        PreparedStatement s;
        ResultSet r = null;
        try{
            conn.setAutoCommit(false);
            s = conn.prepareStatement("SELECT * FROM Customer");
            r = s.executeQuery();
            conn.commit();
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
        return r;
    }

    void newAttribute(String at) throws SQLException
    {
        try{
            PreparedStatement s;
            conn.setAutoCommit(false);
            s = conn.prepareStatement("ALTER TABLE Customer ADD COLUMN \"" + at + "\" TEXT DEFAULT 00000");
            s.execute();
            conn.commit();
            s.close();
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
    }

    void deleteAttribute(int point) throws SQLException
    {
        try{
            String s1 = buildSqlStatement4(point), s3 = buildSqlStatement5(point);
            conn.setAutoCommit(false);
            PreparedStatement st1, st2, st3, st4;
            st1 = conn.prepareStatement("CREATE TABLE CustomerTemp (" + s1 + ")");
            st1.execute();
            st2 = conn.prepareStatement("INSERT INTO CustomerTemp SELECT " + s3 + " FROM Customer");
            st2.execute();
            st3= conn.prepareStatement("DROP TABLE Customer");
            st3.execute();
            st4 = conn.prepareStatement("ALTER TABLE CustomerTemp RENAME TO Customer");
            st4.execute();
            conn.commit();
            st1.close();
            st2.close();
            st3.close();
            st4.close();
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
    }

    String[] reColumnsNames() throws SQLException
    {
        String[] f = null;
        try{
            PreparedStatement s;
            ResultSet r;
            conn.setAutoCommit(false);
            s = conn.prepareStatement("SELECT * FROM Customer");
            r = s.executeQuery();
            conn.commit();
            ResultSetMetaData rsmd = r.getMetaData();
            f = new String[rsmd.getColumnCount()];
            for (int i=0; i< rsmd.getColumnCount(); i++)
            {
                f[i] = rsmd.getColumnName(i+1);
            }
            s.close();
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
        return f;
    }

    private String buildSqlStatement1() throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=0; i<columnsNames.length; i++)
        {
            if (i != columnsNames.length-1)
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private String buildSqlStatement2(int point, String name) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=0; i<columnsNames.length; i++)
        {
            if ((i != point)&&(i != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i == point)&&(i != columnsNames.length - 1))
                sql.append("\"").append(name).append("\"").append(",");
            else if (i == point)
                sql.append("\"").append(name).append("\"");
            else
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private String buildSqlStatement3(int point, String name) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=0; i<columnsNames.length; i++)
        {
            if ((i == 0)&&(i != columnsNames.length - 1))
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL" + ",");
            else if (i == 0)
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL");
            else if ((i != point)&&(i != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i == point)&&(i != columnsNames.length - 1))
                sql.append("\"").append(name).append("\"").append(" TEXT").append(",");
            else if (i == point)
                sql.append("\"").append(name).append("\"").append(" TEXT");
            else
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
        }
        return sql.toString();
    }

    private String buildSqlStatement4(int point) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=0; i<columnsNames.length; i++)
        {
            if ((i == 0)&&(columnsNames.length > 2))
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL" + ",");
            else if (i == 0)
                sql.append("\"Id\" INTEGER PRIMARY KEY NOT NULL");
            else if ((i != point)&&(i != columnsNames.length - 1)&&(i != columnsNames.length - 2))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i != point)&&(i == columnsNames.length - 2)&&(point == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
            else if ((i != point)&&(i == columnsNames.length - 2)&&(point != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT").append(",");
            else if ((i != point)&&(i == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(" TEXT");
        }
        return sql.toString();
    }

    private String buildSqlStatement5(int point) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=0; i<columnsNames.length; i++)
        {
            if ((i == 0)&&(columnsNames.length > 2))
                sql.append("\"" + "Id" + "\"" + ",");
            else if (i == 0)
                sql.append("\"" + "Id" + "\"");
            else if ((i != point)&&(i != columnsNames.length - 1)&&(i != columnsNames.length - 2))
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i != point)&&(i == columnsNames.length - 2)&&(point == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"");
            else if ((i != point)&&(i == columnsNames.length - 2)&&(point != columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else if ((i != point)&&(i == columnsNames.length - 1))
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private String buildSqlStatement6() throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=1; i<columnsNames.length; i++)
        {
            if (i != columnsNames.length-1)
                sql.append("\"").append(columnsNames[i]).append("\"").append(",");
            else
                sql.append("\"").append(columnsNames[i]).append("\"");
        }
        return sql.toString();
    }

    private String buildSqlStatement7(ArrayList<TextField> t) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=1; i<columnsNames.length; i++)
        {
            if (i != columnsNames.length-1)
                sql.append("\"").append(t.get(i - 1).getText()).append("\"").append(",");
            else
                sql.append("\"").append(t.get(i - 1).getText()).append("\"");
        }
        return sql.toString();
    }

    private String buildSqlStatement8(ArrayList<TextField> g) throws SQLException
    {
        boolean[] blank;
        blank = new boolean[g.size()];
        for(int i=0; i<g.size(); i++)
        {
            if(g.get(i).getText().equals(""))
                blank[i] = true;
        }
        StringBuilder sql = new StringBuilder();
        String[] columnsNames = reColumnsNames();
        for(int i=1; i<columnsNames.length; i++)
        {
            if (!blank[i-1])
                sql.append("\"").append(columnsNames[i]).append("\"=").append("'").append(g.get(i - 1).getText()).append("'").append(",");
        }
        StringBuilder sb = new StringBuilder(sql.toString());
        sb.deleteCharAt(sql.length() - 1);
        return sb.toString();
    }

    void renameColumn(int point, String name) throws SQLException
    {
        try{
            String s1 = buildSqlStatement1(), s2 = buildSqlStatement2(point + 1, name), s3 = buildSqlStatement3(point + 1, name);
            conn.setAutoCommit(false);
            PreparedStatement st1, st2, st3, st4;
            st1 = conn.prepareStatement("ALTER TABLE Customer RENAME TO CustomerTemp");
            st1.execute();
            st2 = conn.prepareStatement("CREATE TABLE Customer(" + s3 + ")");
            st2.execute();
            st3 = conn.prepareStatement("INSERT INTO Customer(" + s2 + ") SELECT " + s1 + " FROM CustomerTemp");
            st3.execute();
            st4 = conn.prepareStatement("DROP TABLE CustomerTemp");
            st4.execute();
            conn.commit();
            st1.close();
            st2.close();
            st3.close();
            st4.close();
        }catch (SQLException e){
            if (conn != null)
                conn.rollback();
        }
    }
}