package com.smartstore.probadores.ui.views.sistemadeprobadores;

import com.smartstore.probadores.ui.backend.data.dto.ExchangeType;
import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.microservices.product.services.ProductService;
import com.smartstore.probadores.ui.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nd4j.linalg.api.ops.impl.reduce.same.Prod;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@PageTitle("Sistema de probadores")
@Route(value = "hello", layout = MainLayout.class)
/*
@RouteAlias(value = "", layout = MainLayout.class)
*/
public class SistemadeprobadoresView extends HorizontalLayout /*implements HasUrlParameter */{

    private TextField name;
    private Button sayHello;

    private ReaderAntennaInBranch readerAntennaInBranch;
    private ProductService productService;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public SistemadeprobadoresView(ProductService productService) throws Exception {
        this.productService = productService;

        VerticalLayout mainLayout = new VerticalLayout();

        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        mainLayout.add(name, sayHello);

        add(mainLayout);

        add(GetSimilarities(this.productService.findById(1).get()));

        ExchangeType exchangeType = productService.GetExchangeType(40.0);
        System.out.println("Pesos uruguayos: " + df.format(exchangeType.getUruguayanPeso()));
        System.out.println("Pesos argentinos: " + df.format(exchangeType.getArgentinianPeso()));
        System.out.println("Reales brasileros: " + df.format(exchangeType.getBrazilianReal()));
        System.out.println("Dólares: " + df.format(exchangeType.getDollar()));

        Product combination = GetCombination(1);
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
    }

    public VerticalLayout GetSimilarities(Product product) {
        VerticalLayout resultDiv = new VerticalLayout();

        List<Product> similarProducts = this.productService.findByCategoryId(product.getCategoryId());
        similarProducts.removeIf(similarProduct -> Objects.equals(similarProduct.getId(), product.getId()));

        Grid<Product> grid = new Grid<>(Product.class, false);
        grid.addColumn(TemplateRenderer
                .<Product>of("<div><img style='height: auto; max-width: 100%;' src='[[item.imagedata]]' alt='[[item.name]]'/></div>")
                .withProperty("imagedata", item -> getImageAsBase64(item.getPicture()))
                .withProperty("name", item -> item.getId())
        ).setHeader("Imágen");
        grid.addColumn(Product::getDescription).setHeader("Descripción");
        grid.addColumn(Product::getPrice).setHeader("Precio");
        grid.setItems(similarProducts);
        grid.setHeight("40px");

        resultDiv.add(new Text("Productos similares:"), grid);

        return resultDiv;
    }

    private String getImageAsBase64(byte[] string) {
        String mimeType = "image/png";
        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(string);
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

            int prediction  = (int)targetFunction.classifyInstance(unlabeledInstances.get(0));

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
        return  instance;
    }

/*    @Override
    public void setParameter(BeforeEvent beforeEvent, Object o) {
        readerAntennaInBranch = (ReaderAntennaInBranch) o;
        System.out.println("readerAntennaInBranch = " + readerAntennaInBranch);
    }*/

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
