package org.example.Dao;

import org.example.App;
import org.example.Dto.LoginResultDto;
import org.example.Model.Bill;
import org.example.Model.Bill_Detail;
import org.example.Presentation.LoginMenu;
import org.example.Util.ColorText;
import org.example.Util.DbConnection;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class BillDao {
    public static void displayBill(boolean billType, String empId, String billStatusNumber) throws SQLException, ClassNotFoundException {
        String storedProc = "{call GetAllBills(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setBoolean(1, billType);
            cs.setString(2, empId);
            cs.setString(3, billStatusNumber);
            ResultSet rs = cs.executeQuery();
            String titleUpperCase = space(45) + "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ " + "Bảng thông tin phiếu " + getTypeName(billType) + " ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase.toUpperCase() + ColorText.RESET);
            designTableForResultSet(rs);
        }
    }

    private static void designPageForResultSet(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            System.out.printf("⎜ %-15s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-25s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-15s⎜\n", "No Data");
        } else
            while (rs.next()) {
                // Hiển thị thông tin nhân viên
                System.out.printf("⎜ %-15s", rs.getString("Bill_id"));
                System.out.printf("⎜ %-20s", rs.getString("Bill_Code"));
                System.out.printf("⎜ %-20s", rs.getBoolean("Bill_Type") ? "Phiếu xuất" : "Phiếu nhập");
                System.out.printf("⎜ %-25s", rs.getString("Emp_id_created"));
                System.out.printf("⎜ %-20s", rs.getString("Created"));
                System.out.printf("⎜ %-20s", rs.getString("Emp_id_auth") == null ? "No Data" :
                        rs.getString("Emp_id_auth"));
                System.out.printf("⎜ %-20s", rs.getString("Auth_date") == null ? "No Data" :
                        rs.getString("Auth_date"));
                String status = null;
                int numStatus = rs.getInt("Bill_Status");
                switch (numStatus) {
                    case 0 -> status = "Tạo";
                    case 1 -> status = "Huỷ";
                    case 2 -> status = "Duyệt";
                }
                System.out.printf("⎜ %-14s ⎜\n", status);
            }
    }

    private static void turnColor(String text, int with) {
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-" + with + "s" + ColorText.RESET, text);
    }

    public static void border() {
        String slag = "⏤";
        System.out.println(ColorText.WHITE_BRIGHT + "◇" + slag.repeat(89) + "◇" + ColorText.RESET);
    }

    public static String space(int repeatNumber) {
        String slag = " ";
        return slag.repeat(repeatNumber);
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
        turnColor("Bill ID ", 15);
        turnColor("Bill Code ", 20);
        turnColor("Bill_Type ", 20);
        turnColor("Emp_id_created ", 25);
        turnColor("Created ", 20);
        turnColor("Emp_id_auth ", 20);
        turnColor("Auth_date ", 20);
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-14s" + ColorText.RESET + " ⎜\n", "Bill_Status ");
        border();
    }

    public static void createBill(Scanner scanner, boolean billType, String loginEmpId) throws SQLException, ClassNotFoundException {
        String storedProc = "{call CreateBill(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Bill bill = new Bill();
            bill.setBillType(billType);
            bill.setBillStatus(0); // create
            bill.setEmpIdCreated(loginEmpId);
            bill.inputData(scanner, App.loginResultDto.accountPermission());
            //
            cs.setString(1, bill.getBillCode());// bill code
            cs.setBoolean(2, bill.getBillType());// bill type
            cs.setString(3, bill.getEmpIdCreated());// EmpIdCreated
            cs.execute();
            // lấy lại BillId khi đã tạo một bảng ghi bill, vì nó auto tăng nên là phải lấy lại sau khi tạo
            bill.setBillId(getBillId(bill.getBillCode()));
            String productNameInput;
            do {
                Notification.successMessage("-> VIEW MODE <-");
                ProductDao.displayProducts(scanner);
                Notification.successMessage("-> CREATE MODE <-");
                Notification.inputPrint("Nhập (tên) các sản phẩm và số lượng sản phẩm để ("
                        + getTypeName(billType) + ") hoặc nhập (exit) để kết thúc tạo Bill : ");
                Bill_Detail billDetail = new Bill_Detail();
                billDetail.setBillId(bill.getBillId());
                // choose product
                productNameInput = scanner.nextLine().trim();
                if (productNameInput.length() > 150) {
                    Notification.errorMessage("Tên sản phẩm vượt quá 150 ký tự. Hãy thử lại!");
                    continue;
                }
                if (productNameInput.equalsIgnoreCase("exit")) break;
                if (!ProductDao.checkExistProduct(null, productNameInput)) {
                    Notification.errorMessage("Sản phẩm không tồn tại!");
                    continue;
                }
                if (!ProductDao.isActiveProduct(null, productNameInput)) {
                    Notification.errorMessage("Sản phẩm hiện đang không hoạt động. Hãy chọn sản phẩm khác!");
                    continue;
                }
                String productId = getIdProduct(productNameInput);
                if (BillDao.checkExistBillDetail(bill.getBillId(), productId)) {
                    Notification.errorMessage("Đơn hàng với sản phẩm " + productNameInput +
                            " đã tồn tại không thể thực hiện tạo mới, hãy thử lại!");
                    continue;
                }
                Notification.successMessage("✓ SUCCESS!");
                billDetail.setProductId(productId);
                billDetail.setType(billType);
                billDetail.inputData(scanner, "thêm mới");
                Bill_DetailDao.createBillDetail(billDetail);
            } while (true);
        }
    }

    public static void updateBill(Scanner scanner, boolean billType, String empId) throws SQLException, ClassNotFoundException {
        Notification.successMessage("-> VIEW MODE <-");
        displayBill(billType, empId, null);
        Notification.successMessage("-> UPDATE MODE <-");
        String storedProc = "{call UpdateBill(?,?,?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            String input; // Found id
            while (true) {
                Notification.inputPrint("Nhập Bill_ID hoặc Bill_Code để cập nhật, hoặc nhập (exit) để huỷ và quay lại : ");
                Long parseBillId = null;
                String billCode = null;
                input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) return;
                // check not null not blank
                if (!Validate.isInputValid(input)) continue;
                // parse
                // check if the input is numeric
                if (isNumeric(input)) {
                    // Input is numeric
                    parseBillId = Long.parseLong(input);
                } else {
                    // Input is not numeric
                    if (input.length() > 10) {
                        Notification.errorMessage("Bill_Code không được nhập quá 10 ký tự");
                        continue;
                    }
                    billCode = input;
                }
                // check tồn tại parseBillId hoặc billCode
                if (!checkExistsBill(parseBillId, billCode, billType)) {
                    Notification.errorMessage("Không tồn tại Bill ở trong phiếu " + getTypeName(billType) + ", hãy thử lại!");
                    continue;
                }
                if (empId != null)
                    if (!checkEmpIsAuthorizeToBill(parseBillId, billCode, empId)) {
                        Notification.waringMessage("Bạn không có quyền sửa đổi trên hoá đơn này. Hãy thử lại!");
                        continue;
                    }
                if (checkBillStatusIsApproved(parseBillId, billCode)) {
                    Notification.errorMessage("Bill bạn đang tìm đang có sẵn nhưng nó đã được duyệt, " +
                            "hãy thử nhập lại nhập mã bill khác đi ạ 😊");
                    continue;
                }
                // call procGetName và set thành tên cho đối tượng đang update
                Notification.inputPrint("OK giờ nhập các trường sau để cập nhật");
                Bill bill = new Bill();
                // update bill, từ đây cho update lại status
                if (parseBillId == null) {
                    cs.setNull(1, Types.BIGINT); // BillId is null
                    bill.setBillCode(billCode); // bill code
                } else {
                    cs.setLong(1, parseBillId); // parseBillId (long) equal input
                    cs.setString(2, input);
                }
                // set origin
                bill.setBillType(billType);
                bill.setBillCode(input);
                // input data
                bill.inputData(scanner, App.loginResultDto.accountPermission());
                cs.setInt(3, bill.getBillStatus());
                cs.setString(4, bill.getEmpIdCreated());
                // dựa trên bill type để cập nhật
                cs.setBoolean(5, billType);
                cs.setString(6, bill.getEmpIdCreated());
                // update bill detail
                cs.execute();
                Notification.waringMessage("* Bạn có muốn cập nhật luôn (Số lượng) và (Giá) của sản phẩm liên kết với bill không ?\n"
                        + "* Nếu muốn cập nhật thì hãy nhập (update)\n"
                        + "* Hoặc nhập (exit) để trở về menu");
                String choice;
                do {
                    choice = scanner.nextLine().trim().toLowerCase();
                    if (!Validate.isInputValid(input)) continue;
                    switch (choice) {
                        case "exit" -> {
                            Notification.successMessage("Đã trở về menu");
                            return;
                        }
                        case "update" -> {
//                            Bill_DetailDao.searchBillDetail(parseBillId, billCode, billType, empId);
                            Notification.inputPrint("OK giờ nhập các trường sau để cập nhật");
                            // trong quá trình nhập sẽ check tồn tại sản phẩm trong các bảng product kể cả
                            // product có liên kết với bill id trong bảng bill detail hay không
                            Bill_Detail billDetail = new Bill_Detail();
                            billDetail.setType(billType);
                            billDetail.inputData(scanner, "cập nhật");
                            // sau khi đã validate, và check tồn tại
                            updateBillDetail(
                                    billDetail.getBillId(),
                                    billDetail.getProductId(),
                                    billDetail.getQuantity(),
                                    billDetail.getPrice());
                        }
                        default -> {
                            Notification.errorMessage("Lệnh nhập không đúng, " +
                                    "lúc này chỉ được nhập (exit) hoặc (update), hãy thử lại! ");
                            continue;
                        }
                    }
                    break;
                } while (true);
            }
        }
    }

    public static void updateBillDetail(Long billId, String productId, int quantity, float price) throws SQLException, ClassNotFoundException {
        String storedProc = "{call UpdateBillDetail(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setLong(1, billId);
            cs.setString(2, productId);
            cs.setInt(3, quantity);
            cs.setFloat(4, price);
            cs.execute();
        }
    }

    public static boolean checkExistsBill(Long billId, String billCode, boolean billType) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkExistBill(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            if (billId == null) {
                cs.setNull(1, Types.BIGINT);
                cs.setString(2, billCode);
            } else {
                cs.setLong(1, billId);
                cs.setString(2, billCode);

            }
            cs.setBoolean(3, billType);
            cs.registerOutParameter(4, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(4);
        }
    }

    public static boolean checkBillStatusIsApproved(Long billId, String billCode) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkBillStatusIsApproved(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            if (billId == null) {
                cs.setNull(1, Types.BIGINT);
                cs.setString(2, billCode);
            } else {
                String input = String.valueOf(billId);
                cs.setLong(1, billId);
                cs.setString(2, input);
            }
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();
            return !cs.getBoolean(3);
        }
    }

    public static boolean checkEmpIsAuthorizeToBill(Long billId, String billCode, String empId) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkEmpIsAuthorizeToBill(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            if (billId == null) {
                cs.setNull(1, Types.BIGINT);
                cs.setString(2, billCode);
            } else {
                String input = String.valueOf(billId);
                cs.setLong(1, billId);
                cs.setString(2, input);
            }
            cs.setString(3, empId);
            cs.registerOutParameter(4, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(4);
        }
    }


