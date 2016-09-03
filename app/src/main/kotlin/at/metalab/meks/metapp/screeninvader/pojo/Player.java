
package at.metalab.meks.metapp.screeninvader.pojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "active",
    "category",
    "paused",
    "url"
})
public class Player {

    @JsonProperty("active")
    private String active;
    @JsonProperty("category")
    private String category;
    @JsonProperty("paused")
    private String paused;
    @JsonProperty("url")
    private String url;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The active
     */
    @JsonProperty("active")
    public String getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    @JsonProperty("active")
    public void setActive(String active) {
        this.active = active;
    }

    /**
     * 
     * @return
     *     The category
     */
    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    /**
     * 
     * @param category
     *     The category
     */
    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 
     * @return
     *     The paused
     */
    @JsonProperty("paused")
    public String getPaused() {
        return paused;
    }

    /**
     * 
     * @param paused
     *     The paused
     */
    @JsonProperty("paused")
    public void setPaused(String paused) {
        this.paused = paused;
    }

    /**
     * 
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
