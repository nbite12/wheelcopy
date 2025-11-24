package com.carusel.app.manager;

import animatefx.animation.AnimationFX;
import animatefx.animation.RotateIn;
import animatefx.animation.RotateOut;
import com.carusel.app.constants.UserType;
import com.carusel.app.constants.WheelIndex;
import com.carusel.app.factory.AppFactory;
import com.carusel.app.factory.ElementFactory;
import com.carusel.app.factory.NavigationFactory;
import com.carusel.app.model.Database;
import com.carusel.app.model.Element;
import com.carusel.app.model.Navigation;
import com.carusel.app.model.clipboard.ClipboardData;
import com.carusel.app.model.clipboard.ClipboardJson;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WheelManager{
	// Singleton
	private static WheelManager instance;

	public static WheelManager getInstance(){
		if(instance == null){
			synchronized(WheelManager.class){
				if(instance == null){
					instance = new WheelManager();
				}
			}
		}
		return instance;
	}

	// Field
	private final Map<WheelIndex, List<Element>> elementMaps;
	private final List<Navigation> navigations;
	private final ObjectProperty<WheelIndex> currentWheelIndex;
	private final Map<WheelIndex, IntegerProperty> currentElementIndexMap;
	private final Map<WheelIndex, Map<Integer, ObjectProperty<ClipboardData>>> clipboardDataMap;

	// Container
	private final Group wheelContainer;
	private final Group navigationContainer;
	private final Group settingContainer;
	private final Group trialLimitContainer;
	private final StackPane root;

	// Animation
	private final AnimationFX spinWheel;
	private final AnimationFX showWheel;
	private final AnimationFX hideWheel;

	// Constants
	private static final double INNER_RADIUS = 100;
	private static final double OUTER_RADIUS = 250;
	private static final double TRANSLATE = 15;

	// CSS
	private static final String WHEEL_INNER_CIRCLE = "wheel-inner-circle";

	// Constructor
	private WheelManager(){
		this.elementMaps = new HashMap<>();
		this.navigations = new ArrayList<>();
		this.currentWheelIndex = new SimpleObjectProperty<>(WheelIndex.FIRST);
		this.currentElementIndexMap = new HashMap<>();
		// this.clipboardRawMap = new LinkedHashMap<>();

		this.clipboardDataMap = new LinkedHashMap<>();

		// Container
		this.wheelContainer = new Group();
		this.navigationContainer = new Group();
		this.settingContainer = new Group();
		this.trialLimitContainer = new Group();
		this.root = new StackPane();

		// Animation
		this.spinWheel = new RotateIn(wheelContainer);
		this.showWheel = new RotateIn(root);
		this.hideWheel = new RotateOut(root);

		init();
	}

	// Initialize
	private void init(){
		// Dev
		for(var wheelIndex : WheelIndex.values()){
			for(int elementIndex = 0; elementIndex < ElementFactory.TOTAL_ELEMENT; elementIndex++){
				Map<Integer, ObjectProperty<ClipboardData>> map = getClipboardDataMap().computeIfAbsent(
						wheelIndex,
						k -> new LinkedHashMap<>()
				);
				ClipboardData clipboardData = new ClipboardData();
				ObjectProperty<ClipboardData> property = new SimpleObjectProperty<>(clipboardData);
				map.put(elementIndex, property);
			}
		}

		// initClipboardRawMap();
		buildElements();
		buildNavigations();
		buildSetting();
		buildTrialLimit();
		buildRoot();
		initElementIndex();
		listenUserTypeChange();

		LicenseManager licenseManager = LicenseManager.getInstance();
		trialLimitContainer.visibleProperty().bind(licenseManager.trialLimitExceedProperty());

		loadData();
	}

	// Build elements
	private void buildElements(){
		for(WheelIndex wheelIndex : WheelIndex.values()){
			List<Element> elements = new ArrayList<>();
			for(int elementIndex = 0; elementIndex < ElementFactory.TOTAL_ELEMENT; elementIndex++){
				ObjectProperty<ClipboardData> property = getClipboardDataProperty(wheelIndex, elementIndex);
				Element element = ElementFactory.createElement(wheelIndex, elementIndex, property);
				elements.add(element);
			}

			// List<Element> elements = ElementFactory.createElements(wheelIndex, );
			for(Element element : elements){
//                ObjectProperty<ClipboardData> property = getClipboardRaw(wheelIndex, element.getIndex());
//                property.addListener(new ChangeListener<ClipboardData>(){
//                    @Override
//                    public void changed(ObservableValue<? extends ClipboardData> observable, ClipboardData oldValue, ClipboardData newValue){
//                        element.setContent(newValue);
//                    }
//                });

				element.registerListener(new Element.OnSelectedListener(){
					@Override
					public void onSelected(Element selectedElement){
						for(Element e : elementMaps.get(wheelIndex)){
							if(!e.equals(selectedElement)){
								e.unselect();
							}
						}
					}
				});

				element.isPinnedProperty().addListener(new ChangeListener<Boolean>(){
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){
						if(!newValue){
							if(element.getIndex() < getCurrentElementIndex()){
								setCurrentElementIndex(element.getIndex());
							}
						}else{
							if(element.getIndex() == getCurrentElementIndex()){
								nextCurrentElementIndex();
							}
						}
					}
				});
			}
			elementMaps.put(wheelIndex, elements);
		}
	}

	// Build navigations
	private void buildNavigations(){
		navigations.addAll(NavigationFactory.createNavigations());
		navigations.forEach(new Consumer<Navigation>(){
			@Override
			public void accept(Navigation navigation){
				navigation.registerListener(new Navigation.OnSelectedListener(){
					@Override
					public void onSelected(){
						for(Navigation e : navigations){
							if(!e.equals(navigation)){
								e.unselect();
							}
						}
					}
				});
			}
		});
	}

	// Build setting
	private void buildSetting(){
		Group buttonSetting = AppFactory.createSettingButton();
		buttonSetting.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				StageManager.getInstance().startSettingStage();
			}
		});

		settingContainer.getChildren().add(buttonSetting);
	}

	// Build trial limit
	private void buildTrialLimit(){
		Group container = AppFactory.createTrialLimit();
		trialLimitContainer.getChildren().add(container);
	}

	// Build root
	private void buildRoot(){
		wheelContainer.getChildren().add(getWheelContainer());
		navigationContainer.getChildren().addAll(getNavigationAsNodes());

		// Container
		Group container = new Group();
		container.getChildren().add(wheelContainer);
		container.getChildren().add(navigationContainer);
		container.getChildren().add(settingContainer);
		container.getChildren().add(trialLimitContainer);

		StackPane stackPane = new StackPane();
		stackPane.getStyleClass().add("background");
		// stackPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

		// Root
		root.getChildren().add(stackPane);
		root.getChildren().add(container);

		stackPane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				StageManager.getInstance().hideStage();
			}
		});
	}

	// Init - Element index
	private void initElementIndex(){
		for(WheelIndex wheelIndex : WheelIndex.values()){
			currentElementIndexMap.put(wheelIndex, new SimpleIntegerProperty(0));
		}
	}

	// Load data
	private void loadData(){
		DatabaseManager databaseManager = DatabaseManager.getInstance();
		Database database = databaseManager.getDatabase();
		for(var mapEntry : database.getClipboardMap().entrySet()){
			var wheelIndex = mapEntry.getKey();
			for(var mapChild : mapEntry.getValue().entrySet()){
				var elementIndex = mapChild.getKey();
				var value = mapChild.getValue();
				var clipboardJson = value.get();
				if(clipboardJson != null){
					ClipboardData clipboardData = ClipboardJson.getClipboardRaw(clipboardJson);
					addClipboard(wheelIndex, elementIndex, clipboardData);
				}
			}
		}
	}

