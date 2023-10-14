package org.example.Dao;

import org.example.Model.Employee;
import org.example.Util.ColorText;
import org.example.Util.DbConnection;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import static org.example.Dao.BillDao.space;

public class EmployeeDao {

    public static boolean checkExistEmp(String id, String name) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkExistEmp(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, id);
            cs.setString(2, name);
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(3);
        }
    }

    public static void displayEmp(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call ListEmployees(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            int offset = 0; // mặt định là từ 1
            cs.setInt(1, offset); // vị trí trang
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            int totalEmp = cs.getInt(2);
            String titleUpperCase = space(37) + "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ " + "Bảng danh sách nhân viên" + " ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase.toUpperCase() + ColorText.RESET);
            displayTable(scanner, cs, offset, totalEmp);
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
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-15s ⎜\n", "No Data");
        } else
            while (rs.next()) {
                // Hiển thị thông tin nhân viên
                System.out.printf("⎜ %-15s", rs.getString("Emp_Id"));
                System.out.printf("⎜ %-20s", rs.getString("Emp_Name"));
                System.out.printf("⎜ %-20s", rs.getString("Birth_Of_Date"));
                System.out.printf("⎜ %-20s", rs.getString("Email"));
                System.out.printf("⎜ %-20s", rs.getString("Phone"));
                System.out.printf("⎜ %-20s", rs.getString("Address"));
                String status = null;
                int numStatus = rs.getInt("Emp_Status");
                switch (numStatus) {
                    case 0 -> status = "Hoạt động";
                    case 1 -> status = "Nghỉ chế độ";
                    case 2 -> status = "Nghỉ việc";
                }
                System.out.printf("⎜ %-16s ⎜\n", status);
            }
    }

    public static void border() {
        String slag = "⏤";
        System.out.println(ColorText.WHITE_BRIGHT + "◇" + slag.repeat(76) + "◇" + ColorText.RESET);
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
        border();
        turnColor("Emp ID ", 15);
        turnColor("Emp Name ", 20);
        turnColor("Birth_Of_Date ", 20);
        turnColor("Email ", 20);
        turnColor("Phone ", 20);
        turnColor("Address ", 20);
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-16s" + ColorText.RESET + " ⎜\n", "Emp_Status ");
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
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%77s%12s\n" + ColorText.RESET, "[Page 1]", "2.⏩️");
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 2 để đi đến trang tiếp theo, hoặc nhập (exit) để quay lại ");
                } else if (offset >= Math.max(0, totalProduct - 1) && offset != 0) {  // Check if on the last page and not on the first page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%62s%15s\n" + ColorText.RESET, "1.⏪️️", "[Page" + (offset / 10 + 1) + ']');
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 1 để trở lại trang trước, hoặc nhập (exit) để quay lại");
                } else if (offset > 0 && offset < Math.max(0, totalProduct - 1)) {  // Check if not on the first or the last page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%61s%15s%13s\n" + ColorText.RESET, "1.⏪️", "[Page" + (offset / 10 + 1) + ']', "2.⏩️");
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 1 hoặc 2 để tải trang, hoặc nhập (exit) để quay lại");
                } else
                    break;
                choice = scanner.nextLine().trim().toLowerCase();
                if (!Validate.isInputValid(choice)) continue;
                if (totalProduct > 10)
                    offset = handlePagination(choice, totalProduct, offset, cs); // cập nhật offset mới để tiếp tục check
            } while (!choice.equalsIgnoreCase("exit"));
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static void addEmp(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call AddEmployee(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Employee employee = new Employee();
            employee.setStatus(0);
            Notification.successMessage("->CREATE MODE<-");
            employee.inputData(scanner, "thêm mới");
            cs.setString(1, employee.getEmpId());
            cs.setString(2, employee.getEmpName());
            cs.setString(3, employee.getBirthday());
            cs.setString(4, employee.getEmail());
            cs.setString(5, employee.getPhoneNumber());
            cs.setString(6, employee.getAddress());
            cs.setInt(7, employee.getStatus());
            cs.execute();
        }
    }

    public static void updateEmpInfo(Scanner scanner) throws SQLException, ClassNotFoundException {
        Notification.successMessage("-> VIEW MODE <-");
        EmployeeDao.displayEmp(scanner);
        Notification.successMessage("-> UPDATE MODE <-");
        String storedProc = "{call UpdateEmployee(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            String foundId; // Found id
            Notification.inputPrint("Nhập id nhân viên để cập nhật, hoặc nhập (exit) để huỷ và quay lại: ");
            while (true) {
                foundId = scanner.nextLine().trim();
                if (foundId.equalsIgnoreCase("exit"))
                    return;
                // check tồn tại
                if (!Validate.isInputValid(foundId)) continue;
                if (foundId.length() > 5) {
                    Notification.errorMessage("ID nhân viên không được lớn hơn 5 ký tự.");
                    continue;
                }
                if (!checkExistEmp(foundId, null)) {
                    Notification.errorMessage("Không tồn tại nhân viên với id : " + foundId);
                    continue;
                }
                Employee newEmployee = new Employee();
                newEmployee.setEmpId(foundId);
                // call procGetName và set thành tên cho đối tượng đang update
                String procGetName = "{call getNameEmpOrStatusBaseOnActionInput(?,?)}";
                try (CallableStatement getNameCall = DbConnection.getConnection().prepareCall(procGetName)) {
                    String action = "name";
                    getNameCall.setString(1, foundId);
                    getNameCall.setString(2, action.toLowerCase()); // action
                    ResultSet rs = getNameCall.executeQuery();
                    if (rs.next()) {
                        // đặt lại tên
                        newEmployee.setEmpName(rs.getString("Emp_Name"));
                    }
                }
                Notification.successMessage("OK bây giờ nhập mới các trường sau để cập nhật: ");
                System.out.println();
                // Khi tới đoạn nhập id hoặc name thì tự input sẽ sử lý
                try (CallableStatement getNameCall = DbConnection.getConnection().prepareCall(procGetName)) {
                    String action = "status";
                    getNameCall.setString(1, foundId);
                    getNameCall.setString(2, action.toLowerCase()); // action
                    ResultSet rs = getNameCall.executeQuery();
                    if (rs.next()) {
                        // đặt lại status
                        newEmployee.setStatus(rs.getInt("Emp_Status"));
                    }
                }
                newEmployee.inputData(scanner, "cập nhật");
                cs.setString(1, newEmployee.getEmpId());
                cs.setString(2, newEmployee.getEmpName());
                cs.setString(3, newEmployee.getBirthday());
                cs.setString(4, newEmployee.getEmail());
                cs.setString(5, newEmployee.getPhoneNumber());
                cs.setString(6, newEmployee.getAddress());
                cs.setInt(7, newEmployee.getStatus());
                Notification.successMessage("Thông tin nhân viên đã được cập nhật thành công");
                break;
            }
            cs.execute();
        }
    }

    public static void updateEmployeeStatus(Scanner scanner) throws SQLException, ClassNotFoundException {
        Notification.successMessage("-> VIEW MODE <-");
        EmployeeDao.displayEmp(scanner);
        Notification.successMessage("-> UPDATE MODE <-");
        String storedProc = "{call UpdateEmployeeStatus(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Notification.inputPrint("Nhập ID nhân viên để cập nhật status, hoặc nhập (exit) để huỷ và quay lại: ");
            // check tồn tại
            int statusNumber;
            do {
                String empId = scanner.nextLine().trim();
                if (empId.equalsIgnoreCase("exit")) return;
                if (!Validate.isInputValid(empId)) continue;
                if (checkExistEmp(empId, null)) {
                    cs.setString(1, empId); // đặt lại id cho parameter 1
                    String choice;
                    System.out.println("Nhập trạng thái mới cho nhân viên, hãy nhập :\n" + ColorText.WHITE_BRIGHT +
                            "- 1. (Hoạt động) | - 2. (Nghỉ chế độ) | 3. (Nghỉ việc) |  hoặc nhập (exit) để quay lại menu" + ColorText.RESET);
                    do {
                        choice = scanner.nextLine().trim();
                        if (!Validate.isInputValid(choice)) continue;
                        if (choice.equalsIgnoreCase("exit")) return;
                        switch (choice) {
                            case "1" -> statusNumber = 0;
                            case "2" -> statusNumber = 1;
                            case "3" -> statusNumber = 2;
                            default -> {
                                Notification.errorMessage("Giá trị nhập phải là số (1 hoặc 2), Hãy thử lại!");
                                continue;
                            }
                        }
                        break;
                    } while (true);
                    break;
                } else
                    Notification.errorMessage("Không tồn tại nhân viên với Id : " + empId);
            }
            while (true);
            cs.setInt(2, statusNumber);
            cs.execute();
            Notification.successMessage("Cập nhật thành công");
        }
    }

    public static void searchEmp(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call SearchEmployee(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {

            String keyword;
            do {
                Notification.inputPrint("Nhập ID hoặc từ khoá tên nhân viên để tìm kiếm, " +
                        "hoặc nhập (exit) để huỷ và quay lại: ");
                keyword = scanner.nextLine().trim();
                if (!Validate.isInputValid(keyword)) continue;
                if (keyword.equalsIgnoreCase("exit")) break;
                cs.setString(1, keyword); // từ khoá
                int offset = 0;
                cs.setInt(2, offset);
                cs.registerOutParameter(3, Types.INTEGER);
                cs.execute();
                int totalProduct = cs.getInt(3);

                displayTable(scanner, cs, offset, totalProduct);
            } while (true);

        }
    }
}
