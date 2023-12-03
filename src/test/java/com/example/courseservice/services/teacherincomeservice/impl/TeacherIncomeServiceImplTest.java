package com.example.courseservice.services.teacherincomeservice.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.courseservice.data.constants.TeacherIncomeStatus;
import com.example.courseservice.data.dto.request.AdminPaymentTeacherRequest;
import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.object.CourseReportInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.TeacherIncomeRepository;
import com.example.courseservice.mappers.TeacherIncomeMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.notificationservice.NotificationService;
import com.example.courseservice.services.sendmailservice.SendEmailService;
import com.example.courseservice.utils.PageableUtil;

class TeacherIncomeServiceImplTest {

    @InjectMocks
    private TeacherIncomeServiceImpl teacherIncomeService;

    @Mock
    private TeacherIncomeMapper teacherIncomeMapper;

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PageableUtil pageableUtil;

    @Mock
    private TeacherIncomeRepository teacherIncomeRepository;

    @Mock
    private SecurityContextService securityContextService;

    private Course course;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        course = Course.builder().build();
    }

    @Test
    public void testCreateTeacherIncome() {
        // Arrange
        TeacherIncomeRequest teacherIncomeRequest = createSampleTeacherIncomeRequest();
        when(teacherIncomeRepository.findByCourseIdAndUserIdAndMonthAndYear(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(teacherIncomeMapper.mapRequestDtoToEntity(teacherIncomeRequest))
                .thenReturn(TeacherIncome.builder().build());
        // Act
        teacherIncomeService.createTeacherIncome(teacherIncomeRequest);

        ArgumentCaptor<TeacherIncome> teacherIncomeCaptor = ArgumentCaptor.forClass(TeacherIncome.class);
        verify(teacherIncomeRepository).save(teacherIncomeCaptor.capture());

        TeacherIncome capturedTeacherIncome = teacherIncomeCaptor.getValue();

        assertEquals(LocalDate.now().getMonthValue(), capturedTeacherIncome.getMonth());
        assertEquals(LocalDate.now().getYear(), capturedTeacherIncome.getYear());
        assertEquals(TeacherIncomeStatus.PENDING, capturedTeacherIncome.getStatus());
    }

    @Test
    void testGetCurrentTeacherIncome() {
        List<TeacherIncomeResponse> result = new ArrayList<>();
        result.add(TeacherIncomeResponse.builder().build());
        List<CourseReportInterface> courseReportInterfaces = createSampleCourseReports();

        when(securityContextService.getCurrentUser()).thenReturn(createSampleUser());
        when(teacherIncomeRepository.getRevenueByTeacherEmailForLast10Months(any()))
                .thenReturn(courseReportInterfaces);
        when(teacherIncomeMapper.mapEntitiesToResponseDtos(courseReportInterfaces)).thenReturn(result);

        // Act
        List<TeacherIncomeResponse> teacherIncomeResponses = teacherIncomeService.getCurrentTeacherIncome();

        // Assert
        assertEquals(1, teacherIncomeResponses.size());
        // Add more assertions as needed
    }

    @Test
    void testAdminPaymentForTeacher() {

        AdminPaymentTeacherRequest adminPaymentTeacherRequest = createSampleAdminPaymentRequest();
        TeacherIncome teacherIncome = createSampleTeacherIncome();
        when(teacherIncomeRepository.findById(any())).thenReturn(Optional.of(teacherIncome));

        teacherIncomeService.adminPaymentForTeacher(adminPaymentTeacherRequest);

        verify(teacherIncomeRepository, times(1)).save(any());

    }

    public TeacherIncomeRequest createSampleTeacherIncomeRequest() {
        return TeacherIncomeRequest.builder()
                .amount(100.0)
                .course(course)
                .userId(1L)
                .build();
    }

    public UserInformation createSampleUser() {
        return UserInformation.builder()
                .id(1L)
                .fullname("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    public List<CourseReportInterface> createSampleCourseReports() {
        List<CourseReportInterface> courseReportInterfaces = new ArrayList<>();
        CourseReportInterface courseReportInterface = new CourseReportInterface() {

            @Override
            public Long getId() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getId'");
            }

            @Override
            public Long getCourseId() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getCourseId'");
            }

            @Override
            public String getTeacherName() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getTeacherName'");
            }

            @Override
            public String getTeacherAvatar() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getTeacherAvatar'");
            }

            @Override
            public String getSubject() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getSubject'");
            }

            @Override
            public String getCourseName() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getCourseName'");
            }

            @Override
            public String getMonthOfYear() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getMonthOfYear'");
            }

            @Override
            public Double getRevenue() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getRevenue'");
            }

            @Override
            public Double getReceivedMoney() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getReceivedMoney'");
            }

            @Override
            public LocalDateTime getPaymentDate() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getPaymentDate'");
            }

            @Override
            public TeacherIncomeStatus getTeacherIncomeStatus() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getTeacherIncomeStatus'");
            }

        };
        courseReportInterfaces.add(courseReportInterface);
        return courseReportInterfaces;
    }

    public AdminPaymentTeacherRequest createSampleAdminPaymentRequest() {
        return AdminPaymentTeacherRequest.builder()
                .id(1L)
                .amount(500.0)
                .paymentCode("ABC123")
                .paymentDate(LocalDateTime.now())
                .build();
    }

    public TeacherIncomeResponse createSampleTeacherIncomeResponse() {
        return TeacherIncomeResponse.builder()
                .courseName("Sample Course")
                .revenue(1000.0)
                .build();
    }

    public TeacherIncome createSampleTeacherIncome() {
        return TeacherIncome.builder()
                .id(1L)
                .money(600000.0)
                .course(course)
                .userId(1L)
                .build();
    }

}