//    private void initClipboardRawMap(){
//        for(WheelIndex wheelIndex : WheelIndex.values()){
//            for(int index = 0; index < ElementFactory.TOTAL_ELEMENT; index++){
//                Map<Integer, ObjectProperty<ClipboardData>> map = clipboardRawMap.computeIfAbsent(
//                        wheelIndex,
//                        k -> new HashMap<>()
//                );
//                ObjectProperty<ClipboardData> property = new SimpleObjectProperty<>();
//                map.put(index, property);
//            }
//        }
//    }

	private void listenUserTypeChange(){
		DatabaseManager databaseManager = DatabaseManager.getInstance();
		Database database = databaseManager.getDatabase();
		ObjectProperty<UserType> userTypeProperty = database.userTypeProperty();
		userTypeProperty.addListener(new ChangeListener<UserType>(){
			@Override
			public void changed(ObservableValue<? extends UserType> observableValue, UserType oldValue, UserType newValue){
				reset();
				rebuildRoot();
			}
		});
	}

	// *****************************************************************************************
	// *** General *****************************************************************************
	// *****************************************************************************************

	public void unselectAllElements(){
		elementMaps
				.get(currentWheelIndex.get())
				.forEach(Element::unselect);
	}

	private void reset(){
		initElementIndex();
	}

	public void rebuildRoot(){
		elementMaps.clear();
		navigations.clear();

		buildElements();
		buildNavigations();

		wheelContainer.getChildren().clear();
		navigationContainer.getChildren().clear();
		root.getChildren().clear();

		buildRoot();
	}

	// *****************************************************************************************
	// *** Wheel *******************************************************************************
	// *****************************************************************************************

	public void showWheel(){
		showWheel.play();
	}

	public void hideWheel(){
		hideWheel.play();
		unselectAllElements();
	}

	public void changeWheel(WheelIndex wheelIndex){
		navigations.stream()
				.filter(navigation -> !navigation.isLocked())
				.filter(navigation -> navigation.getWheelIndex() == wheelIndex)
				.findFirst()
				.ifPresent(new Consumer<Navigation>(){
					@Override
					public void accept(Navigation navigation){
						navigation.setSelected(true);
						unselectAllElements();
						setCurrentWheelIndex(wheelIndex);

						wheelContainer.getChildren().clear();
						wheelContainer.getChildren().add(getWheelContainer());

						spinWheel.play();
					}
				});
	}

	public void changeToNextWheel(){
		WheelIndex previousWheelIndex = getCurrentWheelIndex();
		WheelIndex currentWheelIndex = WheelIndex.getNextWheelIndex(previousWheelIndex);
		changeWheel(currentWheelIndex);
	}

	public void changeToPreviousWheel(){
		WheelIndex previousWheelIndex = getCurrentWheelIndex();
		WheelIndex currentWheelIndex = WheelIndex.getPreviousWheelIndex(previousWheelIndex);
		changeWheel(currentWheelIndex);
	}

	// *****************************************************************************************
	// *** Container ***************************************************************************
	// *****************************************************************************************

	private Group getWheelContainer(){
		Group container = new Group();
		container.getChildren().addAll(getBaseCircle(), getInnerCircle());
		container.getChildren().addAll(getElementAsNodes());
		return container;
	}

	private Circle getBaseCircle(){
		Circle baseCircle = new Circle(350);
		baseCircle.setFill(Color.TRANSPARENT);
		baseCircle.setDisable(true);
		return baseCircle;
	}

	private Circle getInnerCircle(){
		double radius = INNER_RADIUS + (OUTER_RADIUS - INNER_RADIUS) / 2.0 + TRANSLATE;
		Circle innerCircle = new Circle(radius);
		innerCircle.getStyleClass().add(WHEEL_INNER_CIRCLE);
		innerCircle.setDisable(true);
		return innerCircle;
	}

	private List<Node> getElementAsNodes(){
		return elementMaps
				.get(currentWheelIndex.get())
				.stream()
				.map(Element::getNode)
				.collect(Collectors.toList());
	}

	private List<Node> getNavigationAsNodes(){
		return navigations
				.stream()
				.map(Navigation::getNode)
				.collect(Collectors.toList());
	}

	// *****************************************************************************************
	// *** Clipboard ***************************************************************************
	// *****************************************************************************************

	public void addClipboard(ClipboardData clipboard){
		if(!isDataAlreadyExist(clipboard)){
			DatabaseManager databaseManager = DatabaseManager.getInstance();
			Database database = databaseManager.getDatabase();

			LicenseManager licenseManager = LicenseManager.getInstance();

			// boolean isTrialExceed = licenseManager.getTrialLimitExceed();
			boolean isTrialExceed = false;
			
			if(!isTrialExceed){
				int currentElementIndex = getCurrentElementIndex();

				WheelIndex wheelIndex = getCurrentWheelIndex();

				ClipboardJson clipboardJson = ClipboardJson.getClipboardJson(clipboard);

				database.setClipboard(wheelIndex, currentElementIndex, clipboardJson);
				setClipboardDataDev(wheelIndex, currentElementIndex, clipboard);

				nextCurrentElementIndex();
			}
		}
	}

	public void addClipboard(WheelIndex wheelIndex, int elementIndex, ClipboardData clipboard){
		if(!isDataAlreadyExist(clipboard)){
			LicenseManager licenseManager = LicenseManager.getInstance();
			boolean isTrialExceed = licenseManager.getTrialLimitExceed();
			if(!isTrialExceed){
				setClipboardDataDev(wheelIndex, elementIndex, clipboard);
			}
		}
	}

	private boolean isDataAlreadyExist(final ClipboardData clipboard){
		return getClipboardDataMap()
				.get(getCurrentWheelIndex())
				.values()
				.stream()
				.map(ObservableObjectValue::get)
				.anyMatch(clipboard::equals);
	}

	public void removeClipboard(int elementIndex){
		DatabaseManager databaseManager = DatabaseManager.getInstance();
		Database database = databaseManager.getDatabase();

		WheelIndex wheelIndex = getCurrentWheelIndex();
		database.removeClipboard(wheelIndex, elementIndex);

		setCurrentElementIndex(elementIndex);
	}

