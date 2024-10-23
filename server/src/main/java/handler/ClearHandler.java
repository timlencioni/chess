package handler;

import com.google.gson.Gson;
import service.ClearService;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class ClearHandler {

    private final ClearService service;

    public ClearHandler(ClearService service) {
        this.service = service;
    }

    public Object clear(Request request, Response response) {
        try {
            service.clear();
            response.status(200);
            String res = new Gson().toJson("{}");
            response.body(res);
            return res;
        }
        catch (ClearException e){
            response.status(500);
            Gson gson = new Gson();
            HashMap<String, String> exc_map = new HashMap<String, String>();
            exc_map.put("message", e.toString());
            return gson.toJson(exc_map);
        }
    }
}
