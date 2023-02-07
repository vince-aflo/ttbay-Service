package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.OfficeDay;
import io.turntabl.ttbay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeDayRepository extends JpaRepository<OfficeDay, String> {
    void deleteByUser(User user);
}
