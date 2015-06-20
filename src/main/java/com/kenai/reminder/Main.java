package com.kenai.reminder;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Main {

    public static void main(String[] args) {
        DateTimeZone current = DateTimeZone.getDefault();
        int offsetMillis = current.getOffset(new DateTime());
        //        Duration previous = new Duration(offsetMillis);
        Period previous = Period.millis(offsetMillis).normalizedStandard();

        // HH:MM
        PeriodFormatter formatter = new PeriodFormatterBuilder()
            .printZeroAlways().minimumPrintedDigits(2).appendHours()
            .appendSuffix(":")
            .printZeroAlways().minimumPrintedDigits(2).appendMinutes()
            .toFormatter();

        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");

        //        Period p = Period.hours(2);
        //        System.out.println(p.toString(formatter));
        //        p = Period.seconds(7200);
        //        p = p.normalizedStandard();
        //        System.out.println(p.toString(formatter));
        //        p = Period.millis(7200000);
        //        p = p.normalizedStandard();
        //        System.out.println(p.toString(formatter));

        for (DateTime each : nextTransitionsFor(current, 5)) {
            System.out.println(each.toString(dateFormat));
            Period next = Period.millis(current.getOffset(each)).normalizedStandard();
            LocalTime time = each.toLocalTime();
            Period delta = previous.minus(next);
            System.out.println(time.plus(delta).toString(timeFormat) + " -> " +time.toString(timeFormat));
            System.out.println(each.minusMillis(1).toString(timeFormat) + " -> " + each.toString(timeFormat));
            System.out.println(previous.toString(formatter) + " -> " + next.toString(formatter));
            System.out.println("---------------------------------------------------------------------");
            previous = next;
        }

    }

    public static List<DateTime> nextTransitionsFor(DateTimeZone timeZone, int n) {
        // TODO check for fixed time zone
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        List<DateTime> result = new ArrayList<DateTime>(n);
        long current = System.currentTimeMillis();
        for (int i = 0; i < n; ++i) {
            long next = timeZone.nextTransition(current);
            if (next != current) {
                result.add(new DateTime(next));
                current = next;
            } else {
                // TODO log warning
                break;
            }
        }
        return result;
    }

}
