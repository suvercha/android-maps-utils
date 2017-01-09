package com.google.maps.android.geojsonkmlabs;

import android.util.Log;

import com.google.maps.android.geojsonkmlabs.geojson.GeoJsonGeometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiGeometry implements Geometry {

    private String geometryType = "MultiGeometry";

    private List<Geometry> mGeometries;

    /**
     * Creates a new MultiGeometry object
     * @param geometries contains list of Polygons, Linestrings or Points
     */
    public MultiGeometry(List<?> geometries) {
        if (geometries == null) {
            throw new IllegalArgumentException("Geometries cannot be null");
        }

        //convert unknown geometry type (due to GeoJSON types) to Geometry type
        ArrayList geometriesList = new ArrayList();
        Iterator<?> geometriesIterator = geometries.iterator();
        while (geometriesIterator.hasNext()) {
            Geometry geometry = (Geometry) geometriesIterator.next();
            geometriesList.add(geometry);
        }

        mGeometries = geometriesList;
    }

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    public String getGeometryType() {
        return geometryType;
    }

    /**
     * Gets the stored geometry object
     *
     * @return geometry object
     */
    public List<Geometry> getGeometryObject() {
        return mGeometries;
    }

    /**
     * Set the type of geometry
     *
     * @param type String describing type of geometry
     */
    public void setGeometryType(String type) {
        geometryType = type;
    }

    /**
     * Gets the type of geometry. The type of geometry conforms to the GeoJSON 'type'
     * specification.
     *
     * @return type of geometry
     */
    public String getType() {
        return getGeometryType();
    }

    @Override
    public String toString() {
        String typeString = "Geometry Coordinates";
        if (geometryType.equals("MultiPoint")) {
            typeString = "LineStrings=";
        }
        if (geometryType.equals("MultiLineString")) {
            typeString = "points=";
        }
        if (geometryType.equals("MultiPolygon")) {
            typeString = "Polygons=";
        }

        StringBuilder sb = new StringBuilder(getGeometryType()).append("{");
        sb.append("\n=" + typeString).append(getGeometryObject());
        sb.append("\n}\n");
        return sb.toString();
    }
}
