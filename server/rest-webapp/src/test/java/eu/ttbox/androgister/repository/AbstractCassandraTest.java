package eu.ttbox.androgister.repository;
 
import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import eu.ttbox.androgister.ApplicationTestConfiguration;
import eu.ttbox.androgister.model.User;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.common.settings.ImmutableSettings;
//import org.elasticsearch.node.Node;
//import org.elasticsearch.node.NodeBuilder;
 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = ApplicationTestConfiguration.class,
        loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("default")
public abstract class AbstractCassandraTest {

    private static boolean isInitialized = false;

 
//    @Autowired
//    private CassandraUserRepository counterRepository;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (!isInitialized) {
            EmbeddedCassandraServerHelper.startEmbeddedCassandra();
            /* create structure and load data */
            String clusterName = "androgister";
            String host = "localhost:9171";
            DataLoader dataLoader = new DataLoader(clusterName, host);
            dataLoader.load(new ClassPathJsonDataSet("dataset/dataset.json")); 
            
            isInitialized = true;
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
//        EmbeddedCassandraServerHelper.stopEmbeddedCassandra();
    }

    protected User constructAUser(String login, String firstName, String lastName) {
        User user = new User();
        user.login = login;
        user.password= ""; 
        user.firstName = firstName;
        user.lastName = lastName;
        user.jobTitle="web developer";
         
        return user;
    }

    protected User constructAUser(String login) {
        return constructAUser(login, null, null);
    }

}