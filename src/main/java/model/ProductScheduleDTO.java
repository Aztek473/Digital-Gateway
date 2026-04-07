package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ProductScheduleDTO(
    @JacksonXmlProperty(localName = "availableFrom") 
    String availableFrom,
    
    @JacksonXmlProperty(localName = "availableTo") 
    String availableTo,
    
    @JacksonXmlProperty(localName = "availableDays") 
    String availableDays
) {}
