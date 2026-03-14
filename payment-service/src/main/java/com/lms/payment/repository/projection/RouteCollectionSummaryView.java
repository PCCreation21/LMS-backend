package com.lms.payment.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RouteCollectionSummaryView {
    String getRouteCode();
    String getRouteName();
    String getRouteOfficer();
    Long getTotalCustomers();
    BigDecimal getTotalCollectedAmount();
    LocalDate getCollectionDate();
}
