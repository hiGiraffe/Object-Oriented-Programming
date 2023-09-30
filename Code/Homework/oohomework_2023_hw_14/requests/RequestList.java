package requests;

import java.util.ArrayList;

public class RequestList {
    private ArrayList<Request> requests;

    public RequestList() {
        requests = new ArrayList<>();
    }

    public void addRequest(Request request) {
        this.requests.add(request);
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }
}