//    public static Bill getBillById(Long billId) throws SQLException, ClassNotFoundException {
//        String storedProc = "{call getBillInfoById(?,?,?,?,?)}";
//        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
//            cs.setLong(1, billId);
//            cs.registerOutParameter(2, Types.VARCHAR); // bill code
//            cs.registerOutParameter(3, Types.BIT);  // bill type
//            cs.registerOutParameter(4, Types.CHAR); // Emp_id_created
//            cs.registerOutParameter(5, Types.CHAR); // Emp_id_auth
//            cs.execute();
//            Bill bill = new Bill();
//            bill.setBillCode(cs.getString(2));
//            bill.setBillType(cs.getBoolean(3));
//            bill.setEmpIdCreated(cs.getString(4));
//            bill.setEmpIdAuth(cs.getString(5));
//            return bill;
//        }
//    }

    public static boolean checkExistBillDetail(Long billId, String productId) throws SQLException, ClassNotFoundException {
        String storedProc = "{call CheckExistBillDetail(?,?,?)}";
        try (CallableStatement stmt = DbConnection.getConnection().prepareCall(storedProc)) {
            stmt.setLong(1, billId);
            stmt.setString(2, productId);
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(3);
        }
    }

    private static String getIdProduct(String productName) throws SQLException, ClassNotFoundException {
        String storedProc = "{call getIdProduct(?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, productName);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) return rs.getString("Product_Id");
            else {
                return null;
            }
        }
    }

    public static Long getBillId(String billCode) throws SQLException, ClassNotFoundException {
        String storedProc = "{call Get_Bill_Id(?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, billCode);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) return rs.getLong("Bill_id");
            else {
                Notification.errorMessage("Không có bản ghi Bill_id nào có bill_Code là : " + billCode);
                return null;
            }
        }
    }

    public static String getTypeName(boolean type) {
        if (type) return "xuất";
        else return "nhập";
    }

    public static boolean isNumeric(String input) {
        return input.matches("\\d+");
    }


