package fi.nls.oskari.wmts;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.nls.oskari.wmts.domain.WMTSCapabilitiesLayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.service.capabilities.OskariLayerCapabilitiesHelper;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.wmts.domain.TileMatrix;
import fi.nls.oskari.wmts.domain.TileMatrixSet;
import fi.nls.oskari.wmts.domain.WMTSCapabilities;
import fi.nls.test.util.ResourceHelper;

public class WMTSCapabilitiesParserTest {

    final String capabilitiesInput_ASDI = ResourceHelper.readStringResource("asdi.xml", this);
    final String capabilitiesInput_NLS = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-input-NLS.xml", this);
    final String capabilitiesInput_Tampere = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-input-tampere.xml", this);
    final String capabilitiesInput_Spain = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-input-spain.xml", this);
    final String capabilitiesInput_IS = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-input-is.xml", this);
    final String capabilitiesInput_LMI = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-input-LMI.xml", this);
    final String expectedJSON_NLS = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-expected-results-NLS.json", this);
    final String expectedJSON_tampere = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-expected-results-tampere.json", this);
    final String expectedJSON_Spain = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-expected-results-spain.json", this);
    final String expectedJSON_IS = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-expected-results-is.json", this);

    final String expectedJSON_NLS_taustakartta = ResourceHelper.readStringResource("WMTSCapabilitiesParserTest-expected-single-layer-NLS-taustakartta.json", this);

    @Test
    public void testASDIParsing() throws Exception {
        WMTSCapabilities caps = WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_ASDI);

        Set<String> crss = new HashSet<>();
        crss.add("EPSG:3575");

        OskariLayer layer = new OskariLayer();
        layer.setId(1);
        layer.setName("arcticsdi_wmts");

        JSONObject options = layer.getOptions();
        options.put("format", "REST");
        assertTrue(options.has("format"));

        OskariLayerCapabilitiesHelper.setPropertiesFromCapabilitiesWMTS(caps, layer, crss);

