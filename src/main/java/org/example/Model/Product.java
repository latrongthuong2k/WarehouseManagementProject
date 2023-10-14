package org.example.Model;

import org.example.Dao.ProductDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static org.example.Util.Validate.isInputValid;

public class Product {
    private String productId;
    private String productName;
    private String manufacturer;
    private Integer batch;
    private Integer quantity;
    private Boolean productStatus;

    public Product() {
        this.quantity = 0;
        this.productStatus = true;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Boolean productStatus) {
        this.productStatus = productStatus;
    }

    //


    // Input
    public void inputData(Scanner scanner, String nameAction) throws SQLException, ClassNotFoundException {
        String input;
        if (this.productId == null) {
            // Nhập mã sp
            Notification.inputPrint("Nhập mã sản phẩm : ");
            do {
                input = scanner.nextLine().trim();
                // nếu là null và empty và giới hạn char
                if (!isInputValid(input)) continue;
                else if (input.length() > 5) {
                    Notification.errorMessage("Input chỉ được tối đa 5 ký tự cho phép, Hãy thử lại!");
                    continue;
                }
                if (ProductDao.checkExistProduct(input, null))
                    Notification.errorMessage("ID sản phẩm đã tồn tại, vui lòng nhập lại ID khác");
                else
                    break;
            } while (true);
            setProductId(input);
            Notification.successMessage("✓ SUCCESS!");
        }

        // lấy tên cũ trong db
        this.productName = ProductDao.getProductName(productId);

        // Nhập tên sp
        Notification.inputPrint("Nhập tên sản phẩm : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            else if (input.length() > 150) {
                Notification.errorMessage("Input chỉ được tối đa 100 ký tự cho phép, Hãy thử lại!");
                continue;
            }
            if (!input.equalsIgnoreCase(this.productName))
                if (ProductDao.checkExistProduct(null, input)) {
                    Notification.errorMessage("Tên sản phẩm đã tồn tại, vui lòng nhập lại tên khác");
                    continue;
                }
            break;
        } while (true);
        setProductName(input);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập tên nhà sản xuất
        Notification.inputPrint("Nhập tên nhà sản xuất : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            else if (input.length() > 200) {
                Notification.errorMessage("Input chỉ được tối đa 200 ký tự cho phép, Hãy thử lại!");
                continue;
            }
            break;
        } while (true);
        setManufacturer(input);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập lô chứa sản phẩm
        Integer batch = null;
        Notification.inputPrint("Nhập lô chứa sản phẩm : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            try {
                batch = Integer.parseInt(input);
                break;
            } catch (InputMismatchException e) {
                Notification.errorMessage("Giá trị nhập phải là số");
            }
        } while (true);
        setBatch(batch);
        Notification.successMessage("✓ SUCCESS!");
        //
        displayInfo(nameAction);
        System.out.println();
    }

    public void displayInfo(String nameAction) {
        System.out.println("Thông tin của sản phẩm vừa thực hiện " + nameAction + " là: ");
        System.out.println(ColorText.WHITE_BRIGHT +
                "\n * Product ID : " + productId +
                "\n * Product Name : " + productName +
                "\n * Name manufacturer : " + manufacturer +
                "\n * Batch : " + batch +
                "\n * Quantity : " + quantity +
                "\n * Status : " + (productStatus ? "Hoạt động" : "Không hoạt động") + ColorText.RESET);
        System.out.println();
    }
}
