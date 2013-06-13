package com.github.shyiko.rook.target.hibernate.cache;

import com.github.shyiko.rook.target.hibernate.cache.model.DummyEntity;
import com.github.shyiko.rook.target.hibernate.cache.model.DummyEntityTwoFieldPK;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 *         2013-06-12
 */
@ContextConfiguration(locations = {
        "classpath:hibernate-spring-test-context.xml"
})
public class SynchronizerContextTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    LocalSessionFactoryBean sessionFactoryBean;


    private String[] mapColumnValues(String entityName, Map<String, String> columnValuesByName) {
        Table table = sessionFactoryBean.getConfiguration().getClassMapping(entityName).getTable();
        List<String> result = new ArrayList<String>();

        for (@SuppressWarnings("unchecked") Iterator<Column> iterator = table.getColumnIterator(); iterator.hasNext(); ) {
            Column column = iterator.next();
            if (columnValuesByName.containsKey(column.getName())) {
                result.add(columnValuesByName.get(column.getName()));
            }
        }
        return result.toArray(new String[result.size()]);
    }

    @Test
    public void testKeyMappings() throws Exception {
        SynchronizationContext synchronizationContext = new SynchronizationContext(sessionFactoryBean.getConfiguration(), sessionFactory);


        {
            List<EvictionTarget> dummyEvictionTargets = new ArrayList<EvictionTarget>(synchronizationContext.getEvictionTargets("rook.dummy_entity"));

            String[] allFieldsFofDummy = mapColumnValues(DummyEntity.class.getName(), new LinkedHashMap<String, String>() {{
                put("id", "id");
                put("name", "name");
            }});

            String[] keyFieldsFofDummy = mapColumnValues(DummyEntity.class.getName(), new LinkedHashMap<String, String>() {{
                put("id", "id");
            }});

            Assert.assertEquals(dummyEvictionTargets.get(0).getPrimaryKey().of(allFieldsFofDummy), keyFieldsFofDummy);
        }


        {
            List<EvictionTarget> dummyEvictionTargets = new ArrayList<EvictionTarget>(synchronizationContext.getEvictionTargets("rook.dummy_entity_2fpk"));

            String[] allFieldsFofDummy2 = mapColumnValues(DummyEntityTwoFieldPK.class.getName(), new LinkedHashMap<String, String>() {{
                put("id", "id");
                put("id2", "id2");
                put("name", "name");
            }});

            String[] keyFieldsFofDummy2 = mapColumnValues(DummyEntityTwoFieldPK.class.getName(), new LinkedHashMap<String, String>() {{
                put("id", "id");
                put("id2", "id2");
            }});

            Assert.assertEquals(dummyEvictionTargets.get(0).getPrimaryKey().of(allFieldsFofDummy2), keyFieldsFofDummy2);
        }

    }

}
