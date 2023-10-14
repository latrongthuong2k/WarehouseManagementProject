package org.example.Dao;

import org.example.Model.Account;
import org.example.Util.ColorText;
import org.example.Util.DbConnection;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.*;
import java.util.Scanner;

import static org.example.Dao.BillDao.space;

public class AccountDao {
    // checks
    public static boolean checkExistsUser(String userName) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkExistsUser(?,?)}";
        try (CallableStatement stmt = DbConnection.getConnection().prepareCall(storedProc)) {
            stmt.setString(1, userName);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }

    public static boolean checkValidAccount(String userName, String pass) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkValidAccount(?,?,?)}";
        try (CallableStatement stmt = DbConnection.getConnection().prepareCall(storedProc)) {
            stmt.setString(1, userName);
            stmt.setString(2, pass);
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(3);
        }
    }

    public static boolean checkAccountPermission(String userName) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkAccountPermission(?,?)}";
        try (CallableStatement stmt = DbConnection.getConnection().prepareCall(storedProc)) {
            stmt.setString(1, userName);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }

    public static String getEmpId(String userName) throws SQLException, ClassNotFoundException {
        String storedProc = "{call GetEmpId(?)}";
        try (CallableStatement stmt = DbConnection.getConnection().prepareCall(storedProc)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Emp_id");
            } else
                return null;
        }
    }

    public static void displayAccounts(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call ListAccounts(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            int offset = 0; // mặt định là từ 1
            cs.setInt(1, offset); // vị trí trang
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            int totalAcc = cs.getInt(2);
            String titleUpperCase = space(10) + "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ " + "Bảng danh sách Accounts" + " ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase.toUpperCase() + ColorText.RESET);
            displayTable(scanner, cs, offset, totalAcc);
        }
    }

    public static int handlePagination(String choice,
                                       int totalProduct,
                                       int offset,
                                       CallableStatement cs) throws SQLException {
        switch (choice) {
            case "1" -> {
                offset -= 10;
                if (offset < 0) {
                    // Ensure offset is not negative
                    offset = 0;
                }
                cs.setInt(1, offset);
                ResultSet rs = cs.executeQuery(); // update lại
                if (offset == 0) {
                    designTitle();
                    designPageForResultSet(rs);
                } else
                    designPageForResultSet(rs);
                rs.close();
                border();
                System.out.println(); // cách ra
            }
            case "2" -> {
                offset += 10;
                if (offset >= totalProduct) {
                    // Ensure offset is within range
                    offset = Math.max(0, totalProduct - 1);
                }
                cs.setInt(1, offset);
                ResultSet rs = cs.executeQuery(); // update lại
                designPageForResultSet(rs);
                rs.close();
                border();
                System.out.println(); // cách ra
            }
            case "exit" -> Notification.successMessage("EXITED VIEW MODE");
            default -> Notification.waringMessage("Lệnh nhập chuyển trang phải là một số nguyên, Hãy thử lại!");
        }
        return offset;
    }

    private static void turnColor(String text, int with) {
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-" + with + "s" + ColorText.RESET, text);
    }

    private static void designPageForResultSet(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            System.out.printf("⎜ %-15s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-15s", "No Data");
            System.out.printf("⎜ %-15s ⎜\n", "No Data");
        } else
            while (rs.next()) {
                // Hiển thị thông tin sản phẩm
                System.out.printf("⎜ %-15s", rs.getString("Acc_id"));
                System.out.printf("⎜ %-20s", rs.getString("User_name"));
                System.out.printf("⎜ %-20s", rs.getString("Emp_Name"));
                System.out.printf("⎜ %-15s", rs.getBoolean("Permission") ? "User" : "Admin");
                System.out.printf("⎜ %-15s ⎜\n", rs.getBoolean("Acc_status") ?
                        "Active" : "Block");
            }
    }

    public static void border() {
        String slag = "⏤";
        System.out.println(ColorText.WHITE_BRIGHT + "◇" + slag.repeat(50) + "◇" + ColorText.RESET);
    }

    private static void designTableForResultSet(ResultSet rs) throws SQLException {
        // Title
        designTitle();
        // page
        designPageForResultSet(rs);
        border();
        System.out.println(); // cách ra
        rs.close();
    }

    private static void designTitle() {
        // Title
        border();
        turnColor("Acc ID ", 15);
        turnColor("User name ", 20);
        turnColor("Emp_Name ", 20);
        turnColor("Permission ", 15);
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-15s" + ColorText.RESET + " ⎜\n", "Acc status ");
        border();
    }

    private static void displayTable(Scanner scanner, CallableStatement cs, int offset, int totalProduct) throws SQLException {
        ResultSet rs = cs.executeQuery();
        try {
            designTableForResultSet(rs); // offset = 1 in ra lần đầu tiên
            String choice;
            do {
                cs.setInt(1, offset); // set lại với offset mới
                rs = cs.executeQuery(); // cập nhật lại resultSet
                if (totalProduct > 10 && offset == 0) {  // Check if on the first page and there are more than 10 products
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%49s%12s\n" + ColorText.RESET, "[Page 1]", "2.⏩️");
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 2 để đi đến trang tiếp theo, hoặc nhập (exit) để quay lại");
                } else if (offset >= Math.max(0, totalProduct - 1) && offset != 0) {  // Check if on the last page and not on the first page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%34s%15s\n" + ColorText.RESET, "1.⏪️️", "[Page" + (offset / 10 + 1) + ']');
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 1 để trở lại trang trước, hoặc nhập (exit) để quay lại");
                } else if (offset > 0 && offset < Math.max(0, totalProduct - 1)) {  // Check if not on the first or the last page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%33s%15s%13s\n" + ColorText.RESET, "1.⏪️", "[Page" + (offset / 10 + 1) + ']', "2.⏩️");
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 1 hoặc 2 để tải trang, hoặc nhập (exit) để quay lại");
                } else
                    break;
                choice = scanner.nextLine().trim().toLowerCase();
                if (!Validate.isInputValid(choice)) continue;
                offset = handlePagination(choice, totalProduct, offset, cs); // cập nhật offset mới để tiếp tục check
            } while (!choice.equalsIgnoreCase("exit"));
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }


    public static void createNewAcc(Scanner scanner) throws SQLException, ClassNotFoundException {
        Notification.successMessage("-> CREATE MODE <-");
        String storedProc = "{call CreateAccount(?,?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Account newAccount = new Account();
            newAccount.setAccStatus(true);
            newAccount.inputData(scanner, "thêm mới");
            cs.setString(1, newAccount.getUserName());
            cs.setString(2, newAccount.getPassword());
            cs.setBoolean(3, newAccount.isPermission());
            cs.setString(4, newAccount.getEmpId());
            cs.setBoolean(5, newAccount.isAccStatus());
            cs.execute();
        }
    }

    public static void updateAccStatus(Scanner scanner) throws SQLException, ClassNotFoundException {
        Notification.successMessage("-> VIEW MODE <-");
        displayAccounts(scanner);
        Notification.successMessage("-> UPDATE MODE <-");
        String storedProc = "{call UpdateAccountStatus(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Notification.inputPrint("Nhập userName để cập nhật status, hoặc nhập (exit) để huỷ và quay lại : ");
            boolean status;
            String userName;
            do {
                userName = scanner.nextLine().trim();
                if (userName.equalsIgnoreCase("exit")) return;
                if (!Validate.isInputValid(userName)) continue;
                if (!checkExistsUser(userName)) {
                    Notification.errorMessage("Không tồn tại userName : " + userName);
                    continue;
                }
                cs.setString(1, userName); // đặt lại userName cho parameter 1
                String choice;
                System.out.println("Nhập trạng thái mới cho tài khoảng ->"
                        + ColorText.YELLOW_BRIGHT + userName + ColorText.RESET + "<- ," +
                        " hãy nhập :\n" + ColorText.WHITE_BRIGHT +
                        "- 0. (Active) | - 1. (Block) |  hoặc nhập (exit) để quay lại menu" + ColorText.RESET);
                do {
                    choice = scanner.nextLine().trim();
                    if (!Validate.isInputValid(choice)) continue;
                    if (choice.equalsIgnoreCase("exit")) return;
                    switch (choice) {
                        case "0" -> status = true;
                        case "1" -> status = false;
                        default -> {
                            Notification.errorMessage("Giá trị nhập phải là số (1 hoặc 2), Hãy thử lại!");
                            continue;
                        }
                    }
                    break;
                } while (true);
                break;

            }
            while (true);
            cs.setBoolean(2, status);
            cs.execute();
            Notification.successMessage("✓ SUCCESS!");
        }
    }

    public static void searchAcc(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call SearchAccount(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {

            String keyword;
            do {
                Notification.inputPrint("Nhập ID hoặc tên từ khoá của UserName hoặc EmployeeName để tìm kiếm tài khoản, " +
                        "hoặc nhập (exit) để quay lại menu: ");
                keyword = scanner.nextLine().trim();
                if (!Validate.isInputValid(keyword)) continue;
                if (keyword.equalsIgnoreCase("exit")) break;
                cs.setString(1, keyword); // từ khoá
                int offset = 0;
                cs.setInt(2, offset);
                cs.registerOutParameter(3, Types.INTEGER); // total
                cs.execute();
                int totalProduct = cs.getInt(3);
                displayTableWhenSearch(scanner, cs, offset, totalProduct, keyword);
            } while (true);
        }
    }

    private static void displayTableWhenSearch(Scanner scanner,
                                               CallableStatement cs,
                                               int offset,
                                               int totalProduct,
                                               String keyword) throws SQLException, ClassNotFoundException {
        ResultSet rs = cs.executeQuery();
        try {
            designTableForResultSet(rs); // offset = 1 in ra lần đầu tiên
            String choice;
            do {
                cs.setInt(2, offset); // set lại với offset mới
                rs = cs.executeQuery(); // cập nhật lại resultSet
                if (totalProduct > 10 && offset == 0) {
                    // Check if on the first page and there are more than 10 products
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%49s%12s\n" +
                            ColorText.RESET, "[Page 1]", "2.⏩️");
                    System.out.println();
                    Notification.inputPrint("Nhập 2 để đi đến trang tiếp theo, \n" +
                            " * Hoặc nhập (update) để tiến hành cập nhật trạng thái toàn bộ kết quả đã tìm được.\n" +
                            " * Hoặc nhập (exit) để quay lại công cụ tìm kiếm.");
                } else if (offset >= totalProduct - (totalProduct % 10) && offset != 0) {
                    // Check if on the last page and not on the first page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%34s%15s\n" +
                            ColorText.RESET, "1.⏪️️", "[Page" + (offset / 10 + 1) + ']');
                    System.out.println();
                    Notification.inputPrint("Nhập 1 để trở lại trang trước, \n" +
                            " * Hoặc nhập (update) để tiến hành cập nhật trạng thái toàn bộ kết quả đã tìm được.\n" +
                            " * Hoặc nhập (exit) để quay lại công cụ tìm kiếm.");
                } else if (offset > 0 && offset < totalProduct - (totalProduct % 10)) {
                    // Check if not on the first or the last page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%33s%15s%13s\n" +
                            ColorText.RESET, "1.⏪️", "[Page" + (offset / 10 + 1) + ']', "2.⏩️");
                    System.out.println();
                    Notification.inputPrint("Nhập 1 hoặc 2 để tải trang, \n" +
                            " * Hoặc nhập (update) để tiến hành cập nhật trạng thái toàn bộ kết quả đã tìm được.\n" +
                            " * hoặc nhập (exit) để quay lại công cụ tìm kiếm.");
                } else
                    Notification.inputPrint(" * Hoặc nhập (update) để tiến hành cập nhật trạng thái toàn bộ kết quả đã tìm được.\n" +
                            " * hoặc nhập (exit) để quay lại công cụ tìm kiếm.");
                choice = scanner.nextLine().trim().toLowerCase();
                if (!Validate.isInputValid(choice)) continue;
                if (choice.equalsIgnoreCase("update")) {
                    callUpdateAllAccStatus(keyword, scanner);
                    cs.setInt(2, 0);
                    rs = cs.executeQuery();
                    designTableForResultSet(rs);
                    rs.close();
                }
                if (totalProduct > 10)
                    offset = handlePagination(choice, totalProduct, offset, cs); // cập nhật offset mới để tiếp tục check
            } while (!choice.equalsIgnoreCase("exit"));
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private static void callUpdateAllAccStatus(String keyword, Scanner scanner) throws SQLException, ClassNotFoundException {
        boolean status;
        String choice;
        System.out.println("Nhập trạng thái mới cho các tài khoảng" +
                " hãy nhập :\n" + ColorText.WHITE_BRIGHT +
                "- 0. (Active) | - 1. (Block) |  hoặc nhập (exit) để quay lại" + ColorText.RESET);
        do {
            choice = scanner.nextLine().trim();
            if (!Validate.isInputValid(choice)) continue;
            if (choice.equalsIgnoreCase("exit")) return;
            switch (choice) {
                case "0" -> status = true;
                case "1" -> status = false;
                default -> {
                    Notification.errorMessage("Giá trị nhập phải là số (0 hoặc 1), Hãy thử lại!");
                    continue;
                }
            }
            break;
        } while (true);
        String procedure = "{call UpdateFoundAllAccountStatus(?,?) }";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(procedure)) {
            cs.setString(1, keyword);
            cs.setBoolean(2, status);
            cs.execute();
        }
    }

    public static boolean isEmpIdUnique(String empId) throws SQLException, ClassNotFoundException {
        boolean returnValue = true;
        String procedure = "{call isEmpIdUnique(?) }";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(procedure)) {
            cs.setString(1, empId);
            int effectRows = cs.executeUpdate();
            if (effectRows > 0)
                returnValue = false;
        }
        return returnValue;
    }

    public static boolean isValidStatusAcc(String empId) throws SQLException, ClassNotFoundException {
        String procedure = "{call checkIsBlock(?,?) }";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(procedure)) {
            cs.setString(1, empId);
            cs.registerOutParameter(2, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(2);
        }
    }
}
