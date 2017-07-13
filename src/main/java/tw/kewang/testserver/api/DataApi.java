package tw.kewang.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("data")
public class DataApi {
    private static volatile List<Result.Member> members = new ArrayList<>();
    private final Gson gson = new Gson();

    @POST
    public Response post(String body) {
        Result res = new Result();
        Result.Member mem = gson.fromJson(body, Result.Member.class);
        res.setMember(mem);
        members.add(res.getMember());
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(mem)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {
        Result.Member detRes = detect(keyword);
        Result res = new Result();
        res.setMember(detRes);
        res.setAns("NO");
        if (detRes != null) {
            res.setAns("OK");
            String jsonStr = gson.toJson(res);
            return Response.ok().entity(jsonStr).build();
        } else {
            return Response.ok().entity(gson.toJson(res)).build();

        }
    }

    @Path("{keyword}")
    @DELETE
    public Response del(@PathParam("keyword") String keyword) {
        Result.Member detRes = detect(keyword);
        Result res = new Result();
        res.setAns("OK");
        if (detRes != null) {
            members.remove(detect(keyword));
        } else {
            res.setAns("NO");
        }
        return Response.ok().entity(gson.toJson(res)).build();
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword, @PathParam("dataToPut") String dataToPut, String body) {
        Result.Member detRes = detect(keyword);
        Result res = new Result();
        if (detRes == null) {
            res.setAns("NO");
            return Response.ok().entity(gson.toJson(res)).build();
        }
        switch (dataToPut) {
            case "name":
                detRes.setName(body);
                res.setMember(detRes);
                break;
            case "sex":
                detRes.setSex(body);
                res.setMember(detRes);
                break;
            case "age":
                detRes.setAge(Integer.parseInt(body));
                res.setMember(detRes);
                break;
            case "email":
                detRes.setEmail(body);
                res.setMember(detRes);
                break;
            case "phoneNumber":
                detRes.setPhoneNumber(body);
                res.setMember(detRes);
                break;
            default:
                res.setAns("NO");
                return Response.ok().entity(gson.toJson(res)).build();
        }
        res.setAns("OK");
        return Response.ok().entity(gson.toJson(res)).build();
    }

    private static Result.Member detect(String key) {

        for (int numberOfIndex = 0; numberOfIndex < members.size(); numberOfIndex++) {
            Result.Member memGet = members.get(numberOfIndex);
            if (memGet.getPhoneNumber().equals(key) || memGet.getName().equals(key) || memGet.getEmail().equals(key)) {
                return memGet;
            }
        }
        return null;
    }

    public static class Result {
        private Member member;
        private String ans;

        public Member getMember() {
            return member;
        }

        public void setMember(Member member) {
            this.member = member;
        }

        public String getAns() {
            return ans;
        }

        public void setAns(String ans) {
            this.ans = ans;
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


    }


}





