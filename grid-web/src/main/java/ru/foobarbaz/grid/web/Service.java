package ru.foobarbaz.grid.web;

import ru.foobarbaz.grid.entity.Edge;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.ConcurrencyShortestPathFinder;
import ru.foobarbaz.grid.logic.ShortestPathFinder;
import ru.foobarbaz.grid.logic.SimpleShortestPathFinder;
import ru.foobarbaz.grid.transport.PathSerializer;
import ru.foobarbaz.grid.transport.TaskDeserializer;
import ru.foobarbaz.grid.transport.TransportConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Path("api")
public class Service {
    private ShortestPathFinder pathFinder = new ConcurrencyShortestPathFinder(new SimpleShortestPathFinder());
    private TaskDeserializer taskDeserializer = TransportConfig.getTaskDeserializer();
    private PathSerializer pathSerializer = TransportConfig.getPathSerializer();
    private Comparator<List<Edge>> comparator =
            Comparator.comparingInt(path -> path.stream().mapToInt(Edge::getWeight).sum());

    @Path("/")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response findPath(String serializedTask){
        try {
            Task task = taskDeserializer.apply(serializedTask);
            task.setPathComparator(comparator);
            List path = pathFinder.getShortestPath(task);
            String serializedPath = pathSerializer.apply(task.getGraph(), path);
            return Response.status(OK).entity(serializedPath).build();
        } catch (RuntimeException e) {
            return Response.status(BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
