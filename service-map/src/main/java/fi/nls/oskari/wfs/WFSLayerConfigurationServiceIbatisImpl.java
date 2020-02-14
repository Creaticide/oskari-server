package fi.nls.oskari.wfs;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;
import fi.nls.oskari.domain.map.wfs.WFSLayerConfiguration;
import fi.nls.oskari.domain.map.wfs.WFSParserConfig;
import fi.nls.oskari.domain.map.wfs.WFSSLDStyle;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.db.BaseIbatisService;
import fi.nls.oskari.util.ConversionHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WFSLayerConfigurationServiceIbatisImpl extends BaseIbatisService<WFSLayerConfiguration> implements WFSLayerConfigurationService {

    private final static Logger log = LogFactory.getLogger(WFSLayerConfigurationServiceIbatisImpl.class);

    @Override
    protected String getNameSpace() {
        return "WFSLayerConfiguration";
    }

    public WFSLayerConfiguration findConfiguration(final int id) {
    	WFSLayerConfiguration conf = queryForObject(getNameSpace() + ".findLayer", id);
    	if(conf == null) {
            return null;
        }
    	final List<WFSSLDStyle> styles = findWFSLayerStyles(id);
    	conf.setSLDStyles(styles);
    	return conf;
    }

    public List<WFSSLDStyle> findWFSLayerStyles(final int layerId) {
        List<WFSSLDStyle> styles = queryForList(getNameSpace() + ".findStylesForLayer", layerId);
        return styles;
    }

    public List<WFSParserConfig> findWFSParserConfigs(String name) {
        List<WFSParserConfig> configs = queryForList(getNameSpace() + ".findParserConfigs", name);
        return configs;
    }


    public void update(final WFSLayerConfiguration layer) {
        try {
            getSqlMapClient().update(getNameSpace() + ".update", layer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update", e);
        }
    }

    public void delete(final int id)  {
        long maplayer_id = Long.valueOf(id);
        final SqlMapSession session = openSession();
        try {
            session.startTransaction();
            // remove wfs layer
            session.delete(getNameSpace() + ".delete", maplayer_id);
            session.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting wfs layer with maplayer_id:" + Long.toString(maplayer_id), e);
        } finally {
            endSession(session);
        }
    }
}
