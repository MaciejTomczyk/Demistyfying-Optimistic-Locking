package org.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class App {
    static String url = "jdbc:postgresql://localhost:5432/postgres";
    static String user = "postgres";
    static String pass = "example";
    static String GET_BY_ID = "SELECT id, name, version FROM cats WHERE id = ?";
    static String GET_ALL = "SELECT * FROM cats";
    static String UPDATE_NAME_BY_ID_AND_VERSION =
            "UPDATE cats SET name = ?, version = version + 1 WHERE id = ? AND version = ?";


    public static void main(String[] args) throws SQLException, InterruptedException {
        var allCats = getAllCats();
        System.out.println(allCats);

        UpdateProcess updateProcess1 = new UpdateProcess(1, "Thread-1");
        UpdateProcess updateProcess2 = new UpdateProcess(1, "Thread-2");

        Thread t1 = new Thread(updateProcess1, "Thread-1");
        Thread t2 = new Thread(updateProcess2, "Thread-2");
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        var catAfterUpdate = getCatById(1);
        System.out.println(catAfterUpdate);


    }

    private static List<Cat> getAllCats() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = conn.prepareStatement(GET_ALL);

        ResultSet rs = ps.executeQuery();
        List<Cat> cats = new ArrayList<>();
        while (rs.next()) {
            cats.add(new Cat(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("version")));
        }
        return cats;
    }

    private static Cat getCatById(int id) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = conn.prepareStatement(GET_BY_ID);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Cat(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("version")
            );
        } else {
            return null;
        }
    }

    private static void updateCatName(Cat cat) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = conn.prepareStatement(UPDATE_NAME_BY_ID_AND_VERSION);
        ps.setString(1, cat.name());
        ps.setInt(2, cat.id());
        ps.setInt(3, cat.version());
        int updatedRows = ps.executeUpdate();
        if (updatedRows == 0) {
            throw new OptimisticLockingException("Cat with id " + cat.id() + " has been modified by another transaction.");
        }
    }

    static void optimisticLockingAwareUpdate(int id, String name) throws SQLException {
        var catToBeUpdated = getCatById(id);
        if (catToBeUpdated != null) {
            var updatedCat = new Cat(catToBeUpdated.id(), name, catToBeUpdated.version());
            updateCatName(updatedCat);
        }
    }
}
