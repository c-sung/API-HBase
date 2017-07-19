package tw.kewang.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import tw.kewang.testserver.Main;

import java.io.IOException;
import java.util.Objects;

@Path("data")
public class DataApi {
    private static volatile List<Member> members = new ArrayList<>();
    private final Gson gson = new Gson();

    @POST
    public Response post(String body) throws IOException {
        Member mem = gson.fromJson(body, Member.class);
        HBaseConnect("post", mem, "",null);
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(mem)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {
        Result res = new Result();
        org.apache.hadoop.hbase.client.Result getBack = null;
        try {
            getBack = HBaseConnect("get", null, keyword,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] valName = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("name"));
        byte[] valAge = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("age"));
        byte[] valSex = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("sex"));
        byte[] valPhoneNumber = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("phoneNumber"));
        byte[] valEmail = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("email"));
        System.out.println(Bytes.toString(valName));
        if (!Objects.equals(Bytes.toString(valName), null)) {
            Member mem = new Member("", "", 0, "", "");
            mem.setName(Bytes.toString(valName));
            mem.setAge(Integer.parseInt(Bytes.toString(valAge)));
            mem.setPhoneNumber(Bytes.toString(valPhoneNumber));
            mem.setSex(Bytes.toString(valSex));
            mem.setEmail(Bytes.toString(valEmail));
            System.out.println(gson.toJson(mem));
            res.setAns("OK");
            res.setMember(mem);
            System.out.println(gson.toJson(res));
            return Response.ok().entity(gson.toJson(res)).build();
        } else {
            res.setAns("NO");
        }
        return Response.ok().entity(gson.toJson(res)).build();
    }

    @Path("{keyword}")
    @DELETE
    public Response del(@PathParam("keyword") String keyword) {
        System.out.println("OK1");
        try {
            HBaseConnect("delete", null, keyword,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Result res = new Result();
        res.setAns("OK");
        return Response.ok().entity(gson.toJson(res)).build();
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword, @PathParam("dataToPut") String dataToPut, String body) {
        Put put = new Put(Bytes.toBytes(keyword));
        put.add(Bytes.toBytes("people"),Bytes.toBytes(dataToPut),Bytes.toBytes(body));
        try {
            HBaseConnect("put",null,keyword,put);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Result res = new Result();
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

    public org.apache.hadoop.hbase.client.Result HBaseConnect(String input, Member inputMember, String rowKey,Put inputPut) throws IOException {

        System.out.println("Trying to establish HBase connection...");
        Configuration hBaseConfig = HBaseConfiguration.create();
        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        System.out.println("HBase Connection succeded...");
        HTable table = new HTable(hBaseConfig, "Member");
        if (input.equals("post")) {
            table.put(postH(inputMember.getName(), String.valueOf(inputMember.getAge()), inputMember.getSex(), inputMember.getPhoneNumber(), inputMember.getEmail()));
            table.close();
        } else if (input.equals("get")) {
            Get getIn = getH(rowKey);
            org.apache.hadoop.hbase.client.Result resName = table.get(getIn);
            return resName;
        } else if (input.equals("put")) {
            table.put(inputPut);
        } else if (input.equals("delete")) {
            Delete delH = new Delete(Bytes.toBytes(rowKey));
            table.delete(delH);
            table.close();
        }
        return null;
    }

    public Put postH(String inputName, String inputAge, String inputSex, String inputPhoneNumber, String inputEmail) {
        Main.row += 1;
        Put input = new Put(Bytes.toBytes(String.valueOf(Main.row)));
        input.addColumn(Bytes.toBytes("people"), Bytes.toBytes("name"), Bytes.toBytes(inputName));
        input.addColumn(Bytes.toBytes("people"), Bytes.toBytes("sex"), Bytes.toBytes(inputSex));
        input.addColumn(Bytes.toBytes("people"), Bytes.toBytes("age"), Bytes.toBytes(inputAge));
        input.addColumn(Bytes.toBytes("people"), Bytes.toBytes("phoneNumber"), Bytes.toBytes(inputPhoneNumber));
        input.addColumn(Bytes.toBytes("people"), Bytes.toBytes("email"), Bytes.toBytes(inputEmail));
        return input;
    }

    public Get getH(String inputRow) {
        Get get = new Get(Bytes.toBytes((inputRow)));
        get.addColumn(Bytes.toBytes("people"), Bytes.toBytes("name"));
        get.addColumn(Bytes.toBytes("people"), Bytes.toBytes("age"));
        get.addColumn(Bytes.toBytes("people"), Bytes.toBytes("sex"));
        get.addColumn(Bytes.toBytes("people"), Bytes.toBytes("phoneNumber"));
        get.addColumn(Bytes.toBytes("people"), Bytes.toBytes("email"));
        return get;
    }

}




