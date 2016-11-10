
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
    "animation",
    "browser",
    "display",
    "image",
    "pdf",
    "player",
    "playlist",
    "radio",
    "shairport",
    "show",
    "sound"
})
public class ScreeninvaderObject {

    @JsonProperty("animation")
    private Animation animation;
    @JsonProperty("browser")
    private Browser browser;
    @JsonProperty("display")
    private Display display;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("pdf")
    private Pdf pdf;
    @JsonProperty("player")
    private Player player;
    @JsonProperty("playlist")
    private Playlist playlist;
    @JsonProperty("radio")
    private Radio radio;
    @JsonProperty("shairport")
    private Shairport shairport;
    @JsonProperty("show")
    private Show show;
    @JsonProperty("sound")
    private Sound sound;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The animation
     */
    @JsonProperty("animation")
    public Animation getAnimation() {
        return animation;
    }

    /**
     * 
     * @param animation
     *     The animation
     */
    @JsonProperty("animation")
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    /**
     * 
     * @return
     *     The browser
     */
    @JsonProperty("browser")
    public Browser getBrowser() {
        return browser;
    }

    /**
     * 
     * @param browser
     *     The browser
     */
    @JsonProperty("browser")
    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    /**
     * 
     * @return
     *     The display
     */
    @JsonProperty("display")
    public Display getDisplay() {
        return display;
    }

    /**
     * 
     * @param display
     *     The display
     */
    @JsonProperty("display")
    public void setDisplay(Display display) {
        this.display = display;
    }

    /**
     * 
     * @return
     *     The image
     */
    @JsonProperty("image")
    public Image getImage() {
        return image;
    }

    /**
     * 
     * @param image
     *     The image
     */
    @JsonProperty("image")
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * 
     * @return
     *     The pdf
     */
    @JsonProperty("pdf")
    public Pdf getPdf() {
        return pdf;
    }

    /**
     * 
     * @param pdf
     *     The pdf
     */
    @JsonProperty("pdf")
    public void setPdf(Pdf pdf) {
        this.pdf = pdf;
    }

    /**
     * 
     * @return
     *     The player
     */
    @JsonProperty("player")
    public Player getPlayer() {
        return player;
    }

    /**
     * 
     * @param player
     *     The player
     */
    @JsonProperty("player")
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * 
     * @return
     *     The playlist
     */
    @JsonProperty("playlist")
    public Playlist getPlaylist() {
        return playlist;
    }

    /**
     * 
     * @param playlist
     *     The playlist
     */
    @JsonProperty("playlist")
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    /**
     * 
     * @return
     *     The radio
     */
    @JsonProperty("radio")
    public Radio getRadio() {
        return radio;
    }

    /**
     * 
     * @param radio
     *     The radio
     */
    @JsonProperty("radio")
    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    /**
     * 
     * @return
     *     The shairport
     */
    @JsonProperty("shairport")
    public Shairport getShairport() {
        return shairport;
    }

    /**
     * 
     * @param shairport
     *     The shairport
     */
    @JsonProperty("shairport")
    public void setShairport(Shairport shairport) {
        this.shairport = shairport;
    }

    /**
     * 
     * @return
     *     The show
     */
    @JsonProperty("show")
    public Show getShow() {
        return show;
    }

    /**
     * 
     * @param show
     *     The show
     */
    @JsonProperty("show")
    public void setShow(Show show) {
        this.show = show;
    }

    /**
     * 
     * @return
     *     The sound
     */
    @JsonProperty("sound")
    public Sound getSound() {
        return sound;
    }

    /**
     * 
     * @param sound
     *     The sound
     */
    @JsonProperty("sound")
    public void setSound(Sound sound) {
        this.sound = sound;
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
