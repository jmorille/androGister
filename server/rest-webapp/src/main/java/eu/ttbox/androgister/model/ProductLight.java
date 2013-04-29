package eu.ttbox.androgister.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(fieldVisibility=Visibility.NON_PRIVATE)
@JsonPropertyOrder(value= {"uuid", "versionDate" , "name"} )
public interface ProductLight {

}
