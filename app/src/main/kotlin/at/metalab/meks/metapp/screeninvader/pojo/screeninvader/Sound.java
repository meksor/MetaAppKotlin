
package at.metalab.meks.metapp.screeninvader.pojo.screeninvader;

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
    "device",
    "mute",
    "volume"
})
public class Sound {

    @JsonProperty("device")
    private String device;
    @JsonProperty("mute")
    private String mute;
    @JsonProperty("volume")
    private String volume;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The device
     */
    @JsonProperty("device")
    public String getDevice() {
        return device;
    }

    /**
     * 
     * @param device
     *     The device
     */
    @JsonProperty("device")
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * 
     * @return
     *     The mute
     */
    @JsonProperty("mute")
    public String getMute() {
        return mute;
    }

    /**
     * 
     * @param mute
     *     The mute
     */
    @JsonProperty("mute")
    public void setMute(String mute) {
        this.mute = mute;
    }

    /**
     * 
     * @return
     *     The volume
     */
    @JsonProperty("volume")
    public String getVolume() {
        return volume;
    }

    /**
     * 
     * @param volume
     *     The volume
     */
    @JsonProperty("volume")
    public void setVolume(String volume) {
        this.volume = volume;
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
