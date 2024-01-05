package ru.pantyukhin.fp2023;

import ru.pantyukhin.fp2023.dto.Earthquakes;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Connection conn;

    private static final String CREATE_STATES_TABLE = "CREATE TABLE IF NOT EXISTS states (" +
            "state TEXT PRIMARY KEY)";
    private static final String INSERT_STATE = "INSERT OR IGNORE INTO states (state) VALUES (?)";
    private static final String SAVE_EARTHQUAKES = "INSERT INTO earthquakes (id, depth_in_meters, " +
            "magnitude_type, magnitude, state, time) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public void openConnection(String dbFileName) throws SQLException {
        String url = "jdbc:sqlite:" + dbFileName + ".db";
        conn = DriverManager.getConnection(url);
    }

    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public void createStatesTable() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_STATES_TABLE);
        }
    }

    public void modifyEarthquakesTable() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS earthquakes(" +
                    "id TEXT PRIMARY KEY, " +
                    "depth_in_meters INTEGER, " +
                    "magnitude_type TEXT, " +
                    "magnitude REAL, " +
                    "state TEXT, " +
                    "time TEXT, " +
                    "FOREIGN KEY(state) REFERENCES states(state))");
        }
        // Переименовываем существующую таблицу
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE earthquakes RENAME TO old_earthquakes");
        }
        // Создаем новую таблицу с внешним ключом
        try (Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE earthquakes (" +
                    "id TEXT PRIMARY KEY, " +
                    "depth_in_meters INTEGER, " +
                    "magnitude_type TEXT, " +
                    "magnitude REAL, " +
                    "state TEXT, " +
                    "time TEXT, " +
                    "FOREIGN KEY(state) REFERENCES states(state))";
            stmt.execute(createTableSQL);
        }
        // Удаляем старую таблицу
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE old_earthquakes");
        }
    }

    public void saveEarthquakes(List<Earthquakes> earthquakesList) throws SQLException {
        conn.setAutoCommit(false);
        createStatesTable();
        modifyEarthquakesTable();
        // Вставка штатов в таблицу States
        try (PreparedStatement pstmtState = conn.prepareStatement(INSERT_STATE)) {
            for (Earthquakes earthquake : earthquakesList) {
                pstmtState.setString(1, earthquake.getState());
                pstmtState.execute();
            }
        }
        // Очистим Earthquakes для обновления данных
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Earthquakes");
        }
        try (PreparedStatement pstmt = conn.prepareStatement(SAVE_EARTHQUAKES)) {
            for (Earthquakes earthquake : earthquakesList) {
                pstmt.setString(1, earthquake.getId());
                pstmt.setInt(2, earthquake.getDepth_in_meters());
                pstmt.setString(3, earthquake.getMagnitude_type());
                pstmt.setFloat(4, earthquake.getMagnitude());
                // Используем колонку state в качестве внешнего ключа
                pstmt.setString(5, earthquake.getState());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = earthquake.getTime().format(formatter);
                pstmt.setString(6, formattedTime);

                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        conn.commit();
        conn.setAutoCommit(true);
    }
    public Map<Integer, Integer> analyzeEarthquakeData() throws SQLException {
        String query = "SELECT SUBSTR(time, 1, 4) as year, COUNT(*) as count " +
                "FROM earthquakes " +
                "GROUP BY year " +
                "ORDER BY year";

        Map<Integer, Integer> earthquakeCountsByYear = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int year = rs.getInt("year");
                int count = rs.getInt("count");
                earthquakeCountsByYear.put(year, count);
            }
        }
        return earthquakeCountsByYear;
    }
    public float printAverageMagnitudeForState(String state) throws SQLException {
        String query = "SELECT AVG(magnitude) AS avg_magnitude FROM earthquakes WHERE state = ?";
        float avgMagnitude = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, state);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    avgMagnitude = rs.getFloat("avg_magnitude");
                }
            }
        }
        return avgMagnitude;
    }
    public String findDeepestEarthquakeStateByYear(int year) throws SQLException {
        String state = null;
        String query = "SELECT state, MAX(depth_in_meters) AS max_depth " +
                "FROM earthquakes " +
                "WHERE SUBSTR(time, 1, 4) = ? " +
                "GROUP BY state " +
                "ORDER BY max_depth DESC " +
                "LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, String.valueOf(year));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    state = rs.getString("state");
                }
            }
        }
        return state;
    }
}