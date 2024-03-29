package com.smartstore.probadores.ui.views.sistemadeprobadores;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.smartstore.probadores.ui.backend.data.dto.ExchangeType;
import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.microservices.product.services.ProductService;
import com.smartstore.probadores.ui.backend.microservices.reader.components.ReaderMaster;
import com.smartstore.probadores.ui.backend.microservices.task.services.TaskService;
import com.smartstore.probadores.ui.views.MainLayout;
import com.smartstore.probadores.ui.views.utils.InMemoryVariables;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
import java.util.*;

import static com.vaadin.flow.component.Unit.PIXELS;

@PageTitle("Sistema de probadores")
@Route(value = "hello", layout = MainLayout.class)
public class SistemadeprobadoresView extends VerticalLayout {

    public static final short MAX_HEIGHT = 350;

    public static Carousel carousel;
    public static ComboBox<Product> productComboBox;
    public static ComboBox<String> coloursComboBox;
    public static ComboBox<String> sizesComboBox;
    public static Button orderItemBtn;
    public static TextField uruguayanPesoTxt;
    public static TextField brazilianRealTxt;
    public static TextField argentinianPesoTxt;
    public static TextField americanDollarTxt;
    public static Row carouselRow;
    public static UI ui;


    private TextField productDescription;
    private TextField productPrice;
    private HorizontalLayout combinationAndSimilarProductsLayout;
    private static Grid<Product> similarProductsGrid;
    private ConfirmDialog confirmDialog;

    public static ReaderMaster readerMaster;

    private ReaderAntennaInBranch readerAntennaInBranch;
    private ProductService productService;
    private TaskService taskService;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public SistemadeprobadoresView(ProductService productService, TaskService taskService) throws Exception {
        this.productService = productService;
        this.taskService = taskService;

        readerAntennaInBranch = InMemoryVariables.readerAntennaInBranch;

        carousel = new Carousel();

        carousel.setMaxHeight(MAX_HEIGHT, PIXELS);

        setupOrderItemBtn();

        Board board = new Board();

        VerticalLayout mainLayout = new VerticalLayout();

        setupAvailableProductComboBoxes();

        setMargin(true);

        mainLayout.add(productComboBox, coloursComboBox, sizesComboBox, orderItemBtn);

        VerticalLayout exchangeLayout = createExchangeLayout();

        HorizontalLayout productInfo = new HorizontalLayout(mainLayout, exchangeLayout);

        carouselRow = board.addRow(carousel, productInfo);

        board.addRow(createCombinationAndSimilarProductsLayout());

        add(board);

        configureReader();
    }

    public HorizontalLayout createCombinationAndSimilarProductsLayout() {

        combinationAndSimilarProductsLayout = new HorizontalLayout();

        combinationAndSimilarProductsLayout.setVisible(false);

        Button openSimilarProductsModalBtn = new Button("Ver productos similares");

        openSimilarProductsModalBtn.addClickListener(e -> {
            createSimilarProductsConfirmDialog(productComboBox.getValue());
            confirmDialog.open();
        });

        VerticalLayout similarProdsLayout = new VerticalLayout(new H4("Productos similares"), openSimilarProductsModalBtn);

        combinationAndSimilarProductsLayout.add(createCombinationLayout(), similarProdsLayout);

        combinationAndSimilarProductsLayout.setAlignItems(Alignment.CENTER);

        return combinationAndSimilarProductsLayout;
    }

    public static UI getLayoutUI(){
         return carousel.getUI().get();
    }

    private void createSimilarProductsConfirmDialog(Product product) {
        confirmDialog = new ConfirmDialog();

        confirmDialog.setWidthFull();
        confirmDialog.setMaxHeight(500, PIXELS);

        confirmDialog.setHeader("Productos similares");

        var similarities = getSimilarities(product);

        confirmDialog.add(createSimilarProductsLayout(similarities));

        confirmDialog.setConfirmText("Confirmar");

        confirmDialog.setCancelable(false);

        confirmDialog.setCloseOnEsc(true);
    }

