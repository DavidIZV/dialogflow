package org.izv.iabd.dialogflow;

import java.util.HashMap;
import java.util.Map;

public class Coche {

    public Map<String, String> prediction_models;
    public Map<String, String> prediction_mlp;
    public static Map<String, String> makes = new HashMap<String, String>() {{
        put("ALFA ROMEO", "500");
        put("AUDI", "600");
        put("MAZDA", "700");
        put("BMW", "800");
        put("CITROEN", "900");
        put("DACIA", "1000");
        put("FIAT", "1100");
        put("FORD", "1200");
        put("HONDA", "1300");
        put("HYUNDAI", "1400");
        put("JAGUAR", "1500");
        put("JEEP", "1600");
        put("KIA", "1700");
        put("LEXUS", "1800");
        put("MERCEDES-BENZ", "1900");
        put("MITSUBISHI", "2000");
        put("MINI", "2100");
        put("NISSAN", "2200");
        put("PORSCHE", "2300");
        put("OPEL", "2400");
        put("SKODA", "2500");
        put("PEUGEOT", "2600");
        put("VOLVO", "2700");
        put("RENAULT", "2800");
        put("SEAT", "2900");
        put("TOYOTA", "3000");
        put("VOLKSWAGEN", "3100");
    }};
}
