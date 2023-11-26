package com.example.courseservice.template;

public class SendMailTemplate {
    public static String rejectEmail(String name, String courseName, String reason) {
        return "<html><body>" +
                "<p> Thân gửi giáo viên, " + name + ", </p>" +
                "<p>Chúng tôi rất tiếc phải thông báo,</p>" +
                "<p>Khóa học tên: " + courseName + "</p>" +
                "<p>Của bạn đã không được thông qua</p>" +
                "<p>Vì lý do" + reason + " </p>" +
                "</body></html>";
    }

    public static String approveEmail(String name, String courseName) {
        return "<html><body>" +
                "<p>Thân gửi giáo viên, " + name + ", </p>" +
                "<p>Khóa học tên: " + courseName + "</p>" +
                "<p>Đã được duyệt, vui lòng đăng nhập vào hệ thống để kiểm tra</p>" +
                "</body></html>";
    }

    public static String paymentSuccessEmail(String name, String courseName, Double cost) {
        return "<html><body>" +
                "<p> Chúc mừng bạn, " + name + ", </p>" +
                "<p> Bạn đã thanh toán thành công,</p>" +
                "<p>Khóa học tên: " + courseName + "</p>" +
                "<p>Với mức giá " + cost + " VND </p>" +
                "</body></html>";
    }

    public static String reportSuccessEmail(String name) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi đã nhận được báo cáo của bạn,</p>" +
                "<p>và sẽ xử lý trong thời gian ngắn nhất </p>" +
                "<p>Chúng tôi rất chân trọng đóng góp của bạn</p>" +
                "</body></html>";
    }
}
