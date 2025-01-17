package com.departure.helper.model;

import java.util.Date;

public class DepartureNotificationData {

    private final String fullName;
    private final Date birthDate;
    private final Date departureDate;

    private DepartureNotificationData(Builder builder) {

        this.fullName = builder.fullName;
        this.birthDate = builder.birthDate;
        this.departureDate = builder.departureDate;
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getFullName() {

        return fullName;
    }

    public Date getBirthDate() {

        return birthDate;
    }

    public Date getDepartureDate() {

        return departureDate;
    }

    public static class Builder {

        private String fullName;
        private Date birthDate;
        private Date departureDate;

        public Builder fullName(String fullName) {

            this.fullName = fullName;
            return this;
        }

        public Builder birthDate(Date birthDate) {

            this.birthDate = birthDate;
            return this;
        }

        public Builder departureDate(Date departureDate) {

            this.departureDate = departureDate;
            return this;
        }

        public DepartureNotificationData build() {

            return new DepartureNotificationData(this);
        }
    }
}

