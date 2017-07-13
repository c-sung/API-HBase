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

    @POST
    public Response post(String body) {
        Result res = new Result();
        Member mem = gson.fromJson(body, Member.class);
        res.setMember(mem);
        members.add(res.getMember());
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(mem)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {
        System.out.println("Someone tried to access this server.");
        Member detRes = detect(keyword);
        Result res = new Result();
        if (detRes != null) {
            res.setMember(detRes);
            res.setAns("OK");
            return Response.ok().entity(gson.toJson(res)).build();
        } else {
            res.setAns("NO");
            return Response.ok().entity(gson.toJson(res)).build();

        }
    }

    @Path("{keyword}")
    @DELETE
    public Response del(@PathParam("keyword") String keyword) {
        Member detRes = detect(keyword);
        Result res = new Result();
        if (detRes != null) {
            members.remove(detRes);
            res.setAns("OK");
        } else {
            res.setAns("NO");
        }
        return Response.ok().entity(gson.toJson(res)).build();
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword, @PathParam("dataToPut") String dataToPut, String body) {
        Member detRes = detect(keyword);
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

    private static Member detect(String key) {

        for (int numberOfIndex = 0; numberOfIndex < members.size(); numberOfIndex++) {
            Member memGet = members.get(numberOfIndex);
            if (memGet.getPhoneNumber().equals(key) || memGet.getName().equals(key) || memGet.getEmail().equals(key)) {
                return memGet;
            }
        }
        return null;
    }

}





