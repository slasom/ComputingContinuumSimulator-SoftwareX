
package es.unex.infinitetime.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import es.unex.infinitetime.persistence.User;

public class UserRemote {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * No args constructor for use in serialization
     * 
     */
    public UserRemote() {
    }

    /**
     *
     * @param password
     * @param id
     * @param email
     * @param username
     */
    public UserRemote(String id, String username, String password, String email) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static UserRemote fromUser(User user) {
        return new UserRemote(
                Long.toString(user.getId()),
                user.getUsername(),
                user.getPassword(),
                user.getEmail());
    }

}
