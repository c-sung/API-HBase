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
    private volatile List<Members> user = new ArrayList<Members>();
    private Gson gson = new Gson();
    private Answer noResult = new Answer("查無此人");
    private Answer yesResult = new Answer("成功");

    @Produces("application/json")
    @POST
    public Response post(String body) {
        Members mem = gson.fromJson(body, Members.class);
        user.add(mem);
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(yesResult)).build();
    }


    @Path("{keywordGet}")
    @GET
    public Response get(@Context HttpHeaders headers, @PathParam("keywordGet") String keyword) {
        if (detect(keyword)!=-1) {
            String jsonStr = gson.toJson(user.get(detect(keyword)));
            return Response.ok().entity(jsonStr).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();

        }
    }

    @Path("{keywordDel}")
    @DELETE
    public Response del(@Context HttpHeaders headers, @PathParam("keywordDel") String keyword) {
        if (detect(keyword)!=-1) {
            user.remove(detect(keyword));
            return Response.ok().entity(gson.toJson(yesResult)).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();
        }
    }


    @Path("{keywordPut}")
    @PUT
    public Response put(@Context HttpHeaders headers, @PathParam("keywordPut") String keyword, String body) {
        int detRes = detect(keyword);
        if (detRes!=-1) {
            Members newstr = gson.fromJson(body, Members.class);
            user.set(detRes, newstr);
            return Response.ok().entity((gson.toJson(yesResult)) + "\n" + gson.toJson(user.get(detRes))).build();
        } else {
            return Response.ok().entity(gson.toJson(noResult)).build();
        }
    }

    private int detect(String key) {

        for (int numberOfIndex = 0; numberOfIndex < user.size(); numberOfIndex++) {
            if (user.get(numberOfIndex).getPhoneNumber().equals(key) || user.get(numberOfIndex).getName().equals(key) || user.get(numberOfIndex).getEmail().equals(key)) {
                return numberOfIndex;
            }
        }
        return -1;
    }


    public class Members {
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

        public Members(String _name, String _sex, int _age, String _phoneNumber, String _email) {
            name = _name;
            sex = _sex;
            age = _age;
            phoneNumber = _phoneNumber;
            email = _email;
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

        public Answer(String _ans) {
            ans = _ans;
        }
    }


}





