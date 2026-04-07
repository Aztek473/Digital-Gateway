package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "productRules")
public record ProductRulesConfig(
    @JacksonXmlProperty(localName = "product")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<ProductRuleDTO> products
) {}
