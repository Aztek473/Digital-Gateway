package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

public record RequiredFieldsDTO(
    @JacksonXmlProperty(localName = "field")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<RequiredFieldItemDTO> fields
) {}
