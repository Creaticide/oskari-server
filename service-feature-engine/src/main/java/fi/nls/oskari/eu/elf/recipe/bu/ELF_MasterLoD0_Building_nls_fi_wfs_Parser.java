package fi.nls.oskari.eu.elf.recipe.bu;

import java.io.IOException;

import fi.nls.oskari.eu.elf.buildings.ELF_MasterLoD0_Building_nls_fi_wfs.Building;
import fi.nls.oskari.fe.input.format.gml.recipe.JacksonParserRecipe;
import fi.nls.oskari.fe.iri.Resource;

public class ELF_MasterLoD0_Building_nls_fi_wfs_Parser extends JacksonParserRecipe {

    @Override
    public void parse() throws IOException {

        // setLenient(true);

        getGeometryDeserializer().mapGeometryTypes(
                "http://www.opengis.net/gml/3.2", "Polygon", "Surface",
                "PolyhedralSurface", "TriangulatedSurface", "Tin",
                "OrientableSurface", "CompositeSurface");

        FeatureOutputContext outputContext = new FeatureOutputContext(
                Building.QN);

        final Resource geom = outputContext.addDefaultGeometryProperty();
        final Resource gn = outputContext.addOutputProperty("name");
        final Resource beginLifespanVersion = outputContext
                .addOutputStringProperty("beginLifespanVersion");
        final Resource inspireId = outputContext.addOutputProperty("inspireId");
        final Resource endLifespanVersion = outputContext
                .addOutputStringProperty("endLifespanVersion");

        outputContext.build();

        OutputFeature<Building> outputFeature = new OutputFeature<Building>(
                outputContext);

        InputFeature<Building> iter = new InputFeature<Building>(Building.QN,
                Building.class);

        while (iter.hasNext()) {
            Building feature = iter.next();
            Resource output_ID = outputContext.uniqueId(feature.id);

            outputFeature.setFeature(feature).setId(output_ID);

            if (feature.geometry2D != null
                    && feature.geometry2D.BuildingGeometry2D != null
                    && feature.geometry2D.BuildingGeometry2D.geometry != null) {
                outputFeature
                        .addGeometryProperty(
                                geom,
                                feature.geometry2D.BuildingGeometry2D.geometry.geometry);
            }

            outputFeature
                    .addProperty(gn, feature.getName())
                    .addProperty(beginLifespanVersion,
                            feature.beginLifespanVersion)
                    .addProperty(inspireId, feature.inspireId)
                    .addProperty(endLifespanVersion, feature.endLifespanVersion);

            outputFeature.build();

        }

    }

}
