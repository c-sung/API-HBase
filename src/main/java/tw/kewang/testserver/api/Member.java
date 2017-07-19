package tw.kewang.testserver.api;

public class Member {

    private String name;
    private String sex;
    private String phoneNumber;
    private String email;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Member(String name, String sex, int age, String phoneNumber, String email) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}

