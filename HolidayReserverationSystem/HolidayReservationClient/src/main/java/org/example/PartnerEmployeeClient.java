package org.example;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import webservices.HolidayReservationService.HolidayReservationServiceBean;
import webservices.HolidayReservationService.Partner;

import java.util.Scanner;

@RequiredArgsConstructor
public class PartnerEmployeeClient implements SystemClient {
    @NonNull
    final Scanner scanner;
    @NonNull
    final HolidayReservationServiceBean holidayReservationServiceBean;
    @NonNull
    final Partner partner;

    public void runApp() {

    }
}
