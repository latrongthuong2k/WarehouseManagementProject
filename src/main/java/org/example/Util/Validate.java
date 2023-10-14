package org.example.Util;


import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Validate {
    public static boolean isInputValid(String input) {
        boolean isValid = false;
        if (input != null && !input.trim().isEmpty()) {
            isValid = true;
        } else if (input == null) {
            Notification.errorMessage("Nhập thất bại: Giá trị không được để null. Hãy thử lại!");
        } else {
            Notification.errorMessage("Nhập thất bại: Giá trị không được để trống. Hãy thử lại!");
        }
        return isValid;
    }

    // kiểm tra birthday và trả về birthday nếu không lỗi
    public static Date validateBirthday(String input) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date utilDate = formatter.parse(input);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            // Assuming Notification is a class you have for displaying error messages
            Notification.errorMessage("Nhập thất bại: Ngày sinh không hợp lệ. Hãy thử lại!");
            return null;
        }
    }

    public static boolean isValidDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            Notification.errorMessage("Nhập thất bại: Ngày nhập không hợp lệ. Hãy thử lại!");
            return false;
        }
    }

    // Phương thức kiểm tra email
    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (pattern.matcher(email).matches()) {
            return true;
        } else {
            Notification.errorMessage("Nhập thất bại: Email không hợp lệ. Hãy thử lại!");
            return false;
        }
    }

    // Phương thức kiểm tra số điện thoại
    public static boolean isPhoneNumberValid(String phoneNumber) {
        String phoneRegex = "^(0[3-9]{1}[0-9]{8})$";
        Pattern pattern = Pattern.compile(phoneRegex);
        if (pattern.matcher(phoneNumber).matches()) {
            return true;
        } else {
            Notification.errorMessage("Nhập thất bại: Số điện thoại không hợp lệ. Hãy thử lại!");
            return false;
        }
    }
}
