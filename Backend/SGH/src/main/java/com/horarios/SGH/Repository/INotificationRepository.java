package com.horarios.SGH.Repository;

import com.horarios.SGH.Model.Notification;
import com.horarios.SGH.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetRole(Role targetRole);

    List<Notification> findByIsSent(Boolean isSent);

    List<Notification> findByTargetRoleAndIsSent(Role targetRole, Boolean isSent);

    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :startDate AND n.createdAt <= :endDate")
    List<Notification> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT n FROM Notification n WHERE n.targetRole = :role AND n.createdAt >= :startDate AND n.createdAt <= :endDate")
    List<Notification> findByRoleAndDateRange(@Param("role") Role role,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    long countByTargetRole(Role targetRole);

    long countByIsSent(Boolean isSent);
}