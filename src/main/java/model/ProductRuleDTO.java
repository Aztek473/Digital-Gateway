package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ProductRuleDTO(
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    String id,
    
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    String name,
    
    @JacksonXmlProperty(localName = "processingCode") 
    String processingCode,
    
    @JacksonXmlProperty(localName = "mti") 
    String mti,
    
    @JacksonXmlProperty(localName = "limits") 
    ProductLimitsDTO limits,
    
    @JacksonXmlProperty(localName = "requiredFields") 
    RequiredFieldsDTO requiredFields,
    
    @JacksonXmlProperty(localName = "schedule") 
    ProductScheduleDTO schedule
) {}
