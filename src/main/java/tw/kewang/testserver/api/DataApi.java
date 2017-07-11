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
    static volatile List<members> user = new ArrayList<members>();
    static volatile List<answer> response = new ArrayList<answer>();
    static int resNm = -1;

    @Produces("application/json")
    @POST
    public Response post(String body) {
        String recieve = body;
        Gson gson = new Gson();
        members mem = gson.fromJson(recieve, members.class);
        user.add(mem);
        System.out.println(gson.toJson(mem));
        answer a1 = new answer("OK");
        response.add(a1);
        resNm += 1;
        return Response.ok().entity(gson.toJson(response.get(resNm))).build();
    }

    static int num = 0;

    @Path("{keywordGet}")
    @GET
    public Response get(@Context HttpHeaders headers, @PathParam("keywordGet") String keyword) {
        Gson gson = new Gson();
        if (detect(keyword)) {
            String jsonStr = gson.toJson(user.get(num));
            return Response.ok().entity(jsonStr).build();
        } else {
            answer a1 = new answer("查無此人");
            response.add(a1);
            resNm += 1;
            return Response.ok().entity(gson.toJson(response.get(resNm))).build();

        }
    }

    @Path("{keywordDel}")
    @DELETE
    public Response del(@Context HttpHeaders headers, @PathParam("keywordDel") String keyword) {
        Gson gson = new Gson();
        if (detect(keyword)) {
            user.remove(num);
            answer a1 = new answer("已成功移除資料");
            response.add(a1);
            resNm += 1;
            return Response.ok().entity(gson.toJson(response.get(resNm))).build();
        } else {
            answer a1 = new answer("查無此人");
            response.add(a1);
            resNm += 1;
            return Response.ok().entity(gson.toJson(response.get(resNm))).build();
        }
    }


    @Path("{keywordPut}")
    @PUT
    public Response PUT(@Context HttpHeaders headers, @PathParam("keywordPut") String keyword, String body) {
        Gson gson = new Gson();
        if (detect(keyword)) {
            members newstr = gson.fromJson(body, members.class);
            user.set(num, newstr);
            answer a1 = new answer("更新成功!");
            response.add(a1);
            resNm += 1;
            return Response.ok().entity(gson.toJson(response.get(resNm)) + "\n" + gson.toJson(user.get(num))).build();
        } else {
            answer a1 = new answer("查無此人");
            response.add(a1);
            resNm += 1;
            return Response.ok().entity(gson.toJson(response.get(resNm))).build();
        }
    }

    static boolean detect(String key) {

        for (num = 0; num < user.size(); num++) {
            if (user.get(num).getPhoneNumber().equals(key) || user.get(num).getName().equals(key) || user.get(num).getEmail().equals(key)) {
                return true;
            }
        }
        return false;
    }


    public class members {
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

        public members(String _name, String _sex, int _age, String _phoneNumber, String _email) {
            name = _name;
            sex = _sex;
            age = _age;
            phoneNumber = _phoneNumber;
            email = _email;
        }
    }

    public class answer {
        private String ans;

        public String getAns() {
            return ans;
        }

        public void setAns(String ans) {
            this.ans = ans;
        }

        public answer(String _ans) {
            ans = _ans;
        }
    }


}