    public VerticalLayout createCombinationLayout() {
        VerticalLayout combinationLayout = new VerticalLayout();

        H4 combinationText = new H4("Combinación sugerida:");
        combinationLayout.add(combinationText);

        productDescription = new TextField("Descripción:");
        productDescription.setReadOnly(true);
        productDescription.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        productPrice = new TextField("Precio:");
        productPrice.setReadOnly(true);
        productPrice.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        combinationLayout.setSpacing(false);

        combinationLayout.add(new HorizontalLayout(productDescription, productPrice));

        return combinationLayout;
    }

    public VerticalLayout createSimilarProductsLayout(List<Product> similarities) {
        VerticalLayout resultDiv = new VerticalLayout();
        similarProductsGrid = new Grid<>(Product.class, false);
        similarProductsGrid.addColumn(TemplateRenderer
                .<Product>of("<div><img style='height: auto; max-width: 100%; max-height: 100px;' src='[[item.imagedata]]' alt='[[item.name]]'/></div>")
                .withProperty("imagedata", item -> getImageAsBase64(item.getPicture()))
                .withProperty("name", item -> item.getId())
        ).setHeader("Imagen");

        similarProductsGrid.addColumn(Product::getDescription).setHeader("Descripción");
        similarProductsGrid.addColumn(Product::getPrice).setHeader("Precio ($UYU)");
        similarProductsGrid.setHeight("400px");

        resultDiv.add(similarProductsGrid);

        System.out.println("similarities.size() = " + similarities.size());

        similarProductsGrid.setItems(similarities);

        return resultDiv;
    }

    public void getCombinationFromProductSelected(int id) {
        try {
            Product combination = GetCombination(id);

            if (combination != null) {
                combinationAndSimilarProductsLayout.setVisible(true);

                productDescription.setValue(combination.getDescription() != null ? combination.getDescription() : "No disponible");

                productPrice.setValue(combination.getPrice() != null ? combination.getPrice().toString() : "No disponible");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupExchange(Double price) {
        try {
            ExchangeType exchangeType = productService.GetExchangeType(price);

            uruguayanPesoTxt.setValue(df.format(exchangeType.getUruguayanPeso()));
            argentinianPesoTxt.setValue(df.format(exchangeType.getArgentinianPeso()));
            brazilianRealTxt.setValue(df.format(exchangeType.getBrazilianReal()));
            americanDollarTxt.setValue(df.format(exchangeType.getDollar()));

        } catch (Exception e) {
            Notification.show("Se produjo un error al obtener el cambio", 5000, Notification.Position.BOTTOM_CENTER);
            e.printStackTrace();
        }
    }

    private VerticalLayout createExchangeLayout() {
        uruguayanPesoTxt = new TextField("Peso uruguayo");
        uruguayanPesoTxt.setReadOnly(true);
        uruguayanPesoTxt.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        argentinianPesoTxt = new TextField("Peso argentino");
        argentinianPesoTxt.setReadOnly(true);
        argentinianPesoTxt.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        brazilianRealTxt = new TextField("Real brasilero");
        brazilianRealTxt.setReadOnly(true);
        brazilianRealTxt.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        americanDollarTxt = new TextField("Dólar americano");
        americanDollarTxt.setReadOnly(true);
        americanDollarTxt.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        VerticalLayout exchangeLayout = new VerticalLayout(uruguayanPesoTxt, argentinianPesoTxt, brazilianRealTxt, americanDollarTxt);
        exchangeLayout.setSpacing(false);

        return exchangeLayout;
    }

    private void setupOrderItemBtn() {
        orderItemBtn = new Button("Pedir prenda");
        orderItemBtn.addClickListener(e -> orderItem());
    }

    private void orderItem() {
        if (productComboBox.getValue() != null && sizesComboBox.getValue() != null && coloursComboBox != null)
            orderItemIfExists();
        else
            Notification.show("Primero debe especificar un producto, talle y color", 5000, Notification.Position.BOTTOM_CENTER);
    }

    private void orderItemIfExists() {
        Product product = productComboBox.getValue();
        String colour = coloursComboBox.getValue();
        String size = sizesComboBox.getValue();
        Long branchId = readerAntennaInBranch.getBranch().getId();

        Barcode barcode = productService.getBarcodeByProductColourAndSize(product, colour, size, branchId);

        if (barcode != null) {
            var answer = taskService.createTaskFromFittingRoom(readerAntennaInBranch.getFittingRoom(),
                    readerAntennaInBranch.getBranch(),
                    barcode);
            if (answer.status == 200)
                Notification.show("Estamos trabajando en tu pedido. En breve te atenderemos", 5000, Notification.Position.MIDDLE);
        } else
            Notification.show("No hay stock del producto del talle y color especificado", 5000, Notification.Position.BOTTOM_CENTER);
    }

    private void setupAvailableProductComboBoxes() {
        productComboBox = new ComboBox<>("Productos");
        coloursComboBox = new ComboBox<>("Colores disponibles");
        sizesComboBox = new ComboBox<>("Talles disponibles");

        productComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                setupExchange(e.getValue().getPrice().doubleValue());
                updateDifferentColoursAndSizes(e.getValue());
                getCombinationFromProductSelected(e.getValue().getIntegerId());
            }
        });
    }

