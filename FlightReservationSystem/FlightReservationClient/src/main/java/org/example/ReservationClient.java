package org.example;

import controllers.VisitorBeanRemote;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.naming.InitialContext;
import javax.validation.constraints.NotNull;
import java.util.Scanner;

@RequiredArgsConstructor
public class ReservationClient implements SystemClient {
    @NotNull
    private final InitialContext initialContext;
    @NonNull
    private final VisitorBeanRemote visitorBeanRemote;

    @Setter(AccessLevel.PRIVATE)
    private Scanner scanner;

    @Override
    public void runApp() {
        this.scanner = new Scanner(System.in);
    }
}
