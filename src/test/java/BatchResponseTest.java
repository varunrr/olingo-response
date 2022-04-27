import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BatchResponseTest {

    private static final String SERVICE_BASE_URL = "https://services.odata.org/TripPinRESTierService/(S(varun))/";
    private static ODataClient oDataClient = getODataClient();

    @Test
    public void testBatchFailureResponse() throws Exception{
        ODataBatchRequest batchRequest = oDataClient.getBatchRequestFactory().getBatchRequest(SERVICE_BASE_URL);
        batchRequest.setAccept("multipart/mixed");

        BatchManager batchManager = batchRequest.payloadManager();
        ODataChangeset oDataChangeSet = batchManager.addChangeset();

        for(ODataBatchableRequest batchableRequest : getBatchableRequests()){
            oDataChangeSet.addRequest(batchableRequest);
        }

        ODataBatchResponse response = batchManager.getResponse();
        Iterator<ODataBatchResponseItem> itemIterator = response.getBody();

        while (itemIterator.hasNext()){
            ODataBatchResponseItem responseItem = itemIterator.next();
            if(responseItem.isChangeset()){
                while (responseItem.hasNext()){
                    System.out.println(responseItem.next().getStatusCode());
                }
            }
        }
    }

    private static List<ODataBatchableRequest> getBatchableRequests() {
        List<ODataBatchableRequest> oDataBatchableRequests = new ArrayList<>();
        for(int i = 0; i < 3; i ++){
            oDataBatchableRequests.add(oDataClient
                    .getCUDRequestFactory()
                    .getDeleteRequest(oDataClient.newURIBuilder(getEndPoint()).build()));
        }

        return oDataBatchableRequests;
    }

    private static String getEndPoint(){
        Random random = new Random();
        return  "People(hciTest" + (random.nextInt(90) + 10) + ")";
    }

    private static ODataClient getODataClient(){
        ODataClient oDataClient = ODataClientFactory.getClient();
        oDataClient.getConfiguration().setContinueOnError(true);

        return oDataClient;
    }
}