    private void updateDifferentColoursAndSizes(Product product) {
        List<Barcode> productVariants = productService.getProductVariantsInStock(product.getId(), readerAntennaInBranch.getBranch().getId());
        List<String> sizes = productVariants.stream().map(Barcode::getSize).distinct().toList();
        List<String> colours = productVariants.stream().map(Barcode::getColour).distinct().toList();

        sizesComboBox.setItems(sizes);
        coloursComboBox.setItems(colours);
    }

    public static void setProductsToCombobox(List<Product> products) {
        productComboBox.setItems(products);
        productComboBox.setItemLabelGenerator(i -> i.getId() + " - " + i.getDescription());

        addImages(products.stream().map(Product::getPicture).toList());
    }

    public static void addImages(List<byte[]> images) {
        System.out.println("images.size() = " + images.size());

        carouselRow.remove(carouselRow.getComponentAt(0));
        carousel.setHideNavigation(false);

        List<Slide> slides = new ArrayList<>();
        for (byte[] image : images) {
            Slide slideImg = new Slide(createSlideContent(image));
            slides.add(slideImg);
        }

        carousel.setSlides((slides.toArray(new Slide[0])));

        carousel.withAutoProgress();
        carousel.withSlideDuration(3);
        carousel.withStartPosition(0);
        carousel.withoutSwipe();
        carousel.setWidthFull();
        carouselRow.addComponentAtIndex(0, carousel);
    }

    public static Component createSlideContent(byte[] img) {
        VerticalLayout d = new VerticalLayout();
        d.setAlignItems(Alignment.CENTER);
        d.setBoxSizing(BoxSizing.CONTENT_BOX);
        d.setMaxHeight(MAX_HEIGHT, PIXELS);

        String url = "data:" + "image/jpeg" + ";base64," + Base64.getEncoder().encodeToString((img));
        Image image = new Image(url, "");
        image.setMaxHeight(MAX_HEIGHT, PIXELS);
        image.setMaxWidth(700, PIXELS);
        d.add(image);
        return d;
    }

    private void configureReader() {
        try {
            if (readerMaster == null) {
                readerMaster = new ReaderMaster(readerAntennaInBranch, productService);
                readerMaster.setUI(UI.getCurrent());
                readerMaster.configureReader();
                readerMaster.startReader();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Product> getSimilarities(Product product) {
        List<Product> similarProducts = this.productService.findByCategoryId(product.getCategoryId());
        similarProducts.removeIf(similarProduct -> Objects.equals(similarProduct.getId(), product.getId()));

        return similarProducts;
    }

    private String getImageAsBase64(byte[] string) {
        String mimeType = "image/png";
        if (string != null)
            return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(string);
        return "";
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
