/*import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);

    }

     @Override
     public void start(Stage primaryStage) throws Exception {
         FXMLLoader loader = new FXMLLoader();
         loader.setLocation(getClass().getResource("Windows/MainWindow.fxml"));
         Parent root = loader.load();
         primaryStage.setScene(new Scene(root, 800, 450));
         primaryStage.setResizable(false);
         primaryStage.setTitle("ТЕЛЕФОННАЯ КНИГА");
         primaryStage.show();
         MainWindow controller = loader.getController();
         primaryStage.setOnCloseRequest(controller.getCloseEventHandler());
     }
    public boolean[][] transMatr(double[][] matr) {
        boolean[][] hasLinkMatr = new boolean[matr.length][matr[0].length];
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[0].length; j++) {
                hasLinkMatr[i][j] = matr[i][j] != 0;
            }
        }
        return hasLinkMatr;
    }

    public void drawStates(Group root, List<String> names) {
        int size = names.size();
        List<Circle> circles = new ArrayList<>();
        List<Text> namesToShow = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            var circle = new Circle(120, 70 + 80 * i, 20);
            var name = new Text(170, 70 + 80 * i, names.get(i));
            circles.add(circle);
            namesToShow.add(name);
        }
        root.getChildren().addAll(circles);
        root.getChildren().addAll(namesToShow);
    }

    public void drawUpArrow(Group root, double lineX, double lineY)
    {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(lineX + 3, lineY + 20,
                lineX - 7, lineY + 15,
                lineX + 3, lineY + 10);
        root.getChildren().add(polygon);
    }

    public void drawDownArrow(Group root, double lineX, double lineY)
    {
        var polygon = new Polygon();
        polygon.getPoints().addAll(lineX, lineY,
                lineX - 10, lineY - 5,
                lineX, lineY - 10);
        root.getChildren().add(polygon);
    }

    public void drawProb(Group root, double value, double x, double y)
    {
        var prob = new Text(x, y, String.valueOf(value));
        prob.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        root.getChildren().add(prob);
    }

    public void drawLinks(Group root, double[][] matr) {
        Path path = new Path();
        var boolMatr = transMatr(matr);
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[0].length; j++) {
                if (boolMatr[i][j]) {
                    if (i != j) {
                        int stateDiff = Math.abs(i - j);
                        var moveTo = new MoveTo(100, 70 + 80 * i);
                        var curX = moveTo.getX();
                        var curY = moveTo.getY();
                        path.getElements().add(moveTo);
                        var curve = new CubicCurveTo(curX + 50, curY - 20 - 2 * stateDiff,
                                curX - 50 * stateDiff, curY + 30 * stateDiff, curX, curY + 82 * stateDiff);
                        path.getElements().add(curve);
                        if (i < j) {
                            drawDownArrow(root, curX, curY + 82 * stateDiff);
                            drawProb(root, matr[i][j], curX - 5 - 18 * stateDiff, curY + 70 * stateDiff);

                        }
                        else {
                            drawUpArrow(root, curX, curY - 82 * stateDiff);
                            drawProb(root, matr[i][j], curX - 3 * stateDiff, curY - 50 * stateDiff);
                        }
                    }
                    else
                    {
                        var moveTo = new MoveTo(120, 65 + 80 * i);
                        var curX = moveTo.getX();
                        var curY = moveTo.getY();
                        path.getElements().add(moveTo);
                        var curve = new CubicCurveTo(curX + 20, curY - 40,
                                curX - 20, curY - 50, curX + 2, curY - 3);
                        path.getElements().add(curve);
                        drawDownArrow(root, curX, curY - 13);
                        drawProb(root, matr[i][j], curX + 15, curY - 20);
                    }
                }
            }
        }
        root.getChildren().addAll(path);
    }

    @Override
    public void start(Stage stage) {
        List<String> namesArr = List.of("Asymptomatic", "Symptomatic", "Death");
        double[][] matr = {{0.44, 0.55, 0.01}, {0.1, 0.8, 0.1}, {0, 0, 1}};
        Group root = new Group();
        drawStates(root, namesArr);
        drawLinks(root, matr);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 500);

        //Setting title to the Stage
        stage.setTitle("Model");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

}
*/
package org.openjfx.run;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException, URISyntaxException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/org/openjfx/view/MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Анализ эксперимента с шоколадом");
        try {
            primaryStage.getIcons().add(new
                    Image(getClass().getResource("/org/openjfx/resources/images/MainIcon.png").toURI(
            ).toString()));
        } catch (NullPointerException e) {
            System.out.println("Ошибка при попытке запустить приложение. Проверьте наличие файла MainIcon.png в папке src");
        }
        primaryStage.show();
        loader.getController();
    }
}