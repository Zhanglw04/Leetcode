package JDBCTEST;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SnakeTest {

    // MySQL 8.0 or later - JDBC driver name and database URL, the configuration depends on the mysql version.
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final  String DB_URL1 = "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    /*
        MySQL 8.0 or earlier - JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://localhost:3306";
     */

    // The username and password for logging in to the database need be set based on your own requirements
    private final String USER = "root";
    private final String PASS = "123456";
    private boolean resultDB = false;
    private boolean resultTable = false;
    private final String databaseName = "Hello";
    private final String tableName = "Test";
    private final  String DB_URL2 = "jdbc:mysql://localhost:3306/" + databaseName +"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

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
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL1,USER,PASS);
            if (!resultDB) {
                String createDatabase = "create database if not exists " + databaseName;
                stmt = conn.prepareStatement(createDatabase);
                stmt.executeUpdate();
                resultDB = true;
            }
        }catch(SQLException se) {
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

    public void createTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL2,USER,PASS);
            if (!resultTable) {
                String createTable = "Create table if not exists " + tableName +
                        "(id INTEGER not null primary key auto_increment, createTime datetime DEFAULT CURRENT_TIMESTAMP,  score INTEGER, gameTime char(10), snakeLength INTEGER, aiLength INTEGER, foodAmount INTEGER)";
                stmt = conn.prepareStatement(createTable);
                stmt.executeUpdate();
                resultTable = true;
            }
        }catch(SQLException se) {
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
    public void recording(List<String> lists) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a Database...");
            conn = DriverManager.getConnection(DB_URL2,USER,PASS);

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

        }catch(SQLException se) {
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
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
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a Database...");
            conn = DriverManager.getConnection(DB_URL2,USER,PASS);

            String sql = "select * from " + tableName + " a where (select count(*)+1 from " + tableName + " b where b.score > a.score) <= ?" +
                    " order by score desc, snakeLength desc, gameTime";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, String.valueOf(num));
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Vector<String> list = new Vector<>();
                list.add(String.valueOf(resultSet.getTimestamp(2)));
                list.add(String.valueOf(resultSet.getInt(3)));
                list.add(resultSet.getString(4));
                list.add(String.valueOf(resultSet.getInt(5)));
                list.add(String.valueOf(resultSet.getInt(6)));
                list.add(String.valueOf(resultSet.getInt(7)));
                res.add(list);
//                System.out.println(resultSet.getInt(1));
//                System.out.println(resultSet.getTimestamp(2));
//                System.out.println(resultSet.getInt(3));
//                System.out.println(resultSet.getString(4));
//                System.out.println(resultSet.getInt(5));
//                System.out.println(resultSet.getInt(6));
//                System.out.println(resultSet.getInt(7));
            }
            resultSet.close();
            stmt.close();
            conn.close();

        }catch(SQLException se) {
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (resultSet!=null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
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
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a Database...");
            conn = DriverManager.getConnection(DB_URL2,USER,PASS);

            String sql = "select * from " + tableName + " order by id desc limit ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, num);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Vector<String> list = new Vector<>();
                list.add(String.valueOf(resultSet.getTimestamp(2)));
                list.add(String.valueOf(resultSet.getInt(3)));
                list.add(resultSet.getString(4));
                list.add(String.valueOf(resultSet.getInt(5)));
                list.add(String.valueOf(resultSet.getInt(6)));
                list.add(String.valueOf(resultSet.getInt(7)));
                res.add(list);
//                System.out.println(resultSet.getInt(1));
//                System.out.println(resultSet.getTimestamp(2));
//                System.out.println(resultSet.getInt(3));
//                System.out.println(resultSet.getString(4));
//                System.out.println(resultSet.getInt(5));
//                System.out.println(resultSet.getInt(6));
//                System.out.println(resultSet.getInt(7));
            }
            resultSet.close();
            stmt.close();
            conn.close();

        }catch(SQLException se) {
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (resultSet!=null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
        return res;
    }

    public static void main(String[] args) {
        SnakeTest test = new SnakeTest();
        test.creatDatabase();
        test.createTable();
        System.out.println(test.isResultDB());
//        List<String> list = new ArrayList<>();
//        list.add("120");
//        list.add("00:12:53");
//        list.add("11");
//        list.add("20");
//        list.add("41");
//        test.recording(list);
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        list = new ArrayList<>();
//        list.add("500");
//        list.add("00:30:01");
//        list.add("30");
//        list.add("0");
//        list.add("51");
//        test.recording(list);
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        list = new ArrayList<>();
//        list.add("10");
//        list.add("00:00:24");
//        list.add("3");
//        list.add("5");
//        list.add("8");
//        test.recording(list);
        Vector<Vector<String>> res = test.getBestRecord(2);
        System.out.println("!");
    }




//            String sql;
//            sql = "SELECT id, name, url FROM websites";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            // 展开结果集数据库
//            while(rs.next()){
//                // 通过字段检索
//                int id  = rs.getInt("id");
//                String name = rs.getString("name");
//                String url = rs.getString("url");
//
//                // 输出数据
//                System.out.print("ID: " + id);
//                System.out.print(", 站点名称: " + name);
//                System.out.print(", 站点 URL: " + url);
//                System.out.print("\n");
//            }
            // 完成后关闭


}
