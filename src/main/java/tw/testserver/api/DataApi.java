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
import java.util.Objects;

@Path("data")
public class DataApi {
    private final Gson gson = new Gson();
    private Result res = new Result();

    @POST
    public Response post(String body) throws IOException {
        Member mem = gson.fromJson(body, Member.class);
        hbaseConnect("post", mem, "", null);
        System.out.println(gson.toJson(mem));
        return Response.ok().entity(gson.toJson(mem)).build();
    }


    @Path("{keyword}")
    @GET
    public Response get(@PathParam("keyword") String keyword) {

        org.apache.hadoop.hbase.client.Result getBack = null;
        try {
            getBack = hbaseConnect("get", null, keyword, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] valName = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("name"));
        byte[] valAge = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("age"));
        byte[] valSex = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("sex"));
        byte[] valPhoneNumber = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("phoneNumber"));
        byte[] valEmail = getBack.getValue(Bytes.toBytes("people"), Bytes.toBytes("email"));
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
    public Response scan(@PathParam("startRow") String startRow,@PathParam("stopRow") String stopRow) throws IOException {
        Configuration hBaseConfig = HBaseConfiguration.create();
        String output = "";
        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        HTable table = new HTable(hBaseConfig, "Member");
        try {
            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes("people"));
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(stopRow));
            ResultScanner scanner = table.getScanner(scan);
            for(org.apache.hadoop.hbase.client.Result result : scanner){
                output=output+result+"\n";
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.ok().entity(output).build();

    }
    public org.apache.hadoop.hbase.client.Result hbaseConnect(String input, Member inputMember, String rowKey, Put inputPut) throws IOException {

        Configuration hBaseConfig = HBaseConfiguration.create();
        hBaseConfig.set("hbase.zookeeper.quorum", "nqmi26");
        HTable table = new HTable(hBaseConfig, "Member");
        org.apache.hadoop.hbase.client.Result checkRow = table.getRowOrBefore(Bytes.toBytes("32767"),Bytes.toBytes("people"));
        Main.row=Integer.parseInt(Bytes.toString(checkRow.getRow()));
        switch (input) {
            case "post":
                table.put(postH(inputMember.getName(), String.valueOf(inputMember.getAge()), inputMember.getSex(), inputMember.getPhoneNumber(), inputMember.getEmail()));
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




