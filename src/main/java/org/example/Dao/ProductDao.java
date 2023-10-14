package org.example.Dao;

import org.example.Model.Product;
import org.example.Util.ColorText;
import org.example.Util.DbConnection;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

import static org.example.Dao.BillDao.space;

public class ProductDao {

    public static void displayProducts(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call ListProducts(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            int offset = 0; // mặt định là từ 1
            cs.setInt(1, offset); // vị trí trang
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            int totalProduct = cs.getInt(2);
            String titleUpperCase = space(25) + "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ " + "Bảng danh sách products" + " ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase.toUpperCase() + ColorText.RESET);
            displayTableProduct(scanner, cs, offset, totalProduct);
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
            System.out.printf("⎜ %-10s", "No Data");
            System.out.printf("⎜ %-10s", "No Data");
            System.out.printf("⎜ %-15s ⎜\n", "No Data");
        } else
            while (rs.next()) {
                // Hiển thị thông tin sản phẩm
                System.out.printf("⎜ %-15s", rs.getString("Product_Id"));
                System.out.printf("⎜ %-20s", rs.getString("Product_Name"));
                System.out.printf("⎜ %-20s", rs.getString("Manufacturer"));
                System.out.printf("⎜ %-20s", rs.getDate("Created"));
                System.out.printf("⎜ %-10s", rs.getInt("Batch"));
                System.out.printf("⎜ %-10s", rs.getInt("Quantity"));
                System.out.printf("⎜ %-15s ⎜\n", rs.getBoolean("Product_Status") ?
                        "Hoạt động" : "Không hoạt động");
            }
    }

    public static void border() {
        String slag = "⏤";
        System.out.println(ColorText.WHITE_BRIGHT + "◇" + slag.repeat(65) + "◇" + ColorText.RESET);
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
        turnColor("Product ID ", 15);
        turnColor("Product Name ", 20);
        turnColor("Manufacturer ", 20);
        turnColor("Created ", 20);
        turnColor("Batch ", 10);
        turnColor("Quantity ", 10);
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-15s" + ColorText.RESET + " ⎜\n", "Status ");
        border();
    }

