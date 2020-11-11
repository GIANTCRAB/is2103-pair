package org.example;

import webservices.HolidayReservationService.HolidayReservationService;
import webservices.HolidayReservationService.HolidayReservationServiceBean;

import javax.xml.ws.BindingProvider;

public class Main {
    public static void main(String[] a) {
        final HolidayReservationService holidayReservationService = new HolidayReservationService();
        final HolidayReservationServiceBean holidayReservationServiceBean = holidayReservationService.getHolidayReservationServiceBeanPort();
        ((BindingProvider) holidayReservationServiceBean).getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        final PartnerClient partnerClient = new PartnerClient(holidayReservationServiceBean);
        partnerClient.runApp();
    }
}
