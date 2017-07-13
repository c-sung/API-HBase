package tw.kewang.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("data")
public class DataApi {
    private static volatile List<Result> results = new ArrayList<>();
    private final Gson gson = new Gson();
    private final Result.Answer noResult = new Result.Answer("not found");
    private final Result.Answer yesResult = new Result.Answer("OK");

    @POST
    public Response post(String body) {
        Result res = new Result();
        Result.Member mem = gson.fromJson(body, Result.Member.class);
        res.setMem1(mem);
        results.add(res);
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(yesResult)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {
        Result detRes = detect(keyword);
        if (detRes != null) {
            detRes.setAns(yesResult);
            System.out.println(detRes);
            String jsonStr = gson.toJson(detRes);
            return Response.ok().entity(jsonStr).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();

        }
    }

    @Path("{keyword}")
    @DELETE
    public Response del(@PathParam("keyword") String keyword) {
        Result detRes = detect(keyword);
        if (detRes != null) {
            detRes.setAns(yesResult);
            results.remove(detect(keyword));
            return Response.ok().entity(gson.toJson(yesResult)).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();
        }
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword,@PathParam("dataToPut") String dataToPut, String body) {
        Result detRes=detect(keyword);
        switch (dataToPut){
            case "name":
                detRes.getMem1().setName(body);
                break;
            case "sex":
                detRes.getMem1().setSex(body);
                break;
            case "age":
                detRes.getMem1().setAge(Integer.parseInt(body));
                break;
            case "email":
                detRes.getMem1().setEmail(body);
                break;
            case "phoneNumber":
                detRes.getMem1().setPhoneNumber(body);
                break;
            default:
                return Response.ok().entity(gson.toJson(noResult)).build();
        }
        return Response.ok().entity(gson.toJson(detRes)).build();
    }

    private static Result detect(String key) {

        for (int numberOfIndex = 0; numberOfIndex < results.size(); numberOfIndex++) {
            Result memGet = results.get(numberOfIndex);
            if (memGet.getMem1().getPhoneNumber().equals(key) || memGet.getMem1().getName().equals(key) || memGet.getMem1().getEmail().equals(key)) {
                return memGet;
            }
        }
        return null;
    }

    public static class Result{
        private Member mem1;
        private Answer ans;
        public Member getMem1(){
            return mem1;
        }
        public void setMem1(Member mem1){
            this.mem1=mem1;
        }
        public Answer getAns(){
            return ans;
        }
        public void setAns(Answer ans){
            this.ans=ans;
        }
        private class Member {
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

            public Member(String name, String sex, int age, String phoneNumber, String email, String ans) {
                this.name = name;
                this.sex = sex;
                this.age = age;
                this.phoneNumber = phoneNumber;
                this.email = email;
            }
        }

        static class Answer {
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


}





