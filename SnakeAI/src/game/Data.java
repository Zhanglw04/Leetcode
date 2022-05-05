package game;

import java.sql.*;
import java.util.List;
import java.util.Vector;

public class Data {

    // MySQL 8.0 or later - JDBC driver name and database URL, the configuration depends on the mysql version.
    private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String dbUrl1 = "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    /*
        MySQL 8.0 or earlier - JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://localhost:3306";
     */

    // The username and password for logging in to the database need be set based on your own requirements
    private String user = "root";
    private String pwd = "123456";
    private boolean resultDB = false;
    private boolean resultTable = false;
    private String databaseName = "SnakeGame";
    private String tableName = "ScoreTable";
    private String dbUrl2 = "jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public boolean isResultTable() {
        return resultTable;
    }

    public void setResultTable(boolean resultTable) {
        this.resultTable = resultTable;
    }

    public boolean isResultDB() {
        return resultDB;
    }

    public void setResultDB(boolean resultDB) {
        this.resultDB = resultDB;
    }

    public void creatDatabase() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbUrl1, user, pwd);
            if (!resultDB) {
                String createDatabase = "create database if not exists " + databaseName;
                stmt = conn.prepareStatement(createDatabase);
                stmt.executeUpdate();
                resultDB = true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void createTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbUrl2, user, pwd);
            if (!resultTable) {
                String createTable = "Create table if not exists " + tableName +
                        "(id INTEGER not null primary key auto_increment, createTime datetime DEFAULT CURRENT_TIMESTAMP,  score INTEGER, gameTime char(10), snakeLength INTEGER, aiLength INTEGER, foodAmount INTEGER)";
                stmt = conn.prepareStatement(createTable);
                stmt.executeUpdate();
                resultTable = true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void recording(List<String> lists) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a Database...");
            conn = DriverManager.getConnection(dbUrl2, user, pwd);

            String sql = "insert into " + tableName + " (score, gameTime, snakeLength, aiLength, foodAmount) value (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(lists.get(0)));
            stmt.setString(2, lists.get(1));
            stmt.setInt(3, Integer.parseInt(lists.get(2)));
            stmt.setInt(4, Integer.parseInt(lists.get(3)));
            stmt.setInt(5, Integer.parseInt(lists.get(4)));
            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }

    public Vector<Vector<String>> getBestRecord(int num) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Vector<Vector<String>> res = new Vector<>();
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a Database...");
            conn = DriverManager.getConnection(dbUrl2, user, pwd);

            String sql = "select * from " + tableName + " order by score desc, snakeLength desc, gameTime limit ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, num);
            resultSet = stmt.executeQuery();
            String before = null;
            while (resultSet.next()) {
                Vector<String> list = new Vector<>();
                if (String.valueOf(resultSet.getTimestamp(2)).equals(before)) {
                    before = String.valueOf(resultSet.getTimestamp(2));
                    continue;
                }
                list.add(String.valueOf(resultSet.getTimestamp(2)));
                list.add(String.valueOf(resultSet.getInt(3)));
                list.add(resultSet.getString(4));
                list.add(String.valueOf(resultSet.getInt(5)));
                list.add(String.valueOf(resultSet.getInt(6)));
                list.add(String.valueOf(resultSet.getInt(7)));
                res.add(list);
                before = String.valueOf(resultSet.getTimestamp(2));
            }
            resultSet.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
        return res;
    }

    public Vector<Vector<String>> getNewRecord(int num) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Vector<Vector<String>> res = new Vector<>();
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a Database...");
            conn = DriverManager.getConnection(dbUrl2, user, pwd);

            String sql = "select * from " + tableName + " order by id desc limit ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, num);
            resultSet = stmt.executeQuery();
            String before = null;
            while (resultSet.next()) {
                if (String.valueOf(resultSet.getTimestamp(2)).equals(before)) {
                    before = String.valueOf(resultSet.getTimestamp(2));
                    continue;
                }
                Vector<String> list = new Vector<>();
                list.add(String.valueOf(resultSet.getTimestamp(2)));
                list.add(String.valueOf(resultSet.getInt(3)));
                list.add(resultSet.getString(4));
                list.add(String.valueOf(resultSet.getInt(5)));
                list.add(String.valueOf(resultSet.getInt(6)));
                list.add(String.valueOf(resultSet.getInt(7)));
                res.add(list);
                before = String.valueOf(resultSet.getTimestamp(2));
            }
            resultSet.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
        return res;
    }
}
