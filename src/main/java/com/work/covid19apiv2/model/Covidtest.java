package com.work.covid19apiv2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Covidtest {

    String email;
    double temperature;
    String covidresult;
    String testdate;
    String created_date;
    String updated_date;
}
