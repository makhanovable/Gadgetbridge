package dev.makhanov.test.service.devices.qhybrid.requests.fossil_hr.json;

import org.json.JSONObject;

import dev.makhanov.test.service.devices.qhybrid.adapter.fossil_hr.FossilHRWatchAdapter;
import dev.makhanov.test.service.devices.qhybrid.requests.fossil_hr.file.FilePutRawRequest;

public class JsonPutRequest extends FilePutRawRequest {
    public JsonPutRequest(JSONObject object, FossilHRWatchAdapter adapter) {
        super((short)(0x0500 | (adapter.getJsonIndex() & 0xFF)), object.toString().getBytes(), adapter);
    }
}
