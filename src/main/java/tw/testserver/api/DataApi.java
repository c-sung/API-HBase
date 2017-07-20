package tw.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import tw.testserver.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Path("data")
public class DataApi {
    private final Gson gson = new Gson();
    private Result res = new Result();
    Configuration hBaseConfig = HBaseConfiguration.create();
    byte[] people=Bytes.toBytes("people");
    byte[] name=Bytes.toBytes("name");
    byte[] age=Bytes.toBytes("age");
    byte[] sex=Bytes.toBytes("sex");
    byte[] email=Bytes.toBytes("email");
    byte[] pN=Bytes.toBytes("phoneNumber");

    @Path("{rowKey}")
    @POST
    public Response post( @PathParam("rowKey") String rowKey,String body) throws IOException {
        System.out.println(rowKey);
        org.apache.hadoop.hbase.client.Result getBack = new org.apache.hadoop.hbase.client.Result();
        try {
            getBack = hbaseConnect("get", null, rowKey, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] valName = getBack.getValue(people, name);
        System.out.println(Bytes.toString(valName));
        if (Bytes.toString(valName)==null) {
            Member mem = gson.fromJson(body, Member.class);
            hbaseConnect("post", mem, rowKey, null);
            System.out.println(gson.toJson(mem));
            return Response.ok().entity(gson.toJson(mem)).build();
        }
        res.setAns("NO");
        return Response.ok().entity(gson.toJson(res)).build();


    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {

        org.apache.hadoop.hbase.client.Result getBack = new org.apache.hadoop.hbase.client.Result();
        try {
            getBack = hbaseConnect("get", null, keyword, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] valName = getBack.getValue(people, name);
        byte[] valAge = getBack.getValue(people, age);
        byte[] valSex = getBack.getValue(people, sex);
        byte[] valPhoneNumber = getBack.getValue(people, pN);
        byte[] valEmail = getBack.getValue(people, email);
        if (Bytes.toString(valName)!="null") {
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
            hbaseConnect("delete", null, keyword, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.setAns("OK");
        return Response.ok().entity(gson.toJson(res)).build();
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword, @PathParam("dataToPut") String dataToPut, String body) {
        Put put = new Put(Bytes.toBytes(keyword));
        put.add(Bytes.toBytes("people"), Bytes.toBytes(dataToPut), Bytes.toBytes(body));
        try {
            hbaseConnect("put", null, keyword, put);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.setAns("OK");
        return Response.ok().entity(gson.toJson(res)).build();
    }

    @Path("{startRow}/{stopRow}")
    @GET
    public Response scan(@PathParam("startRow") String startRow, @PathParam("stopRow") String stopRow) throws IOException {
        ArrayList<Result> results = new ArrayList<>();
        Scan s = new Scan();
        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        HTable table = new HTable(hBaseConfig, "Member");
        s.setTimeRange(Long.parseLong(startRow),Long.parseLong(stopRow));
        ResultScanner rs = table.getScanner(s);
        for (org.apache.hadoop.hbase.client.Result result = rs.next();result!=null;result=rs.next()) {
            byte[] valName = result.getValue(people, name);
            byte[] valAge = result.getValue(people, age);
            byte[] valSex = result.getValue(people, sex);
            byte[] valPhoneNumber = result.getValue(people, pN);
            byte[] valEmail = result.getValue(people, email);
            if (!Objects.equals(Bytes.toString(valName), null)) {
                Member mem = new Member("", "", 0, "", "");
                mem.setName(Bytes.toString(valName));
                mem.setAge(Integer.parseInt(Bytes.toString(valAge)));
                mem.setPhoneNumber(Bytes.toString(valPhoneNumber));
                mem.setSex(Bytes.toString(valSex));
                mem.setEmail(Bytes.toString(valEmail));
                res.setAns("OK");
                res.setMember(mem);
                results.add(res);
                System.out.println(gson.toJson(results));
            } else {
                res.setAns("NO");
                results.add(res);
            }
        }

        return Response.ok().entity(gson.toJson(results)).build();

    }

    private org.apache.hadoop.hbase.client.Result hbaseConnect(String input, Member inputMember, String rowKey, Put inputPut) throws IOException {

        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        HTable table = new HTable(hBaseConfig, "Member");
        switch (input) {
            case "post":
                table.put(postH(inputMember.getName(), String.valueOf(inputMember.getAge()), inputMember.getSex(), inputMember.getPhoneNumber(), inputMember.getEmail(),rowKey));
                table.close();
                break;
            case "get":
                Get getIn = getH(rowKey);
                org.apache.hadoop.hbase.client.Result resName = table.get(getIn);
                table.close();
                return resName;
            case "put":
                table.put(inputPut);
                table.close();
                break;
            case "delete":
                Delete delH = new Delete(Bytes.toBytes(rowKey));
                table.delete(delH);
                table.close();
                break;
        }
        return null;
    }

    private Put postH(String inputName, String inputAge, String inputSex, String inputPhoneNumber, String inputEmail,String rowKey) {
        Put input = new Put(Bytes.toBytes(rowKey));

        input.addColumn(people, name, Bytes.toBytes(inputName));
        input.addColumn(people, sex, Bytes.toBytes(inputSex));
        input.addColumn(people,age, Bytes.toBytes(inputAge));
        input.addColumn(people, pN, Bytes.toBytes(inputPhoneNumber));
        input.addColumn(people, email, Bytes.toBytes(inputEmail));
        return input;
    }

    private Get getH(String inputRow) {
        Get get = new Get(Bytes.toBytes((inputRow)));
        get.addColumn(people, name);
        get.addColumn(people, age);
        get.addColumn(people, sex);
        get.addColumn(people, pN);
        get.addColumn(people, email);
        return get;
    }

}




