import edu.uci.ics.jung.graph.Graph;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.ShortestPathFinder;
import ru.foobarbaz.grid.transport.PathDeserializer;
import ru.foobarbaz.grid.transport.TaskSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class GridClientPathFinder<G extends Graph<V, E>, V ,E> implements ShortestPathFinder<G, V, E> {

    private final String serverUrl;
    private final TaskSerializer<G, V, E> taskSerializer;
    private final PathDeserializer<G, V, E> pathDeserializer;

    public GridClientPathFinder(String serverUrl, TaskSerializer<G, V, E> taskSerializer, PathDeserializer<G, V, E> pathDeserializer) {
        this.serverUrl = serverUrl;
        this.taskSerializer = taskSerializer;
        this.pathDeserializer = pathDeserializer;
    }

    @Override
    public List<E> getShortestPath(Task<G, V, E> task) {
        String taskStr = taskSerializer.apply(task);
        String pathStr = callService(taskStr);
        return pathStr == null ? null : pathDeserializer.apply(task.getGraph(), pathStr);
    }

    private String callService(String request){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(serverUrl);
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
