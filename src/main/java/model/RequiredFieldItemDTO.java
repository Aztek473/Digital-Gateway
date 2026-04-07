package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record RequiredFieldItemDTO(
    @JacksonXmlProperty(isAttribute = true, localName = "de") 
    int de,
    
    @JacksonXmlProperty(isAttribute = true, localName = "name") 
    String name
) {}
