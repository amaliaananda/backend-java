package com.backend.travelid.controller;

import com.backend.travelid.entity.Notification;
import com.backend.travelid.repository.NotificationRepository;
import com.backend.travelid.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Test
    public void testGetById() throws Exception {
        when(notificationService.getByID(any(Long.class))).thenReturn(Collections.singletonMap("data", "notification"));
        mockMvc.perform(get("/notification/1"))
//                .header("Authorization", "Bearer your_access_token_here"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("notification"));
    }

    @Test
    public void testGetByCustomerId() throws Exception {
        when(notificationService.getByCustomerId(any(Long.class))).thenReturn(Collections.singletonList(new Notification()));
        mockMvc.perform(get("/notification/getByCustomerId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testGetByCustomerEmail() throws Exception {
        when(notificationService.getByCustomerEmail(any(String.class))).thenReturn(Collections.singletonList(new Notification()));
        mockMvc.perform(get("/notification/getByCustomerEmail/ferdyansahalfariz@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testList() throws Exception {
        // Mock data
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        Page<Notification> page = new PageImpl<>(notifications);

        // Mock behavior
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Perform request
        mockMvc.perform(get("/notification/listNotifications?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

}