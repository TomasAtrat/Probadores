package com.smartstore.probadores.ui.backend.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class HTTPAnswer {
    public int status;
    public List<String> errors;

    public static HTTPAnswer create(List<String> errors){
        if(errors != null && errors.size() > 0) return new HTTPAnswer(400, errors);

        return new HTTPAnswer(200, errors);
    }

}