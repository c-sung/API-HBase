package tw.testserver.api;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

@Path("data")
public class DataApi {
    private static final Gson GSON = new Gson();
    private Result res = new Result();
    private static final Configuration hBaseConfig = HBaseConfiguration.create();
    private static final byte[] PEOPLE = Bytes.toBytes("people");
    private static final byte[] NAME = Bytes.toBytes("name");
    private static final byte[] AGE = Bytes.toBytes("age");
    private static final byte[] SEX = Bytes.toBytes("sex");
    private static final byte[] EMAIL = Bytes.toBytes("email");
    private static final byte[] PN = Bytes.toBytes("phoneNumber");

    @Path("{account}")
    @POST
    public Response post(@PathParam("account") String account, String body) throws IOException {
        System.out.println(account);
        org.apache.hadoop.hbase.client.Result getBack = new org.apache.hadoop.hbase.client.Result();
        try {
            getBack = hbaseConnect("get", null, account, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] valName = getBack.getValue(PEOPLE, NAME);
        System.out.println(Bytes.toString(valName));
        if (Bytes.toString(valName) == null) {
            Member mem = GSON.fromJson(body, Member.class);
            hbaseConnect("post", mem, account, null);
            System.out.println(GSON.toJson(mem));
            res.setAns("OK");
            res.setMember(mem);
        } else {
            res.setAns("NO");
        }
        return Response.ok().entity(GSON.toJson(res)).build();
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
        byte[] valName = getBack.getValue(PEOPLE, NAME);
        byte[] valAge = getBack.getValue(PEOPLE, AGE);
        byte[] valSex = getBack.getValue(PEOPLE, SEX);
        byte[] valPhoneNumber = getBack.getValue(PEOPLE, PN);
        byte[] valEmail = getBack.getValue(PEOPLE, EMAIL);
        if (Bytes.toString(valName) != null) {
            Member mem = new Member();
            mem.setName(Bytes.toString(valName));
            mem.setAge(Integer.parseInt(Bytes.toString(valAge)));
            mem.setPhoneNumber(Bytes.toString(valPhoneNumber));
            mem.setSex(Bytes.toString(valSex));
            mem.setEmail(Bytes.toString(valEmail));
            System.out.println(GSON.toJson(mem));
            res.setAns("OK");
            res.setMember(mem);
            System.out.println(GSON.toJson(res));
        } else {
            res.setAns("NO");
        }
        return Response.ok().entity(GSON.toJson(res)).build();
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
        return Response.ok().entity(GSON.toJson(res)).build();
    }

    @Path("{keyword}/{dataToPut}")
    @PUT
    public Response put(@PathParam("keyword") String keyword, @PathParam("dataToPut") String dataToPut, String body) {
        Put put = new Put(Bytes.toBytes(keyword));
        put.add(PEOPLE, Bytes.toBytes(dataToPut), Bytes.toBytes(body));
        try {
            hbaseConnect("put", null, keyword, put);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.setAns("OK");
        return Response.ok().entity(GSON.toJson(res)).build();
    }

    @Path("{startTime}/{stopTime}")
    @GET
    public Response scan(@PathParam("startTime") String startTime, @PathParam("stopTime") String stopTime) throws IOException {
        Scan s = new Scan();
        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        HTable table = new HTable(hBaseConfig, "Member");
        s.setTimeRange(Long.parseLong(startTime), Long.parseLong(stopTime));
        ResultScanner rs = table.getScanner(s);
        ResultArray resultArray = new ResultArray();
        for (org.apache.hadoop.hbase.client.Result result = rs.next(); result != null; result = rs.next()) {
            byte[] valName = result.getValue(PEOPLE, NAME);
            byte[] valAge = result.getValue(PEOPLE, AGE);
            byte[] valSex = result.getValue(PEOPLE, SEX);
            byte[] valPhoneNumber = result.getValue(PEOPLE, PN);
            byte[] valEmail = result.getValue(PEOPLE, EMAIL);
            if (Bytes.toString(valName) != null) {
                Member mem = new Member();
                mem.setName(Bytes.toString(valName));
                mem.setAge(Integer.parseInt(Bytes.toString(valAge)));
                mem.setPhoneNumber(Bytes.toString(valPhoneNumber));
                mem.setSex(Bytes.toString(valSex));
                mem.setEmail(Bytes.toString(valEmail));
                resultArray.setMember(mem);
                resultArray.setAns("OK");
            } else {
                resultArray.setAns("NO");
            }
        }
        return Response.ok().entity(GSON.toJson(resultArray)).build();
    }

    private org.apache.hadoop.hbase.client.Result hbaseConnect(String input, Member inputMember, String rowKey, Put inputPut) throws IOException {

        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        HTable table = new HTable(hBaseConfig, "Member");
        switch (input) {
            case "post":
                table.put(postH(inputMember.getName(), String.valueOf(inputMember.getAge()), inputMember.getSex(), inputMember.getPhoneNumber(), inputMember.getEmail(), rowKey));
                break;
            case "get":
                Get getIn = getH(rowKey);
                org.apache.hadoop.hbase.client.Result resName = table.get(getIn);
                return resName;
            case "put":
                table.put(inputPut);
                break;
            case "delete":
                Delete delH = new Delete(Bytes.toBytes(rowKey));
                table.delete(delH);
                break;
        }
        table.close();
        return null;
    }

    private Put postH(String inputName, String inputAge, String inputSex, String inputPhoneNumber, String inputEmail, String rowKey) {
        Put input = new Put(Bytes.toBytes(rowKey));

        input.addColumn(PEOPLE, NAME, Bytes.toBytes(inputName));
        input.addColumn(PEOPLE, SEX, Bytes.toBytes(inputSex));
        input.addColumn(PEOPLE, AGE, Bytes.toBytes(inputAge));
        input.addColumn(PEOPLE, PN, Bytes.toBytes(inputPhoneNumber));
        input.addColumn(PEOPLE, EMAIL, Bytes.toBytes(inputEmail));
        return input;
    }

    private Get getH(String inputRow) {
        Get get = new Get(Bytes.toBytes((inputRow)));
        get.addColumn(PEOPLE, NAME);
        get.addColumn(PEOPLE, AGE);
        get.addColumn(PEOPLE, SEX);
        get.addColumn(PEOPLE, PN);
        get.addColumn(PEOPLE, EMAIL);
        return get;
    }

}