    private static void displayTableProduct(Scanner scanner, CallableStatement cs, int offset, int totalProduct) throws SQLException {
        ResultSet rs = cs.executeQuery();
        try {
            designTableForResultSet(rs); // offset = 1 in ra lần đầu tiên
            String choice;
            do {
                cs.setInt(1, offset); // set lại với offset mới
                rs = cs.executeQuery(); // cập nhật lại resultSet
                if (totalProduct > 10 && offset == 0) {  // Check if on the first page and there are more than 10 products
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%66s%12s\n" + ColorText.RESET, "[Page 1]", "2.⏩️");
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 2 để đi đến trang tiếp theo, hoặc nhập (exit) để quay lại");
                } else if (offset >= Math.max(0, totalProduct - 1) && offset != 0) {  // Check if on the last page and not on the first page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%51s%15s\n" + ColorText.RESET, "1.⏪️️", "[Page" + (offset / 10 + 1) + ']');
                    System.out.println(); // cách ra
                    Notification.inputPrint("Nhập 1 để trở lại trang trước, hoặc nhập (exit) để quay lại");
                } else if (offset > 0 && offset < Math.max(0, totalProduct - 1)) {  // Check if not on the first or the last page
                    System.out.printf(ColorText.YELLOW_BRIGHT + "%50s%15s%13s\n" + ColorText.RESET, "1.⏪️", "[Page" + (offset / 10 + 1) + ']', "2.⏩️");
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


    public static void addProduct(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call AddProduct(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Product product = new Product();
            product.inputData(scanner, "thêm mới");
            cs.setString(1, product.getProductId());
            cs.setString(2, product.getProductName());
            cs.setString(3, product.getManufacturer());
            cs.setInt(4, product.getBatch());
            cs.execute();
        }
    }

    public static void updateProduct(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call UpdateProduct(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            String foundId; // Found id
            Notification.successMessage("-> VIEW MODE <-");
            displayProducts(scanner);
            Notification.successMessage("-> UPDATE MODE <-");
            while (true) {
                Notification.inputPrint("Nhập Id sản phẩm cần tìm để cập nhật, " +
                        "hoặc nhập (exit) để huỷ và quay lại: ");
                foundId = scanner.nextLine().trim();
                if (foundId.equalsIgnoreCase("exit"))
                    return;
                // check tồn tại
                if (!Validate.isInputValid(foundId)) // check not null
                    continue;
                if (foundId.length() > 5) {
                    Notification.errorMessage("Id sản phẩm không được quá 5 ký tự");
                    continue;
                }
                if (!checkExistProduct(foundId, null)) {
                    Notification.errorMessage("Không tồn tại sản phẩm ID : " + foundId);
                    continue;
                }
                Notification.successMessage("OK bây giờ nhập mới các trường sau để cập nhật: ");
                Product newProduct = new Product();
                newProduct.setProductId(foundId);
                newProduct.inputData(scanner, "cập nhật");
                cs.setString(1, newProduct.getProductId());
                // tên
                cs.setString(2, newProduct.getProductName());
                // nhà sản xuẩt
                cs.setString(3, newProduct.getManufacturer());
                // số lô
                cs.setInt(4, newProduct.getBatch());
                Notification.successMessage("✓ Sản phẩm đã được cập nhật thành công!");
                cs.execute();
            }
        }
    }


    public static boolean checkExistProduct(String id, String name) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkExistProduct(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, id);
            cs.setString(2, name);
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(3);
        }
    }

    public static boolean isActiveProduct(String productId, String productName) throws SQLException, ClassNotFoundException {
        String storedProc = "{call isActiveProduct(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, productId);
            cs.setString(2, productName);
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(3);
        }
    }


    public static void updateProductStatus(Scanner scanner) throws SQLException, ClassNotFoundException {
        // hiển thị lại bảng
        displayProducts(scanner);
        String storedProc = "{call UpdateProductStatus(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            Notification.inputPrint("Nhập ID sản phẩm để cập nhật status, hoặc nhập (exit) để huỷ và quay lại: ");
            // check tồn tại
            boolean status;
            do {
                String productId = scanner.nextLine().trim();
                if (productId.equalsIgnoreCase("exit"))
                    return;
                if (!Validate.isInputValid(productId)) continue;
                if (!checkExistProduct(productId, null)) {
                    Notification.errorMessage("Không tồn tại sản phẩm với Id : " + productId);
                    continue;
                }
                cs.setString(1, productId); // đặt lại id cho parameter 1
                String choice;
                System.out.println("Nhập trạng thái mới cho sản phẩm nhập :\n" + ColorText.WHITE_BRIGHT +
                        "- 0. (Không hoạt động) hoặc - 1. (Hoạt động) |  hoặc nhập (exit) để quay lại menu" + ColorText.RESET);
                do {
                    choice = scanner.nextLine().trim();
                    if (!Validate.isInputValid(choice)) continue;
                    if (choice.equalsIgnoreCase("exit")) return;
                    switch (choice) {
                        case "0" -> status = false;
                        case "1" -> status = true;
                        default -> {
                            Notification.errorMessage("Giá trị nhập phải là số (0 hoặc 1), Hãy thử lại!");
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
            Notification.successMessage("✓ Cập nhật thành công!");
        }
    }

    public static String getProductName(String productId) throws SQLException, ClassNotFoundException {
        String storedProc = "{call getNameProduct(?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, productId);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) return rs.getString("Product_Name");
            else {
                return null;
            }
        }
    }

    public static void searchProduct(Scanner scanner) throws SQLException, ClassNotFoundException {
        String storedProc = "{call searchProduct(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            String keyword;
            do {
                Notification.inputPrint("Nhập ID hoặc tên từ khoá của sản phẩm để tìm kiếm, " +
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

                displayTableProduct(scanner, cs, offset, totalProduct);
            } while (true);
        }
    }
}