//    public void setClipboard(int elementIndex, ClipboardData clipboard){
//        DatabaseManager databaseManager = DatabaseManager.getInstance();
//        Database database = databaseManager.getDatabase();
//        ClipboardJson clipboardJson = ClipboardJson.getClipboardJson(clipboard);
//
//        WheelIndex wheelIndex = getCurrentWheelIndex();
//        database.setClipboard(wheelIndex, elementIndex, clipboardJson);
//        setClipboardData(wheelIndex, elementIndex, clipboard);
//    }

	// *****************************************************************************************
	// *** Field *******************************************************************************
	// *****************************************************************************************

	public StackPane getRoot(){
		return root;
	}

	public List<Element> getElements(){
		WheelIndex wheelIndex = getCurrentWheelIndex();
		return elementMaps.get(wheelIndex);
	}

	public Element getElement(WheelIndex wheelIndex, int elementIndex){
		return elementMaps.get(wheelIndex).get(elementIndex);
	}

	public List<Element> getElementsAvailable(){
		return getElements()
				.stream()
				.filter(new Predicate<Element>(){
					@Override
					public boolean test(Element element){
						return !element.isPinned() && !element.isLocked();
					}
				})
				.collect(Collectors.toList());
	}

	public List<Element> getElementsNotPinned(){
		return getElements()
				.stream()
				.filter(new Predicate<Element>(){
					@Override
					public boolean test(Element element){
						return !element.isPinned();
					}
				})
				.collect(Collectors.toList());
	}

	public List<Element> getElementsNotLocked(){
		return getElements()
				.stream()
				.filter(element -> !element.isLocked())
				.collect(Collectors.toList());
	}

	public WheelIndex getCurrentWheelIndex(){
		return currentWheelIndex.get();
	}

	// Current element index
	private IntegerProperty currentElementIndexProperty(WheelIndex wheelIndex){
		return currentElementIndexMap.get(wheelIndex);
	}

	private IntegerProperty currentElementIndexProperty(){
		WheelIndex wheelIndex = getCurrentWheelIndex();
		return currentElementIndexProperty(wheelIndex);
	}

	private void setCurrentElementIndex(int index){
		currentElementIndexProperty().set(index);
	}

	private int getCurrentElementIndex(){
		IntegerProperty elementIndex = currentElementIndexProperty();
		return elementIndex.get();
	}

	private void nextCurrentElementIndex(){
		// Find empty & not locked lement first
		for(Element element : getElements()){
			if(element.isEmpty() && !element.isLocked()){
				setCurrentElementIndex(element.getIndex());
				return;
			}
		}

		// Prevent there is no available elements
		// or just single element exist
		if(getElementsAvailable().size() <= 1) return;

		// Candidate next element index
		int currentElementIndex = getCurrentElementIndex();
		int nextElementIndex;
		if(currentElementIndex < getElementsNotLocked().size() - 1){
			nextElementIndex = currentElementIndex + 1;
		}else nextElementIndex = 0;

		// Find not pinned element
		int counter = nextElementIndex;
		while(true){
			if(counter == currentElementIndex){
				setCurrentElementIndex(counter);
				break;
			}

			Element element = getElements().get(counter);
			if(!element.isPinned()){
				setCurrentElementIndex(counter);
				break;
			}

			counter++;
		}
	}

	// Current wheel index
	public void setCurrentWheelIndex(WheelIndex currentWheelIndex){
		this.currentWheelIndex.set(currentWheelIndex);
	}

