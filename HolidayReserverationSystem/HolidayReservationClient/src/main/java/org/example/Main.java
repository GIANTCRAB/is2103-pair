package org.example;

import webservices.HolidayReservationService.HolidayReservationService;
import webservices.HolidayReservationService.HolidayReservationServiceBean;

public class Main {
    public static void main(String[] a) {
        HolidayReservationService holidayReservationService = new HolidayReservationService();
        HolidayReservationServiceBean holidayReservationServiceBean = holidayReservationService.getHolidayReservationServiceBeanPort();
        //TODO: use the service bean to do login etc
    }
}
