package com.google.maps.android.geojsonkmlabs.geojson;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.geojsonkmlabs.Feature;
import com.google.maps.android.geojsonkmlabs.Geometry;
import com.google.maps.android.geojsonkmlabs.Renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Renders GeoJsonFeature objects onto the GoogleMap as Marker, Polyline and Polygon objects. Also
 * removes GeoJsonFeature objects and redraws features when updated.
 */
/* package */ class GeoJsonRenderer extends Renderer implements Observer {

    private final static Object FEATURE_NOT_ON_MAP = null;


    /**
     * Creates a new GeoJsonRender object
     *
     * @param map map to place GeoJsonFeature objects on
     * @param features
     */
    /* package */ GeoJsonRenderer(GoogleMap map, HashMap<? extends Feature, Object> features) {
        super(map, features);

    }


    /**
     * Changes the map that GeoJsonFeature objects are being drawn onto. Existing objects are
     * removed from the previous map and drawn onto the new map.
     *
     * @param map GoogleMap to place GeoJsonFeature objects on
     */
    /* package */
    public void setMap(GoogleMap map) {
        super.setMap(map);
        for (Feature feature : super.getFeatures()) {
            redrawFeatureToMap((GeoJsonFeature)feature, map);
        }
    }

    /**
     * Adds all of the stored features in the layer onto the map if the layer is not already on the
     * map.
     */
    /* package */ void addLayerToMap() {
        if (!isLayerOnMap()) {
            setLayerVisibility(true);
            for (Feature feature : super.getFeatures()) {
                addFeature((GeoJsonFeature)feature);
            }
        }
    }


    /**
     * Adds a new GeoJsonFeature to the map if its geometry property is not null.
     *
     * @param feature feature to add to the map
     */
    /* package */ void addFeature(GeoJsonFeature feature) {
        super.addFeature(feature);
        if (isLayerOnMap()) {
            feature.addObserver(this);
        }
    }

    /**
     * Removes all GeoJsonFeature objects stored in the mFeatures hashmap from the map
     */
    /* package */ void removeLayerFromMap() {
        if (isLayerOnMap()) {
            for (Feature feature : super.getFeatures()) {
                removeFromMap(super.getAllFeatures().get(feature));
                feature.deleteObserver(this);
            }
            setLayerVisibility(false);
        }
    }

    /**
     * Removes a GeoJsonFeature from the map if its geometry property is not null
     *
     * @param feature feature to remove from map
     */
    /* package */ void removeFeature(GeoJsonFeature feature) {
        // Check if given feature is stored
        super.removeFeature(feature);
        if (super.getFeatures().contains(feature)) {
            feature.deleteObserver(this);
        }
    }


    /**
     * Redraws a given GeoJsonFeature onto the map. The map object is obtained from the mFeatures
     * hashmap and it is removed and added.
     *
     * @param feature feature to redraw onto the map
     */
    private void redrawFeatureToMap(GeoJsonFeature feature) {
        redrawFeatureToMap(feature, getMap());
    }

    private void redrawFeatureToMap(GeoJsonFeature feature, GoogleMap map) {
        removeFromMap(getAllFeatures().get(feature));
        putFeatures(feature, FEATURE_NOT_ON_MAP);
        if (map != null && feature.hasGeometry()) {
            putFeatures(feature, addFeatureToMap(feature, feature.getGeometry()));
        }
    }

    /**
     * Update is called if the developer sets a style or geometry in a GeoJsonFeature object
     *
     * @param observable GeoJsonFeature object
     * @param data       null, no extra argument is passed through the notifyObservers method
     */
    public void update(Observable observable, Object data) {
        if (observable instanceof GeoJsonFeature) {
            GeoJsonFeature feature = ((GeoJsonFeature) observable);
            boolean featureIsOnMap = getAllFeatures().get(feature) != FEATURE_NOT_ON_MAP;
            if (featureIsOnMap && feature.hasGeometry()) {
                // Checks if the feature has been added to the map and its geometry is not null
                // TODO: change this so that we don't add and remove
                redrawFeatureToMap(feature);
            } else if (featureIsOnMap && !feature.hasGeometry()) {
                // Checks if feature is on map and geometry is null
                removeFromMap(getAllFeatures().get(feature));
                putFeatures(feature, FEATURE_NOT_ON_MAP);
            } else if (!featureIsOnMap && feature.hasGeometry()) {
                // Checks if the feature isn't on the map and geometry is not null
                addFeature(feature);
            }
        }
    }
}
