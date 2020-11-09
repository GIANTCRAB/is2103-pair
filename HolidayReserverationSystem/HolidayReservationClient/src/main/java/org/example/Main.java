package org.example;

import webservices.HolidayReservationService.HolidayReservationService;
import webservices.HolidayReservationService.HolidayReservationServiceBean;

public class Main {
    public static void main(String[] a) {
        final HolidayReservationService holidayReservationService = new HolidayReservationService();
        final HolidayReservationServiceBean holidayReservationServiceBean = holidayReservationService.getHolidayReservationServiceBeanPort();
        final PartnerClient partnerClient = new PartnerClient(holidayReservationServiceBean);
        partnerClient.runApp();
    }
}
