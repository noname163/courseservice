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
}