        assertFalse("'format' value should have been removed since the layer doesn't support RESTful WMTS", options.has("format"));
    }

    @Test
    public void testParsingMapServerMultiColonTileMatrix_LMI() throws Exception {
        WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_LMI);
        // Just testing we are not throwing exception for having tilematrix reference of "EPSG:3057:0"
        /*
        Limit of "EPSG:3857:0" on layer:

            <TileMatrixLimits>
                <TileMatrix>EPSG:3857:0</TileMatrix>

        Should map to:

        <TileMatrixSet>
            <ows:Identifier>EPSG:3857</ows:Identifier>
            ...
            <TileMatrix>
                <ows:Identifier>0</ows:Identifier>
         */
    }
    @Test
    public void testAsJSON_NLS() throws Exception {
        WMTSCapabilities caps = WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_NLS);
        JSONObject expected = JSONHelper.createJSONObject(expectedJSON_NLS);
        JSONObject actual = WMTSCapabilitiesParser.asJSON(caps, "http://oskari.testing.fi", "EPSG:3067");
        compareMatrixSets(expected, actual);
    }

    @Test
    public void testAsJSON_Tampere() throws Exception {
        WMTSCapabilities caps = WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_Tampere);
        JSONObject expected = JSONHelper.createJSONObject(expectedJSON_tampere);
        JSONObject actual = WMTSCapabilitiesParser.asJSON(caps, "http://oskari.testing.fi", "EPSG:3067");
        compareMatrixSets(expected, actual);
    }

    @Test
    public void testAsJSON_Spain() throws Exception {
        WMTSCapabilities caps = WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_Spain);
        JSONObject expected = JSONHelper.createJSONObject(expectedJSON_Spain);
        JSONObject actual = WMTSCapabilitiesParser.asJSON(caps, "http://oskari.testing.fi", "EPSG:3067");
        compareMatrixSets(expected, actual);
    }

    @Test
    public void testAsJSON_IS() throws Exception {
        WMTSCapabilities caps = WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_IS);
        JSONObject expected = JSONHelper.createJSONObject(expectedJSON_IS);
        JSONObject actual = WMTSCapabilitiesParser.asJSON(caps, "http://oskari.testing.fi", "EPSG3057");
        compareMatrixSets(expected, actual);
    }

    private void compareMatrixSets(JSONObject expected, JSONObject actual) throws JSONException {
        JSONObject msActual = actual.getJSONObject("matrixSets");
        JSONObject msExpected = expected.getJSONObject("matrixSets");
        Iterator<String> matrixSetIds = msExpected.keys();
        while (matrixSetIds.hasNext()) {
            String id = matrixSetIds.next();
            JSONObject tms1 = msExpected.getJSONObject(id);
            JSONObject tms2 = msActual.getJSONObject(id);
            String identifier1 = tms1.getString("identifier");
            String identifier2 = tms2.getString("identifier");
            assertEquals(identifier1, identifier2);
            String proj1 = tms1.getString("projection");
            String proj2 = tms2.getString("projection");
            assertEquals(proj1, proj2);
            JSONArray matrixIds1 = tms1.getJSONArray("matrixIds");
            JSONArray matrixIds2 = tms2.getJSONArray("matrixIds");
            assertEquals(matrixIds1.length(), matrixIds2.length());
            for (int i = 0; i < matrixIds1.length(); i++) {
                JSONObject matrix1 = matrixIds1.getJSONObject(i);
                JSONObject matrix2 = findByIdentifier(matrixIds2, matrix1.getString("identifier"));
                assertTrue(JSONHelper.isEqual(matrix1, matrix2));
            }
        }
    }

    private JSONObject findByIdentifier(JSONArray matrixIds, String identifier) throws JSONException {
        for (int i = 0; i < matrixIds.length(); i++) {
            JSONObject matrix = matrixIds.getJSONObject(i);
            if (identifier.equals(matrix.getString("identifier"))) {
                return matrix;
            }
        }
        return null;
    }

    @Test
    public void parsesTileMatrixSetInformationCorrectly() throws IOException, IllegalArgumentException, XMLStreamException {
        List<TileMatrix> tileMatrices = new ArrayList<>(16);

        final double[] topLeftCorner = { -548576.0, 8388608.0 };
        double scaleDenominator = 29257142.85714285820722579956;
        for (int i = 0; i <= 15; i++) {
            int len = 1 << i;
            tileMatrices.add(new TileMatrix("" + i, scaleDenominator, topLeftCorner, 256, 256, len, len));
            scaleDenominator /= 2;
        }
        TileMatrixSet expected = new TileMatrixSet("ETRS-TM35FIN",
                "urn:ogc:def:crs:EPSG:6.3:3067", tileMatrices);

        WMTSCapabilities wmtsCapabilities;
        try (InputStream in = new BufferedInputStream(
                getClass().getClassLoader().getResourceAsStream("WMTSCapabilities.xml"))) {
            wmtsCapabilities = WMTSCapabilitiesParser.parseCapabilities(in);
        }
        Collection<TileMatrixSet> tileMatrixSets = wmtsCapabilities.getTileMatrixSets();

        assertEquals(1, tileMatrixSets.size());
        TileMatrixSet actual = tileMatrixSets.iterator().next();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCrs(), actual.getCrs());

        Map<String, TileMatrix> eTileMatrises = expected.getTileMatrixMap();
        Map<String, TileMatrix> aTileMatrises = actual.getTileMatrixMap();
        assertEquals(eTileMatrises.size(), aTileMatrises.size());
        for (String key : eTileMatrises.keySet()) {
            assertTrue(aTileMatrises.containsKey(key));
            TileMatrix eMatrix = eTileMatrises.get(key);
            TileMatrix aMatrix = aTileMatrises.get(key);
            assertEquals(eMatrix.getId(), aMatrix.getId());
            assertEquals(eMatrix.getScaleDenominator(), aMatrix.getScaleDenominator(), 0);
            assertEquals(eMatrix.getMatrixWidth(), aMatrix.getMatrixWidth());
            assertEquals(eMatrix.getMatrixHeight(), aMatrix.getMatrixHeight());
            assertEquals(eMatrix.getTileWidth(), aMatrix.getTileWidth());
            assertEquals(eMatrix.getTileHeight(), aMatrix.getTileHeight());
            assertEquals(eMatrix.getId(), aMatrix.getId());
            assertArrayEquals(eMatrix.getTopLeftCorner(), aMatrix.getTopLeftCorner(), 0);
        }
    }

    /**
     * These are used for giving OpenLayers on frontend a filtered response instead of the the whole XML.
     * Note! Any changes to JSON response needs a matching change on the frontend/layerJSONFormatterWMTS.
     * @throws Exception
     */
    @Test
    public void testOLJSON_NLS() throws Exception {
        WMTSCapabilities caps = WMTSCapabilitiesParser.parseCapabilities(capabilitiesInput_NLS);

        WMTSCapabilitiesLayer layer = caps.getLayer("taustakartta");
        ObjectMapper mapper = new ObjectMapper();
        // TODO: add more layers for comparing
        assertEquals("JSON should match", mapper.readTree(expectedJSON_NLS_taustakartta), mapper.readTree( mapper.writeValueAsString(layer)));
    }



}
