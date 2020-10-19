package com.aquent.crudapp.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Formatter {

    public String formatPhoneNumber(String phoneNumber) {
        return String.format("(%s)%s", phoneNumber.substring(0,3),
                phoneNumber.substring(3));
    }

    public String formatAddress(String streetAddress, String city, String state, String zip) {
        return String.join(" ", streetAddress, city, state, zip);
    }
}
