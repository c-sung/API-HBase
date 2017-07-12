package tw.kewang.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("data")
public class DataApi {
    private static volatile List<Member> members = new ArrayList<>();
    private final Gson gson = new Gson();
    private final Answer noResult = new Answer("查無此人");
    private final Answer yesResult = new Answer("成功");

    @Produces("application/json")
    @POST
    public Response post(String body) {
        Member mem = gson.fromJson(body, Member.class);
        members.add(mem);
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(yesResult)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@Context HttpHeaders headers, @PathParam("keyword") String keyword) {
        int detRes = detect(keyword);
        if (detRes!=-1) {
            String jsonStr = gson.toJson(members.get(detRes));
            return Response.ok().entity(jsonStr).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();

        }
    }

    @Path("{keyword}")
    @DELETE
    public Response del(@Context HttpHeaders headers, @PathParam("keyword") String keyword) {
        if (detect(keyword)!=-1) {
            members.remove(detect(keyword));
            return Response.ok().entity(gson.toJson(yesResult)).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();
        }
    }


    @Path("{keyword}")
    @PUT
    public Response put(@Context HttpHeaders headers, @PathParam("keyword") String keyword, String body) {
        int detRes = detect(keyword);
        if (detRes!=-1) {
            Member newstr = gson.fromJson(body, Member.class);
            members.set(detRes, newstr);
            return Response.ok().entity((gson.toJson(yesResult))).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();
        }
    }

    private static int detect(String key) {

        for (int numberOfIndex = 0; numberOfIndex < members.size(); numberOfIndex++) {
            Member memGet = members.get(numberOfIndex);
            if (memGet.getPhoneNumber().equals(key) || memGet.getName().equals(key) || memGet.getEmail().equals(key)) {
                return numberOfIndex;
            }
        }
        return -1;
    }


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





