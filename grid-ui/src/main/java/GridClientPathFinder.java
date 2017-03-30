import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.ShortestPathFinder;
import ru.foobarbaz.grid.transport.PathDeserializer;
import ru.foobarbaz.grid.transport.TaskSerializer;
import ru.foobarbaz.grid.transport.TransportConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class GridClientPathFinder implements ShortestPathFinder {

    private static final String SERVER_URL = "http://foobarbaz.ru:8080/grid-web/api/";
    private TaskSerializer taskSerializer = TransportConfig.getTaskSerializer();
    private PathDeserializer pathDeserializer = TransportConfig.getPathDeserializer();

    @Override
    public List getShortestPath(Task task) {
        String taskStr = taskSerializer.apply(task);
        String pathStr = callService(taskStr);
        return pathStr == null ? null : pathDeserializer.apply(task.getGraph(), pathStr);
    }

    private String callService(String request){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(SERVER_URL);
            postRequest.setEntity(new StringEntity(request));

            System.out.println("Post request:\n" + request);
            HttpResponse response = httpClient.execute(postRequest);
            String responseBody =  new BufferedReader(new InputStreamReader((response.getEntity().getContent())))
                    .lines().collect(Collectors.joining("\n"));
            System.out.println("Get response:\n" + responseBody);
            httpClient.getConnectionManager().shutdown();

            switch (response.getStatusLine().getStatusCode()){
                case HttpURLConnection.HTTP_NO_CONTENT: return null;
                case HttpURLConnection.HTTP_OK: return responseBody;
                default: throw new RuntimeException("Failed : " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
