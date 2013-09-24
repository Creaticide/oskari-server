<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <Name>MyPlaces</Name>
    <UserStyle>
      <Title>MyPlaces</Title>
      <FeatureTypeStyle>
      <Rule>
        <ogc:Filter>
          <ogc:PropertyIsEqualTo>
            <ogc:Function name="in2">
              <ogc:Function name="geometryType">
                <ogc:PropertyName>geometry</ogc:PropertyName>
              </ogc:Function>
              <ogc:Literal>Point</ogc:Literal>
              <ogc:Literal>MultiPoint</ogc:Literal>
            </ogc:Function>
            <ogc:Literal>true</ogc:Literal>
          </ogc:PropertyIsEqualTo>       
       </ogc:Filter>
        
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>circle</WellKnownName>
                <Fill>
                   <CssParameter name="stroke"><ogc:PropertyName>dot_color</ogc:PropertyName></CssParameter>
                   <CssParameter name="fill"><ogc:PropertyName>dot_color</ogc:PropertyName></CssParameter>
                </Fill>
              </Mark>
              <Size>10</Size><!--<ogc:PropertyName>dot_size</ogc:PropertyName>-->
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <Rule>
          <ogc:Filter>      
            <ogc:PropertyIsEqualTo>
              <ogc:Function name="in2">
                <ogc:Function name="geometryType">
                  <ogc:PropertyName>geometry</ogc:PropertyName>
                </ogc:Function>
                <ogc:Literal>LineString</ogc:Literal>
                <ogc:Literal>MultiLineString</ogc:Literal>
              </ogc:Function>
              <ogc:Literal>true</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          
      <LineSymbolizer>
 <Stroke>
           <CssParameter name="stroke"><ogc:PropertyName>stroke_color</ogc:PropertyName></CssParameter>
           <CssParameter name="stroke-width"><ogc:PropertyName>stroke_width</ogc:PropertyName></CssParameter>
           <CssParameter name="stroke-linecap">round</CssParameter>
         </Stroke>
          </LineSymbolizer>
          <PointSymbolizer>
            <Geometry><ogc:Function name="vertices"><ogc:PropertyName>geometry</ogc:PropertyName></ogc:Function></Geometry>
            <Graphic>
              <Mark>
                <WellKnownName>square</WellKnownName>
                <Fill>
                  <CssParameter name="fill"><ogc:PropertyName>stroke_color</ogc:PropertyName></CssParameter>
                </Fill>
              </Mark>
              <Size>6</Size>
            </Graphic>
          </PointSymbolizer>
        <TextSymbolizer>
         <Label>
             <ogc:PropertyName>name</ogc:PropertyName><!-- ( <ogc:PropertyName>stroke_color</ogc:PropertyName>, <ogc:PropertyName>stroke_width</ogc:PropertyName>,
             <ogc:PropertyName>fill_color</ogc:PropertyName> )-->

         </Label>
         <Font>
           <CssParameter name="font-family">Arial</CssParameter>
           <CssParameter name="font-size">11</CssParameter>
           <CssParameter name="font-style">normal</CssParameter>
           <CssParameter name="font-weight">bold</CssParameter>
         </Font>
         <LabelPlacement>
           <PointPlacement>
             <AnchorPoint>
               <AnchorPointX>0.5</AnchorPointX>
               <AnchorPointY>0.5</AnchorPointY>
             </AnchorPoint>
           </PointPlacement>
         </LabelPlacement>
         <Fill>
           <CssParameter name="fill"><ogc:PropertyName>stroke_color</ogc:PropertyName></CssParameter>
         </Fill>
         <VendorOption name="autoWrap">60</VendorOption>
         <VendorOption name="maxDisplacement">150</VendorOption>
       </TextSymbolizer>
        </Rule>
                
       <Rule>  <ogc:Filter>
     <ogc:PropertyIsEqualTo>
       <ogc:Function name="in2">
          <ogc:Function name="geometryType">
              <ogc:PropertyName>geometry</ogc:PropertyName>
          </ogc:Function>
          <ogc:Literal>Polygon</ogc:Literal>
          <ogc:Literal>MultiPolygon</ogc:Literal>
       </ogc:Function>
       <ogc:Literal>true</ogc:Literal>
     </ogc:PropertyIsEqualTo>
   </ogc:Filter>
       <PolygonSymbolizer>
         <Fill>
           <CssParameter name="fill"><ogc:PropertyName>fill_color</ogc:PropertyName></CssParameter>
         </Fill>
         <Stroke>
           <CssParameter name="stroke"><ogc:PropertyName>border_color</ogc:PropertyName></CssParameter>
           <CssParameter name="stroke-width"><ogc:PropertyName>border_width</ogc:PropertyName></CssParameter>
         </Stroke>
       </PolygonSymbolizer>
       <TextSymbolizer>
         <Label>
           <ogc:PropertyName>name</ogc:PropertyName><!-- ( <ogc:PropertyName>stroke_color</ogc:PropertyName>, <ogc:PropertyName>stroke_width</ogc:PropertyName>,
             <ogc:PropertyName>fill_color</ogc:PropertyName> )-->
         </Label>
         <Font>
           <CssParameter name="font-family">Arial</CssParameter>
           <CssParameter name="font-size">11</CssParameter>
           <CssParameter name="font-style">normal</CssParameter>
           <CssParameter name="font-weight">bold</CssParameter>
         </Font>
         <LabelPlacement>
           <PointPlacement>
             <AnchorPoint>
               <AnchorPointX>0.5</AnchorPointX>
               <AnchorPointY>0.5</AnchorPointY>
             </AnchorPoint>
           </PointPlacement>
         </LabelPlacement>
         <Fill>
           <CssParameter name="fill"><ogc:PropertyName>stroke_color</ogc:PropertyName></CssParameter>
         </Fill>
         <VendorOption name="autoWrap">60</VendorOption>
         <VendorOption name="maxDisplacement">150</VendorOption>
       </TextSymbolizer>
     </Rule>
   </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>