//    public static String getAuthIdBill(Long billId, String billCode) throws SQLException, ClassNotFoundException {
//        String storedProc = "{call GetAuthIdBill(?,?)}";
//        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
//            if (billId == null) {
//                cs.setNull(1, Types.BIGINT);
//                cs.setString(2, billCode);
//            } else {
//                String input = String.valueOf(billId);
//                cs.setLong(1, billId);
//                cs.setString(2, input);
//            }
//            ResultSet rs = cs.executeQuery();
//            if (rs.next()) return rs.getString("Emp_id_auth");
//            else {
//                Notification.errorMessage("Emp_Auth_id này không được liên kết với bill_id : " + billId);
//                return null;
//            }
//        }
//    }


//    private static boolean checkAuthorise(String empId, Long billId, String billCode) throws SQLException, ClassNotFoundException {
//        String authId = getAuthIdBill(billId, billCode);
//        return authId.equals(empId);
//    }

    public static void approveBill(Scanner scanner, boolean billType, String loginAccEmpId) throws SQLException, ClassNotFoundException {
        displayBill(billType, null, null);
        Long billId;
        String billCode;
        String input;
        do {
            billId = null;
            billCode = null;
            Notification.inputPrint("Nhập bill_id hoặc bill_code để duyệt hoặc nhập (exit) để thoát");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            // kiểm tra not blank not null
            // Tuỳ theo bill_id hoặc bill code để duyệt
            if (isNumeric(input)) billId = Long.parseLong(input);
            else {
                if (input.length() > 10) {
                    Notification.errorMessage("Bill_Code không được nhập quá 10 ký tự");
                    continue;
                }
                billCode = input;
            }
            // kiểm tra tồn tại trong phiếu nhập hoặc xuất
            if (!checkExistsBill(billId, billCode, billType)) {
                Notification.errorMessage("Không tồn tại Bill ở trong phiếu " + getTypeName(billType) + ", hãy thử lại!");
                continue;
            }
            // kiểm tra nó dã được duyệt chưa
            if (checkBillStatusIsApproved(billId, billCode)) {
                Notification.errorMessage("Bill bạn đang tìm đang có sẵn nhưng nó đã được duyệt, " +
                        "hãy thử nhập lại nhập mã bill khác đi ạ 😊");
                continue;
            }
            if (billType) { // true : phiếu xuất
                if (!Bill_DetailDao.checkValidQuantityToSales(billId, billCode)) {
                    Notification.errorMessage("Không thể duyệt bill này vì số lượng sản phẩm trong kho không đủ. " +
                            "Hãy chọn bill khác!");
                    continue;
                }
            }
            break;
        } while (true);

        String storedProc = "{call ApproveBill(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            if (billId == null) {
                cs.setNull(1, Types.BIGINT);
                cs.setString(2, billCode);
            } else {
                String billIdToString = String.valueOf(billId);
                cs.setLong(1, billId);
                cs.setString(2, billIdToString);

            }
            cs.setBoolean(3, billType);
            cs.setString(4, loginAccEmpId); // set emp_id_Auth khi duyệt
            cs.execute();
        }
        Notification.successMessage("Phiếu đã được duyệt");
    }

