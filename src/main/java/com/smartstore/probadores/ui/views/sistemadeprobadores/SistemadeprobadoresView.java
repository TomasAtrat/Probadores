package com.smartstore.probadores.ui.views.sistemadeprobadores;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.microservices.product.services.ProductService;
import com.smartstore.probadores.ui.backend.microservices.reader.components.ReaderMaster;
import com.smartstore.probadores.ui.views.MainLayout;
import com.smartstore.probadores.ui.views.utils.InMemoryVariables;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.codec.binary.Base64;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.vaadin.flow.component.Unit.PIXELS;

@PageTitle("Sistema de probadores")
@Route(value = "hello", layout = MainLayout.class)
public class SistemadeprobadoresView extends VerticalLayout {

    public static TextField name;
    private Button sayHello;
    public static Carousel carousel;

    private ReaderAntennaInBranch readerAntennaInBranch;
    private ProductService productService;

    public SistemadeprobadoresView(ProductService productService) throws Exception {
        this.productService = productService;

        carousel = new Carousel();

        Board board = new Board();
        board.addRow(carousel);

        VerticalLayout mainLayout = new VerticalLayout();

        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        //setVerticalComponentAlignment(Alignment.END, name, sayHello);

        mainLayout.add(name, sayHello);

        board.addRow(mainLayout);

        add(board);

        Product combination = GetCombination(2);
        if (combination != null) {
            VerticalLayout combinationLayout = new VerticalLayout();

            Text combinationText = new Text("Combinación sugerida:");
            combinationLayout.add(combinationText);

            TextField productDescription = new TextField("Descripción:");
            productDescription.setValue(combination.getDescription() != null ? combination.getDescription() : "No disponible");
            productDescription.setReadOnly(true);
            combinationLayout.add(productDescription);

            TextField productPrice = new TextField("Precio:");
            productPrice.setValue(combination.getPrice() != null ? combination.getPrice().toString() : "No disponible");
            productPrice.setReadOnly(true);
            combinationLayout.add(productPrice);

            add(combinationLayout);

        }
        configureReader();
    }

    public static void addImages(List<byte[]> images) {
        List<Slide> slides = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            Slide slideImg = new Slide(createSlideContent(images.get(i)));
            slides.add(slideImg);
        }

        carousel.setSlides(slides.toArray(new Slide[0]));

        carousel.withAutoProgress();
        carousel.withSlideDuration(2);
        carousel.withStartPosition(1);
        carousel.withoutSwipe();
        carousel.setWidthFull();
    }

    public static Component createSlideContent(byte[] img) {
        VerticalLayout d = new VerticalLayout();
        d.setAlignItems(Alignment.CENTER);
        d.setBoxSizing(BoxSizing.CONTENT_BOX);
        d.setMaxHeight(500, PIXELS);

        String url = "data:"+"image/jpeg"+";base64," + Base64.encodeBase64String(img);
        Image image = new Image(url, "");
        image.setMaxHeight(400, PIXELS);
        image.setMaxWidth(800, PIXELS);
        d.add(image);
        return d;
    }

    private void configureReader() {
        readerAntennaInBranch = InMemoryVariables.readerAntennaInBranch;

        System.out.println("readerAntennaInBranch = " + readerAntennaInBranch);

        try {
            ReaderMaster readerMaster = new ReaderMaster(readerAntennaInBranch, productService);
            readerMaster.setUI(UI.getCurrent());
            readerMaster.configureReader();
            readerMaster.startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Product GetCombination(int productId) throws Exception {

        Product result = null;
        Product product = null;
        if (productService.findById(productId).isPresent()) {
            product = productService.findById(productId).get();
        }

        if (product != null) {
            List<Label> labelList = getLabels();

            ArrayList<Attribute> attributes = new ArrayList<>();
            Attribute categoryAttribute = new Attribute("categoryFeature");
            attributes.add(categoryAttribute);
            Attribute modelAttribute = new Attribute("modelFeature");
            attributes.add(modelAttribute);
            Attribute suggestAttribute = new Attribute("suggestLabel");
            attributes.add(suggestAttribute);

            Instances trainingDataset = new Instances("trainData", attributes, 5000);
            trainingDataset.setClassIndex(2);

            for (Label label : labelList) {
                trainingDataset.add(instanceMaker(categoryAttribute,
                        modelAttribute,
                        suggestAttribute,
                        label.categoryLabel,
                        label.modelLabel,
                        label.suggestLabel));
            }

            Classifier targetFunction = new SimpleLinearRegression();
            targetFunction.buildClassifier(trainingDataset);

            Instances unlabeledInstances = new Instances("predictionset", attributes, 1);
            unlabeledInstances.setClassIndex(2);
            Instance unlabeled = new DenseInstance(3);
            unlabeled.setValue(categoryAttribute, product.getCategoryId().getId());
            unlabeled.setValue(modelAttribute, product.getModelId().getId());
            unlabeledInstances.add(unlabeled);

            int prediction = (int) targetFunction.classifyInstance(unlabeledInstances.get(0));

            Evaluation evaluation = new Evaluation(trainingDataset);
            evaluation.evaluateModel(targetFunction, trainingDataset);

            if (productService.findById(prediction).isPresent()) {
                result = productService.findById(prediction).get();
            }
        }

        return result;
    }

    public List<Label> getLabels() {
        List<Label> result = new ArrayList<>();

        Path pathToFile = Paths.get("src/main/resources/data/linear_regression_data.csv");

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            String line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(",");
                Label label = new Label(Double.parseDouble(attributes[0]),
                        Double.parseDouble(attributes[1]),
                        Double.parseDouble(attributes[2]));

                result.add(label);

                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public Instance instanceMaker(Attribute categoryAttribute, Attribute modelAttribute, Attribute suggestAttribute,
                                  double number1, double number2, double number3) {
        Instance instance = new DenseInstance(3);
        instance.setValue(categoryAttribute, number1);
        instance.setValue(modelAttribute, number2);
        instance.setValue(suggestAttribute, number3);
        return instance;
    }

    public class Label {
        public double categoryLabel;
        public double modelLabel;
        public double suggestLabel;

        public Label(double categoryLabel, double modelLabel, double suggestLabel) {
            this.categoryLabel = categoryLabel;
            this.modelLabel = modelLabel;
            this.suggestLabel = suggestLabel;
        }
    }

}
