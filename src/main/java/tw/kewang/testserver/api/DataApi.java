package tw.kewang.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("data")
public class DataApi {
    private static volatile List<Member> members = new ArrayList<>();
    private final Gson gson = new Gson();
    private final Answer noResult = new Answer("not found");
    private final Answer yesResult = new Answer("OK");
    @POST
    public Response post(String body) {
        Member mem = gson.fromJson(body, Member.class);
        mem.setAns("OK");
        members.add(mem);
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(yesResult)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {
        Member detRes = detect(keyword);
        if (detRes != null) {
            String jsonStr = gson.toJson(detRes);
            return Response.ok().entity(jsonStr).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();

        }
    }

    @Path("{keyword}")
    @DELETE
    public Response del(@PathParam("keyword") String keyword) {
        Member detRes = detect(keyword);
        if (detRes != null) {
            members.remove(detect(keyword));
            return Response.ok().entity(gson.toJson(yesResult)).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();
        }
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword,@PathParam("dataToPut") String dataToPut, String body) {
        Member detRes=detect(keyword);
        switch (dataToPut){
            case "name":
                detRes.setName(body);
                break;
            case "sex":
                detRes.setSex(body);
                break;
            case "age":
                detRes.setAge(Integer.parseInt(body));
                break;
            case "email":
                detRes.setEmail(body);
                break;
            case "phoneNumber":
                detRes.setPhoneNumber(body);
                break;
            default:
                return Response.ok().entity(gson.toJson(noResult)).build();
        }
        return Response.ok().entity(gson.toJson(detRes)).build();
    }

    private static Member detect(String key) {

        for (int numberOfIndex = 0; numberOfIndex < members.size(); numberOfIndex++) {
            Member memGet = members.get(numberOfIndex);
            if (memGet.getPhoneNumber().equals(key) || memGet.getName().equals(key) || memGet.getEmail().equals(key)) {
                return memGet;
            }
        }
        return null;
    }


    public class Member {
        private String name;
        private String sex;
        private String phoneNumber;
        private String email;
        private int age;
        private String ans;

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

        public String getAns() {
            return ans;
        }

        public void setAns(String ans) {
            this.ans = ans;
        }

        public Member(String name, String sex, int age, String phoneNumber, String email, String ans) {
            this.name = name;
            this.sex = sex;
            this.age = age;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.ans = ans;
        }
    }

    public class Answer {
        private String ans;

        public String getAns() {
            return ans;
        }

        public void setAns(String ans) {
            this.ans = ans;
        }

        public Answer(String ans) {
            this.ans = ans;
        }
    }


}