//    public static void approveBillWhenSearch(Scanner scanner,
//                                             ResultSet rs,
//                                             boolean billType,
//                                             String loginAccEmpId) throws SQLException, ClassNotFoundException {
//        String input;
//        do {
//            Notification.inputPrint("Nhập (yes) để duyệt tất cả kết quả tìm kiếm Bill có status là (tạo) ," +
//                    " hoặc nhập (exit) để huỷ duyệt và quay lại : ");
//            input = scanner.nextLine().trim();
//            if (!Validate.isInputValid(input)) continue;
//            if (input.equalsIgnoreCase("exit")) return;
//            if (!input.equalsIgnoreCase("yes")) {
//                Notification.errorMessage("Lệnh nhập không đúng hãy nhập lại!, hoặc nhập (exit) để huỷ duyệt ");
//                continue;
//            }
//            break;
//        } while (true);
//        String storedProc = "{call ApproveBill(?,?,?,?)}";
//        while (rs.next()) {
//            try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
//                cs.setLong(1, rs.getLong("Bill_Id"));
//                cs.setString(2, rs.getString(rs.getString("Bill_Code")));
//                cs.setBoolean(3, billType);
//                cs.setString(4, loginAccEmpId); // set emp_id_Auth khi duyệt
//                cs.execute();
//            }
//        }
//        rs.close();
//        Notification.successMessage("Các phiếu (Tạo) tìm kiếm được đã được duyệt");
//    }

    public static void searchBillForAdminAndUser(Scanner scanner,
                                                 Boolean billType,
                                                 String loginAccEmpId) throws SQLException, ClassNotFoundException {
        String storedProc = "{call SearchBill(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            String keyword;
            Long parseBillId = null;
            String billCode = null;
            do {
                Notification.inputPrint("Nhập Bill_ID hoặc Bill_Code để tìm kiếm phiếu " + getTypeName(billType) +
                        ", hoặc nhập (exit) để quay lại menu: ");

                keyword = scanner.nextLine().trim();
                if (!Validate.isInputValid(keyword)) continue;
                if (keyword.equalsIgnoreCase("exit")) break;
                if (isNumeric(keyword)) {
                    // Input is numeric
                    parseBillId = Long.parseLong(keyword);
                } else {
                    // Input is not numeric
                    billCode = keyword;
                }
//                 check tồn tại parseBillId hoặc billCode
                if (!checkExistsBill(parseBillId, billCode, billType)) {
                    Notification.errorMessage("Không tồn tại Bill ở trong phiếu " + getTypeName(billType) + ", hãy thử lại!");
                    continue;
                }
                if (parseBillId == null) {
                    cs.setNull(1, Types.BIGINT);
                    cs.setString(2, keyword);
                } else {
                    cs.setLong(1, parseBillId);  //parseBillId equal keyword but in type Long
                    cs.setString(2, keyword);
                }
                cs.setBoolean(3, billType);
                ResultSet rs = cs.executeQuery();
                // User vs admin
                designTableForResultSet(rs);
                // just admin allow actions
                if (loginAccEmpId != null) {
                    String choice;
                    do {
                        Notification.inputPrint(
                                "* Bạn có thể nhập số : 1. (update) để tiến hành chọn Bill cần cập nhật " + getTypeName(billType) +
                                        "\n* Hoặc có thể nhập số : 2. (approve) để duyệt bill." +
                                        "\n* Hoặc nhập (3) để tiếp tục tìm kiếm ");
                        choice = scanner.nextLine().trim();
                        if (choice.equals("1"))
                            updateBill(scanner, billType, null);
                        else if (choice.equals("2")) {
                            approveBill(scanner, billType, loginAccEmpId);
                        } else if (choice.equals("3"))
                            break;
                        else
                            Notification.errorMessage("Vui lòng chọn 1 hoặc 2, hoặc nhập (3) để tiếp tục tìm kiếm");
                    } while (true);
                }
            } while (true);

        }
    }
}
