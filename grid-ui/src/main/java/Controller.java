import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ru.foobarbaz.grid.entity.Edge;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.*;
import ru.foobarbaz.grid.transport.TransportConfig;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

public class Controller {
    @FXML
    private ImageView graphImage;
    @FXML
    private TextField verticesField;
    @FXML
    private TextField sourceField;
    @FXML
    private TextField targetField;
    @FXML
    private Label pathLengthLabel;
    @FXML
    private Label executionTimeLabel;

    private static final int SIZE = 1000;
    private VisualizationImageServer<Integer, Edge> visualizationImageServer;
    private DirectedGraph<Integer, Edge> graph;
    private ShortestPathFinder<DirectedGraph<Integer, Edge>, Integer, Edge> pathFinder =
            new GridClientPathFinder<>(
                    "http://foobarbaz.ru:8080/grid-web/api/",
                    TransportConfig.getTaskSerializer(),
                    TransportConfig.getPathDeserializer()
            );
//            new ConcurrencyShortestPathFinder<>(new SimpleShortestPathFinder<>());

    private ToIntFunction<List<Edge>> pathLengthFunction = path -> path.stream().mapToInt(Edge::getWeight).sum();
    private Comparator<List<Edge>> comparator = Comparator.comparingInt(pathLengthFunction);
    private Integer source, target;
    private List<Edge> pathEdges;
    private List<Integer> pathVertices;
    private Long startTime;

    public void loadGraph(){
        if (startTime != null){ alert("The task in progress. Please wait"); return; }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Graph from File");
        File file = fileChooser.showOpenDialog(graphImage.getScene().getWindow());

        if (file == null) return;

        String data;
        try {
            data = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            alert("Error during reading data");
            e.printStackTrace();
            return;
        }

        pathEdges = Collections.emptyList();
        pathVertices = Collections.emptyList();

        try {
            graph = TransportConfig.getGraphDeserializer().apply(data);
        } catch (Exception e){
            alert("Error during de-serializing data");
            e.printStackTrace();
            return;
        }

        source = 1;
        target = graph.getVertexCount();
        sourceField.setText(source.toString());
        targetField.setText(target.toString());
        verticesField.setText(String.valueOf(graph.getVertexCount()));

        initVisualizationServer(graph);
        renderGraphImage();
    }

    public void saveGraph(){
        if (graph == null){
            alert("Nothing to save...");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph to File");
        File file = fileChooser.showOpenDialog(graphImage.getScene().getWindow());

        if (file == null) return;
        String data;
        try {
            data = TransportConfig.getGraphSerializer().apply(graph);
        } catch (Exception e){
            alert("Error during serializing data");
            e.printStackTrace();
            return;
        }

        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            out.print(data);
        } catch (Exception e){
            alert("Error during saving data");
            e.printStackTrace();
        }
    }

    public void generateGraph(){
        if (startTime != null){ alert("The task in progress. Please wait"); return; }

        pathEdges = Collections.emptyList();
        pathVertices = Collections.emptyList();

        try {
            int vertices = Integer.valueOf(verticesField.getText());
            graph = GraphGenerator.generateDirectedGraph(vertices);
        } catch (NumberFormatException e){
            alert("Incorrect number format!");
            return;
        }

        source = 1;
        target = graph.getVertexCount();
        sourceField.setText(source.toString());
        targetField.setText(target.toString());

        initVisualizationServer(graph);
        renderGraphImage();
    }

    public void processTask(){
        if (startTime != null){ alert("Task already in progress"); return; }
        if (graph == null){ alert("Load or generate a graph at first"); return; }

        try {
            source = Integer.valueOf(sourceField.getText());
            target = Integer.valueOf(targetField.getText());
        }  catch (NumberFormatException e){
            alert("Incorrect number format!");
            return;
        }
        if (source == null || target == null) return;

        if (!graph.getVertices().contains(source) || !graph.getVertices().contains(target)) {
            alert("Specified vertices doesn't exist!");
            return;
        }

        startTime = System.currentTimeMillis();
        pathLengthLabel.setText("Computing...");

        javafx.concurrent.Task infoTask = new javafx.concurrent.Task() {
            @Override
            protected Object call() throws Exception {
                while (startTime != null) {
                    long executionTime = System.currentTimeMillis() - startTime;
                    String timeString = Duration.ofMillis(executionTime).toString();
                    updateMessage("Execution Time:\n" + timeString.substring(2, timeString.length() - 1));
                    Thread.sleep(200);
                }
                return null;
            }
        };
        Thread infoThread = new Thread(infoTask);
        executionTimeLabel.textProperty().bind(infoTask.messageProperty());
        infoThread.setDaemon(true);
        infoThread.start();


        javafx.concurrent.Task executionTask = new javafx.concurrent.Task() {
            @Override
            protected Object call() throws Exception {
                pathEdges = pathFinder.getShortestPath(new Task<>(graph, source, target, comparator));
                pathVertices = GraphUtils.pathFromEdgesToVertices(graph, pathEdges);
                return null;
            }
        };
        executionTask.setOnSucceeded(event -> {
            renderGraphImage();
            pathLengthLabel.setText(
                    "Path:\n" + GraphUtils.verticesPathToString(pathVertices) +
                    "\nCost: " + pathLengthFunction.applyAsInt(pathEdges));
            startTime = null;
        });
        Thread executionThread = new Thread(executionTask);
        executionThread.setDaemon(true);
        executionThread.start();
    }

    private void alert(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void renderGraphImage() {
        Dimension imageSize = new Dimension(SIZE + 80, SIZE + 80);
        BufferedImage img = (BufferedImage) visualizationImageServer.getImage(
                new Point2D.Double(imageSize.getWidth() / 2, imageSize.getHeight() / 2),
                imageSize
        );
        graphImage.setImage(SwingFXUtils.toFXImage(img, null));
    }

    private void initVisualizationServer(Graph<Integer, Edge> graph) {
        Layout<Integer, Edge> layout = new FRLayout<>(graph);

        Dimension layoutSize = new Dimension(SIZE, SIZE);
        layout.setSize(layoutSize);
        VisualizationImageServer<Integer, Edge> vis = new VisualizationImageServer<>(layout, layoutSize);
        vis.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vis.getRenderContext().setEdgeLabelTransformer(edge -> Integer.toString(edge == null ? 0 : edge.getWeight()));
        vis.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        vis.getRenderContext().setVertexFillPaintTransformer(v -> {
            if (v == null) return Color.black;
            if (v.equals(source)) return Color.red;
            if (v.equals(target)) return Color.green;
            if (pathVertices.contains(v)) return Color.cyan;
            return Color.white;
        });
        vis.getRenderContext().setEdgeStrokeTransformer(e ->
                pathEdges.contains(e) ? new BasicStroke(3) : new BasicStroke(1));
        vis.getRenderContext().setEdgeDrawPaintTransformer(e ->
                pathEdges.contains(e) ? Color.blue: Color.black);

        visualizationImageServer = vis;
    }
}
