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

    public static String acceptReportEmail(String name, String reportContent) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi đã xử lý báo cáo với nội dung " + reportContent + "</p>" +
                "<p>Chúng tôi rất chân trọng đóng góp của bạn</p>" +
                "</body></html>";
    }

    public static String rejectReportEmail(String name, String videoName) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi không nhận thấy video tên " + videoName + "</p>" +
                "<p>,vi phạm qui chuẩn của hệ thống.</p>" +
                "<p>Chúng tôi rất chân trọng đóng góp của bạn</p>" +
                "</body></html>";
    }

    public static String reportMessageToTeacherEmail(String name, String videoName, String courseName, String reason) {
        return "<html><body>" +
                "<p>Thân gửi giáo viên, " + name + ", </p>" +
                "<p>Chúng tôi nhận thấy video tên " + videoName + "</p>" +
                "<p>Thuộc khóa học " + courseName + " của bạn</p>" +
                "<p>, đã vi phạm qui chuẩn của hệ thống.</p>" +
                "<p>Lý do " + reason + "</p>" +
                "<p>.Vì vậy chúng tôi đã gỡ video khỏi nền tảng.</p>" +
                "<p>Chúng tôi rất chân trọng đóng góp của bạn</p>" +
                "</body></html>";
    }

    public static String recivedRefundRequestEmail(String name, String courseName, String transactionId) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi đã nhận được yêu cầu hoàn tiền khóa học " + courseName + " của bạn</p>" +
                "<p>Với mã giao dịch là " + transactionId + "</p>" +
                "<p>Chúng tôi sẽ xử lý trong thời gian 1 tuần</p>" +
                "<p>Chúng bạn học tập hiệu quả với hệ thống của chúng tôi</p>" +
                "</body></html>";
    }

    public static String rejectRefundEmail(String name, String courseName, String transactionId, String reason) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi đã nhận được yêu cầu hoàn tiền khóa học " + courseName + " của bạn</p>" +
                "<p>Với mã giao dịch là " + transactionId + "</p>" +
                "<p>Tuy nhiên yêu cầu của bạn không được chấp nhận với lý do: </p>" +
                "<p>" + reason + "</p>" +
                "<p>Chúng bạn học tập hiệu quả với hệ thống của chúng tôi</p>" +
                "</body></html>";
    }

    public static String acceptedRefundEmail(String name, String courseName, String transactionId) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi đã nhận được yêu cầu hoàn tiền khóa học " + courseName + " của bạn</p>" +
                "<p>Với mã giao dịch là " + transactionId + "</p>" +
                "<p>Chúng tôi đã hoàn lại tiền cho bạn. Vui lòng kiểm tra số tài khoản của bạn</p>" +
                "<p>Chúng bạn học tập hiệu quả với hệ thống của chúng tôi</p>" +
                "</body></html>";
    }

    public static String paymentTeacherEmail(String name, String courseName, String transactionId, String amount) {
        return "<html><body>" +
                "<p>Cảm ơn bạn, " + name + ", </p>" +
                "<p>Chúng tôi đã gửi tiền khóa học " + courseName + " của bạn</p>" +
                "<p>Với mã giao dịch là " + transactionId + "</p>" +
                "<p>Với khoản tiền là " + amount + " Vui lòng kiểm tra số tài khoản của bạn</p>" +
                "<p>Chúng bạn có trải nghiệm tuyệt vời trên hệ thống của chúng tôi</p>" +
                "<p>Mọi thắc mắc vui lòng liên hệ email cepasupport@gmail.com</p>" +
                "</body></html>";
    }

    public static String notificationRefundEmailForTeacher(String courseName, Double amount,
            Double remain) {
        return "<html><body>" +
                "<p>Chúng tôi rất tiếc khi phải thông báo khóa học, " + courseName + ", </p>" +
                "<p>Đã có học sinh yêu cầu hoàn tiền và đã được chấp nhận</p>" +
                "<p>Chúng tôi đã hoàn lại tiền cho học sinh. Vì vậy thu nhập của bạn đã giảm " + amount + " VND</p>" +
                "<p>Số tiền còn lại của bạn là " + remain + " VND</p>" +
                "<p>Cảm ơn bạn đã đọc mail</p>" +
                "</body></html>";
    }
}