//    public Map<WheelIndex, Map<Integer, ObjectProperty<ClipboardData>>> getClipboardRawMap(){
//        return clipboardRawMap;
//    }
//
//    public ObjectProperty<ClipboardData> getClipboardRaw(WheelIndex wheelIndex, int elementIndex){
//        return clipboardRawMap.get(wheelIndex).get(elementIndex);
//    }
//
//    public void setClipboardData(WheelIndex wheelIndex, int index, ClipboardData value){
//        clipboardRawMap.get(wheelIndex).get(index).set(value);
//    }

	// Clipboard map
	public Map<WheelIndex, Map<Integer, ObjectProperty<ClipboardData>>> getClipboardDataMap(){
		return clipboardDataMap;
	}
	public ObjectProperty<ClipboardData> getClipboardDataProperty(WheelIndex wheelIndex, int elementIndex){
		return clipboardDataMap.get(wheelIndex).get(elementIndex);
	}
	public ClipboardData getClipboardData(WheelIndex wheelIndex, int elementIndex){
		return getClipboardDataProperty(wheelIndex, elementIndex).get();
	}
	public void setClipboardDataDev(WheelIndex wheelIndex, int elementIndex, ClipboardData value){
		clipboardDataMap.get(wheelIndex).get(elementIndex).set(value);
	}
}
