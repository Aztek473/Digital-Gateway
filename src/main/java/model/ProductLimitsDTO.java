package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ProductLimitsDTO(
    @JacksonXmlProperty(isAttribute = true) 
    String currency,
    
    @JacksonXmlProperty(localName = "minAmount") 
    long minAmount,
    
    @JacksonXmlProperty(localName = "maxAmount") 
    long maxAmount,
    
    @JacksonXmlProperty(localName = "dailyLimit") 
    long dailyLimit
) {}
