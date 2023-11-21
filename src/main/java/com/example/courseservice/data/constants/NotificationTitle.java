package com.example.courseservice.data.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationTitle {
    public static final String PAYMENT_TITLE = "Thông báo bạn đã toán khóa học ";
    public static final String LEARNING_TITLE = "Thông báo tiến độ học tập ";
    public static final String SYSTEM_TITLE = "Có người đã phản hồi bình luận của bạn ";
    public static final String ENDING = "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.";
    public static final String OPENING = "Xin chào bạn, ";
    public static final String SEND_URL = "/topic/notification/";
    
}
