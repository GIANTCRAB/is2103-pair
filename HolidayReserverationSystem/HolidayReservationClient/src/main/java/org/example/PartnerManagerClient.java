package org.example;

import lombok.NonNull;
import webservices.HolidayReservationService.HolidayReservationServiceBean;
import webservices.HolidayReservationService.Partner;

import java.util.Scanner;

public class PartnerManagerClient extends PartnerEmployeeClient {

    public PartnerManagerClient(@NonNull Scanner scanner, @NonNull HolidayReservationServiceBean holidayReservationServiceBean, @NonNull Partner partner) {
        super(scanner, holidayReservationServiceBean, partner);
    }

    public void runApp() {

    }
}
