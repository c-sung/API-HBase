package tw.kewang.testserver.api;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("name")
    public String name;
    @SerializedName("sex")
    public String sex;
    @SerializedName("phoneNumber")
    public String phonenumber;
    @SerializedName("age")
    public int age;
    @SerializedName("email")
    public String email;
}
