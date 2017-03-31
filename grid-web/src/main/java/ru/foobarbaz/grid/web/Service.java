package ru.foobarbaz.grid.web;

import edu.uci.ics.jung.graph.DirectedGraph;
import ru.foobarbaz.grid.broker.GridShortestPathFinder;
import ru.foobarbaz.grid.entity.Edge;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.ShortestPathFinder;
import ru.foobarbaz.grid.transport.PathSerializer;
import ru.foobarbaz.grid.transport.TaskDeserializer;
import ru.foobarbaz.grid.transport.TransportConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.base.Throwables.getRootCause;
import static javax.ws.rs.core.Response.Status.*;

@Path("api")
public class Service {
    private ShortestPathFinder<DirectedGraph<Integer, Edge>, Integer, Edge> pathFinder = new GridShortestPathFinder<>(
            TransportConfig.getTaskSerializer(),
            TransportConfig.getPathDeserializer()
    );
    private TaskDeserializer<DirectedGraph<Integer, Edge>, Integer, Edge> taskDeserializer = TransportConfig.getTaskDeserializer();
    private PathSerializer<DirectedGraph<Integer, Edge>, Integer, Edge> pathSerializer = TransportConfig.getPathSerializer();
    private Comparator<List<Edge>> comparator =
            Comparator.comparingInt(path -> path.stream().mapToInt(Edge::getWeight).sum());

    @Path("/")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response findPath(String serializedTask){
        try {
            Task<DirectedGraph<Integer, Edge>, Integer, Edge> task =
                    taskDeserializer.apply(serializedTask);
            task.setPathComparator(comparator);
            List<Edge> path = pathFinder.getShortestPath(task);
            if (path == null) return Response.status(NO_CONTENT).entity("Path not found").build();

            String serializedPath = pathSerializer.apply(task.getGraph(), path);
            return Response.status(OK).entity(serializedPath).build();
        } catch (RuntimeException e) {
            Throwable rootCause = getRootCause(e);
            Response.Status status = rootCause instanceof RuntimeException ?
                    BAD_REQUEST : INTERNAL_SERVER_ERROR;
            return Response.status(status).entity(formatException(rootCause)).build();
        } catch (Exception e){
            Throwable rootCause = getRootCause(e);
            return Response.status(INTERNAL_SERVER_ERROR).entity(formatException(rootCause)).build();
        }
    }

    private String formatException(Throwable e){
        return e.toString() + "\n\n" +
                Arrays.stream(e.getStackTrace())
                        .limit(5)
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"));
    }
}
