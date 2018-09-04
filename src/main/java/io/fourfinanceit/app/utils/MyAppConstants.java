package io.fourfinanceit.app.utils;

public final class MyAppConstants {

    private MyAppConstants() {}

    public static final String STATUS_WAITING_FOR_APPROVAL = "WAITING_FOR_APPROVAL";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final Double STANDARD_INTEREST_FACTOR = 1d / 7;
    public static final Double EXTENDED_INTEREST_FACTOR = 1.5 / 7;
    public static final Double OVERDUE_INTEREST_FACTOR = 3d / 7;

    public static final long MAX_LOAN_AMOUNT = 5000;
    public static final long MAX_LOAN_TERM_IN_DAYS = 30 * 12;
    public static final long MAX_LOAN_EXTEND_TERM_IN_DAYS = 30 * 3;
    public static final int LOANS_PER_USER_LIMIT = 5;
    public static final int FORBIDDEN_HOUR_MIN = 0;
    public static final int FORBIDDEN_HOUR_MAX = 7;

}
