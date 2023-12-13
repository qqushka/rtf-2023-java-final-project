package ru.pantyukhin.fp2023;

import ru.pantyukhin.fp2023.dto.Earthquakes;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static final String SAVE_EARTHQUAKES = "INSERT INTO EARTHQUAKES(id, depth_in_meters," +
            " magnitude_type, magnitude, state, time) values (?, ?, ?, ?, ?, ?)";

    private Connection conn;

    public void close() throws SQLException {
        conn.close();
    }
    public void openConnection(String dbFileName) throws SQLException {
        String url = "jdbc:sqlite:" + dbFileName + ".db";
        conn = DriverManager.getConnection(url);
    }

    public void saveEarthquakes(List<Earthquakes> earthquakesList) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM EARTHQUAKES");
        }
        conn.setAutoCommit(false);
        try (PreparedStatement pstms = conn.prepareStatement(SAVE_EARTHQUAKES)) {
            earthquakesList.forEach(earthquakes -> {
                try {
                    pstms.setString(1, earthquakes.getId());
                    pstms.setInt(2, earthquakes.getDepth_in_meters());
                    pstms.setString(3, earthquakes.getMagnitude_type());
                    pstms.setFloat(4, earthquakes.getMagnitude());
                    pstms.setString(5, earthquakes.getState());
                    pstms.setString(6, earthquakes.getTime());
                    pstms.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            pstms.executeBatch();
            conn.commit();
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Map<Integer, Integer> analyzeEarthquakeData() {
        String query = "SELECT SUBSTR(time, 1, 4) as year, COUNT(*) as count " +
                "FROM earthquakes " +
                "GROUP BY year " +
                "ORDER BY year";

        Map<Integer, Integer> earthquakeCountsByYear = null;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            earthquakeCountsByYear = new HashMap<>();

            while (rs.next()) {
                int year = rs.getInt("year");
                int count = rs.getInt("count");
                earthquakeCountsByYear.put(year, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return earthquakeCountsByYear;
    }

    public float printAverageMagnitudeForState(String state) {
        String query = "SELECT AVG(magnitude) AS avg_magnitude FROM earthquakes WHERE state = ?";

        float avgMagnitude = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, state);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                avgMagnitude = rs.getFloat("avg_magnitude");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avgMagnitude;
    }

    public String deepestEarthquakeStateIn2013(int year) {
        String state = null;

        String query = "SELECT state, MAX(depth_in_meters) AS max_depth " +
                "FROM earthquakes " +
                "WHERE SUBSTR(time, 1, 4) = ? " +
                "GROUP BY state " +
                "ORDER BY max_depth DESC " +
                "LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, String.valueOf(year));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                state = rs.getString("state");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return state;
    }
}
