package com.recareer.backend.email.service;

import com.recareer.backend.reservation.entity.Reservation;

public interface EmailService {
    void sendMentoringConfirmationEmail(Reservation reservation);
}